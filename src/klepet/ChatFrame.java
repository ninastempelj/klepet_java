package klepet;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ChatFrame extends JFrame implements ActionListener, KeyListener, WindowListener {
	
	
	private JTextArea outputSkupni; //prikaz sporoèil
	private JTextField inputSkupni; // pisanje sporoèil
	private JLabel napisVzdevek;     
	public JTextField vzdevek;  // okno v katerega napišeš vzdevek
	private JButton prijavniGumb;
	private JButton odjavniGumb;
	private JButton prikazi; // - pomozni gumb nekaj èasa
	private JPanel skupniPogovor;
	private JPanel zasebniPogovor;
	private JSplitPane razdeliPogovor;
	private JPanel uporabniki;
	private Robot robot;
	private Boolean prijavljen;
	private String prejsnji; // hrani vse uporabnike, ki so se prijavili v tem oknu
	private JTextField inputZasebni;
	private JTextArea outputZasebni;
	private JSplitPane razdeliVrstico;
	private JSplitPane razdeliUporabnik;
	private JTextArea izpisUporabnikov;
	
	public ChatFrame() {
		super();
		Container pane = this.getContentPane();  // naredimo osnovno plošèo 
		
		/*
		 * Razdelimo naše okno na štiri dele: 
		 * - najprej navpièno na vrstico z vzdevkom in gumbi in vse ostalo
		 * - to potem na uporabnike in pogovore
	     * - pogovore pa še na zasebne in skupni.
		 * 
		 * Najprej moramo sestaviti vse te štiri komponente:
		 * 
		 * To je vrstica za vzdevek in gumbe:
		 */
		
		JPanel vzdevekVrstica = new JPanel();  
		vzdevekVrstica.setLayout(new FlowLayout(FlowLayout.LEFT));
		/*
		 * GridBagConstraints vzdevekConstraint = new GridBagConstraints();
		vzdevekConstraint.gridx = 0;
		vzdevekConstraint.gridy = 0;
		vzdevekConstraint.fill = 2;
		vzdevekConstraint.weightx = 1;
		vzdevekConstraint.weighty = 0;
		pane.add(vzdevekVrstica, vzdevekConstraint);
		*/
		
		/*
		 * To je okno za vse razen vrstice z vzdevki
		 */
		JPanel ostalo = new JPanel();
		
		/*
		 * Okno z vsemi pogovori
		 */
		JPanel pogovori = new JPanel();
		
		/*
		 * To je podokno za skupni pogovor.
		 */
		this.skupniPogovor = new JPanel();
		skupniPogovor.setMinimumSize(new Dimension(30, 100));
		skupniPogovor.setLayout(new GridBagLayout());
		
		/*
		 * To je podokno za zasebni pogovor.
		 */
		this.zasebniPogovor = new JPanel();
		zasebniPogovor.setMinimumSize(new Dimension(30, 100));
		zasebniPogovor.setLayout(new GridBagLayout());
		
		/*
		 * To je podokno za prikaz prijavljenih uporabnikov.
		 */
		this.uporabniki = new JPanel();
		uporabniki.setMinimumSize(new Dimension(30, 100));
		
		/*
		 * Razdeljevanje okna:
		 */
		
		this.razdeliVrstico = new JSplitPane(JSplitPane.VERTICAL_SPLIT, vzdevekVrstica, ostalo);
		this.razdeliUporabnik = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pogovori, uporabniki);
		this.razdeliPogovor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, skupniPogovor, zasebniPogovor);
		razdeliPogovor.setOneTouchExpandable(true);
		razdeliUporabnik.setOneTouchExpandable(true);
		
		/*
		 * GridBagConstraints razdeljenoConstraint = new GridBagConstraints();
		razdeljenoConstraint.gridx = 0;
		razdeljenoConstraint.gridy = 1;
		razdeljenoConstraint.fill = 2;
		razdeljenoConstraint.weightx = 1;
		razdeljenoConstraint.weighty = 1;
		*/
		pane.add(razdeliVrstico);
		ostalo.add(razdeliUporabnik);
		pogovori.add(razdeliPogovor);
		
		/*
		 * Elementi v vrstici za vzdevek:
		 */
		napisVzdevek = new JLabel("Vzdevek: ");
		vzdevekVrstica.add(napisVzdevek);
		
		vzdevek = new JTextField(10);
		vzdevek.setText(System.getProperty("user.name"));
		vzdevekVrstica.add(vzdevek);
		
		this.prijavniGumb =  new JButton("Prijava");
		vzdevekVrstica.add(prijavniGumb);
		prijavniGumb.addActionListener(this);
		  
		this.odjavniGumb =  new JButton("Odjava");
		vzdevekVrstica.add(odjavniGumb);
		odjavniGumb.addActionListener(this); 
		   
		this.prikazi = new JButton("Prikaži");
		vzdevekVrstica.add(prikazi);
		prikazi.addActionListener(this);
		
		/*
		 * Elementi v uporabnikih
		 */
		
		this.izpisUporabnikov = new JTextArea(20, 20);
		this.izpisUporabnikov.setEditable(false);
		uporabniki.add(izpisUporabnikov);
		izpisiUporabnike(Komunikacija.vpisaniUporabniki());
		
		/*
		 * Elementi skupnega pogovora
		 */
		this.outputSkupni = new JTextArea(20, 40);
		this.outputSkupni.setEditable(false);
		GridBagConstraints pogovorSkupniConstraint = new GridBagConstraints();
		pogovorSkupniConstraint.gridx = 0;
		pogovorSkupniConstraint.gridy = 0;
		pogovorSkupniConstraint.fill = 1;
		pogovorSkupniConstraint.weightx = 1;
		pogovorSkupniConstraint.weighty = 1;
		JScrollPane pogovorSkupni = new JScrollPane(outputSkupni);
		skupniPogovor.add(pogovorSkupni, pogovorSkupniConstraint);
		
		this.inputSkupni = new JTextField(40);
		GridBagConstraints pisanjeSkupniConstraint = new GridBagConstraints();
		pisanjeSkupniConstraint.gridx = 0;
		pisanjeSkupniConstraint.gridy = 3;
		pisanjeSkupniConstraint.fill = 2;
		pisanjeSkupniConstraint.weightx = 1;
		pisanjeSkupniConstraint.weighty = 1;
		skupniPogovor.add(inputSkupni, pisanjeSkupniConstraint);
		inputSkupni.addKeyListener(this);
	    addWindowListener(this);

	    /*
	     * Elementi zasebnega pogovora.
	     */
		this.outputZasebni = new JTextArea(20, 40);
		this.outputZasebni.setEditable(false);
		GridBagConstraints pogovorZasebniConstraint = new GridBagConstraints();
		pogovorZasebniConstraint.gridx = 0;
		pogovorZasebniConstraint.gridy = 2;
		pogovorZasebniConstraint.fill = 1;
		pogovorZasebniConstraint.weightx = 1;
		pogovorZasebniConstraint.weighty = 1;
		JScrollPane pogovorZasebni = new JScrollPane(outputZasebni);
		zasebniPogovor.add(pogovorZasebni, pogovorZasebniConstraint);
		
	    this.inputZasebni = new JTextField(40);
		GridBagConstraints pisanjeZasebniConstraint = new GridBagConstraints();
		pisanjeZasebniConstraint.gridx = 0;
		pisanjeZasebniConstraint.gridy = 3;
		pisanjeZasebniConstraint.fill = 2;
		pisanjeZasebniConstraint.weightx = 1;
		pisanjeZasebniConstraint.weighty = 1;
		zasebniPogovor.add(inputZasebni, pisanjeZasebniConstraint);
		inputZasebni.addKeyListener(this);
	    addWindowListener(this);
	    

	    /*
	     * Elementi prikaza uporabnikov
	     */
		// TODO: uporabniško okno
	    
	    
	    this.robot = new Robot(this);
		this.prijavljen = false;
		this.prejsnji = new String();
	}
	
	/*
	 * Ta metoda sporoèilo izpiše na zaslon, èe gre za naše sporoèilo, ga pošlje naprej.
	 */
	public void izpisiSporocilo(Sporocilo sporocilo) {
		String chat = this.outputSkupni.getText();
		String posiljatelj = sporocilo.getSender();
		this.outputSkupni.setText(chat + posiljatelj + ": " + sporocilo.getText() + "\n");
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
			zaNapisat += nekdo.getUsername() + "\n";
		}
		this.izpisUporabnikov.setText(zaNapisat);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Sporocilo obvestilo = new Sporocilo("true", ""); // izpisalo se bo obvestilo, kaj se je zgodilo. 
		obvestilo.setSender("Sistem");
		obvestilo.setGlobal("true");
		String ime = vzdevek.getText();
		
		if (e.getSource() == prijavniGumb) {
			try{
				if (this.prijavljen) {
					Komunikacija.odjaviSe(ime);
					robot.deaktiviraj();
				}
			Komunikacija.logirajSe(ime);
			this.prejsnji = (ime);
			this.prijavljen = true;
			obvestilo.setText("Prijava " + ime + " je uspela.");
			robot.aktiviraj();
			izpisiSporocilo(obvestilo);
			} catch (Exception ef) {
				ef.printStackTrace();
				obvestilo.setText("Uporabnik " + ime + " je že prijavljen.");
				izpisiSporocilo(obvestilo);
				}
			
		}
		
		if (e.getSource() == odjavniGumb) {
			robot.deaktiviraj();
			Komunikacija.odjaviSe(ime);
			obvestilo.setText("Odjava " + ime +" je uspela.");
			izpisiSporocilo(obvestilo);
			this.prijavljen = false;
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
		if (e.getSource() == this.inputSkupni) {
			if (e.getKeyChar() == '\n') {
				Sporocilo sporocilo = new Sporocilo("true", this.inputSkupni.getText());
				sporocilo.setSender(vzdevek.getText());
				this.izpisiSporocilo(sporocilo);
				Komunikacija.posljiSporocilo(sporocilo);
				this.inputSkupni.setText("");
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
		if (this.prijavljen) {
		Komunikacija.odjaviSe(this.prejsnji);
		robot.deaktiviraj();
		}
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
		inputSkupni.requestFocusInWindow();
		
	}
	

}
