package klepet;

public class Uporabnik {
	private String last_active;
	private String username;
	
	public Uporabnik() {
	}
	
	public Uporabnik(String ime, String aktiven) {
		this.setUsername(ime);
		this.setLast_active(aktiven);
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
	

}
