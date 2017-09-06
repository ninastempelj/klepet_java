package klepet;

import java.util.List;

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
		timer = new Timer();
		timer.scheduleAtFixedRate(this, 0, 1000);
	}
	
	public void deaktiviraj() {
		
		timer.cancel();	
	}

	@Override
	public void run() {
		List <Sporocilo> novaSporocila = Komunikacija.novaSporocila(chat.vzdevek.getText());
		//// "", "{}", "[]" "||" " " "{ }"
		//if (novaSporocila == "") {
		this.chat.izpisiSporocilo(novaSporocila);
		
		//}
		//String novo = novaSporocila + "[]";
		//char[] pretvarjamo = novo.toCharArray();
		//	System.out.println(novaSporocila == 
		//			Character.toString((char) 123) + Character.toString((char) 125));
		
		//System.out.println("Delam, delam, delam, delam kot zamorc " + chat.vzdevek.getText());
	}
	
	
}
