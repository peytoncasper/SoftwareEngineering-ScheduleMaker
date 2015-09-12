import json
from pprint import pprint

import threading
from queue import Queue
import time
#multithreading components

from Functions import *
#Webscraping Functionality

arrayWrite_lock = threading.Lock()

# The threader thread pulls an worker from the queue and processes it
def threader():
    while True:
        # gets an workorder from the queue
        workorder = q.get()

        Semester = workorder[0]
        Acronym = workorder[1]
        Name = workorder[2]

        DepartmentsArray = workorder[3]

        Courses = GetAllDeptCourses(Acronym, Semester)

        Dept = {}
        Dept['ACRONYM'] = Acronym
        Dept['NAME'] = Name

        #Threaded stuff here?

        Dept['COURSES'] = Courses['COURSES'] #GetAllDeptCourses Results Go Here

        with arrayWrite_lock:
            DepartmentsArray.append(Dept)

        # completed with the job
        q.task_done()

ActiveSemesterFile = "DepartmentData\ActiveSemesters.JSON"
DepartmentsFile = "DepartmentData\Departments.JSON"
ActiveCoursesDataFile = "DepartmentData\AllCourses.JSON"

with open(ActiveSemesterFile) as SemestersDataFile:
    Semesters = json.load(SemestersDataFile)
    
with open(DepartmentsFile) as DepartmentDataFile:
    DepartmentsDictionary = json.load(DepartmentDataFile)

DepartmentData = []

# Create the queue and threader 
q = Queue()

# how many threads are we going to allow for
for x in range(25):
     t = threading.Thread(target=threader)

     # classifying as a daemon, so they will die when the main dies
     t.daemon = True

     # begins, must come after daemon definition
     t.start()


for key in Semesters:
    Semester = {}
    Departments = []
    Semester['NUMBER'] = key
    Semester['DESCRIPTION'] = Semesters[key]
    Semester['DEPARTMENTS'] = Departments

    for DepartmentAcronym in DepartmentsDictionary:
        workorder = (key, DepartmentAcronym, DepartmentsDictionary[DepartmentAcronym], Departments)
        q.put(workorder)

    q.join()
        
    DepartmentData.append(Semester)

DepartmentDataFile = open(ActiveCoursesDataFile,'w')
DepartmentDataFile.write(json.dumps(DepartmentData, sort_keys = True, indent = 4))
DepartmentDataFile.close()

print("Update Complete")

