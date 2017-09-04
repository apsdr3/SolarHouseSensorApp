from django.http import HttpResponse
import time
import socket

def index(request):
	return HttpResponse("BASE WEBSITE")

def getRequest(request):
	# DB query
	# Set up data for the get request
	# Send data
	return HttpResponse("temperature:10:0:0:0")

def postRequest(request):
	
	##time.sleep(2)
	#return HttpResponse("YOU GOT INTO POST REQUEST")

	#HttpResponse("YOU GOT INTO POST REQUEST")
	
	temperature = 0
	#humidity = 0
	#co2 = 0
	#energy = 0

	time.sleep(2)
	
	temperature = request.POST.get('temperature')
	if temperature != 0:
		#temperatureFunction(temperature)
		return HttpResponse('Temperature has been stored')
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
	return HttpResponse('No data has been stored')


#----------------------------------------------------------------------




def createDatabases():
	# I think this is necessary
	connection = sqlite3.connect("SILO.db") # this can be name something else


	CREATE TABLE Temperature ( 
		datetime DATETIME, 
		temperature INT NOT NULL);

	CREATE TABLE Humidity (
		datetime DATETIME,
		humidity INT NOT NULL);

	CREATE TABLE C02 (
		datetime DATETIME,
		C02 INT NOT NULL);

	CREATE TABLE Extra (
		datetime DATETIME,
		extra INT NOT NULL);



def storeMessage():
	connection = sqlite3.connect("SILO.db")
	cursor = connection.cursor()
	a = datetime.datetime.strptime('my date', "%b %d %Y %H:%M")


	# code to get the data that is being sent
	data = conn.recv(9090)
	# # # # # # 


	if (string[0:1].lower() == "t"):
		sql_command = 'INSERT INTO Temperature (datetime, temperature) VALUES (%s, givenTemperature)', (a.strftime('%Y-%m-%d %H:%M:%S'));
		cursor.execute(sql_command)

	if (string[0:1].lower() == "h"):
		sql_command = 'INSERT INTO Humidity (datetime, humidity) VALUES (%s, givenHumidity)', (a.strftime('%Y-%m-%d %H:%M:%S'));
		cursor.execute(sql_command)

	if (string[0:1].lower() == "c"):
		sql_command = 'INSERT INTO C02 (datetime, c02) VALUES (%s, givenC02)', (a.strftime('%Y-%m-%d %H:%M:%S'));
		cursor.execute(sql_command)

	if (string[0:1].lower() == "e"):
		sql_command = 'INSERT INTO Extra (datetime, extra) VALUES (%s, givenExtra)', (a.strftime('%Y-%m-%d %H:%M:%S'));
		cursor.execute(sql_command)

	# never forget this, if you want the changes to be saved:
	connection.commit()


	conn.send(data)
	conn.close()




def queryDatabase(data_request):
	connection = sqlite3.connect("SILO.db")
	cursor = connection.cursor()

	# get request
	data = conn.recv(9090)
	


	# The "-6 days" parameter may not need the space

	# Get the data requested from the past day, week, and month
	sql_query_day = "SELECT AVG(data_request) FROM  data_request WHERE date BETWEEN datetime('now', 'start of day') AND datetime('now', 'localtime');"
	sql_query_week = "SELECT AVG(data_request) FROM  data_request WHERE date BETWEEN datetime('now', '-6 days') AND datetime('now', 'localtime');"
	sql_query_month = "SELECT AVG(data_request) FROM  data_request WHERE date BETWEEN datetime('now', 'start of month') AND datetime('now', 'localtime');"
	query_total_data_request = data_request.title() + cursor.execute(sql_query_day) + cursor.execute(sql_query_week) + cursor.execute(sql_query_month)


	conn.send(query_total_data_request)

	conn.close()