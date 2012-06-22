package tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
	public static void main(String[] args) throws Exception {
		try {
			if (args.length != 2) {
				System.err.println("Usage: lab4-part1-client <host> <port>");
				System.exit(1);
			}
			
			Socket clientSocket = new Socket(args[0], Integer.parseInt(args[1]));
			
			OutputStream out = clientSocket.getOutputStream();
			
			int character;
			while ((character = System.in.read()) != -1)
				out.write(character);
			
			clientSocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
