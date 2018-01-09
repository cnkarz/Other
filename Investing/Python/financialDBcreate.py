###############################################################
################	GET FILENAMES INSIDE A FOLDER	#####################

import os
import os.path

myPath = "C:\\MTX_ASC_DATA"

myFiles = os.listdir(myPath)
fileNames = list()

for f in myFiles:
	name = os.path.splitext(f)[0]
	fileNames.append(name)
	
table = "CREATE TABLE rates (date INTEGER PRIMARY KEY"
columns = ""

for f in fileNames:
	columns = columns + ", " + f + " FLOAT"
	
statement = table + columns + ");"


###############################################################
################	CONNECT TO DB AND CREATE TABLE	###################


import mysql.connector as mysql

conn = mysql.connect(user= 'root', password= '12345678', host= 'localhost', database= 'financialdata')

c = conn.cursor()

c.execute(statement) # see the part above


###############################################################
################	ADD DATES TO THE DATABASE	#######################

import os
import csv

os.chdir("D:\\Documents\\Work")

count = 0

with open("dates.csv", "rb") as datescsv:
	datesR = csv.reader(datescsv)

	for row in datesR:
		for date in row:
			c.execute("INSERT INTO rates (date) VALUES (%d);" % int(date) )
			count += 1

print(str(count) + " rows added to the database..")


###############################################################
###############################################################

conn.commit()
			
conn.close()