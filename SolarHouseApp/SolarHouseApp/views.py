from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
import time
import socket
import sqlite3
import string

def index(request):
	return HttpResponse("BASE WEBSITE")

def getRequestTemperature(request):
	variableQuery = queryDatabase("temperature")
	variableQuery = fixMessage(variableQuery)
	return HttpResponse(variableQuery)

def getRequestHumidity(request):
	variableQuery = queryDatabase("humidity")
	variableQuery = fixMessage(variableQuery)
	return HttpResponse(variableQuery)

def getRequestCO2(request):
	variableQuery = queryDatabase("co2")
	variableQuery = fixMessage(variableQuery)
	return HttpResponse(variableQuery)

def getRequestEnergy(request):
	variableQuery = queryDatabase("energy")
	variableQuery = fixMessage(variableQuery)
	return HttpResponse(variableQuery)

def postRequest(request):
	variable = request.body.decode('utf-8')
	variable = variable.split("=")
	if request.method == 'POST':
		variableString = variable[0]
		variableInt = float(variable[1])
		storeMessage(variableString, variableInt)

	return HttpResponse('HI DICK')

def fixMessage(variableQuery):
	variableQuery = variableQuery.split("[")
	variableQuery = "".join(variableQuery)
	variableQuery = variableQuery.split("]")
	variableQuery = "".join(variableQuery)
	variableQuery = variableQuery.split("(")
	variableQuery = "".join(variableQuery)
	variableQuery = variableQuery.split(")")
	variableQuery = "".join(variableQuery)
	variableQuery = variableQuery.split(",")
	variableQuery = "".join(variableQuery)
	print(variableQuery)
	return variableQuery

def storeMessage(data_type, givenData):
	connection = sqlite3.connect("silo.db")
	cursor = connection.cursor()


	if (data_type[0:1].lower() == "t"):
		sql_command = 'INSERT INTO Temperature (datetime, temperature) VALUES (CURRENT_TIMESTAMP, ' + str(givenData) + ');'
		cursor.execute(sql_command)

	if (data_type[0:1].lower() == "h"):
		sql_command = 'INSERT INTO Humidity (datetime, humidity) VALUES (CURRENT_TIMESTAMP, ' + str(givenData) + ');'
		cursor.execute(sql_command)

	if (data_type[0:1].lower() == "c"):
		sql_command = 'INSERT INTO CO2 (datetime, co2) VALUES (CURRENT_TIMESTAMP, ' + str(givenData) + ');'
		cursor.execute(sql_command)

	if (data_type[0:1].lower() == "e"):
		sql_command = 'INSERT INTO Energy (datetime, energy) VALUES (CURRENT_TIMESTAMP, ' + str(givenData) + ');'
		cursor.execute(sql_command)

	# never forget this, if you want the changes to be saved:
	connection.commit()




