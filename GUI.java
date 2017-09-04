package SHA;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
		JFrame jf = new JFrame("S&T Solar House App");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		new GUI(jf);
		jf.setVisible(true);
		jf.setResizable(false);
		jf.pack();
	}
	
	public GUI(JFrame jf) {
		initDisplay(jf);
	}
	
	private void initDisplay(JFrame jf) {
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
        
        //populateTestQueue();
		waitForData();
	}
	
	private void waitForData() {
		double[] newData = new double[kNumLabels];
		HashMap<String, Integer> TypeToInt = new HashMap<String, Integer>();
		TypeToInt.put("temperature",0);
		TypeToInt.put("humidity",1);
		TypeToInt.put("co2",2);
		TypeToInt.put("energy",3);
		HashMap<Integer, String> IntToType = new HashMap<Integer, String>();
		IntToType.put(0,"Temperature");
		IntToType.put(1,"Humidity");
		IntToType.put(2,"CO2");
		IntToType.put(3,"Energy");
		
		Timer dataTimer = new Timer(200, new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				for (int type = 0; type < kNumLabels; type++) {
					URL url = null;
					try {
						url = new URL("http://192.168.2.9:9090/getRequest" + IntToType.get(type) + "/");
					} catch (MalformedURLException murle) {
						murle.printStackTrace();
					}
					URLConnection conn = null;
				    try {
						conn = url.openConnection();
						System.out.println("Connection Successful");
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					StringBuilder result = new StringBuilder();
					BufferedReader rd = null;
					try {
						rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				    String line;
				    try {
						while ((line = rd.readLine()) != null) {
						   result.append(line);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				    try {
						rd.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				    String answer = result.toString();
				    System.out.println(answer);
					
					//String s2 = new String(input, "UTF-8");
					
					String[] data = answer.split(":");
					int i = TypeToInt.get(data[0]);
					
					for(int k = 0; k < 5; k++) {
						System.out.println(data[k]);
					}
			        
					//read new data
					newData[i] = Double.parseDouble(data[1]);
					//read average data
					for(int j = 0; j < 3; j++) {
						int intData = (int) ((Double.parseDouble(data[j+2])*10)+.5);
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
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
			}
		});
		dataTimer.setRepeats(true);
		dataTimer.setCoalesce(true);
		dataTimer.start();
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

//@SuppressWarnings("serial")
class DrawGraph extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int PREF_W = 375, PREF_H = 172;
	private static final int BORDER_GAP = 25;
	private static final Color GRAPH_COLOR = Color.blue;
	private static final Stroke GRAPH_STROKE = new BasicStroke(1.5f);
	private static final int GRAPH_POINT_WIDTH = 6;
	private static final int Y_HATCH_CNT = 9;
//	private Vector<Double> scores;
//	static Point p;
	//public static JPanel mainPanel;
	private JFrame frame;
	private int dataType;
	private GUI gui;
	private int numPts;
	private String name;

	public DrawGraph(GUI uiout, Point p, int dt, int pointsToPlot, String title) {
		dataType = dt;
		gui = uiout;
		numPts = pointsToPlot;
		// Start the Graph
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocation(p);
		frame.setVisible(true);
	}
	
	public DrawGraph(JPanel panel, GUI uiout, int dt, int pointsToPlot, String title) {
		dataType = dt;
		gui = uiout;
		numPts = pointsToPlot;
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setBackground(Color.darkGray.brighter().brighter().brighter());
		panel.add(this);
		name = title;
	}
	
	public static String[] AXES_TEMPERATURE = {"74", "71", "68"};
	public static String[] AXES_HUMIDITY = {"60", "47.5", "35"};
	public static String[] AXES_CO2 = {"1000", "500", "0"};
	public static String[] AXES_ENERGY = {"1000", "500", "0"};
	public static double UNIT_TEMPERATURE = 15;
	public static double UNIT_HUMIDITY = 3.6;
	public static double UNIT_CO2 = .091;
	public static double UNIT_ENERGY = .091;
	
	public void startTimer() {
		Timer timer = new Timer(500, new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		timer.setRepeats(true);
		timer.setCoalesce(true);
		timer.start();
	}

	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		setLayout(null);

		List<Point> graphPoints = new ArrayList<Point>();
		double offset=0, unit=1;
		int bottom = 0;
		String[] axes = AXES_TEMPERATURE;
		if(dataType == 0) {
			unit = UNIT_TEMPERATURE;
			axes = AXES_TEMPERATURE;
			offset = 1080;
		} else if(dataType == 1) {
			unit = UNIT_HUMIDITY;
			axes = AXES_HUMIDITY;
			offset = 185;
		} else if (dataType == 2){
			unit = UNIT_CO2;
			axes = AXES_CO2;
			offset = 59;
			bottom = 45;
		} else if (dataType == 3){
			unit = UNIT_ENERGY;
			axes = AXES_ENERGY;
			offset = 59;
			bottom = 45;
		}

		for(int i=numPts-1;i>=0;i--){
			int y1 = (int) (((double) getHeight())-gui.sendArray(i, dataType)*unit-100+offset);
			int x1 = i*15 + BORDER_GAP;
			graphPoints.add(new Point(x1, y1));
		}
		// create x and y axes 
		g2.drawLine(BORDER_GAP+25, getHeight()/2 + 60, BORDER_GAP+25, getHeight()/2 - 60);
		g2.drawLine(BORDER_GAP+25, getHeight()/2 + bottom, 
				getWidth()/2 +148, getHeight()/2 + bottom);

		g2.drawString(axes[0],8,getHeight()/2-40);
		g2.drawString(axes[1],8,getHeight()/2+4);
		g2.drawString(axes[2],8,getHeight()/2+48);
		g2.drawString(name, getWidth()/2-180, 20);
		g2.drawString("Time (hrs)", getWidth()/2-25, getHeight()-10);

		// create hatch marks for y axis. 
		for (int i = 0; i < Y_HATCH_CNT; i++) {
			int x0 = BORDER_GAP+18;
			int x1 = x0+14;
			int y0 = 15*i + getHeight()/2 - 60;
			int y1 = y0;
			g2.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		for (int i = 1; i < numPts; i++) {
			int x0 = 15*i + getWidth() - getWidth() + 50;
			int x1 = x0;
			int y0 = getHeight()/2+ bottom+7;
			int y1 = y0 - 14;
			g2.drawLine(x0, y0, x1, y1);
		}

		Stroke oldStroke = g2.getStroke();
		g2.setColor(GRAPH_COLOR);
		g2.setStroke(GRAPH_STROKE);
		for (int i = 0; i < graphPoints.size() - 1; i++) {
			int x1 = graphPoints.get(i).x+25;
			int y1 = graphPoints.get(i).y;
			int x2 = graphPoints.get(i + 1).x+25;
			int y2 = graphPoints.get(i + 1).y;
			g2.drawLine(x1, y1, x2, y2);         
		}

		g2.setStroke(oldStroke);      

		for (int i = 0; i < graphPoints.size(); i++) {
			int x = i*15 + BORDER_GAP - (GRAPH_POINT_WIDTH/2) +(1/2)+25;
			int y = (int) (((double)getHeight())-gui.sendArray(i, dataType)*unit
					-100+offset-((double)GRAPH_POINT_WIDTH)/2);
			int ovalW = GRAPH_POINT_WIDTH;
			int ovalH = GRAPH_POINT_WIDTH;
			g2.setColor(gui.sendTLCcolor(gui.sendArray(i, dataType), dataType));
			g2.fillOval(x, y, ovalW, ovalH);
		}
		startTimer();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREF_W, PREF_H);
	}
	public void actionPerformed(ActionEvent arg0) {
		repaint();		
	}
}
