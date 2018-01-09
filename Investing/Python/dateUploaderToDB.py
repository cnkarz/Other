import sqlite3
import csv


conn = sqlite3.connect("D:\\Programs\\sqlite3\\financialdata.db")

c = conn.cursor()

#c.execute("CREATE TABLE daily_closing_rate (date INTEGER PRIMARY KEY);")
#count = 0

#with open("dates.csv", "rb") as datescsv:
#	datesR = csv.reader(datescsv)

#	for row in datesR:
#		for date in row:
#			c.execute("INSERT INTO daily_closing_rate (date) VALUES (%d);" % int(date) )
#			count += 1

#print(str(count) + " rows added to the database..")
all = c.execute("SELECT * FROM daily_closing_rate;")



all = c.execute("PRAGMA table_info(daily_closing_rate)")

for each in all:
		print each
		
conn.commit()
			
conn.close()
datescsv.close()