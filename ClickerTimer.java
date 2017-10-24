
package yoyo;

/**
 * @author colinbeckford
 * Clicker Timer program
 * Allows user to time yo-yo freestyles while scoring them using keyboard presses
 * Before starting the scoring, in order for the final result to have a correct title, the user must enter data into the text boxes and hit the Submit button
 * initialArray values are continuously updated throughout, such as percentage of +2 difficult clicks, positive clicker count, negative clicker count
 * After freestyle is complete, the clicks per second value is revealed
 * Final result is an exported CSV file featuring strings that state a combination of the amount of clicks and the time the clicks took place, i.e. "1 click at 4.0 seconds"
 */
//importing necessary files

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.util.Collections;




//class declaration, using JFrame WindowBuilder and the KeyListener operation keyPressed()
public class ClickerTimer extends JFrame implements KeyListener {
	//data to be put on the JFrame
	JTextField input;
	public JLabel lblRaw = new JLabel("0");
	public JLabel lblMinus = new JLabel("-0");
	public JLabel lblPlus = new JLabel("+0");
	public JLabel lblPercentDouble = new JLabel("Percent double clicks: 0");
	public JLabel lblCPS = new JLabel("Clicks per second: ");
	public static JLabel lblTimeCount = new JLabel("Time passed: 0 seconds");
	public JLabel lblBestSegment = new JLabel("");
	public JButton btnSubmit = new JButton("Submit");
	private JTextField txtJudgeName;
	private JTextField txtPlayerName;
	private JTextField txtCompetitionRound;
	
	//variable data that is updated throughout program being run
	int clicker = 0;
	int plus = 0;
	int minus = 0;
	int two = 0;
	static int keypress = 0;
	static double centisecondsPassed = 0.0;
	static double centisecondsPassedvideo = 0.0;
	static DecimalFormat numberFormat = new DecimalFormat("#.00");
	ArrayList<String> clickTimes = new ArrayList<String>();
	String judgeName;
	String YTlink;
	String playerName;
	String CompetitionRound;
	//initial array for data of click value and time frame. size of 200 is assumed as over 200 keypresses is unlikely
	static double[][] initialArray = new double[200][2];
	//test to work on get best segment
	static int segmentclicker = 0;
	static ArrayList<String> segments = new ArrayList();
	static ArrayList<Integer> intsegments = new ArrayList();
	
	
	private static String secondsToString(int seconds) 
	{
	    return String.format("%02d:%02d", seconds / 60, seconds % 60);
	}
	
	
	
