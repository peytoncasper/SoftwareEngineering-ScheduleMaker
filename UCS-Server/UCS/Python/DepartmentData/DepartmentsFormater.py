filename = input("Enter file name (without .txt ending)")

destinationFile = open(filename+".json", 'w')
destinationFile.write("{\n")

filename += ".txt"
with open(filename) as f:
    content = f.readlines()
    for entry in content:
        parts = entry.split(" (")
        destinationFile.write("\"")
        destinationFile.write(parts[1][:-2])
        destinationFile.write("\":")
        destinationFile.write("\""+parts[0]+"\"")
        destinationFile.write(",\n")
        
destinationFile.write("}")
destinationFile.close()
