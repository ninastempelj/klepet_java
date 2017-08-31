package klepet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ProverbClient {

	public static void main(String[] args) throws IOException {
		URL naslov = new URL("https://www.fmf.uni-lj.si/");
		URLConnection c = naslov.openConnection();
		BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
		String inputLine = r.readLine();
		while(inputLine != null) {
			System.out.println(inputLine);
			inputLine = r.readLine();
		}
	}

}

