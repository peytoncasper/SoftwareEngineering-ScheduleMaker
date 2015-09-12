#Sample execution. Adjust call to python according to system
#python3 ThreadSectionSearch.py "{\"sections\":[\"83165\",\"81353\"],\"semester\":2158}"

import threading
from queue import Queue
import time
#multithreading components

from Functions import *
#Webscraping Functionality

import sys
import json
#print(sys.argv[1])
data=json.loads(sys.argv[1])
#input parsing

#readable = True
results = []
print_lock = threading.Lock()

def processClass(sectionNumber):
    courseResults = SectionSearch(sectionNumber, data['semester'])
    with print_lock:
        results.append(courseResults)

# The threader thread pulls an worker from the queue and processes it
def threader():
    while True:
        # gets an worker from the queue
        sectionNumber = q.get()

        # Run the example job with the avail worker in queue (thread)
        processClass(sectionNumber)

        # completed with the job
        q.task_done()

# Create the queue and threader 
q = Queue()

# how many threads are we going to allow for
for x in range(25):
     t = threading.Thread(target=threader)

     # classifying as a daemon, so they will die when the main dies
     t.daemon = True

     # begins, must come after daemon definition
     t.start()

start = time.time()

# jobs assigned.
for sectionNumber in data['sections']:
    q.put(sectionNumber)

# wait until the thread terminates.
q.join()

# with 10 workers and 20 tasks, with each task being .5 seconds, then the completed job
# is ~1 second using threading. Normally 20 tasks with .5 seconds each would take 10 seconds.
ReturnJSON = {}
ReturnJSON['TimeTaken'] = time.time() - start
ReturnJSON['Results'] = results
ReturnJSON['Success'] = True
print(json.dumps(ReturnJSON, indent = 2).replace('\\u00a0',''))
#print('Entire job took:',time.time() - start)
