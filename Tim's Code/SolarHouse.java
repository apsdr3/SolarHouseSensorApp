package solarhouse;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.concurrent.TimeUnit;

public class SolarHouse {
    
    public static final String SERVER_URL = "http://192.168.2.3:9090/postRequest/";
    public static final String SERVER_URL_POST = "http://192.168.2.3:9090/postRequest/";
    
    public static final int ENERGY_DELAY_MINUTES = 5;

    public static void main(String[] args) throws IOException {
        
        SendDataToServer("temperature=-9000");
        
        //SerialConnection TemperatureConnection = new SerialConnection("TemperatureConnection", "COM5");
        //SerialConnection C02Connection = new SerialConnection("C02Connection","COM6");
        //SerialConnection HumidityConnection = new SerialConnection("HumidityConnection","COM7");
	//TemperatureConnection.initialize();
        //C02Connection.initialize();
        //HumidityConnection.initialize();
        
        try {
            while (true) {
                //Socket socket = listener.accept();
                try {
                    //sets up connection to client
                    //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    //HTTP request
                    URL enphase = new URL("https://api.enphaseenergy.com/api/v2/systems/1305050/summary?key=b01bc857724dea13fc28806b749a9e9a&user_id=4e6a59794d446b300a");
                    URLConnection enphaseConnect = enphase.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(enphaseConnect.getInputStream()));
                    String inputLine;
                    String[] finalString;
                    inputLine = in.readLine(); 
                    finalString = inputLine.split(",");
                    String[] splitString = finalString[4].split(":");
                    String energyString = "energy=";
                    energyString += splitString[1];
                    
                    SendDataToServer(energyString);
                    TimeUnit.MINUTES.sleep(ENERGY_DELAY_MINUTES);
                    //SendDataToServer()
                } catch (Exception e) {
                    
                }
            }
        }
        finally {
            //listener.close();
        }
    }
 
    public static void SendDataToServer(String sendString) {       
        try {
            
            byte[] data = sendString.getBytes( StandardCharsets.UTF_8 );
            int length = data.length;
            URL url = new URL(SERVER_URL_POST);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( length ));
            conn.setUseCaches(false);
            DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
            wr.write(data);
            System.out.println(conn.getResponseCode());
            
            url = new URL(SERVER_URL);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                    System.out.println(strTemp);
            }
            
            conn.disconnect();
          
        } catch (Exception e) {
            System.out.println("Failed to send data to the server. Error: " + e.getMessage());
        }
    }
}