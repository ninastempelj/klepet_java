package klepet;


import java.awt.Color;
import java.util.Random;
import javax.swing.JTextPane;

public class Uporabnik {
	private String last_active;
	private String username;
	private JTextPane output;
	private Color barva;
	private final Random rnd = new Random();

	public Uporabnik() {
		this.barva = new Color(rnd.nextInt());
	}

	public Uporabnik(String ime, String aktiven) {
		this.setUsername(ime);
		this.setLast_active(aktiven);
		this.barva = new Color(rnd.nextInt());
	}

	public Uporabnik(String ime, String aktiven, JTextPane output) {
		this.setUsername(ime);
		this.setLast_active(aktiven);
		this.setOutput(output);
		this.barva = new Color(rnd.nextInt());
	}

	public String getLast_active() {
		return last_active;
	}

	public void setLast_active(String last_active) {
		this.last_active = last_active;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public JTextPane getOutput() {
		return output;
	}

	public void setOutput(JTextPane output) {
		this.output = output;
	}

	public void setOutput() {
		this.output = new JTextPane();
		this.output.setEditable(false);
	}

	public Color getBarva() {
		return barva;
	}
}
