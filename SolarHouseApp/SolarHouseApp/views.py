from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
import time
import socket
import sqlite3
import string

def index(request):
	return HttpResponse("BASE WEBSITE")

def getRequestTemperature(request):
	# DB query
	# Set up data for the get request
	# Send data
	#return HttpResponse("humidity:11:0:0:0")
	return HttpResponse(queryDatabase("temperature"))

def getRequestHumidity(request):
	return HttpResponse(queryDatabase("humidity"))

def getRequestCO2(request):
	return HttpResponse(queryDatabase("co2"))

def getRequestEnergy(request):
	return HttpResponse(queryDatabase("energy"))

@csrf_exempt
def postRequest(request):
	"""
	time.sleep(2)
	reqString = request.readline()
	reqStringSplit = reqString.split(':')

	value = int(reqStringSplit[1])
	

	return HttpResponse('Data is received')
	"""

	time.sleep(2)
	#return HttpResponse("YOU GOT INTO POST REQUEST")

	#HttpResponse("YOU GOT INTO POST REQUEST")
	
	#temperature = 0
	#humidity = 0
	#co2 = 0
	#energy = 0

	time.sleep(2)
	
	temperature = request.POST.get('temperature')

	if isinstance(temperature, str):
		#temperatureFunction(temperature)
		return HttpResponse('Temperature has been stored ' + str(temperature))
	"""
	humidity = request.POST.get('humidity')
	if humidity != 0:
		#humidityFunction(humidity)
		return HttpResponse('Humidity has been stored')	
	
	co2 = request.POST.get('co2')
	if co2 != 0:
		#co2Function(co2)
		return HttpResponse('CO2 has been stored')
	energy = request.POST.get('energy')
	if energy != 0:
		#energyFunction(energy)
		return HttpResponse('Energy has been stored')
	
	time.sleep(2)
	"""
	return HttpResponse('No data has been stored ' + str(temperature))





#----------------------------------------------------------------------


# not working

"""
def createDatabases():
	# I think this is necessary
	connection = sqlite3.connect("SILO.db") # this can be name something else


	CREATE TABLE 
	IF NOT EXISTS Temperature ( 
		datetime DATETIME, 
		temperature INT NOT NULL);

	CREATE TABLE
	IF NOT EXISTS Humidity (
		datetime DATETIME,
		humidity INT NOT NULL);

	CREATE TABLE
	IF NOT EXISTS CO2 (
		datetime DATETIME,
		co2 INT NOT NULL);

	CREATE TABLE
	IF NOT EXISTS Energy (
		datetime DATETIME,
		energy INT NOT NULL);
"""

"""
def storeMessage():
	connection = sqlite3.connect("Users\hedce\Desktop\SolarHouse\Python\Django\SolarHouseSensorApp\SolarHouseApp\sqlite\silo.db")
	cursor = connection.cursor()
	a = datetime.datetime.strptime('my date', "%b %d %Y %H:%M")


	if (string[0:1].lower() == "t"):
		sql_command = 'INSERT INTO Temperature (datetime, temperature) VALUES (%s, givenTemperature)', (a.strftime('%Y-%m-%d %H:%M:%S'));
		cursor.execute(sql_command)

	if (string[0:1].lower() == "h"):
		sql_command = 'INSERT INTO Humidity (datetime, humidity) VALUES (%s, givenHumidity)', (a.strftime('%Y-%m-%d %H:%M:%S'));
		cursor.execute(sql_command)

	if (string[0:1].lower() == "c"):
		sql_command = 'INSERT INTO CO2 (datetime, co2) VALUES (%s, givenCO2)', (a.strftime('%Y-%m-%d %H:%M:%S'));
		cursor.execute(sql_command)

	if (string[0:1].lower() == "e"):
		sql_command = 'INSERT INTO Energy (datetime, energy) VALUES (%s, givenEnergy)', (a.strftime('%Y-%m-%d %H:%M:%S'));
		cursor.execute(sql_command)

	# never forget this, if you want the changes to be saved:
	connection.commit()
"""



