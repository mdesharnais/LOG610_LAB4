package http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHttp implements Runnable {

	public ConnectionHttp(Socket socket) {
		this.socket = socket;
		System.out.println("Nouvelle connection sur le port " + socket.getPort() + ".");
	}
	
	@Override
	public void run() {
		try {
			String fichier = lireRequeteHttp();
			envoyerFichier(fichier);
			fermerConnexion();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String lireLigne(InputStream in) throws IOException {
		String ligne = "";
		int caractere = 0;
		int dernierCaractere = 0;
		
		while ((caractere = in.read()) != -1 && !(dernierCaractere == '\r' && caractere == '\n')) {
			ligne += (char) caractere;
			dernierCaractere = caractere;
		}
		
		return ligne.substring(0, ligne.length() - 1);
	}
	
	public String lireRequeteHttp() throws IOException {
		InputStream in = socket.getInputStream();
		
		String ligne = lireLigne(in);
		String fichier = ligne.split(" ")[1];
		lireLigne(in);
		lireLigne(in);
		lireLigne(in);
		lireLigne(in);
		lireLigne(in);
		
		return fichier;
	}
	
	public void envoyerFichier(String fichier) throws IOException {
		OutputStream out = socket.getOutputStream();
		out.write('a');
	}
	
	public void fermerConnexion() throws IOException {
		socket.close();
	}

	private Socket socket;
}
