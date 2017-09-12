import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class Poker extends JFrame {
	private Container pane;
	private JPanel namePanel, newGamePanel;
	private JLabel totalPotLabel;
	private Player[] allPlayer;
	private int totalPotValue;
	JTextField[] nameField;
	
	public Poker() {
		this.pane = this.getContentPane();
		setResizable(true);
	}
	
	public void addComponentsToPane() {
		JPanel start = new JPanel();
		start.setPreferredSize(new Dimension(400, 40));
		//start.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JSlider playerNumber = new JSlider(2,10);
		playerNumber.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if(!source.getValueIsAdjusting()) {
					int value = (int)source.getValue();
					System.out.println(value);
					addPlayerSlot(value);
				}
			}
		});
		start.add(new JLabel("Number of players"));
		start.add(playerNumber);
		pane.add(start, BorderLayout.NORTH);
	}
	
	public void addPlayerSlot(int n) {
		if(namePanel != null) {
			pane.remove(namePanel);
			pane.remove(newGamePanel);
		}
		namePanel = new JPanel();
		namePanel.setLayout(new GridLayout(0, 1));
		namePanel.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
				
		allPlayer = new Player[n];
		
		nameField = new JTextField[n];
		for(int i=0; i<n ;i++) {
			JPanel temp = new JPanel();
			allPlayer[i] = new Player();
			allPlayer[i].payTo = new int[n];
			nameField[i] = new JTextField(10);
			nameField[i].setActionCommand(""+i);
			nameField[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						int index = Integer.parseInt(e.getActionCommand());
						JTextField temp = (JTextField)e.getSource();
						allPlayer[index].name = temp.getText();
					} catch (Exception e2) {
						System.out.println("Error: adding player name.");
					}
				}
			});
			temp.add(new JLabel("Player #"+(i+1)));
			temp.add(new JLabel("     "));
			temp.add(nameField[i]);
			namePanel.add(temp);
		}
		
		newGamePanel = new JPanel();
		JButton newGameButton = new JButton("New Game");
		newGameButton.setActionCommand("first");
		newGameButton.setPreferredSize(new Dimension(100, 50));
		newGameButton.addActionListener(new newGameButtonListener());