	//constructor for all of the labels, buttons, and textfields on the JFrame
	public ClickerTimer() {
		setVisible(true);
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		input = new JTextField();
		input.setBounds(6, 6, 388, 26);
		getContentPane().add(input);
		input.addKeyListener(this);
		input.setColumns(10);
		
		
		lblPlus.setFont(new Font("Lucida Bright", Font.PLAIN, 35));
		lblPlus.setBounds(6, 42, 106, 31);
		getContentPane().add(lblPlus);
		
		
		lblMinus.setFont(new Font("Lucida Bright", Font.PLAIN, 35));
		lblMinus.setBounds(6, 90, 106, 31);
		getContentPane().add(lblMinus);
	
		lblRaw.setFont(new Font("Lucida Grande", Font.PLAIN, 50));
		lblRaw.setHorizontalAlignment(SwingConstants.CENTER);
		lblRaw.setBounds(146, 49, 106, 100);
		getContentPane().add(lblRaw);
		
	
		lblPercentDouble.setBounds(6, 219, 217, 16);
		getContentPane().add(lblPercentDouble);
		
		lblTimeCount.setBounds(6, 247, 191, 16);
		getContentPane().add(lblTimeCount);
		
		lblCPS.setBounds(6, 275, 172, 16);
		getContentPane().add(lblCPS);
		
		lblBestSegment.setBounds(6, 302, 303, 16);
		getContentPane().add(lblBestSegment);
		lblBestSegment.setVisible(true);
		
		txtJudgeName = new JTextField();
		txtJudgeName.setHorizontalAlignment(SwingConstants.CENTER);
		txtJudgeName.setText("Judge Name");
		txtJudgeName.setBounds(6, 154, 130, 26);
		txtJudgeName.setVisible(true);
		getContentPane().add(txtJudgeName);
		txtJudgeName.setColumns(10);
		
		txtPlayerName = new JTextField();
		txtPlayerName.setHorizontalAlignment(SwingConstants.CENTER);
		txtPlayerName.setText("Player Name");
		txtPlayerName.setBounds(139, 154, 130, 26);
		txtPlayerName.setVisible(true);
		getContentPane().add(txtPlayerName);
		txtPlayerName.setColumns(10);
		
		txtCompetitionRound = new JTextField();
		txtCompetitionRound.setHorizontalAlignment(SwingConstants.CENTER);
		txtCompetitionRound.setText("Contest & Round");
		txtCompetitionRound.setBounds(270, 154, 130, 26);
		txtCompetitionRound.setVisible(true);
		getContentPane().add(txtCompetitionRound);
		txtCompetitionRound.setColumns(10);
		

		
		
		//action listener for the submit button which saves the text inputs into string variables (listed above)
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				judgeName = txtJudgeName.getText();
				playerName = txtPlayerName.getText();
				CompetitionRound = txtCompetitionRound.getText();
			}
		});
		btnSubmit.setBounds(148, 178, 117, 29);
		btnSubmit.setVisible(true);
		getContentPane().add(btnSubmit);
		
		JButton btnStartVideo = new JButton("Start Video");
		btnStartVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				ClickerTimer.startvideo();
			}
		});
		btnStartVideo.setBounds(270, 44, 117, 29);
		getContentPane().add(btnStartVideo);
		
		
		
		
		lblCPS.setVisible(false);
	}
	//creates an instance of the Timer class to have a consistent time during the scoring
	static Timer timer = new Timer();
	//timertask to be ran once the timer is activated
	static TimerTask task = new TimerTask() {
		public void run () 
		{
			//incrementing of a counter of seconds that is displayed on a label
			centisecondsPassed++;
			lblTimeCount.setText("Time passed: " + centisecondsPassed/100 + " seconds");
		}
	};
	//start command for timer
	public static void start () 
	{
		timer.scheduleAtFixedRate(task, 1000, 10);
		
	}
	
	//creates an instance of the Timer class to have a consistent time during the scoring
		static Timer timervideo = new Timer();
		//timertask to be ran once the timer is activated
		static TimerTask taskvideo = new TimerTask() {
			public void run () 
			{
				//incrementing of a counter of seconds that is displayed on a label
				centisecondsPassedvideo++;
				if (centisecondsPassedvideo/100%10 == 0)
				{
					String startTime = secondsToString((int)((centisecondsPassedvideo/100)-10));
					String endTime = secondsToString((int)((centisecondsPassedvideo/100)));
					segments.add(segmentclicker + " clicks between " + startTime + " and " + endTime + ".");
					intsegments.add(segmentclicker);
					segmentclicker = 0;
				}
				
			}
		};
		//start command for timer
		public static void startvideo () 
		{
			timervideo.scheduleAtFixedRate(taskvideo, 1000, 10);
			
		}
	
	public static double[][] filter()
	{
	double[][] finalArray = new double[keypress][2];
	 for (int i = 0; i < keypress; i++)
	 {
		
        finalArray[i][0] = initialArray[i][0]; 
        	finalArray[i][1] = initialArray[i][1];
        	
	 }
	 return finalArray;
	}
	
	public void getbestsegment()
	{
		int max = Integer.MIN_VALUE;
		int maxindex = Integer.MIN_VALUE;
	    for (int i=0; i<intsegments.size(); i++)
	    {
	        if(intsegments.get(i) > max)
	        {
	            max = intsegments.get(i);
	            maxindex = i;
	        }
	    }
	   

	    lblBestSegment.setText((segments.get(maxindex)));
	}
	
