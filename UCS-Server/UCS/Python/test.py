from pprint import pprint

def testfunc(sampleTuple):
    pprint(sampleTuple[0])
    sampleTuple[0]['add'] = "new"
    pprint(testDict)

testDict = {}
testDict['ex'] = "ex"

testTuple = (testDict,)
testfunc(testTuple)

testDict['old'] = "old"
pprint(testDict)
