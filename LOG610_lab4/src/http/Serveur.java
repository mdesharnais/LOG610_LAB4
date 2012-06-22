package http;

import java.io.IOException;
import java.net.ServerSocket;

public class Serveur {
	
	private ServerSocket server;
	
	public Serveur(int port) throws IOException {
		server = new ServerSocket(port);
	}
	
	public void accepterConnexion() throws IOException {
		new Thread(new ConnectionHttp(server.accept())).start();
	}
	
	public static void main(String[] args) {
		try {
			/*
			if(args.length != 1) {
				System.err.println("Usage: lab4-part1-serveur <port>");
				System.exit(1);
			}
			Serveur s = new Serveur(Integer.valueOf(args[0]));
			*/
			
			Serveur s = new Serveur(55555);
			
			while(true) {
				s.accepterConnexion();
			}
			//server.close();		Boucle infinie precedemment => server.close() non necessaire car jamais atteint.
		} catch (IOException e) { System.out.println(e); }
	}
	
}
