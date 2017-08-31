package klepet;

//  Ta robot nam bo preverjal, èe je kaj novih sporoèil na strežniku.
//  To mora sporoèit chatframu? ali Chit chat?

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
		timer = new Timer();
		timer.scheduleAtFixedRate(this, 0, 1000);
	}
	
	public void deaktiviraj() {
		timer.cancel();	
	}

	@Override
	public void run() {
		String novaSporocila = komunikacija.sporocila(chat.vzdevek.getText());
		
	}
	
	
}
