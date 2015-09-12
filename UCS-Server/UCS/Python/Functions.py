import requests
from bs4 import BeautifulSoup
import json
import re
import time

readable = False

def renderMultiCourses(soup):
    # Get each course table
    coursesTables = soup.findAll('div', {'id':re.compile('^win0divSSR_CLSRSLT_WRK_GROUPBOX2\$')})
    Result = {}
    Courses = []
    try:
        for coursesTable in coursesTables:
            Course = {}
            Sections = []
            
            tables = coursesTable.find_all('tr', {'id':re.compile('^trSSR_CLSRCH_MTG')})
            '''
            for table in tables:
                detailSpans = table.find_all('span')
                detailDict = {}
                Section = {}
                for span in detailSpans:
                    detailDict[span.get('id').partition('_')[2].partition('$')[0]] = span.text.split('\r')[0]

                daysString = detailDict['DAYTIME'].split(' ',1)[0]
                
                if 'TBA' not in daysString:
                    Section['MeetingDays']=[daysString[i:i+2].upper() for i in range(0, len(detailDict['DAYTIME'].split(' ',1)[0]), 2)]
                    Section['MeetingTime']=detailDict['DAYTIME'].split(' ',1)[1].replace(' ','')
                else:
                    Section['MeetingDays']=[]
                    Section['MeetingTime']=detailDict['DAYTIME']
                
                Section['CourseNumber']=detailDict['CLASS_NBR']
                Section['Section']=detailDict['CLASSNAME'].split('-')[0]
                Section['Room']=detailDict['ROOM'].replace("\xa0","")
                Section['Instructor']=detailDict['INSTR']
                Section['Status']=table.find('img').get('alt').upper().replace(' ','_')
                
                Sections.append(Section)
            '''
            CourseInfo = coursesTable.find('div', {'id':re.compile('^win0divSSR_CLSRSLT_WRK_GROUPBOX2GP')})
            
            if CourseInfo is not None:
                CourseInfo = CourseInfo.text
                CourseInfo = CourseInfo.split(' - ')
                CourseName = CourseInfo[1][:-1]
                #Department = CourseInfo[0].split(' ')[0][1:]
                CourseID = CourseInfo[0].split(' ')[1]

            Course['ID'] = CourseID
            Course['NAME'] = CourseName.split('\u00a0')[0]
            #Course['Department'] = Department
            #Course['CourseResults'] = Sections

            Courses.append(Course)

        Result['COURSES'] = Courses
            
    except AttributeError as err:
        Result['ERROR'] = str(err)
        Result['message'] = 'Built before error:' + str(Section)
        LogLocation = time.strftime("%m %d-%H:%M:%M") + " LogDump.html"
        ErrorLog = open(LogLocation, 'w+')
        print(soup.prettify(), file=ErrorLog)
        ErrorLog.close()
        

    return Result

def renderCourses(soup):
    # Get each section table
    tables = soup.find_all('tr', {'id':re.compile('^trSSR_CLSRCH_MTG')})
    classes = []
    Result = {}
    try:
        for table in tables:
            detailSpans = table.find_all('span')
            detailDict = {}
            Section = {}
            for span in detailSpans:
                detailDict[span.get('id').partition('_')[2].partition('$')[0]] = span.text.split('\r')[0]

            daysString = detailDict['DAYTIME'].split(' ',1)[0]
            
            if 'TBA' not in daysString:
                Section['MeetingDays']=[daysString[i:i+2].upper() for i in range(0, len(detailDict['DAYTIME'].split(' ',1)[0]), 2)]
                Section['MeetingTime']=detailDict['DAYTIME'].split(' ',1)[1].replace(' ','')
            else:
                Section['MeetingDays']=[]
                Section['MeetingTime']=detailDict['DAYTIME']
            
            Section['CourseNumber']=detailDict['CLASS_NBR']
            Section['Section']=detailDict['CLASSNAME'].split('-')[0]
            Section['Room']=detailDict['ROOM']
            Section['Instructor']=detailDict['INSTR']
            Section['Status']=table.find('img').get('alt').upper().replace(' ','_')
            
            classes.append(Section)



        CourseInfo = soup.find('div', {'id':re.compile('^win0divSSR_CLSRSLT_WRK_GROUPBOX2GP')})

        if CourseInfo is not None:
            CourseInfo = CourseInfo.text
            CourseInfo = CourseInfo.split(' - ')
            CourseName = CourseInfo[1][:-1]
            Department = CourseInfo[0].split(' ')[0][1:]
            CourseID = CourseInfo[0].split(' ')[1]

            Result['CourseId'] = CourseID
            Result['CourseName'] = CourseName.split('\u00a0')[0]
            Result['Department'] = Department
            Result['CourseResults'] = classes
            
    except AttributeError as err:
        Result['ERROR'] = str(err)
        Result['message'] = 'Built before error:' + str(Section)
        LogLocation = time.strftime("%m %d-%H:%M:%M") + " LogDump.html"
        ErrorLog = open(LogLocation, 'w+')
        print(soup.prettify(), file=ErrorLog)
        ErrorLog.close()
        

    return Result
    
