import serial
from urllib.request import urlopen


def getEnphase():
    response = urlopen('https://api.enphaseenergy.com/api/v2/systems/1305050/summary?key=b01bc857724dea13fc28806b749a9e9a&user_id=4e6a59794d446b300a')
    message = response.read()
    print(message.decode('utf-8'))
    return message

if __name__ == "__main__":
    #connects serial serial ports
    """
    arduino1 = serial.Serial(port='COM4', baudrate=9600)
    arduino2 = serial.Serial(port='COM5', baudrate=9600)
    

    print("connected to: " + ser.portstr)
    ser.write("help\n");
    while True:
    line = ser.readline();
    if line:
        print(line),
    ser.close()
    """
    getEnphase()