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
import java.util.List;

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
//	private JButton prikazi;  - pomozni gumb nekaj èasa
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
	    
	    //this.prikazi = new JButton("Prikaži");
	    //vzdevekVrstica.add(prikazi);
	    //prikazi.addActionListener(this); 
	    
	    this.robot = new Robot(this);
		
	}
	
	/**
	 * @param person - the person sending the message
	 * @param message - the message content
	 */
	
	//public ___ preberiSporocila():
	
	/**
	 * S to funkcijo objekt Sporocilo pošljemo naslovniku
	 * @param posiljatelj 
	 * @param vsebina
	 */
	public void izpisiSporocilo(Sporocilo sporocilo) {
		String chat = this.output.getText();
		String posiljatelj = sporocilo.getSender();
		this.output.setText(chat + posiljatelj + ": " + sporocilo.getText() + "\n");
		if (posiljatelj == vzdevek.getText()) {
			sporocilo.setSender(posiljatelj);
		Komunikacija.posljiSporocilo(sporocilo);
		}
	}

	
	public void izpisiSporocilo(List<Sporocilo> novaSporocila) {
		for (Sporocilo sporocilo : novaSporocila) {
			izpisiSporocilo(sporocilo);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Sporocilo obvestilo = new Sporocilo("true", "");
		obvestilo.setSender("Sistem");
		if (e.getSource() == prijavniGumb) {
			try{
			Komunikacija.logirajSe(vzdevek.getText());
			obvestilo.setText("Prijava je uspela.");
			izpisiSporocilo(obvestilo);
			robot.aktiviraj();
			} catch (Exception ef) {
				obvestilo.setText("Uporabnik s tem imenom je že prijavljen.");
		  		izpisiSporocilo(obvestilo);
			  } 
		}
		if (e.getSource() == odjavniGumb) {
			robot.deaktiviraj();
			Komunikacija.odjaviSe(vzdevek.getText());
			obvestilo.setText("Odjava je uspela.");
			izpisiSporocilo(obvestilo);
		}
		
		/*
		 * prikazovanje vseh spororèil v debilni obliki za preverjanje a jih sploh dobivam
		 */
		//if (e.getSource() == prikazi) {
		//	objaviSporocilo(Komunikacija.novaSporocila(vzdevek.getText()));
		//}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getSource() == this.input) {
			if (e.getKeyChar() == '\n') {
				Sporocilo sporocilo = new Sporocilo("true", this.input.getText());
				sporocilo.setSender(vzdevek.getText());
				this.izpisiSporocilo(sporocilo);
				Komunikacija.posljiSporocilo(sporocilo);
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

	/*
	 * Èe uporabnik zapre okno brez odjave, se odjavi samodejno.
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		Komunikacija.odjaviSe(vzdevek.getText());
		robot.deaktiviraj();
		
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
