# -*- coding: utf-8 -*-
"""

This script writes an array of dates of weekdays as YYYYMMDD
in a csv file. It starts with user's input, 
which must be a monday in the same format

Created on Wed Jan 25 07:36:22 2017

@author: cenka
"""
import csv

def displayDate(year, month, day):
	date = "%d%02d%02d" % (year, month, day)
	return date

monthLength = {"01":[31, 31], "02":[28, 29], "03":[31, 31], 
               "04":[30, 30], "05":[31, 31], "06":[30, 30], 
               "07":[31, 31], "08":[31, 31], "09":[30, 30], 
               "10":[31, 31], "11":[30, 30], "12":[31, 31]}
               
leapYears = [2008, 2012, 2016, 2020, 2024, 2028, 2032, 2036, 2040, 2044, 2048, 2052, 2056, 2060]


firstDay = raw_input("Please enter the starting date: ")
lastDay = int(raw_input("Please enter the ending date: "))

year = int(firstDay[:4])
month = int(firstDay[4:6])
day = int(firstDay[6:])

intFirstDay = int(firstDay)
count = 1
listIndex = 0

listOfDates = list()
listOfDates.append(intFirstDay)

while intFirstDay < lastDay :
    
	day += 1
	count += 1	

	for key in monthLength :

		if year not in leapYears : 
		
			if int(key) == month and monthLength[key][0] +1 == day :
				month += 1
				day = 1	

		else :
			if int(key) == month and monthLength[key][1] + 1 == day :
				month += 1
				day = 1
				
	if month == 13 :
         year += 1
         month = 1

	if count == 6 or count == 7:
		continue
		
	elif count == 8 :
		count = 1
    
	listIndex += 1
	
	firstDay = displayDate(year, month, day)
	intFirstDay = int(firstDay)
	
	listOfDates.append(intFirstDay)

with open("dates.csv", "wb") as datecsv:
    datewr = csv.writer(datecsv)
    
    for k in range(len(listOfDates)):
        datewr.writerow([listOfDates[k]])

datecsv.close()