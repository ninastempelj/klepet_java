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
	
	
	private JTextArea output; //prikaz sporoèil
	private JTextField input; // pisanje sporoèil
	private JLabel napisVzdevek;     
	public JTextField vzdevek;  // okno v katerega napišeš vzdevek
	private JButton prijavniGumb;
	private JButton odjavniGumb;
	private JButton prikazi; // - pomozni gumb nekaj èasa
//	private JPanel skupniPogovor;
//	private JPanel zasebniPogovor;
//	private JSplitPane split;
	private Robot robot;
	
	public ChatFrame() {
		super();
		Container pane = this.getContentPane();  // naredimo osnovno plošèo 
		pane.setLayout(new GridBagLayout());
		
		napisVzdevek = new JLabel("Vzdevek: ");
		vzdevek = new JTextField(10);
		vzdevek.setText(System.getProperty("user.name"));
		
		/*
		 * Tu naredimo vrstico z vzdevkom in gumbi.
		 */
		JPanel vzdevekVrstica = new JPanel();  
		vzdevekVrstica.setLayout(new FlowLayout(FlowLayout.LEFT));
		vzdevekVrstica.add(napisVzdevek);
		vzdevekVrstica.add(vzdevek);
		GridBagConstraints vzdevekConstraint = new GridBagConstraints();
		vzdevekConstraint.gridx = 0;
		vzdevekConstraint.gridy = 0;
		vzdevekConstraint.fill = 2;
		vzdevekConstraint.weightx = 1;
		vzdevekConstraint.weighty = 0;
		pane.add(vzdevekVrstica, vzdevekConstraint);	
		
		
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
		GridBagConstraints pogovorConstraint = new GridBagConstraints();
		pogovorConstraint.gridx = 0;
		pogovorConstraint.gridy = 1;
		pogovorConstraint.fill = 1;
		pogovorConstraint.weightx = 1;
		pogovorConstraint.weighty = 1;
		JScrollPane pogovor = new JScrollPane(output);
		pane.add(pogovor, pogovorConstraint);
		
		this.input = new JTextField(40);
		GridBagConstraints pisanjeConstraint = new GridBagConstraints();
		pisanjeConstraint.gridx = 0;
		pisanjeConstraint.gridy = 2;
		pisanjeConstraint.fill = 2;
		pane.add(input, pisanjeConstraint);
		input.addKeyListener(this);
	    addWindowListener(this);
	    
	    this.prijavniGumb =  new JButton("Prijava");
	    vzdevekVrstica.add(prijavniGumb);
	    prijavniGumb.addActionListener(this);
	    
	    this.odjavniGumb =  new JButton("Odjava");
	    vzdevekVrstica.add(odjavniGumb);
	    odjavniGumb.addActionListener(this); 
	    
	    this.prikazi = new JButton("Prikaži");
	    vzdevekVrstica.add(prikazi);
	    prikazi.addActionListener(this); 
	    
	    this.robot = new Robot(this);
		
	}
	
	/**
	 * @param person - the person sending the message
	 * @param message - the message content
	 */
	
	//public ___ preberiSporocila():
	
	/**
	 * Ta metoda sporoèilo izpiše na zaslon, èe gre za naše sporoèilo, ga pošlje naprej.
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

	/*
     * Ta metoda izpiše na zaslon vsa sporoèila v seznamu.
	 */
	public void izpisiSporocilo(List<Sporocilo> novaSporocila) {
		for (Sporocilo sporocilo : novaSporocila) {
			izpisiSporocilo(sporocilo);
		}
		
	}
	
	public void izpisiUporabnike(List<Uporabnik> uporabniki) {
		String zaNapisat = new String();
		for (Uporabnik nekdo : uporabniki) {
			zaNapisat += nekdo.getUsername() + ", ";
		}
		String chat = this.output.getText();
		this.output.setText(chat + zaNapisat + "\n");
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Sporocilo obvestilo = new Sporocilo("true", ""); // izpisalo se bo obvestilo, kaj se je zgodilo. 
		obvestilo.setSender("Sistem");
		obvestilo.setGlobal("true");
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
		 * prikazovanje uporabnikov
		 */
		if (e.getSource() == prikazi) {
			izpisiUporabnike(Komunikacija.vpisaniUporabniki());
		}
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