//		newGameButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				for (int i = 0; i < allPlayer.length; i++) {
//					allPlayer[i].name = nameField[i].getText();
//				}
//				addGamePanel();
//			}
//		});
		
		newGamePanel.add(newGameButton);
		pane.add(namePanel, BorderLayout.CENTER);
		pane.add(newGamePanel, BorderLayout.SOUTH);
		this.pack();
	}
	
	public void addGamePanel() {
		pane.removeAll();
		totalPotValue = 0;
		JPanel potPanel = new JPanel();
		potPanel.setLayout(new GridBagLayout());
		totalPotLabel = new JLabel("0 $");
		totalPotLabel.setFont(new Font(totalPotLabel.getFont().getName(), Font.PLAIN, 20));
		TitledBorder potTitledBorder = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Pot");
		potTitledBorder.setTitleJustification(TitledBorder.CENTER);
		potPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)
				,potTitledBorder));
		potPanel.setPreferredSize(new Dimension(400, 100));
		potPanel.add(totalPotLabel);
		
		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(new GridLayout(0,2 ));
		gamePanel.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
		for (Player player : allPlayer) {
			player.nameButton = new JRadioButton(player.name);
			player.potField = new JLabel("0 $");
			player.potValue = 0;
			gamePanel.add(player.nameButton);
			gamePanel.add(player.potField);
		}
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(0, 3));
		JButton raiseButton = new JButton("Raise");
		JTextField raiseField = new JTextField();
		JButton winButton = new JButton("Win");
		
		raiseButton.setActionCommand("raise");
		raiseField.setActionCommand("raise");
		winButton.setActionCommand("win");
		
		raiseButton.addActionListener(new controlButtonListener(raiseField));
		raiseField.addActionListener(new controlButtonListener(raiseField));
		winButton.addActionListener(new controlButtonListener(raiseField));
		
		controlPanel.add(raiseButton);
		controlPanel.add(raiseField);
		controlPanel.add(winButton);
		
		pane.add(potPanel, BorderLayout.NORTH);
		pane.add(gamePanel, BorderLayout.CENTER);
		pane.add(controlPanel, BorderLayout.SOUTH);
		pack();
	}
	
	public void addWinPanel() {
		pane.removeAll();
		//Font font = new Font("SansSerif", Font.PLAIN, 20);
		JPanel winPanel = new JPanel();
		winPanel.setLayout(new GridLayout(0, 2));
		//winPanel.setPreferredSize(new Dimension(400,400));
		winPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		int winNumber = 0;
		for(Player player : allPlayer) {
			if(player.win) {
				totalPotValue -= player.potValue;
				winNumber++;
			}
		}
		//Add payment
		for(int i = 0 ; i < allPlayer.length ; i++) {
			if(allPlayer[i].win) {
				int winPrice = totalPotValue/winNumber;
				for(Player losePlayer : allPlayer) {
					if(!losePlayer.win) {
						if(losePlayer.potValue > winPrice ) {
							losePlayer.potValue -= winPrice;
							losePlayer.payTo[i] += winPrice;
							winPrice = 0;
						}
						else {
							winPrice -= losePlayer.potValue;
							losePlayer.payTo[i] += losePlayer.potValue;
							losePlayer.potValue = 0;
						}
					}
				}
			}
		}
		//Calculate Payment
		for (int i = 0; i < allPlayer.length; i++) {
			for (int j = 0; j < allPlayer[i].payTo.length; j++) {
				int a = allPlayer[i].payTo[j];
				int b = allPlayer[j].payTo[i];
				if(a != 0 && b != 0) {
					int temp = Math.min(a, b);
					allPlayer[i].payTo[j] -= temp;
					allPlayer[j].payTo[i] -= temp;
				}
			}
		}
		//Show Payment
		for(Player player : allPlayer) {
			Boolean isShowPayTo = false;
			for (int i = 0; i < player.payTo.length; i++) {
				if(player.payTo[i] > 0) {
					isShowPayTo = true;
				}
			}
			if(isShowPayTo) {
				winPanel.add(new JLabel(player.name + " pay to"));
				winPanel.add(new JLabel(""));
				
				for (int i = 0; i < player.payTo.length; i++) {
					if(player.payTo[i] != 0) {
						winPanel.add(new JLabel(allPlayer[i].name));
						winPanel.add(new JLabel(player.payTo[i] + " $"));
					}
				}
				winPanel.add(new JLabel(""));
				winPanel.add(new JLabel(""));
			}
		}
		
		newGamePanel = new JPanel();
		JButton newGameButton = new JButton("New Game");
		newGameButton.setPreferredSize(new Dimension(100, 50));
		newGameButton.addActionListener(new newGameButtonListener());
		
		newGamePanel.add(newGameButton);
		
		JPanel temp = new JPanel();
		temp.setPreferredSize(new Dimension(400, 0));
		pane.add(temp, BorderLayout.NORTH);
		pane.add(winPanel, BorderLayout.CENTER);
		pane.add(newGamePanel, BorderLayout.SOUTH);
		pack();
		
	}
	
	
	
	public static void createAndShowGUI() {
		Poker frame = new Poker();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addComponentsToPane();
		frame.pack();
        frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		 SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();				
			}
		});
	}
	
	class Player {
		String name;
		int potValue;
		int[] payTo;
		boolean win;
		JLabel potField;
		JRadioButton nameButton;
		
		public Player(String name) {
			this.name = name;
		}
		
		public Player() {}
	}
	
	class controlButtonListener implements ActionListener {
		JTextField raiseField;
		public controlButtonListener(JTextField raiseField) {
			this.raiseField = raiseField;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if(action.equals("raise")) {
				try {
					int raise = Integer.parseInt(raiseField.getText());
					int count = 0;
					for (Player player : allPlayer) {
						if(player.nameButton.isSelected()) {
							player.potValue += raise;
							player.potField.setText(player.potValue + " $");
							count++;
						}
						player.nameButton.setSelected(false);
					}
					totalPotValue += raise*count;
					totalPotLabel.setText(totalPotValue + " $");
					
				} catch (Exception e2) {}
			}
			else if(action.equals("win")) {
				for (Player player : allPlayer) {
					if(player.nameButton.isSelected()) {
						player.win = true;
					}
					else {
						player.win = false;
					}
				}
				addWinPanel();
			}
		}
	}
	
	class newGameButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand() == "first")
				for (int i = 0; i < allPlayer.length; i++) {
					allPlayer[i].name = nameField[i].getText();
				}
			else {
				totalPotValue = 0;
				for(Player player : allPlayer) {
					player.potValue = 0;
				}
			}
			addGamePanel();
		}
	}
}