def queryDatabase(data_type):
	#connection = sqlite3.connect("Users\hedce\Desktop\SolarHouse\Python\Django\SolarHouseSensorApp\SolarHouseApp\sqlite\silo.db")
	connection = sqlite3.connect("silo.db")
	cursor = connection.cursor()

	print(data_type)

	# The "-6 days" parameter may not need the space
	# Get the data requested from the past day, week, and month

	if(data_type == "temperature"):
		# sql_query_day = "SELECT AVG(CAST(temperature AS REAL)) FROM Temperature WHERE datetime >= datetime('now', '-1 days');"
		sql_query_day = "SELECT AVG(CAST(temperature AS REAL)) FROM Temperature WHERE datetime >= datetime('now', '-1 days');"
		cursor.execute(sql_query_day)
		temperature_day = cursor.fetchall()
		sql_query_week = "SELECT AVG(CAST(temperature AS REAL)) FROM Temperature WHERE datetime >= datetime('now', '-6 days');"
		cursor.execute(sql_query_week)
		temperature_week = cursor.fetchall()
		sql_query_month = "SELECT AVG(CAST(temperature AS REAL)) FROM Temperature WHERE datetime >= datetime('now', '-30 days');"
		cursor.execute(sql_query_month)
		temperature_month = cursor.fetchall()
		query_total = "temperature:" + str(temperature_day) + ":" + str(temperature_week) + ":" + str(temperature_month)

	if(data_type == "humidity"):
		sql_query_day = "SELECT AVG(CAST(humidity AS REAL)) FROM Humidity WHERE datetime >= datetime('now', '-1 days');"
		cursor.execute(sql_query_day)
		humidity_day = cursor.fetchall()
		sql_query_week = "SELECT AVG(CAST(humidity AS REAL)) FROM Humidity WHERE datetime >= datetime('now', '-6 days');"
		cursor.execute(sql_query_week)
		humidity_week = cursor.fetchall()
		sql_query_month = "SELECT AVG(CAST(humidity AS REAL)) FROM Humidity WHERE datetime >= datetime('now', '-30 days');"
		cursor.execute(sql_query_month)
		humidity_month = cursor.fetchall()
		query_total = "humidity:" + str(humidity_day) + ":" + str(humidity_week) + ":" + str(humidity_month)

	if(data_type == "co2"):
		sql_query_day = "SELECT AVG(CAST(CO2 AS REAL)) FROM CO2 WHERE datetime >= datetime('now', '-1 days');"
		cursor.execute(sql_query_day)
		co2_day = cursor.fetchall()
		sql_query_week = "SELECT AVG(CAST(CO2 AS REAL))FROM CO2 WHERE datetime >= datetime('now', '-6 days');"
		cursor.execute(sql_query_week)
		co2_week = cursor.fetchall()
		sql_query_month = "SELECT AVG(CAST(CO2 AS REAL)) FROM CO2 WHERE datetime >= datetime('now', '-30 days');"
		cursor.execute(sql_query_month)
		co2_month = cursor.fetchall()
		query_total = "co2:" + str(co2_day) + ":" + str(co2_week) + ":" + str(co2_month)

	if(data_type == "energy"):
		sql_query_day = "SELECT AVG(CAST(Energy AS REAL)) FROM Energy WHERE datetime >= datetime('now', '-1 days');"
		cursor.execute(sql_query_day)
		energy_day = cursor.fetchall()
		sql_query_week = "SELECT AVG(CAST(Energy AS REAL)) FROM Energy WHERE datetime >= datetime('now', '-6 days');"
		cursor.execute(sql_query_week)
		energy_week = cursor.fetchall()
		sql_query_month = "SELECT AVG(CAST(Energy AS REAL)) FROM Energy WHERE datetime >= datetime('now', '-30 days');"
		cursor.execute(sql_query_month)
		energy_month = cursor.fetchall()
		query_total = "energy:" + str(energy_day) + ":" + str(energy_week) + ":" + str(energy_month)

	print(query_total)
	
	return query_total
