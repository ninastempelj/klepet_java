package klepet;


//  Ta robot nam bo preverjal, èe je kaj novih sporoèil na strežniku.
//  To mora sporoèit chatframu

import java.util.Timer;
import java.util.TimerTask;


public class Robot extends TimerTask {
	private ChatFrame chat;
	private Timer timer;



	public Robot(ChatFrame chat) {
		this.chat = chat;
	}

	/**
	 * Activate the robot!
	 */
	public void aktiviraj() {
		this.timer = new Timer();
		this.timer.scheduleAtFixedRate(this, 0, 1000);
	}
	
	public void deaktiviraj() {
		
		this.timer.cancel();	
	}

	@Override
	public void run() {
		this.chat.izpisiSporocilo(Komunikacija.novaSporocila(chat.getPrejsnji()));
		this.chat.izpisiUporabnike(Komunikacija.vpisaniUporabniki());
	}
	
	
}
