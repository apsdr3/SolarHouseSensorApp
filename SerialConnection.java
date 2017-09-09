import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.concurrent.TimeUnit;
import com.fazecast.jSerialComm.*;

public class SerialConnection {
    
    public static final String SERVER_URL = "http://192.168.2.3:9090/postRequest/";
    
    public static final int ENERGY_DELAY_MINUTES = 5;

    public static void main(String[] args) throws IOException {
        
        SerialPort port1 = SerialPort.getCommPort("COM2");
        SerialPort port2 = SerialPort.getCommPort("COM6");
        port1.openPort();
        port2.openPort();

        System.out.println("attempting to setup serial connections");
        port1.addDataListener(new SerialPortDataListener() {
           @Override
           public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
           @Override
           public void serialEvent(SerialPortEvent event)
           {
              if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                 return;
              byte[] newData = new byte[port1.bytesAvailable()];
              int numRead = port1.readBytes(newData, newData.length);
              System.out.println("Read " + numRead + " bytes.");
           }
        });

        port2.addDataListener(new SerialPortDataListener() {
           @Override
           public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
           @Override
           public void serialEvent(SerialPortEvent event)
           {
              if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                 return;
              byte[] newData = new byte[port2.bytesAvailable()];
              int numRead = port2.readBytes(newData, newData.length);
              System.out.println("Read " + numRead + " bytes.");
           }
        });



        try {
            while (true) {
                try {
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
                } catch (Exception e) {
                    
                }
            }
        }
        finally {
        }
    }
 
    public static void SendDataToServer(String sendString) {    
        System.out.println("attempting to send \"" + sendString + "\"");

        try {
            
            byte[] data = sendString.getBytes( StandardCharsets.UTF_8 );
            int length = data.length;
            URL url = new URL(SERVER_URL);
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