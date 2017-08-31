package klepet;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Preprost strežnik, ki sprejema TCP povezave in odgovarja
 * klientu tako, da ponavlja vse, kar mu klient pošlje.
 * Èe klient pošlje {@code "bye"}, ga strežnik odklopi.
 * 
 * @author Andrej Bauer
 *
 */
public class EchoServer {
	private static final int defaultEchoPort = 4444;

	public static void main(String[] args) {
		int echoPort = defaultEchoPort;
		if (args.length > 0) {
			echoPort = Integer.parseInt(args[0]);
		}
		ServerSocket listener = null;
		try {
			
			listener = new ServerSocket(echoPort);
			System.out.println("EchoServer listening on port " + echoPort);
			while (true) {
				Socket socket = listener.accept();
				EchoHandler handler = new EchoHandler(socket);
				System.out.println("Client connected on socket "  + socket);
				handler.start();
			}
		} catch (IOException exc) {
			System.out.println("Connection error: " + exc);
		} finally {
			if (listener != null) {
				try {
					listener.close();
				} catch (IOException exc) {
					System.out.println("Error on closing the listener: " + exc);
				}
			}
		}

	}

}