//where anAction is a javax.swing.Action
	//unused
	public void keyTyped(KeyEvent e) {
	}
	//unused
	public void keyReleased(KeyEvent e) {
	}
	//method to account for key presses (for scoring, starting, and stopping purposes)
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		//enter key being pressed will start the timer
		if (keyCode == KeyEvent.VK_ENTER)
		{
			ClickerTimer.start();
		}
		//the number 1 being pressed will add one click to the overall clicker and the strictly + click counter
		//it will also add a string to the arrayList that states "1 click at " + whatever second it is clicked
		if (keyCode == KeyEvent.VK_1)
		{
			getContentPane().setBackground(Color.white);
			keypress++;
			clicker++;
			lblRaw.setText("" + clicker);
			plus++;
			segmentclicker++;
			lblPlus.setText("+" + plus);
			//if statement for grammar purposes in arrayList
			if (centisecondsPassed > 1)
			{
				clickTimes.add(1 + " click at " + centisecondsPassedvideo/100 + " seconds");
			}
			else
			{
				clickTimes.add(1 + " click at " + centisecondsPassedvideo/100 + " second");
			}
			
			//explained below
			lblPercentDouble.setText("" + (two*100/plus) + "% double clicks");
			
			initialArray[keypress-1][0] = 1;
			initialArray[keypress-1][1] = centisecondsPassedvideo/100;		
		}
		//the number 2 being pressed will add two clicks to the overall clicker and to the strictly + click counter, as well as flash a different color on the screen to show difficulty
		//the +2 difficulty click is also being used in a consistently updating string that displays the percentage of total clicks being +2 (difficult),
		//which is formatted by division of (the total value of +2 clicks * 100) divided by total clicks
		//it will also add a string to the arrayList that states "2 clicks at " + whatever second it is clicked
		if (keyCode == KeyEvent.VK_2)
		{
			getContentPane().setBackground(Color.lightGray);
			keypress++;
			clicker+=2;
			two+=2;
			plus+=2;
			segmentclicker+=2;
			lblRaw.setText("" + clicker);
			lblPlus.setText("+" + plus);
			lblPercentDouble.setText("" + (two*100/plus) + "% double clicks");
			//if statement for grammar purposes in arrayList
			if (centisecondsPassed/100 > 1)
			{
				clickTimes.add(2 + " clicks at " + centisecondsPassedvideo/100 + " seconds");
			}
			else
			{
				clickTimes.add(2 + " clicks at " + centisecondsPassedvideo/100 + " seconds");
			}
			
			
			initialArray[keypress-1][0] = 2;
			initialArray[keypress-1][1] = centisecondsPassedvideo/100;
			
		}
		//the minus key being pressed will deduct one click from the overall clicker and add one click  to the strictly - click counter
			//it will also add a string to the arrayList that states "1 miss at " + whatever second it is clicked
		if (keyCode == KeyEvent.VK_MINUS)
		{
			getContentPane().setBackground(Color.white);
			keypress++;
			clicker-=1;
			lblRaw.setText("" + clicker);
			minus++;
			lblMinus.setText("-" + minus);
			segmentclicker-=1;
			if (centisecondsPassed > 1)
			{
				clickTimes.add(1 + " miss at " + centisecondsPassedvideo/100 + " seconds");
			}
			else
			{
				clickTimes.add(1 + " miss at " + centisecondsPassedvideo/100 + " seconds");
			}
			
			initialArray[keypress-1][0] = -1;
			initialArray[keypress-1][1] = centisecondsPassedvideo/100;
			
		}
		//the number 0 being pressed acts as a stopper for the timer, while showing the completed data
		if (keyCode == KeyEvent.VK_0)
		{
			//the task is ended, which stops the timer and updating of the secondsPassed
			task.cancel();
			taskvideo.cancel();
			//sets text in time count label to total amount of time
			lblTimeCount.setText(ClickerTimer.centisecondsPassed/100 + " seconds total.");
			//reveals and shows the clicks per second using basic division with a decimal format system
			lblCPS.setText("Clicks per second: " + numberFormat.format((double)clicker/(centisecondsPassed/100)));
			lblCPS.setVisible(true);
			clickTimes.add("Clicks per second: " + numberFormat.format((double)clicker/(centisecondsPassed/100)));
			clickTimes.add("" + (two*100/plus) + "% double clicks");
			clickTimes.add("Total Positive - " + plus + ", Total Negative - " + minus + ", Total Raw Score - " + clicker + "");
			getbestsegment();

			//writes out a csv file of the array to the computer.
			try {
				//users string should be edited to personal preference to be used properly
				/**
				FileWriter writer = new FileWriter("/Users/colinbeckford/Desktop/Code/JB2SavedData/" + playerName + "_" + CompetitionRound + "_by_" + judgeName + ".csv");
				String collect = clickTimes.stream().collect(Collectors.joining(", "));
			    writer.write(collect);
			    writer.close();
			    **/
			    BufferedWriter br = new BufferedWriter(new FileWriter("/Users/colinbeckford/Desktop/Code/JB2SavedData/" + playerName + "_" + CompetitionRound + "_by_" + judgeName + ".csv"));
			    StringBuilder sb = new StringBuilder();
			    for (int i=0; i<keypress; i++) {
			    sb.append("[" + ClickerTimer.filter()[i][0] + " " + ClickerTimer.filter()[i][1] + "], ");
			    }
			    br.write(sb.toString());
			    br.close();
			    
			    }
			 catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	
		
	}


		
	
	//main method to run the clicker timer
	public static void main(String[] args)
	{
		ClickerTimer test = new ClickerTimer();
		
		
	}
}