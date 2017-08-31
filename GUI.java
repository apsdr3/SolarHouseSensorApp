package SolarHouseApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GUI {
	private final static int kNumLabels = 4;
	
	private final static int kBigFontSize = 40;
	private final static int kSmallFontSize = 25;
	
	public static final int kMaxDatapoints = 20;
	
	private static final int kDataReceiveIntervalMinutes = 5;
	private static final int kNumDataUntilGraph = 20;
	
	ArrayList<Queue<Double>> storedData = new ArrayList<>();
	JLabel[] dataLabels = new JLabel[kNumLabels];
	JLabel[] averageLabels = new JLabel[kNumLabels];
	double[][] averageData = new double[kNumLabels][3];
	
	private final double[][] thresholds = { { 68, 69.5, 72.5, 74 }, { 35, 42.5, 52.5, 60 }, { -1, -1, 750, 1000 }, { 100, 250, 10000, 10000 } };
	
	private final String[] names = { "Temperature (F)", "Humidity (%)", "Carbon Dioxide (ppm)", "Energy (kWh)" };
	
	private void populateTestQueue() {
		Random rnd = new Random();
		double[] newData = { 71, 47.5, 500, 500 };
		Timer testDataTimer = new Timer(100, new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				newData[0] += ((double) rnd.nextInt(3))/4 - .25;
				newData[1] += ((double) rnd.nextInt(3)) - 1;
				newData[2] += rnd.nextInt(81) - 40;
				if(newData[2] < 0) newData[2] = 0;
				newData[3] += rnd.nextInt(81) - 40;
				if(newData[3] < 0) newData[3] = 0;
				for (int i = 0; i < kNumLabels; i++) {
					while (storedData.get(i).size() > 20) {
						storedData.get(i).remove();
					}
					int intData = (int) ((newData[i]*10)+.5);
					double setData = ((double) intData)/10;
		    		    dataLabels[i].setText("" + setData);
		    		    dataLabels[i].setForeground(sendTLCcolor(Double.parseDouble(dataLabels[i].getText()),i));
					averageLabels[i].setText(" Day: " + rnd.nextInt(10) + ", Week: " + rnd.nextInt(10) + ", Month: " + rnd.nextInt(10));
				}
				storedData.get(0).add(newData[0]);
				storedData.get(1).add(newData[1]);
				storedData.get(2).add(newData[2]);
				storedData.get(3).add(newData[3]);
			}
		});
		testDataTimer.setRepeats(true);
		testDataTimer.setCoalesce(true);
		testDataTimer.start();
	}
	
	public static void main(String[] args) {
        Socket s = null;
        boolean connected = true;
		try {
			s = new Socket("192.168.2.35", 9090);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			connected = false;
		} catch (IOException e) {
			e.printStackTrace();
			connected = false;
		}
		
		System.out.println(connected);
		
		JFrame jf = new JFrame("S&T Solar House App");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		new GUI(jf, connected, s);
		jf.setVisible(true);
		jf.setResizable(false);
		jf.pack();
	}
	
	public GUI(JFrame jf, boolean c, Socket s) {
		initDisplay(jf, c);
		//waitForData(s);
	}
	
	private void initDisplay(JFrame jf, boolean connected) {
		if(!connected) populateTestQueue();
		
		storedData.add(new LinkedList<>());
		storedData.add(new LinkedList<>());
		storedData.add(new LinkedList<>());
		storedData.add(new LinkedList<>());
		
		JLabel[] nameLabels = {
				new JLabel("Temperature:"),
				new JLabel("Humidity:"),
				new JLabel("Carbon Dioxide:"),
				new JLabel("Energy:") };
		JLabel[] unitLabels = {
				new JLabel("°F"),
				new JLabel("%"),
				new JLabel("ppm"),
				new JLabel("kWh") };
		
		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    pane.setBackground(Color.darkGray.brighter().brighter());

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 5;
		c.ipady = 10;

        for(int i = 0; i < kNumLabels; i++) {
        		int top = 10, bottom = 60;
        		if(i == 0)
        			top = 45;
        		else if(i == 3)
        			bottom = 45;
        		
        		c.insets = new Insets(top,150,0,10);
    		    c.gridx = 0;
    		    c.gridy = 2*i;
    		    c.gridwidth = 2;
    		    nameLabels[i].setFont(new Font("Courier New", Font.PLAIN,kBigFontSize));
    		    pane.add(nameLabels[i], c);
    		    
    		    c.insets = new Insets(top,0,0,5);
    		    //c.ipadx = 0;
    		    c.gridwidth = 1;
    		    c.gridx = 2;
    		    dataLabels[i] = new JLabel();
    		    dataLabels[i].setFont(new Font("Courier New", Font.BOLD,kBigFontSize));
    		    if (storedData.get(i).size() > 0) {
	    		    dataLabels[i].setText("" + storedData.get(i).toArray()[0]);
	    		    dataLabels[i].setForeground(sendTLCcolor(Double.parseDouble(dataLabels[i].getText()),i));
    		    } else {
    		    		dataLabels[i].setText("null");
    		    }
    		    pane.add(dataLabels[i], c);
    		    
    		    c.insets = new Insets(top,0,0,200);
    		    c.gridx = 3;
    		    unitLabels[i].setFont(new Font("Courier New", Font.PLAIN,kBigFontSize));
    		    pane.add(unitLabels[i], c);
    		    
    		    c.insets = new Insets(0,160,bottom,0);
    		    c.gridx = 0;
    		    c.gridy = 2*i+1;
    		    JLabel average = new JLabel("Average:");
    		    average.setFont(new Font("Courier New", Font.BOLD,kSmallFontSize));
    		    pane.add(average, c);
    		    
    		    c.insets = new Insets(0,0,bottom,0);
    		    c.gridx = 1;
    		    c.gridwidth = 3;
    		    averageLabels[i] = new JLabel(" Day: null, Week: null, Month: null");
    		    averageLabels[i].setFont(new Font("Courier New", Font.PLAIN,kSmallFontSize));
    		    pane.add(averageLabels[i], c);
        }
        jf.add(pane, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 0));
        for(int i = 0; i < 4; i++) {
        		new DrawGraph(panel, this, i, kMaxDatapoints, names[i]);
        }
        jf.add(panel, BorderLayout.EAST);
	}
	
	@SuppressWarnings("null")
	private void waitForData(Socket s) {
		double[] newData = new double[kNumLabels];
		Map<String, Integer> TypeToInt = null;
		TypeToInt.put("temperature",0);
		TypeToInt.put("humidity",1);
		TypeToInt.put("co2",2);
		TypeToInt.put("energy",3);
		
		while(true) {

	        BufferedReader input = null;
			try {
				input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
	        String answer = null;
			try {
				answer = input..readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//String s2 = new String(input, "UTF-8");
			
			String[] data = answer.split(":");
			int i = TypeToInt.get(data[0]);
	        
			//read new data
			newData[i] = Double.parseDouble(data[1]);
			//read average data
			for(int j = 2; j < 5; j++) {
				int intData = (int) ((Double.parseDouble(data[j])*10)+.5);
				double setData = ((double) intData)/10;
				averageData[i][j] = setData;
			}
			
			//clear extra members of storedData
			while (storedData.get(i).size() > 20) {
				storedData.get(i).remove();
			}
			//round to 1 sig fig
			int intData = (int) ((newData[i]*10)+.5);
			double setData = ((double) intData)/10;
    		    dataLabels[i].setText("" + setData);
    		    dataLabels[i].setForeground(sendTLCcolor(Double.parseDouble(dataLabels[i].getText()),i));
			averageLabels[i].setText(" Day: " + averageData[i][0] + ", Week: " + averageData[i][1] + ", Month: " + averageData[i][2]);
			storedData.get(i).add(newData[i]);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public double sendArray(int i, int dataType){
		if(storedData.get(dataType).size() > i)
			return (double) storedData.get(dataType).toArray()[i];
		if(dataType == 0)
			return 71;
		if(dataType == 1)
			return 47.5;
		return 0;
	}
	
	public Color sendTLCcolor(double testValue, int dataType){
		final int THRESHOLD_RED_HIGH = 3;
		final int THRESHOLD_YELLOW_HIGH = 2;
		final int THRESHOLD_YELLOW_LOW = 1;
		final int THRESHOLD_RED_LOW = 0;
		// Start low and work your way up
		if (testValue <  thresholds[dataType][THRESHOLD_RED_LOW]) return Color.red;
		if (testValue <= thresholds[dataType][THRESHOLD_YELLOW_LOW]) return Color.yellow;
		if (testValue <= thresholds[dataType][THRESHOLD_YELLOW_HIGH]) return Color.green;
		if (testValue <= thresholds[dataType][THRESHOLD_RED_HIGH]) return Color.yellow;
		return Color.red;
	}
}