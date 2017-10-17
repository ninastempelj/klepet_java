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


	private JTextArea outputSkupni; //prikaz sporoèil
	private JTextField inputSkupni; // pisanje sporoèil
	private JLabel napisVzdevek;     
	public JTextField vzdevek;  // okno v katerega napišeš vzdevek
	private JButton prijavniGumb;
	private JButton odjavniGumb;
	//private JButton prikazi; // - pomozni gumb nekaj èasa
	//private JPanel skupniPogovor;
	//private JPanel zasebniPogovor;
	//private JSplitPane razdeliPogovor;
	private JPanel uporabniki;
	private Robot robot;
	private Boolean prijavljen;
	private String prejsnji; // hrani zadnjega uporabnika, ki se je  prijavil v tem oknu.
	private JTextField inputZasebni;
	private JTextArea outputZasebni;
	private JSplitPane razdeliVrstico;
	private JSplitPane razdeliUporabnik;
	private JTabbedPane privatniPogovori;  
	private Set<String> trenutniUporabniki; // vsebuje imena vseh trenutnih uporabnikov
	private List<Uporabnik> vsiUporabniki;  // objekti uporabnikov, ki so kadarkoli bili vpisani (skupaj z njihovimi inputi in outputi)
	private Map<String, Integer> indeksiUporabnikov;  // slovar, ki imena uporabnikov povezuje z njihovimi objekti (preko indeksov)
	private Integer stevecUporabnikov; // Šteje uporabnike, zato da dobivajo pravilne indekse

	// ------------------------------------------------------------------------------------------------

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
		 * To je okno za vse razen vrstice z vzdevki
		 */
		JPanel ostalo = new JPanel();

		/*
		 * Okno z vsemi pogovori
		 */
		JPanel pogovori = new JPanel();
		pogovori.setMinimumSize(new Dimension(30, 100));
		pogovori.setLayout(new GridBagLayout());

		/*
		 * To je podokno za prikaz prijavljenih uporabnikov in privatnih pogovorov
		 */
		this.uporabniki = new JPanel();
		uporabniki.setMinimumSize(new Dimension(30,40));
		//uporabniki.setMaximumSize(new Dimension(32,50));
		uporabniki.setLayout(new GridBagLayout());

		/*
		 * Razdeljevanje okna:
		 */

		this.razdeliVrstico = new JSplitPane(JSplitPane.VERTICAL_SPLIT, vzdevekVrstica, ostalo);
		this.razdeliUporabnik = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pogovori, uporabniki);
		razdeliUporabnik.setOneTouchExpandable(true);

		pane.add(razdeliVrstico);
		ostalo.add(razdeliUporabnik);


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

		//this.prikazi = new JButton("Prikaži");
		//vzdevekVrstica.add(prikazi);
		//prikazi.addActionListener(this);

		/*
		 * Elementi v uporabnikih - na zaèetku je okno prazno, èe ni nihèe prijavljen v pogovor
		 * sicer tu potekajo tudi zasebni pogovori
		 */

		this.privatniPogovori = new JTabbedPane();
		GridBagConstraints privatniPogovorConstraint = new GridBagConstraints();
		privatniPogovorConstraint.gridx = 0;
		privatniPogovorConstraint.gridy = 0;
		privatniPogovorConstraint.fill = 1;
		privatniPogovorConstraint.weightx = 1;
		privatniPogovorConstraint.weighty = 1;
		uporabniki.add(privatniPogovori, privatniPogovorConstraint);
		this.trenutniUporabniki = new HashSet<String>();
		this.stevecUporabnikov = 0;
		this.vsiUporabniki = new ArrayList<Uporabnik>();
		this.indeksiUporabnikov = new HashMap<String, Integer>();
		izpisiUporabnike(Komunikacija.vpisaniUporabniki());

		this.inputZasebni = new JTextField(40);
		GridBagConstraints privatniInputConstraint = new GridBagConstraints();
		privatniInputConstraint.gridx = 0;
		privatniInputConstraint.gridy = 1;
		privatniInputConstraint.fill = 2;
		privatniInputConstraint.weightx = 0;
		privatniInputConstraint.weighty = 1;
		uporabniki.add(inputZasebni, privatniInputConstraint);
		inputZasebni.addKeyListener(this);
		addWindowListener(this);


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
		this.inputSkupni.setMinimumSize(new Dimension(1,40));
		GridBagConstraints pisanjeSkupniConstraint = new GridBagConstraints();
		pisanjeSkupniConstraint.gridx = 0;
		pisanjeSkupniConstraint.gridy = 3;
		pisanjeSkupniConstraint.fill = 2;
		pisanjeSkupniConstraint.weightx = 1;
		pisanjeSkupniConstraint.weighty = 0;
		pogovori.add(inputSkupni, pisanjeSkupniConstraint);
		inputSkupni.addKeyListener(this);
		addWindowListener(this);



		this.prijavljen = false;
		this.prejsnji = new String();
	}

	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * --------------------------             METODE                ---------------------------- 
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	 
	 */

	/*
	 * Ta metoda sporoèilo izpiše na zaslon.
	 */
	public void izpisiSporocilo(Sporocilo sporocilo, JTextArea output) {
		if (!(sporocilo.getText().equals(""))) {
			String posiljatelj = sporocilo.getSender();
			String chat = output.getText();

			//To se izvede, èe sporoèilo pošilja neprijavljeni uporabnik (to smo lahko le mi):
			if (!vzdevek.getText().equals(this.prejsnji) && !posiljatelj.equals("Sistem")) {
				Sporocilo obvestilo = new Sporocilo();
				obvestilo.setSender("Sistem");
				obvestilo.setText("Uporabnik " + vzdevek.getText() + " ni prijavljen.");
				izpisiSporocilo(obvestilo, output);
			}else{
				output.setText(chat + posiljatelj + ": " + sporocilo.getText() + "\n");
				output.setCaretPosition(output.getDocument().getLength());
			}
		}else {
			System.out.println("ni vsebine");
		}
	}

	/*
	 * Ta metoda izpiše na zaslon vsa sporoèila v seznamu.
	 */

	public void izpisiSporocilo(List<Sporocilo> novaSporocila) {
		for (Sporocilo sporocilo : novaSporocila) {
			if (sporocilo.getGlobal()) {
				izpisiSporocilo(sporocilo, this.outputSkupni);
			}else {
				String imePosiljatelja = sporocilo.getSender();
				Uporabnik posiljatelj = this.vsiUporabniki.get(indeksiUporabnikov.get(imePosiljatelja));
				izpisiSporocilo(sporocilo, posiljatelj.getOutput());
			}
		}
	}

	/*
	 * funkcija pregleda trenutne uporabnike in nove doda med zavihke za zasebni pogovor,
	 * hkrati pa tiste, ki se odjavijo, odstrani iz zavihkov. 
	 */

	public void izpisiUporabnike(List<Uporabnik> uporabniki) {
		Set<String> zacasniTrenutniUporabniki = new HashSet<String>();

		for (Uporabnik nekdo : uporabniki) {
			String trenutnoIme = nekdo.getUsername(); 
			zacasniTrenutniUporabniki.add(trenutnoIme);
			if (!this.trenutniUporabniki.contains(trenutnoIme) 
					&& !trenutnoIme.equals(vzdevek.getText())) {  //s samim sabo se ne želimo pogovarjati
				this.trenutniUporabniki.add(trenutnoIme);
				dodajZavihek(nekdo);
			}
		}
		this.trenutniUporabniki.removeAll(zacasniTrenutniUporabniki);
		for (String ime : this.trenutniUporabniki) {
			if (!ime.equals(vzdevek.getText()) && !ime.equals(this.prejsnji)){ 
				this.privatniPogovori.removeTabAt(this.privatniPogovori.indexOfTab(ime));
			}
		}
		this.trenutniUporabniki = new HashSet<String>(zacasniTrenutniUporabniki);
	}


	public String getPrejsnji() {
		return this.prejsnji;
	}


	/*
	 * Ta metoda nam naredi zavihek za novega uporabnika, 
	 * hkrati pa si še zapomni njegova output in input, 
	 * da lahko kasneje sporoèila pišemo v prava okna.
	 */
	public void dodajZavihek(Uporabnik nekdo) {
		nekdo.setOutput();
		String ime = nekdo.getUsername();
		this.vsiUporabniki.add(nekdo);
		this.indeksiUporabnikov.put(ime, this.stevecUporabnikov);
		JScrollPane scrollPane = new JScrollPane(nekdo.getOutput());
		this.privatniPogovori.addTab(ime, scrollPane);
		this.stevecUporabnikov += 1;
	}


	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * -----------------  ODZIVI NA DOGODKE   -----------------------
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */

	@Override
	public void actionPerformed(ActionEvent e) {
		Sporocilo obvestilo = new Sporocilo(true, ""); // izpisalo se bo obvestilo, kaj se je zgodilo. 
		obvestilo.setSender("Sistem");
		obvestilo.setGlobal(true);
		String ime = vzdevek.getText();

		/*
		 * klik gumba Prijavi
		 */
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
				this.privatniPogovori.removeAll();
				obvestilo.setText("Prijava " + ime + " je uspela.");
				robot.aktiviraj();
				izpisiSporocilo(obvestilo, this.outputSkupni);
				this.trenutniUporabniki = new HashSet<String>();
			} catch (Exception ef) {
				ef.printStackTrace();
				obvestilo.setText("Uporabnik " + ime + " je že prijavljen.");
				izpisiSporocilo(obvestilo, this.outputSkupni);
			}

		}

		/*
		 * klik gumba Odjavi
		 */
		if (e.getSource() == odjavniGumb) {
			robot.deaktiviraj();
			Komunikacija.odjaviSe(ime);
			obvestilo.setText("Odjava " + ime +" je uspela.");
			izpisiSporocilo(obvestilo, this.outputSkupni);
			this.prijavljen = false;
			this.privatniPogovori.removeAll();
		}


	}
	/*
	 * Pritisk na tipko.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getSource() == this.inputSkupni) {
			/*
			 * Pritisnjena tipka je Enter v skupni pogovor - sproži izpis sporoèila in pošiljanje.
			 */
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
				/*
				 * Pritisnjena tipka je Enter v zasebni pogovor - sproži izpis sporoèila in pošiljanje.
				 */
				Sporocilo sporocilo = new Sporocilo(false, this.inputZasebni.getText());
				sporocilo.setSender(vzdevek.getText());

				Integer indeksAktivnegaZavihka = privatniPogovori.getSelectedIndex();
				String imePrejemnika = privatniPogovori.getTitleAt(indeksAktivnegaZavihka);
				Uporabnik prejemnik = vsiUporabniki.get(indeksiUporabnikov.get(imePrejemnika));

				System.out.println(imePrejemnika + " uporabnik " + prejemnik.getUsername());
				sporocilo.setRecipient(imePrejemnika);
				this.izpisiSporocilo(sporocilo, prejemnik.getOutput());
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
	 * Èe uporabnik zapre okno brez odjave, ga program odjavi samodejno.
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
