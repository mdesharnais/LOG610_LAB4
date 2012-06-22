package tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
	
	private ServerSocket server;
	private  Socket connectSocket;
	
	public Serveur(int port) throws IOException {
		server = new ServerSocket(port);
	}
	
	public void accepterConnexion() throws IOException {
		connectSocket = server.accept();
	}
	
	public void attendreEcriture() throws IOException {
		InputStream in = connectSocket.getInputStream();
		int caractere;
		while ( ( caractere = in.read() ) != -1 )
			System.out.print((char)caractere);
	}
	
	public void fermerConnexion() throws IOException {
		connectSocket.close();
	}
	
	public static void main(String[] args) {
		try {
			if(args.length != 1) {
				System.err.println("Usage: lab4-part1-serveur <port>");
				System.exit(1);
			}
			Serveur s = new Serveur(Integer.valueOf(args[0]));
			
			while(true) {
				s.accepterConnexion();
				s.attendreEcriture();
				s.fermerConnexion();
			}
			//server.close();		Boucle infinie pr�c�demment => server.close() non n�cessaire car jamais atteint.
		} catch (IOException e) { System.out.println(e); }
	}
	
}
