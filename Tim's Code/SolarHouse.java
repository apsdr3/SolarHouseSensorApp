package solarhouse;

import java.io.*;
import java.net.*;

public class SolarHouse {
    
    public static final String SERVER_URL = "http://192.168.2.9:9090/postRequest/";

    public static void main(String[] args) throws IOException {
        //ServerSocket listener = new ServerSocket(9091);
        
        //SerialConnection TemperatureConnection = new SerialConnection("TemperatureConnection", "COM5");
        //SerialConnection C02Connection = new SerialConnection("C02Connection","COM6");
        //SerialConnection HumidityConnection = new SerialConnection("HumidityConnection","COM7");
	//TemperatureConnection.initialize();
        //C02Connection.initialize();
        //HumidityConnection.initialize();
        
        SendDataToServer("temperature=", 50);
        /*
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
        }*/
    }
 
    public static void SendDataToServer(String sendString, int value) {       
        try {
            String type = "application/x-www-form-urlencoded";
            String valueString = Integer.toString(value);
            String encodedData = URLEncoder.encode( valueString, "UTF-8" );
            String stringToSend = sendString + encodedData;
            URL u = new URL(SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty( "Content-Type", type );
            conn.setRequestProperty( "Content-Length", String.valueOf(stringToSend.length()));
            OutputStream os = conn.getOutputStream();
            os.write(stringToSend.getBytes());
            System.out.println(os);
            System.out.println(conn.getResponseCode());
            System.out.println(conn.getResponseMessage());
            System.out.println(conn.getContentType());
            
            BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream()));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                    System.out.println(strTemp);
            }
            
            System.out.println("Sent: " + stringToSend);
        } catch (Exception e) {
            System.out.println("Failed to send data to the server. Error: " + e.getMessage());
        }
    }
}