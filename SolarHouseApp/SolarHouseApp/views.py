from django.http import HttpResponse
import time
import socket

def index(request):
	return HttpResponse("BASE WEBSITE")

def getRequest(request):
	return HttpResponse("YOU GOT INTO GET REQEUST")

def postRequest(request):
	HttpResponse("YOU GOT INTO POST REQUEST")
	temperature = 0
	humidity = 0
	co2 = 0
	energy = 0

	time.sleep(2)
	
	temperature = request.POST.get('temperature')
	if temperature != 0:
		temperatureFunction(temperature)
		return HttpResponse('Temperature has been stored')

	humidity = request.POST.get('humidity')
	if humidity != 0:
		temperatureFunction(humidity)
		return HttpResponse('Humidity has been stored')	
	
	co2 = request.POST.get('co2')
	if co2 != 0:
		temperatureFunction(co2)
		return HttpResponse('CO2 has been stored')

	energy = request.POST.get('energy')
	if energy != 0:
		temperatureFunction(energy)
		return HttpResponse('Energy has been stored')

	return HttpResponse('No data has been stored')