def SearchSetup( semester ):
    # make initial call to myMav website
    session = requests.Session()
    session.get('https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL')

    # Setup POST params for setting semester
    postParameters = {}
    postParameters['ICACTION']  = 'CLASS_SRCH_WRK2_STRM$273$'
    postParameters['CLASS_SRCH_WRK2_STRM$273$']  = semester

    # submit POST request and store response for error handling
    session.post('https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL', data=postParameters)
    return session

def CourseSearch(department, classNumber, semester):
    
    session = SearchSetup(semester)

    # Setup POST params for setting department
    postParameters = {}
    postParameters['ICACTION']  = 'SSR_CLSRCH_WRK_SUBJECT$0'
    postParameters['SSR_CLSRCH_WRK_SUBJECT$0']  = department

    # submit POST request and store response for error handling
    session.post('https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL', data=postParameters)

    # Setup POST params for setting class number
    postParameters = {}
    postParameters['ICACTION']  = 'CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH'
    postParameters['SSR_CLSRCH_WRK_CATALOG_NBR$1']  = classNumber
    postParameters['SSR_CLSRCH_WRK_SSR_OPEN_ONLY$chk$3']  = 'N'

    # submit POST request and store response for parsing of class results
    classSearchResponse = session.post('https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL', data=postParameters)
    soup = BeautifulSoup(classSearchResponse.text, "html.parser")
    
    Result = renderCourses(soup)
    Result['Semester'] = semester

    '''
    if readable:
        return (json.dumps(Result, indent = 4).replace('\\u00a0',''))
    else:
        return (json.dumps(Result).replace('\\u00a0',''))
    '''
    return Result

def SectionSearch(sectionNumber, semester):
    
    session = SearchSetup(semester)

    # Setup POST params for expanding advanced seach area
    postParameters = {}
    postParameters['ICACTION']  = 'DERIVED_CLSRCH_SSR_EXPAND_COLLAPS$149$$1'

    # submit POST request and store response for error handling
    session.post('https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL', data=postParameters)

    # Setup POST params for setting department1`    
    postParameters = {}
    postParameters['ICACTION']  = 'CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH'
    postParameters['SSR_CLSRCH_WRK_SSR_OPEN_ONLY$chk$3']  = 'N'
    postParameters['SSR_CLSRCH_WRK_SSR_EXACT_MATCH1$1']  = 'G'
    postParameters['SSR_CLSRCH_WRK_CATALOG_NBR$1']  = '0'
    postParameters['SSR_CLSRCH_WRK_CLASS_NBR$8']  = sectionNumber

    # submit POST request and store response for error handling
    departmentSelectResponse = session.post('https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL', data=postParameters)
    soup = BeautifulSoup(departmentSelectResponse.text, "html.parser")

    Result = renderCourses(soup)
    Result['Semester'] = semester
    
    '''
    if readable:
        return (json.dumps(Result, indent = 4).replace('\\u00a0',''))
    else:
        return (json.dumps(Result).replace('\\u00a0',''))
    '''
    return Result

def GetAllDeptCourses(department, semester):
    
    session = SearchSetup(semester)

    # Setup POST params for setting department
    postParameters = {}
    postParameters['ICACTION']  = 'SSR_CLSRCH_WRK_SUBJECT$0'
    postParameters['SSR_CLSRCH_WRK_SUBJECT$0']  = department

    # submit POST request and store response for error handling
    session.post('https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL', data=postParameters)

    # Setup POST params for setting class number
    postParameters = {}
    postParameters['ICACTION']  = 'CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH'
    postParameters['SSR_CLSRCH_WRK_SSR_OPEN_ONLY$chk$3']  = 'N'
    postParameters['SSR_CLSRCH_WRK_SSR_EXACT_MATCH1$1']  = 'G'
    postParameters['SSR_CLSRCH_WRK_CATALOG_NBR$1']  = '0'

    # submit POST request and store response for error handling
    departmentSelectResponse = session.post('https://sis-cs-prod.uta.edu/psc/ACSPRD/EMPLOYEE/PSFT_ACS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL', data=postParameters)
    soup = BeautifulSoup(departmentSelectResponse.text, "html.parser")

    logfile = open("Log.html",'w')
    logfile.write(departmentSelectResponse.text)
    logfile.close()

    Result = renderMultiCourses(soup)
    #Result['Semester'] = semester
    
    '''
    if readable:
        return (json.dumps(Result, indent = 4).replace('\\u00a0',''))
    else:
        return (json.dumps(Result).replace('\\u00a0',''))
    '''
    return Result
