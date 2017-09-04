package solarhouse;

import java.io.*;
import java.net.*;

public class SolarHouse {
    
    public static final int SERVER_PORT = 9090;
    public static final String SERVER_IP = "127.0.0.1";

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(9091);
        //energy c02 humidity
        SerialTest TemperatureConnection = new SerialTest("TemperatureConnection", "COM5");
        SerialTest C02Connection = new SerialTest("C02Connection","COM6");
        SerialTest HumidityConnection = new SerialTest("HumidityConnection","COM7");
	TemperatureConnection.initialize();
        C02Connection.initialize();
        HumidityConnection.initialize();
        
        try {
            while (true) {
                Socket socket = listener.accept();
                try {
                    //sets up connection to client
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    
                    //HTTP request
                    URL enphase = new URL("https://api.enphaseenergy.com/api/v2/systems/726152/summary?key=b01bc857724dea13fc28806b749a9e9a&user_id=4e6a59794d446b300a");
                    URLConnection enphaseConnect = enphase.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(enphaseConnect.getInputStream()));
                    String inputLine;
                    String[] finalString;
                    inputLine = in.readLine(); 
                    finalString = inputLine.split(",");
                    //System.out.println(finalString[3] + finalString[4]);
                    out.println(finalString[4]);
                    in.close();
                    
                    //SendDataToServer()
                } finally {
                    socket.close();
                }
            }
        }
        finally {
            listener.close();
        }
    }
 
    public static void SendDataToServer(String sendString) {       
        try {
            ServerSocket serverSocket = new ServerSocket();
            
            //InetSocketAddress addr = new InetSocketAddress("localhost", 8001);
            //serverSocket.bind(addr);
            InetAddress ip = InetAddress.getByName(SERVER_IP);
            InetSocketAddress addr = new InetSocketAddress(ip, SERVER_PORT);
            serverSocket.bind(addr);
            
            System.out.println("Attempting to communicate with server...");
            
            Socket clientSocket = serverSocket.accept();
            System.out.println("Established Connection... ");
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            //BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //in.readLine()
            //does this wait for the server to respond or not?
            
            System.out.println("Sending data to server...");
            out.println(sendString);
            
            //in.close();
            out.close();
        } catch (Exception e) {
            System.out.println("Failed to send data to the server. Error: " + e.getMessage());
        }
    }
}