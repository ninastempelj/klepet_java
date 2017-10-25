package klepet;

import java.awt.Color;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
public class ChatFrame extends JFrame 
				implements ActionListener, KeyListener, WindowListener {


	private JTextArea outputJavni; //prikaz sporoèil
	private JTextField inputJavni; // pisanje sporoèil
	private JLabel napisVzdevek;     
	public JTextField vzdevek;  // okno v katerega napišeš vzdevek
	private JButton prijavniGumb;
	private JButton odjavniGumb;
	private JPanel zasebniPogovor;
	private Robot robot;
	private Boolean prijavljen; // ali je trenutno v tem oknu kdo prijavljen
	private String prejsnji; // hrani zadnjega prijavljenega v tem oknu.
	private JTextField inputZasebni; 
	private JSplitPane pogovori;  
	private JTabbedPane outputZasebni;  // okvir za zavihke zasebnih pogovorov
	private Set<String> trenutniUporabniki; // imena vseh trenutnih uporabnikov
	// Imena uporabnikov povezuje z njihovimi objekti:
	private Map<String, Uporabnik> objektiUporabnikov;
	//private Set<String> hranjeniPogovori; // zavihki odjavljenih uporabnikov
	private JTextArea niUporabnikov; // zavihek, ko ni prijavljenih uporabnikov
	private boolean prikazanNiUporabnikov;
	private Color siva = new Color(238, 238, 238);
	// ------------------------------------------------------------------------

	// TODO: Barvanje uporabnikov
	public ChatFrame() {
		super();
		Container pane = this.getContentPane();  // shranimo osnovno plošèo
		pane.setLayout(new GridBagLayout());
		
		/*
		 * Razdelimo naše okno na tri dele: 
		 * - najprej na vrhu naredimo vrstico z vzdevkom in gumbi
		 * - preostanek razdelimo na zasebne in javni pogovor
		 * 
		 * To je vrstica za vzdevek in gumbe:
		 */
		JPanel vzdevekVrstica = new JPanel();  
		vzdevekVrstica.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		GridBagConstraints vrsticaCon = new GridBagConstraints();
		vrsticaCon.gridx = 0;
		vrsticaCon.gridy = 0;
		vrsticaCon.fill = 1;
		vrsticaCon.weightx = 1;
		vrsticaCon.weighty = 0;
		
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
		
		pane.add(vzdevekVrstica, vrsticaCon);
		
		/*
		 * komponenta javnega pogovora
		 */
		JPanel javniPogovor = new JPanel();
		javniPogovor.setLayout(new GridBagLayout());
		
		this.outputJavni = new JTextArea(20, 28);
		this.outputJavni.setEditable(false);
		GridBagConstraints pogovorSkupniCon = new GridBagConstraints();
		pogovorSkupniCon.gridx = 0;
		pogovorSkupniCon.gridy = 0;
		pogovorSkupniCon.fill = 1;
		pogovorSkupniCon.weightx = 0.5;
		pogovorSkupniCon.weighty = 1;
		JScrollPane pogovorSkupni = new JScrollPane(outputJavni);
		javniPogovor.add(pogovorSkupni, pogovorSkupniCon);

		this.inputJavni = new JTextField(28);
		GridBagConstraints pisanjeSkupniCon = new GridBagConstraints();
		pisanjeSkupniCon.gridx = 0;
		pisanjeSkupniCon.gridy = 3;
		pisanjeSkupniCon.fill = 1;
		pisanjeSkupniCon.weightx = 0.5;
		pisanjeSkupniCon.weighty = 0;
		javniPogovor.add(this.inputJavni, pisanjeSkupniCon);
		this.inputJavni.addKeyListener(this);
		addWindowListener(this);
		
		/*
		 * To je komponenta za prikaz možnih zasebnih pogovorov:
		 * na zaèetku je odprt zavihek ni prijavljenih, 
		 * ko se uporabnik prijavi tu potekajo zasebni pogovori.
		 */
		this.zasebniPogovor = new JPanel();
		zasebniPogovor.setLayout(new GridBagLayout());

		this.outputZasebni = new JTabbedPane();
		GridBagConstraints privatniPogovorCon = new GridBagConstraints();
		privatniPogovorCon.gridx = 0;
		privatniPogovorCon.gridy = 0;
		privatniPogovorCon.fill = 1;
		privatniPogovorCon.weightx = 0.5;
		privatniPogovorCon.weighty = 1;
		zasebniPogovor.add(this.outputZasebni, privatniPogovorCon);
		this.outputZasebni.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
                if (e.getSource() instanceof JTabbedPane) {
                    JTabbedPane output = (JTabbedPane) e.getSource();
                    output.setBackgroundAt(output.getSelectedIndex(), siva);
                }
            }
		});

		this.inputZasebni = new JTextField(40);
		GridBagConstraints privatniInputCon = new GridBagConstraints();
		privatniInputCon.gridx = 0;
		privatniInputCon.gridy = 1;
		privatniInputCon.fill = 1;
		privatniInputCon.weightx = 0.5;
		privatniInputCon.weighty = 0;
		zasebniPogovor.add(inputZasebni, privatniInputCon);
		inputZasebni.addKeyListener(this);
		addWindowListener(this);

		/*
		 * Razdeljevanje pogovorov:
		 */
		this.pogovori = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				javniPogovor, zasebniPogovor);
		GridBagConstraints pogovoriCon = new GridBagConstraints();
		pogovoriCon.gridx = 0;
		pogovoriCon.gridy = 1;
		pogovoriCon.fill = 1;
		pogovoriCon.weightx = 1;
		pogovoriCon.weighty = 1;
		
		pogovori.setOneTouchExpandable(true);
		pane.add(pogovori, pogovoriCon);

		// zaèetna dejanja:
		this.niUporabnikov = new JTextArea(18,40);
		this.niUporabnikov.setEditable(false);
		this.outputZasebni.addTab("ni prijavljenih uporabnikov", 
				this.niUporabnikov);
		this.prikazanNiUporabnikov = true;
		this.prijavljen = false;
		this.prejsnji = new String();
		this.trenutniUporabniki = new HashSet<String>();
		this.objektiUporabnikov = new HashMap<String, Uporabnik>();
		
	}

	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * ------------------          METODE             ------------------- 
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	 
	 */

	/*
	 * Ta metoda sporoèilo izpiše na zaslon.
	 */
	// TODO: drugaèen font za moja in tuja
	// TODO: èas poslanega sporoèila
	// TODO: zvok ob novem sporoèilu 

	public void izpisiSporocilo(Sporocilo sporocilo, JTextArea output) {
		String posiljatelj = sporocilo.getSender();
		String jaz = vzdevek.getText();
		String chat = output.getText();

		//To se izvede, èe se uporabnik še ni prijavil:
		if (!prijavljen && !posiljatelj.equals("Sistem")) {
			Sporocilo obvestilo = new Sporocilo();
			obvestilo.setSender("Sistem");
			obvestilo.setText("Uporabnik " + jaz + " ni prijavljen.");
			izpisiSporocilo(obvestilo, output);
		}else{
			output.setText(chat + 
					posiljatelj + ": " + sporocilo.getText() + "\n");
			output.setCaretPosition(output.getDocument().getLength());
		}
		if (!posiljatelj.equals(jaz) && !posiljatelj.equals("Sistem")) {
			Integer index = this.outputZasebni.indexOfTab(posiljatelj);
			if (! index.equals(this.outputZasebni.getSelectedIndex())) {
				this.outputZasebni.setBackgroundAt(index, 
					objektiUporabnikov.get(posiljatelj).getBarva());
			}
		}
	}

	/*
	 * Ta metoda izpiše na zaslon vsa sporoèila v seznamu.
	 */
	public void izpisiSporocilo(List<Sporocilo> novaSporocila) {
		for (Sporocilo sporocilo : novaSporocila) {
			if (sporocilo.getGlobal()) {
				izpisiSporocilo(sporocilo, this.outputJavni);
			}else {
				String imePosiljatelja = sporocilo.getSender();
				Uporabnik posiljatelj = this.objektiUporabnikov.get(
						imePosiljatelja);
				izpisiSporocilo(sporocilo, posiljatelj.getOutput());
			}
		}
	}

	/*
	 * funkcija pregleda trenutne uporabnike 
	 * in nove doda med zavihke za zasebni pogovor,
	 * hkrati pa tiste, ki se odjavijo, odstrani iz evidenc. 
	 */
	public void izpisiUporabnike(List<Uporabnik> uporabniki) {
		Set<String> prejsnjiUporabniki = new HashSet<String>(
				this.trenutniUporabniki); 
		this.trenutniUporabniki = new HashSet<String>();
		for (Uporabnik nekdo : uporabniki) {
			String trenutnoIme = nekdo.getUsername(); 
			this.trenutniUporabniki.add(trenutnoIme);
			if (!prejsnjiUporabniki.contains(trenutnoIme) 
					&& !trenutnoIme.equals(this.vzdevek.getText())
					&& !trenutnoIme.equals(this.prejsnji)) {  
				dodajZavihek(nekdo);
			}
		}
		prejsnjiUporabniki.removeAll(this.trenutniUporabniki);
		for (String ime : prejsnjiUporabniki) {
			odjavljenUporabnik(ime); 
		}
		this.trenutniUporabniki.remove(this.prejsnji);
		if (!prikazanNiUporabnikov 
				&& this.trenutniUporabniki.equals(new HashSet<String>())) {
			outputZasebni.addTab("ni prijavljenih uporabnikov", niUporabnikov);
			prikazanNiUporabnikov = true;
		}else {
			if (prikazanNiUporabnikov && 
					!this.trenutniUporabniki.equals(new HashSet<String>())) {
				outputZasebni.removeTabAt(0);
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
		// TODO: ko se odjavi, naj ostane pogovor, èe je bilo kaj vsebine
		nekdo.setOutput();
		String ime = nekdo.getUsername();
		this.objektiUporabnikov.put(ime, nekdo);
		JScrollPane scrollPane = new JScrollPane(nekdo.getOutput());
		this.outputZasebni.addTab(ime, scrollPane);
	}

	/*
	 * Odstrani zavihek in pozabi uporabnika 
	 * (èe se prijavi nov uporabnik z istim imenom, 
	 * želimo z njim imeti nov pogovor)
	 */
	private void odstraniZavihek(String ime) {
		this.outputZasebni.removeTabAt(this.outputZasebni.indexOfTab(ime));
		this.objektiUporabnikov.remove(ime);  
	}

	/*
	 * Èe smo z uporabnikom zaèeli pogovor, ga ohrani,
	 * dokler se ne prijavi nekdo z istim imenom, potem ga zaène na novo, 
	 * saj je lahko to druga oseba.
	 */
	private void odjavljenUporabnik(String ime) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Poskrbi za odjavo: robot, prikaz uporabnikov, komunikacija s strežnikom
	 */
	private void odjaviSe() {
		robot.deaktiviraj();
		Komunikacija.odjaviSe(this.prejsnji);
		this.prijavljen = false;
		outputZasebni.removeAll();
		trenutniUporabniki = new HashSet<String>();
		outputZasebni.addTab("ni prijavljenih uporabnikov", niUporabnikov);
		prikazanNiUporabnikov = true;
	}

	/*
	 * Poskrbi za prijavo: robot, prikaz uporabnikov, komunikacija s strežnikom
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
		/*
		 *  Pripravimo sporoèilo, ki bo obvestilo uporabnika,
		 *  kaj se je zgodilo, èe je to potrebno. 
		 */
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
				izpisiSporocilo(obvestilo, this.outputJavni);
			} catch (Exception ef) {
				ef.printStackTrace();
				obvestilo.setText("Uporabnik " + ime + " je že prijavljen.");
				izpisiSporocilo(obvestilo, this.outputJavni);
			}
		}

		/*
		 * klik gumba Odjavi
		 */
		if (e.getSource() == odjavniGumb) {
			odjaviSe();
			obvestilo.setText("Odjava " + ime +" je uspela.");
			izpisiSporocilo(obvestilo, this.outputJavni);	
		}
	}

	/*
	 * Pritisk na tipko.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getSource() == this.inputJavni) {
			/*
			 * Pritisnjena tipka je Enter v skupni pogovor -
			 * sproži izpis sporoèila in pošiljanje.
			 */
			if (e.getKeyChar() == '\n') {
				if (!this.inputJavni.getText().equals("")) {
					Sporocilo sporocilo = new Sporocilo(true,
							this.inputJavni.getText());
					sporocilo.setSender(vzdevek.getText());
					this.izpisiSporocilo(sporocilo, this.outputJavni);
					Komunikacija.posljiSporocilo(sporocilo);
					this.inputJavni.setText("");
				}
			}
		}

		if (e.getSource() == this.inputZasebni) {
			if (e.getKeyChar() == '\n') {
				/*
				 * Pritisnjena tipka je Enter v zasebni pogovor -
				 * sproži izpis sporoèila in pošiljanje.
				 */
				if (!prikazanNiUporabnikov 
						&& !this.inputZasebni.getText().equals("")) {
					Sporocilo sporocilo = new Sporocilo(false, 
							this.inputZasebni.getText());
					sporocilo.setSender(vzdevek.getText());

					Integer indeksAktivnegaZavihka = 
							outputZasebni.getSelectedIndex();
					String imePrejemnika = 
							outputZasebni.getTitleAt(indeksAktivnegaZavihka);
					Uporabnik prejemnik = 
							objektiUporabnikov.get(imePrejemnika);

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
