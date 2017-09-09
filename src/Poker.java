import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class Poker extends JFrame {
	private Container pane;
	private JPanel namePanel;
	private JLabel totalPotLabel;
	private JLabel[] potField;
	private JRadioButton[] nameButton;
	private String[] name;
	private int[] potValue;
	private int totalPotValue;
	
	public Poker() {
		this.pane = this.getContentPane();
		setResizable(true);
	}
	
	public void addComponentsToPane() {
		JPanel start = new JPanel();
		start.setLayout(new GridLayout(1,3));
		start.setPreferredSize(new Dimension(200, 40));
		start.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JTextField playerNumber = new JTextField();
		playerNumber.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int n = 0;
				try {
					Object obj = e.getSource();
					JTextField temp;
					if(obj instanceof JTextField) {
						temp = (JTextField)obj;
						n = Integer.parseInt(temp.getText());
						addPlayerSlot(n);
					}
				} catch (Exception e2) {
					System.out.println("Error: adding player name slot.");
				}
				
			}
		});
		start.add(new JLabel("# of players"));
		start.add(playerNumber);
		pane.add(start, BorderLayout.NORTH);
	}
	
	public void addGamePanel() {
		pane.removeAll();
		totalPotValue = 0;
		JPanel potPanel = new JPanel();
		totalPotLabel = new JLabel("0 $");
		TitledBorder potTitledBorder = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Pot");
		potTitledBorder.setTitleJustification(TitledBorder.CENTER);
		totalPotLabel.setBorder(BorderFactory.createCompoundBorder(potTitledBorder
				,BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		potPanel.setPreferredSize(new Dimension(200, 70));
		potPanel.add(totalPotLabel);
		
		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(new GridLayout(0,2 ));
		gamePanel
		.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
		nameButton = new JRadioButton[name.length];
		potField = new JLabel[name.length];
		potValue = new int[name.length];
		for (int i = 0; i < name.length; i++) {
			nameButton[i] = new JRadioButton(name[i]);
			potField[i] = new JLabel("0 $");
			potValue[i] = 0;
			gamePanel.add(nameButton[i]);
			gamePanel.add(potField[i]);
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
	
	public void addPlayerSlot(int n) {
		if(namePanel != null) {
			pane.remove(namePanel);
		}
		if(n > 8) n = 8;
		else if(n < 2) n = 2;
		namePanel = new JPanel();
		namePanel.setLayout(new GridLayout(0, 2));
		namePanel.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
		
		namePanel.add(new JLabel("Player name :"));
		namePanel.add(new JLabel(""));
		JTextField[] nameField = new JTextField[n];
		name = new String[n];
		for(int i=0; i<n ;i++) {
			nameField[i] = new JTextField();
			namePanel.add(new JLabel("Player #"+(i+1)));
			namePanel.add(nameField[i]);
			nameField[i].setActionCommand(""+i);
			nameField[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						int index = Integer.parseInt(e.getActionCommand());
						JTextField temp = (JTextField)e.getSource();
						name[index] = temp.getText();
					} catch (Exception e2) {
						System.out.println("Error: adding player name.");
					}
				}
			});
		}
		
		JButton newGameButton = new JButton("New Game");
		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < nameField.length; i++) {
					name[i] = nameField[i].getText();
				}
				addGamePanel();
			}
		});
		
		pane.add(namePanel, BorderLayout.CENTER);
		pane.add(newGameButton, BorderLayout.SOUTH);
		this.pack();
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
					for (int i = 0; i < nameButton.length; i++) {
						if(nameButton[i].isSelected()) {
							potValue[i] += raise;
							potField[i].setText(potValue[i] + " $");
							count++;
						}
						nameButton[i].setSelected(false);
					}
					totalPotValue += raise*count;
					totalPotLabel.setText(totalPotValue + " $");
					
				} catch (Exception e2) {
				}
			}
			else if(action.equals("win")) {
				
			}
		}
		
	}
}