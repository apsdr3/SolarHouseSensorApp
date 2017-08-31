package SolarHouseApp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

//@SuppressWarnings("serial")
public class DrawGraph extends JPanel implements ActionListener {
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

//	private static void createAndShowGui(int dataType) {
//		List<Integer> scores = new ArrayList<Integer>();
//		int maxDataPoints = 16;
//		for (int i = 0; i < maxDataPoints ; i++) {
//			scores.add(i);
//		}
//		mainPanel = new DrawGraph(scores);
//
//	}


//	public static void main(String[] args) {
//		int sID = 0;
//		if (args != null) {
//			sID = Integer.valueOf(args[0]);
//		}
//		SwingUtilities.invokeLater(new GuiRun(sID));
//	}

//	private class GuiRun implements Runnable {
//		private int dataType;
//		public GuiRun(int sID) {
//			dataType = sID;
//		}
//		public void run() {
//			createAndShowGui(dataType);
//		}
//	}

//	public void redraw(){
//		repaint();
//	}


	/** Only listens for actions that would indicate a redraw is needed.
	 * 
	 * @param arg0
	 */
	public void actionPerformed(ActionEvent arg0) {
		repaint();		
	}

//	public static Point close(){
//		if(mainPanel!=null){
//			p = frame.getLocationOnScreen();
//			frame.dispose();
//		}
//		return p;
//	}

//	static void newGraph(Point in, int dataType){
//		if(mainPanel==null){
//			p = new Point(500,125);
//		}else{
//			p = in;
//		}
//		String[] args = {new Integer(dataType).toString()};
//		main(args);
//	}
}