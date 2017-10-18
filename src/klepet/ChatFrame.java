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
import java.io.IOException;
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
	private JPanel okvirPrivatnihPogovorov;
	private Robot robot;
	private Boolean prijavljen; // ali je trenutno v tem oknu kdo prijavljen
	private String prejsnji; // hrani zadnjega uporabnika, ki se je  prijavil v tem oknu.
	private JTextField inputZasebni; 
	private JSplitPane razdeliVrstico;  // okvir v katerem loèimo vrstico od ostalih elementov
	private JSplitPane razdeliPogovor;  // okvir v katerem loèimo javni pogovor od zasebnih
	private JTabbedPane privatniPogovori;  // okvir za zavihke zasebnih pogovorov
	private Set<String> trenutniUporabniki; // vsebuje imena vseh trenutnih uporabnikov
	private Map<String, Uporabnik> objektiUporabnikov;  // slovar, ki imena uporabnikov povezuje z njihovimi objekti (preko indeksov)
	private JTextArea niUporabnikov;
	private boolean prikazanNiUporabnikov;
	// ------------------------------------------------------------------------------------------------

	public ChatFrame() {
		super();
		Container pane = this.getContentPane();  // naredimo osnovno plošèo 

		/*
		 * Razdelimo naše okno na tri dele: 
		 * - najprej navpièno na vrstico z vzdevkom in gumbi in vse ostalo
		 * - to potem na zasebne in javni pogovor
		 * 
		 * TODO: raztegovanje okna!!!
		 * 
		 * Najprej moramo sestaviti vse te tri komponente:
		 * 
		 * To je vrstica za vzdevek in gumbe:
		 */

		JPanel vzdevekVrstica = new JPanel();  
		vzdevekVrstica.setLayout(new FlowLayout(FlowLayout.LEFT));

		/*
		 * To je okno za vse razen vrstice z vzdevki
		 */
		JPanel pogovori = new JPanel();

		/*
		 * Okno z javnim pogovorom
		 */
		JPanel javniPogovor = new JPanel();
		//javniPogovor.setMinimumSize(new Dimension(30, 100));
		javniPogovor.setLayout(new GridBagLayout());

		/*
		 * To je podokno za prikaz možnih privatnih pogovorov
		 */
		this.okvirPrivatnihPogovorov = new JPanel();
		//okvirPrivatnihPogovorov.setMinimumSize(new Dimension(30,40));
		okvirPrivatnihPogovorov.setLayout(new GridBagLayout());

		/*
		 * Razdeljevanje okna:
		 */

		this.razdeliVrstico = new JSplitPane(JSplitPane.VERTICAL_SPLIT, vzdevekVrstica, pogovori);
		this.razdeliPogovor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, javniPogovor, okvirPrivatnihPogovorov);
		razdeliPogovor.setOneTouchExpandable(true);

		pane.add(razdeliVrstico);
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
		okvirPrivatnihPogovorov.add(privatniPogovori, privatniPogovorConstraint);
		niUporabnikov = new JTextArea(18,40);
		niUporabnikov.setEditable(false);
		privatniPogovori.addTab("ni prijavljenih uporabnikov", niUporabnikov);
		prikazanNiUporabnikov = true;

		this.inputZasebni = new JTextField(40);
		GridBagConstraints privatniInputConstraint = new GridBagConstraints();
		privatniInputConstraint.gridx = 0;
		privatniInputConstraint.gridy = 1;
		privatniInputConstraint.fill = 2;
		privatniInputConstraint.weightx = 0;
		privatniInputConstraint.weighty = 1;
		okvirPrivatnihPogovorov.add(inputZasebni, privatniInputConstraint);
		inputZasebni.addKeyListener(this);
		addWindowListener(this);

		/*
		 * Elementi skupnega pogovora
		 */
		this.outputSkupni = new JTextArea(20, 35);
		this.outputSkupni.setEditable(false);
		GridBagConstraints pogovorSkupniConstraint = new GridBagConstraints();
		pogovorSkupniConstraint.gridx = 0;
		pogovorSkupniConstraint.gridy = 0;
		pogovorSkupniConstraint.fill = 1;
		pogovorSkupniConstraint.weightx = 1;
		pogovorSkupniConstraint.weighty = 1;
		JScrollPane pogovorSkupni = new JScrollPane(outputSkupni);
		javniPogovor.add(pogovorSkupni, pogovorSkupniConstraint);

		this.inputSkupni = new JTextField(40);
		//this.inputSkupni.setMinimumSize(new Dimension(1,40));
		GridBagConstraints pisanjeSkupniConstraint = new GridBagConstraints();
		pisanjeSkupniConstraint.gridx = 0;
		pisanjeSkupniConstraint.gridy = 3;
		pisanjeSkupniConstraint.fill = 2;
		pisanjeSkupniConstraint.weightx = 1;
		pisanjeSkupniConstraint.weighty = 0;
		javniPogovor.add(inputSkupni, pisanjeSkupniConstraint);
		inputSkupni.addKeyListener(this);
		addWindowListener(this);

		this.prijavljen = false;
		this.prejsnji = new String();
		this.trenutniUporabniki = new HashSet<String>();
		this.objektiUporabnikov = new HashMap<String, Uporabnik>();
	}

	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * --------------------------             METODE                ---------------------------- 
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	 
	 */

	/*
	 * Ta metoda sporoèilo izpiše na zaslon.
	 */
	public void izpisiSporocilo(Sporocilo sporocilo, JTextArea output) {
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
				Uporabnik posiljatelj = this.objektiUporabnikov.get(imePosiljatelja);
				izpisiSporocilo(sporocilo, posiljatelj.getOutput());
			}
		}
	}

	/*
	 * funkcija pregleda trenutne uporabnike in nove doda med zavihke za zasebni pogovor,
	 * hkrati pa tiste, ki se odjavijo, odstrani iz evidenc. 
	 */
	public void izpisiUporabnike(List<Uporabnik> uporabniki) {
		Set<String> zacasniTrenutniUporabniki = new HashSet<String>();
		for (Uporabnik nekdo : uporabniki) {
			String trenutnoIme = nekdo.getUsername(); 
			zacasniTrenutniUporabniki.add(trenutnoIme);
			if (!this.trenutniUporabniki.contains(trenutnoIme) 
					&& !trenutnoIme.equals(vzdevek.getText())
					&& !trenutnoIme.equals(this.prejsnji)) {  //s samim sabo se ne želim pogovarjati
				dodajZavihek(nekdo);
			}
		}
		// zacasniTrenutniUporabniki - vsi trenutni uporabniki
		// trenutniUporabniki - uporabniki v prejšnji sekundi
		this.trenutniUporabniki.removeAll(zacasniTrenutniUporabniki);
		this.trenutniUporabniki.remove(vzdevek.getText());
		this.trenutniUporabniki.remove(this.prejsnji);
		for (String ime : this.trenutniUporabniki) {
			odstraniZavihek(ime); 
		}
		this.trenutniUporabniki = new HashSet<String>(zacasniTrenutniUporabniki);
		this.trenutniUporabniki.remove(this.prejsnji);
		if (!prikazanNiUporabnikov && this.trenutniUporabniki.equals(new HashSet<String>())) {
			privatniPogovori.addTab("ni prijavljenih uporabnikov", niUporabnikov);
			prikazanNiUporabnikov = true;
		}else {
			if (prikazanNiUporabnikov && !this.trenutniUporabniki.equals(new HashSet<String>())) {
				privatniPogovori.removeTabAt(0);
				prikazanNiUporabnikov = false;
			}
		}
	}

	/*
	 * Ta metoda nam naredi zavihek za novega uporabnika, 
	 * hkrati pa si še zapomni njegov output, 
	 * da lahko kasneje sporoèila pišemo v prava okna.
	 */
	public void dodajZavihek(Uporabnik nekdo) {
		nekdo.setOutput();
		String ime = nekdo.getUsername();
		this.objektiUporabnikov.put(ime, nekdo);
		JScrollPane scrollPane = new JScrollPane(nekdo.getOutput());
		this.privatniPogovori.addTab(ime, scrollPane);
	}

	/*
	 * Odstrani zavihek in pozabi uporabnika 
	 * (èe se prijavi nov uporabnik z istim imenom, želimo z njim imeti nov pogovor)
	 */
	private void odstraniZavihek(String ime) {
		this.privatniPogovori.removeTabAt(this.privatniPogovori.indexOfTab(ime));
		this.objektiUporabnikov.remove(ime);  
	}

	/*
	 * Poskrbi za odjavo - robot, prikaz uporabnikov, komunikacija s strežnikom
	 */
	private void odjaviSe() {
		robot.deaktiviraj();
		Komunikacija.odjaviSe(this.prejsnji);
		this.prijavljen = false;
		privatniPogovori.removeAll();
		trenutniUporabniki = new HashSet<String>();
		privatniPogovori.addTab("ni prijavljenih uporabnikov", niUporabnikov);
		prikazanNiUporabnikov = true;
	}

	/*
	 * poskrbi za prijavo - robot, prikaz uporabnikov, komunikacija s strežnikom
	 */
	private void prijaviSe(String ime) throws IOException, IOException {
		this.robot = new Robot(this);
		Komunikacija.logirajSe(ime);
		this.prejsnji = ime;
		this.prijavljen = true;
		robot.aktiviraj();
	}

	public String getPrejsnji() {
		return this.prejsnji;
	}

	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * -----------------  ODZIVI NA DOGODKE   -----------------------
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */

	@Override
	public void actionPerformed(ActionEvent e) {
		// pripravimo sporoèilo, ki bo obvestilo uporabnika, kaj se je zgodilo, èe je to potrebno.
		Sporocilo obvestilo = new Sporocilo(true, "");  
		obvestilo.setSender("Sistem");
		obvestilo.setGlobal(true);
		String ime = vzdevek.getText();

		/*
		 * klik gumba Prijavi
		 */
		if (e.getSource() == prijavniGumb) {
			try{
				if (this.prijavljen) {
					odjaviSe();
				}
				prijaviSe(ime);
				obvestilo.setText("Prijava " + ime + " je uspela.");
				izpisiSporocilo(obvestilo, this.outputSkupni);
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
			odjaviSe();
			obvestilo.setText("Odjava " + ime +" je uspela.");
			izpisiSporocilo(obvestilo, this.outputSkupni);	
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
				if (!this.inputSkupni.getText().equals("")) {
					Sporocilo sporocilo = new Sporocilo(true, this.inputSkupni.getText());
					sporocilo.setSender(vzdevek.getText());
					this.izpisiSporocilo(sporocilo, this.outputSkupni);
					Komunikacija.posljiSporocilo(sporocilo);
					this.inputSkupni.setText("");
				}
			}
		}

		if (e.getSource() == this.inputZasebni) {
			if (e.getKeyChar() == '\n') {
				/*
				 * Pritisnjena tipka je Enter v zasebni pogovor - sproži izpis sporoèila in pošiljanje.
				 */
				if (!prikazanNiUporabnikov && !this.inputZasebni.getText().equals("")) {
					Sporocilo sporocilo = new Sporocilo(false, this.inputZasebni.getText());
					sporocilo.setSender(vzdevek.getText());

					Integer indeksAktivnegaZavihka = privatniPogovori.getSelectedIndex();
					String imePrejemnika = privatniPogovori.getTitleAt(indeksAktivnegaZavihka);
					Uporabnik prejemnik = objektiUporabnikov.get(imePrejemnika);

					sporocilo.setRecipient(imePrejemnika);
					this.izpisiSporocilo(sporocilo, prejemnik.getOutput());
					Komunikacija.posljiSporocilo(sporocilo);
					this.inputZasebni.setText("");
				}
			}
		}
	}

	/*
	 * Èe uporabnik zapre okno brez odjave, ga program odjavi samodejno.
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		if (this.prijavljen) {
			Komunikacija.odjaviSe(this.prejsnji);
			robot.deaktiviraj();
			this.prijavljen = false;
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		vzdevek.requestFocusInWindow();
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
}
