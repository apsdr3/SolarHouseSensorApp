from django.http import HttpResponse
import time
import socket

def index(request):
	return HttpResponse("BASE WEBSITE")

def getRequest(request):
	return HttpResponse("YOU GOT INTO TEST")

def postRequest(request):
	return HttpResponse("YOU GOT INTO TEST_POST")

#scripts to run before running server:
# python manage.py migrate
# python manage.py runserver <IPv4 Address>:9090




"""
def index(request):
	HttpResponse("Hello, world. You're at the polls index.")
	time.sleep(1)

	s = socket.socket()
	s.bind(("192.168.2.35", 9090))

	s.listen(2)
	c, addr = s.accept()     # Establish connection with client.
	
	time.sleep(1)
	HttpResponse("GOT CONNECTION")
	
	#print('Got connection from', addr)
	message = '99'
	c.send(message.encode(encoding='utf-8'))
	c.close()                # Close the connection

	time.sleep(1)
	return HttpResponse("DONE")
"""

"""
import socket               # Import socket module

s = socket.socket()         # Create a socket object
host = '127.0.0.1'          # Get local machine name
port = 9090                # Reserve a port for your service.
s.bind((host, port))        # Bind to the port

s.listen(5)                 # Now wait for client connection.
while True:
	c, addr = s.accept()     # Establish connection with client.
	print('Got connection from', addr)
	message = '99'
	c.send(message.encode(encoding='utf-8'))
	c.close()                # Close the connection
	exit()                 #one connection to test
"""