def queryDatabase(data_type):
	#connection = sqlite3.connect("Users\hedce\Desktop\SolarHouse\Python\Django\SolarHouseSensorApp\SolarHouseApp\sqlite\silo.db")
	connection = sqlite3.connect("silo.db")
	cursor = connection.cursor()

	print(data_type)

	# The "-6 days" parameter may not need the space
	# Get the data requested from the past day, week, and month

	if(data_type == "temperature"):
		# sql_query_day = "SELECT AVG(CAST(temperature AS REAL)) FROM Temperature WHERE datetime >= datetime('now', '-1 days');"
		sql_query_latest = "SELECT temperature FROM Temperature WHERE datetime = (SELECT MAX(datetime) FROM Temperature);"
		cursor.execute(sql_query_latest)
		temperature_latest = cursor.fetchall()
		sql_query_day = "SELECT AVG(CAST(temperature AS REAL)) FROM Temperature WHERE datetime >= datetime('now', '-1 days');"
		cursor.execute(sql_query_day)
		temperature_day = cursor.fetchall()
		sql_query_week = "SELECT AVG(CAST(temperature AS REAL)) FROM Temperature WHERE datetime >= datetime('now', '-6 days');"
		cursor.execute(sql_query_week)
		temperature_week = cursor.fetchall()
		sql_query_month = "SELECT AVG(CAST(temperature AS REAL)) FROM Temperature WHERE datetime >= datetime('now', '-30 days');"
		cursor.execute(sql_query_month)
		temperature_month = cursor.fetchall()
		query_total = "Temperature:" + str(temperature_latest) + ":" + str(temperature_day) + ":" + str(temperature_week) + ":" + str(temperature_month)

		if str(temperature_latest) == 'None':
			temperature_latest = '0'
		if str(temperature_day) == 'None':
			temperature_day = '0'
		if str(temperature_week) == 'None':
			temperature_week = '0'
		if str(temperature_month) == 'None':
			temperature_month = '0'

	if(data_type == "humidity"):
		sql_query_latest = "SELECT humidity FROM Humidity WHERE datetime = (SELECT MAX(datetime) FROM Humidity);"
		cursor.execute(sql_query_latest)
		humidity_latest = cursor.fetchall()
		sql_query_day = "SELECT AVG(CAST(humidity AS REAL)) FROM Humidity WHERE datetime >= datetime('now', '-1 days');"
		cursor.execute(sql_query_day)
		humidity_day = cursor.fetchall()
		sql_query_week = "SELECT AVG(CAST(humidity AS REAL)) FROM Humidity WHERE datetime >= datetime('now', '-6 days');"
		cursor.execute(sql_query_week)
		humidity_week = cursor.fetchall()
		sql_query_month = "SELECT AVG(CAST(humidity AS REAL)) FROM Humidity WHERE datetime >= datetime('now', '-30 days');"
		cursor.execute(sql_query_month)
		humidity_month = cursor.fetchall()
		query_total = "Humidity:" + str(humidity_latest) + ":"  + str(humidity_day) + ":" + str(humidity_week) + ":" + str(humidity_month)

		if str(humidity_latest) == 'None':
			humidity_latest = '0'
		if str(humidity_day) == 'None':
			humidity_day = '0'
		if str(humidity_week) == 'None':
			humidity_week = '0'
		if str(humidity_month) == 'None':
			humidity_month = '0'

	if(data_type == "co2"):
		sql_query_latest = "SELECT co2 FROM CO2 WHERE datetime = (SELECT MAX(datetime) FROM CO2);"
		cursor.execute(sql_query_latest)
		co2_latest = cursor.fetchall()
		sql_query_day = "SELECT AVG(CAST(co2 AS REAL)) FROM CO2 WHERE datetime >= datetime('now', '-1 days');"
		cursor.execute(sql_query_day)
		co2_day = cursor.fetchall()
		sql_query_week = "SELECT AVG(CAST(co2 AS REAL))FROM CO2 WHERE datetime >= datetime('now', '-6 days');"
		cursor.execute(sql_query_week)
		co2_week = cursor.fetchall()
		sql_query_month = "SELECT AVG(CAST(co2 AS REAL)) FROM CO2 WHERE datetime >= datetime('now', '-30 days');"
		cursor.execute(sql_query_month)
		co2_month = cursor.fetchall()
		query_total = "Co2:" + str(co2_latest) + ":"  + str(co2_day) + ":" + str(co2_week) + ":" + str(co2_month)

		if str(co2_latest) == 'None':
			co2_latest = '0'
		if str(co2_day) == 'None':
			co2_day = '0'
		if str(co2_week) == 'None':
			co2_week = '0'
		if str(co2_month) == 'None':
			co2_month = '0'

	if(data_type == "energy"):
		sql_query_latest = "SELECT energy FROM Energy WHERE datetime = (SELECT MAX(datetime) FROM Energy);"
		cursor.execute(sql_query_latest)
		energy_latest = cursor.fetchall()
		sql_query_day = "SELECT AVG(CAST(energy AS REAL)) FROM Energy WHERE datetime >= datetime('now', '-1 days');"
		cursor.execute(sql_query_day)
		energy_day = cursor.fetchall()
		sql_query_week = "SELECT AVG(CAST(energy AS REAL)) FROM Energy WHERE datetime >= datetime('now', '-6 days');"
		cursor.execute(sql_query_week)
		energy_week = cursor.fetchall()
		sql_query_month = "SELECT AVG(CAST(energy AS REAL)) FROM Energy WHERE datetime >= datetime('now', '-30 days');"
		cursor.execute(sql_query_month)
		energy_month = cursor.fetchall()

		if str(energy_latest) == 'None':
			energy_latest = '0'
		if str(energy_day) == 'None':
			energy_day = '0'
		if str(energy_week) == 'None':
			energy_week = '0'
		if str(energy_month) == 'None':
			energy_month = '0'

		query_total = "Energy:" + str(energy_latest) + ":"  + str(energy_day) + ":" + str(energy_week) + ":" + str(energy_month)
	
	return query_total
