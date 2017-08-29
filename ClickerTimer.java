/**
 * @author colinbeckford
 * Clicker Timer program
 * Allows user to time yo-yo freestyles while scoring them using keyboard presses
 * Before starting the scoring, in order for the final result to have a correct title, the user must enter data into the text boxes and hit the Submit button
 * Multiple values are continuously updated throughout, such as percentage of +2 difficult clicks, positive clicker count, negative clicker count
 * After freestyle is complete, the clicks per second value is revealed
 * Final result is an exported CSV file featuring strings that state a combination of the amount of clicks and the time the clicks took place, i.e. "1 click at 4.0 seconds"
 */
//importing necessary files
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


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
	public JButton btnSubmit = new JButton("Submit");
	private JTextField txtJudgeName;
	private JTextField txtYoutubeLink;
	private JTextField txtPlayerName;
	private JTextField txtCompetitionRound;
	//variable data that is updated throughout program being run
	int clicker = 0;
	int plus = 0;
	int minus = 0;
	int two = 0;
	static double secondsPassed = 0.0;
	static DecimalFormat numberFormat = new DecimalFormat("#.00");
	ArrayList<String> clickTimes = new ArrayList<String>();
	String judgeName;
	String YTlink;
	String playerName;
	String CompetitionRound;
	//isn't used yet - meant to be used for more specific timing purposes
	private final static SimpleDateFormat date = new SimpleDateFormat("mm.ss.SS");
	static long startTime = System.nanoTime();
	
	//constructor for all of the labels, buttons, and textfields on the JFrame
	public ClickerTimer() {
		setVisible(true);
		setSize(400,400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		input = new JTextField();
		input.setBounds(6, 6, 438, 26);
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
		lblRaw.setBounds(163, 44, 106, 100);
		getContentPane().add(lblRaw);
		
	
		lblPercentDouble.setBounds(6, 219, 217, 16);
		getContentPane().add(lblPercentDouble);
		
		lblTimeCount.setBounds(6, 247, 191, 16);
		getContentPane().add(lblTimeCount);
		
		lblCPS.setBounds(6, 275, 172, 16);
		getContentPane().add(lblCPS);
		
		txtJudgeName = new JTextField();
		txtJudgeName.setHorizontalAlignment(SwingConstants.CENTER);
		txtJudgeName.setText("Judge Name");
		txtJudgeName.setBounds(6, 154, 130, 26);
		txtJudgeName.setVisible(true);
		getContentPane().add(txtJudgeName);
		txtJudgeName.setColumns(10);
		
		txtYoutubeLink = new JTextField();
		txtYoutubeLink.setHorizontalAlignment(SwingConstants.CENTER);
		txtYoutubeLink.setText("Youtube Link");
		txtYoutubeLink.setBounds(6, 181, 130, 26);
		txtYoutubeLink.setVisible(true);
		getContentPane().add(txtYoutubeLink);
		txtYoutubeLink.setColumns(10);
		
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
		txtCompetitionRound.setBounds(139, 181, 130, 26);
		txtCompetitionRound.setVisible(true);
		getContentPane().add(txtCompetitionRound);
		txtCompetitionRound.setColumns(10);
		
		//action listener for the submit button which saves the text inputs into string variables (listed above)
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				judgeName = txtJudgeName.getText();
				YTlink = txtYoutubeLink.getText();
				playerName = txtPlayerName.getText();
				CompetitionRound = txtCompetitionRound.getText();
			}
		});
		btnSubmit.setBounds(277, 154, 117, 29);
		btnSubmit.setVisible(true);
		getContentPane().add(btnSubmit);
		
		
		lblCPS.setVisible(false);
	}
	//creates an instance of the Timer class to have a consistent time during the scoring
	static Timer timer = new Timer();
	//timertask to be ran once the timer is activated
	static TimerTask task = new TimerTask() {
		public void run () 
		{
			//incrementing of a counter of seconds that is displayed on a label
			secondsPassed++;
			lblTimeCount.setText("Time passed: " + secondsPassed + " seconds");
			//unused
			Date elapsed = new Date(System.nanoTime() - startTime);
		}
	};
	//start command for timer
	public static void start () 
	{
		timer.scheduleAtFixedRate(task, 1000, 1000);
		
	}
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
			clicker++;
			lblRaw.setText("" + clicker);
			plus++;
			lblPlus.setText("+" + plus);
			//if statement for grammar purposes in arrayList
			if (secondsPassed > 1)
			{
				clickTimes.add(1 + " click at " + secondsPassed + " seconds");
			}
			else
			{
				clickTimes.add(1 + " click at " + secondsPassed + " second");
			}
			//explained below
			lblPercentDouble.setText("" + (two*100/plus) + "% double clicks");
				
		}
		//the number 2 being pressed will add two clicks to the overall clicker and to the strictly + click counter, as well as flash a different color on the screen to show difficulty
		//the +2 difficulty click is also being used in a consistently updating string that displays the percentage of total clicks being +2 (difficult),
		//which is formatted by division of (the total value of +2 clicks * 100) divided by total clicks
		//it will also add a string to the arrayList that states "2 clicks at " + whatever second it is clicked
		if (keyCode == KeyEvent.VK_2)
		{
			getContentPane().setBackground(Color.lightGray);
			clicker+=2;
			two+=2;
			plus+=2;
			lblRaw.setText("" + clicker);
			lblPlus.setText("+" + plus);
			lblPercentDouble.setText("" + (two*100/plus) + "% double clicks");
			//if statement for grammar purposes in arrayList
			if (secondsPassed > 1)
			{
				clickTimes.add(2 + " clicks at " + secondsPassed + " seconds");
			}
			else
			{
				clickTimes.add(2 + " clicks at " + secondsPassed + " second");
			}
		//the minus key being pressed will deduct one click from the overall clicker and add one click  to the strictly - click counter
		//it will also add a string to the arrayList that states "1 miss at " + whatever second it is clicked
		}
		if (keyCode == KeyEvent.VK_MINUS)
		{
			getContentPane().setBackground(Color.white);
			clicker-=1;
			lblRaw.setText("" + clicker);
			minus++;
			lblMinus.setText("-" + minus);
			if (secondsPassed > 1)
			{
				clickTimes.add(1 + " miss at " + secondsPassed + " seconds");
			}
			else
			{
				clickTimes.add(1 + " miss at " + secondsPassed + " second");
			}
		}
		//the number 0 being pressed acts as a stopper for the timer, while showing the completed data
		if (keyCode == KeyEvent.VK_0)
		{
			//the task is ended, which stops the timer and updating of the secondsPassed
			task.cancel();
			//sets text in time count label to total amount of time
			lblTimeCount.setText(ClickerTimer.secondsPassed + " seconds total.");
			//reveals and shows the clicks per second using basic division with a decimal format system
			lblCPS.setText("Clicks per second: " + numberFormat.format((double)plus/secondsPassed));
			lblCPS.setVisible(true);
			//writes out a csv file of the array to the computer.
			try {
				//users string should be edited to personal preference to be used properly
				FileWriter writer = new FileWriter("/Users/colinbeckford/Desktop/Code/" + playerName + "_" + CompetitionRound + "_by_" + judgeName + ".csv");
				String collect = clickTimes.stream().collect(Collectors.joining(", "));
				System.out.println(collect);
			    writer.write(collect);
			    writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		//backspace acts as a complete reset and sets everything to 0
		if (keyCode == KeyEvent.VK_BACK_SPACE)
		{
			getContentPane().setBackground(Color.white);
			clicker = 0;
			lblRaw.setText("" + clicker);
			plus = 0;
			minus = 0;
			two = 0;
			lblPlus.setText("+" + plus);
			lblMinus.setText("-" + minus);
			lblPercentDouble.setText("0% double clicks");
			task.cancel();
			lblTimeCount.setText("Time passed: 0 seconds");
		}
		
	}
	//main method to run the clicker timer
	public static void main(String[] args)
	{
		ClickerTimer test = new ClickerTimer();
		
	}
}
