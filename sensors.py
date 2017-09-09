import serial
from urllib.request import urlopen

#enphase query
def getEnphase():
    response = urlopen('https://api.enphaseenergy.com/api/v2/systems/1305050/summary?key=b01bc857724dea13fc28806b749a9e9a&user_id=4e6a59794d446b300a')
    message = response.read()
    message = message.decode('utf-8')
    return message

if __name__ == "__main__":
    
    try:
        while True:
            #connects serial serial ports
            try:
                #arduino1 = serial.Serial(port='COM1', baudrate=9600, timeout=1)
                arduino2 = serial.Serial(port='COM4', baudrate=9600, timeout=1)
            except ValueError:
                #arduino1.close()
                arduino2.close()
            #arduino1.write('command for reading out device 1')
            #output1 = arduino1.readline()

            #arduino2.write('command for reading out device 2')
            # now you have to wait at least 100ms for device 2 to respond
            output2 = arduino2.readline()

            #print(output1.decode('utf-8'))
            print(output2.decode('utf-8'))
    
    except KeyboardInterrupt:
            #arduino1.close()
            arduino2.close()