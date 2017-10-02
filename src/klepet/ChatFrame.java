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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ChatFrame extends JFrame implements ActionListener, KeyListener, WindowListener {
	
	
	private JTextArea outputSkupni; //prikaz sporo�il
	private JTextField inputSkupni; // pisanje sporo�il
	private JLabel napisVzdevek;     
	public JTextField vzdevek;  // okno v katerega napi�e� vzdevek
	private JButton prijavniGumb;
	private JButton odjavniGumb;
	private JButton prikazi; // - pomozni gumb nekaj �asa
	private JPanel skupniPogovor;
	private JPanel zasebniPogovor;
	private JSplitPane razdeliPogovor;
	private JPanel uporabniki;
	private Robot robot;
	private Boolean prijavljen;
	private String prejsnji; // hrani zadnjega uporabnika, ki se je  prijavil v tem oknu.
	private JTextField inputZasebni;
	private JTextArea outputZasebni;
	private JSplitPane razdeliVrstico;
	private JSplitPane razdeliUporabnik;
	private JTabbedPane izpisUporabnikov;  //prej je blo druga�e!!!!
	private Set<String> trenutniUporabniki;
	//private Map<String, Integer> trenutniUporabnikiIndeksi;
	//private Integer stevecUporabnikov;
	
	public ChatFrame() {
		super();
		Container pane = this.getContentPane();  // naredimo osnovno plo��o 
		
		/*
		 * Razdelimo na�e okno na �tiri dele: 
		 * - najprej navpi�no na vrstico z vzdevkom in gumbi in vse ostalo
		 * - to potem na uporabnike in pogovore
	     * - pogovore pa �e na zasebne in skupni.
		 * 
		 * Najprej moramo sestaviti vse te �tiri komponente:
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
		// Ti dve vrstici zbri�i, za prej�nji videz
		pogovori.setMinimumSize(new Dimension(30, 100));
		pogovori.setLayout(new GridBagLayout());
		/*
		 * To je podokno za skupni pogovor.
		 */
		//this.skupniPogovor = new JPanel();
		//skupniPogovor.setMinimumSize(new Dimension(30, 100));
		//skupniPogovor.setLayout(new GridBagLayout());
		
		/*
		 * To je podokno za zasebni pogovor.
		 */
		//this.zasebniPogovor = new JPanel();
		//zasebniPogovor.setMinimumSize(new Dimension(30, 100));
		//zasebniPogovor.setLayout(new GridBagLayout());
		
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
		//this.razdeliPogovor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, skupniPogovor, zasebniPogovor);
		//razdeliPogovor.setOneTouchExpandable(true);
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
		//pogovori.add(razdeliPogovor);
		
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
		   
		this.prikazi = new JButton("Prika�i");
		vzdevekVrstica.add(prikazi);
		prikazi.addActionListener(this);
		
		/*
		 * Elementi v uporabnikih
		 */
		/*
		this.izpisUporabnikov = new JTextArea(20, 20);
		this.izpisUporabnikov.setEditable(false);
		uporabniki.add(izpisUporabnikov);
		izpisiUporabnike(Komunikacija.vpisaniUporabniki());
		*/

		this.trenutniUporabniki = new HashSet<String>();
		
		this.izpisUporabnikov = new JTabbedPane();
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
		pogovori.add(pogovorSkupni, pogovorSkupniConstraint);
		
		this.inputSkupni = new JTextField(40);
		GridBagConstraints pisanjeSkupniConstraint = new GridBagConstraints();
		pisanjeSkupniConstraint.gridx = 0;
		pisanjeSkupniConstraint.gridy = 3;
		pisanjeSkupniConstraint.fill = 2;
		pisanjeSkupniConstraint.weightx = 1;
		pisanjeSkupniConstraint.weighty = 1;
		pogovori.add(inputSkupni, pisanjeSkupniConstraint);
		inputSkupni.addKeyListener(this);
	    addWindowListener(this);

	    /*
	     * Elementi zasebnega pogovora.
	     */
	    /*
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
	    */

	    /*
	     * Elementi prikaza uporabnikov
	     */
		// TODO: uporabni�ko okno
	    
	    
		this.prijavljen = false;
		this.prejsnji = new String();
	}
	
	/*
	 * Ta metoda sporo�ilo izpi�e na zaslon, �e gre za na�e sporo�ilo, ga po�lje naprej.
	 */
	public void izpisiSporocilo(Sporocilo sporocilo, JTextArea output) {
		String posiljatelj = sporocilo.getSender();
		String chat = output.getText();
		
		//To se izvede, �e sporo�ilo po�ilja neprijavljeni uporabnik:
		if (!vzdevek.getText().equals(this.prejsnji) && !posiljatelj.equals("Sistem")) {
			//System.out.println("vzdevek je " + vzdevek.getText() + ", prej�nji pa " + this.prejsnji);
			Sporocilo obvestilo = new Sporocilo();
			obvestilo.setSender("Sistem");
			obvestilo.setText("Uporabnik " + vzdevek.getText() + " ni prijavljen.");
			izpisiSporocilo(obvestilo, output);
		}else{
			output.setText(chat + posiljatelj + ": " + sporocilo.getText() + "\n");
			if (posiljatelj == vzdevek.getText()) {
				sporocilo.setSender(posiljatelj);
				Komunikacija.posljiSporocilo(sporocilo);
			}
		}
		
		
	}

	/*
     * Ta metoda izpi�e na zaslon vsa sporo�ila v seznamu.
	 */
	public void izpisiSporocilo(List<Sporocilo> novaSporocila) {
		for (Sporocilo sporocilo : novaSporocila) {
			if (sporocilo.getGlobal()) {
				izpisiSporocilo(sporocilo, this.outputSkupni);
			}else {
				izpisiSporocilo(sporocilo, this.outputZasebni);
			}
		}

	}
	
	/*
	 * funkcija pregleda trenutne uporabnike in nove doda med zavihke za zasebni pogovor,
	 * hkrati pa tiste ki se odjavijo odstrani iz zavihkov. 
	 */
	public void izpisiUporabnike(List<Uporabnik> uporabniki) {
		Set<String> zacasniTrenutniUporabniki = new HashSet<String>();
		
		for (Uporabnik nekdo : uporabniki) {
			String trenutnoIme = nekdo.getUsername(); 
			zacasniTrenutniUporabniki.add(trenutnoIme);
			if (!this.trenutniUporabniki.contains(trenutnoIme) 
					&& !trenutnoIme.equals(vzdevek.getText())) {
				this.trenutniUporabniki.add(trenutnoIme);
				this.izpisUporabnikov.addTab(trenutnoIme, inputZasebni);
			}
		}
		
		this.trenutniUporabniki.removeAll(zacasniTrenutniUporabniki);
		for (String ime : this.trenutniUporabniki) {
			if (!ime.equals(vzdevek.getText()) && !ime.equals(this.prejsnji)){
			this.izpisUporabnikov.removeTabAt(this.izpisUporabnikov.indexOfTab(ime));
			}
		}
		this.trenutniUporabniki = new HashSet<String>(zacasniTrenutniUporabniki);
		//System.out.println(trenutniUporabniki);
	}
	
	
	public String getPrejsnji() {
		return this.prejsnji;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Sporocilo obvestilo = new Sporocilo(true, ""); // izpisalo se bo obvestilo, kaj se je zgodilo. 
		obvestilo.setSender("Sistem");
		obvestilo.setGlobal(true);
		String ime = vzdevek.getText();
		
		if (e.getSource() == prijavniGumb) {
			try{
				if (this.prijavljen) {
					robot.deaktiviraj();
					Komunikacija.odjaviSe(this.prejsnji);
				}

			this.robot = new Robot(this);
			Komunikacija.logirajSe(ime);
			this.prejsnji = (ime);
			this.prijavljen = true;
			this.izpisUporabnikov.removeAll();
			obvestilo.setText("Prijava " + ime + " je uspela.");
			robot.aktiviraj();
			izpisiSporocilo(obvestilo, this.outputSkupni);
			this.trenutniUporabniki = new HashSet<String>();
			} catch (Exception ef) {
				ef.printStackTrace();
				obvestilo.setText("Uporabnik " + ime + " je �e prijavljen.");
				izpisiSporocilo(obvestilo, this.outputSkupni);
				}
			
		}
		
		if (e.getSource() == odjavniGumb) {
			robot.deaktiviraj();
			Komunikacija.odjaviSe(ime);
			obvestilo.setText("Odjava " + ime +" je uspela.");
			izpisiSporocilo(obvestilo, this.outputSkupni);
			this.prijavljen = false;
			this.izpisUporabnikov.removeAll();
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
				Sporocilo sporocilo = new Sporocilo(true, this.inputSkupni.getText());
				sporocilo.setSender(vzdevek.getText());
				this.izpisiSporocilo(sporocilo, this.outputSkupni);
				Komunikacija.posljiSporocilo(sporocilo);
				this.inputSkupni.setText("");
			}
		}
		
		if (e.getSource() == this.inputZasebni) {
			if (e.getKeyChar() == '\n') {
				Sporocilo sporocilo = new Sporocilo(false, this.inputZasebni.getText());
				sporocilo.setSender(vzdevek.getText());
				sporocilo.setRecipient("Nina");
				this.izpisiSporocilo(sporocilo, outputZasebni);
				Komunikacija.posljiSporocilo(sporocilo);
				this.inputZasebni.setText("");
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
	 * �e uporabnik zapre okno brez odjave, se odjavi samodejno.
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
