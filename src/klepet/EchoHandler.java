package klepet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoHandler extends Thread {
	private Socket client;
	
	public EchoHandler(Socket socket) {
		this.client = socket;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					System.out.println("Client on socket " + client + " disconnected.");
					break;
				} else {
					if (line.trim().equals("quit")) {
						writer.println("bye bye");
						System.out.println("Client on socket " + client + " quit properly.");
						break;
					} else {
						writer.println(line);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Client on socket " + client + " quit unexpectedly.");
		}
		finally {
			try {
				client.close();
			} catch (IOException exc) {
				System.out.println("Client on socket " + client + " closed uncleanly.");
			}
		}
	}

}
