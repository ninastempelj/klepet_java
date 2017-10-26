package klepet;

import java.awt.Dimension;

public class ChitChat {

	public static void main(String[] args) {
		ChatFrame chatFrame = new ChatFrame();
		chatFrame.pack();
		chatFrame.setMinimumSize(new Dimension(550, 350));
		chatFrame.setResizable(true);
		chatFrame.setVisible(true);
		chatFrame.setTitle("FMF pogovor");
	}

}
