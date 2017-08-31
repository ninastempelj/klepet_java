package klepet;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ChatFrame extends JFrame implements ActionListener, KeyListener, WindowListener {
	
	
	private JTextArea output;
	private JTextField input;
	private JLabel napis;
	public JTextField vzdevek;
	private JButton prijavniGumb;
	private JButton odjavniGumb;
//	private JPanel skupniPogovor;
//	private JPanel zasebniPogovor;
//	private JSplitPane split;
	private Robot robot;
	
	public ChatFrame() {
		super();
		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());
		napis = new JLabel("Vzdevek: ");
		vzdevek = new JTextField(10);
		vzdevek.setText(System.getProperty("user.name"));
		
		JPanel vzdevekVrstica = new JPanel();
		vzdevekVrstica.setLayout(new FlowLayout(FlowLayout.LEFT));
		vzdevekVrstica.add(napis);
		vzdevekVrstica.add(vzdevek);
		GridBagConstraints nastaviConstraint = new GridBagConstraints();
		nastaviConstraint.gridx = 0;
		nastaviConstraint.gridy = 0;
		nastaviConstraint.fill = 2;
		nastaviConstraint.weightx = 1;
		nastaviConstraint.weighty = 0;
		pane.add(vzdevekVrstica, nastaviConstraint);	
		
		
//		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, skupniPogovor, zasebniPogovor);
//		split.setOneTouchExpandable(true);
//		
//		this.skupniPogovor = new JPanel();
//		GridBagConstraints skupniConstraint = new GridBagConstraints();
//		skupniConstraint.gridx = 0;
//		skupniConstraint.gridy = 0;
//		skupniConstraint.fill = 1;
//		skupniConstraint.weightx = 1;
//		skupniConstraint.weighty = 1;
//		
//		this.zasebniPogovor = new JPanel();
//		GridBagConstraints zasebniConstraint = new GridBagConstraints();
//		zasebniConstraint.gridx = 0;
//		zasebniConstraint.gridy = 0;
//		zasebniConstraint.fill = 1;
//		zasebniConstraint.weightx = 1;
//		zasebniConstraint.weighty = 1;
//		pane.add(split);
		
		this.output = new JTextArea(20, 40);
		this.output.setEditable(false);
		GridBagConstraints scrollPaneConstraint = new GridBagConstraints();
		scrollPaneConstraint.gridx = 0;
		scrollPaneConstraint.gridy = 1;
		scrollPaneConstraint.fill = 1;
		scrollPaneConstraint.weightx = 1;
		scrollPaneConstraint.weighty = 1;
		JScrollPane scrollPane = new JScrollPane(output);
		pane.add(scrollPane, scrollPaneConstraint);
		
		this.input = new JTextField(40);
		GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.gridx = 0;
		inputConstraint.gridy = 2;
		inputConstraint.fill = 2;
		pane.add(input, inputConstraint);
		input.addKeyListener(this);
	    addWindowListener(this);
		
	    //this.uporabniki
	    
	    this.prijavniGumb =  new JButton("Prijava");
	    vzdevekVrstica.add(prijavniGumb);
	    prijavniGumb.addActionListener(this);
	    
	    this.odjavniGumb =  new JButton("Odjava");
	    vzdevekVrstica.add(odjavniGumb);
	    odjavniGumb.addActionListener(this); 
	    
	    this.robot = new Robot(this);
		
	}
	
	/**
	 * @param person - the person sending the message
	 * @param message - the message content
	 */
	
	//public ___ preberiSporocila():
	
	public void objaviSporocilo(String person, String message) {
		String chat = this.output.getText();
		this.output.setText(chat + person + ": " + message + "\n");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == prijavniGumb) {
			try{
			komunikacija.logirajSe(vzdevek.getText());
			objaviSporocilo("Sistem", "Prijava je uspela.");
			robot.aktiviraj();
			} catch (Exception ef) {
		  		objaviSporocilo("Sistem", "Uporabnik s tem imenom je že prijavljen.");
			  } 
		}
		if (e.getSource() == odjavniGumb) {
			robot.deaktiviraj();
			komunikacija.odjaviSe(vzdevek.getText());
			objaviSporocilo("Sistem", "Odjava je uspela.");
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getSource() == this.input) {
			if (e.getKeyChar() == '\n') {
				this.objaviSporocilo(vzdevek.getText(), this.input.getText());
				this.input.setText("");
			}
		}		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		input.requestFocusInWindow();
		
	}
	

}
