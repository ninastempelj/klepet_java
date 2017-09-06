package klepet;


public class Sporocilo {
	private String global;
	private String sender;
	private String text;
	private String recipient;
	private String sent_at;
	
	
	/*
	 * Ustvari prazno sporocilo.
	 */
	public Sporocilo() {
	}
	
	public Sporocilo(String globalno, String vsebina) {
		this.global = globalno;
		this.text = vsebina;
	}
	
	public Sporocilo(String globalno, String vsebina, String naslovnik) {
		this.global = globalno;
		this.text = vsebina;
		this.recipient = naslovnik;
	}
	
	public Sporocilo(String globalno, String naslovnik, String posiljatelj, 
			String vsebina, String sent_at) {
		this.global = globalno;
		this.text = vsebina;
		this.sender = posiljatelj;
		this.recipient = naslovnik;
		this.setSent_at(sent_at);
	}
	
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String naslovnik) {
		this.recipient = naslovnik;
	}
	public String getText() {
		return text;
	}
	public void setText(String vsebina) {
		this.text = vsebina;
	}
	public String getGlobal() {
		return global;
	}
	public void setGlobal(String globalno) {
		this.global = globalno;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String posiljatelj) {
		this.sender = posiljatelj;
	}

	public String getSent_at() {
		return sent_at;
	}

	public void setSent_at(String sent_at) {
		this.sent_at = sent_at;
	}
	
	

}