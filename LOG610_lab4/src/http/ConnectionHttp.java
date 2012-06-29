package http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.activation.MimetypesFileTypeMap;

public class ConnectionHttp implements Runnable {

	public ConnectionHttp(Socket socket) {
		this.socket = socket;
		System.out.println("Nouvelle connection sur le port " + socket.getPort() + ".");
	}
	
	@Override
	public void run() {
		try {
			String fichier = lireRequeteHttp();
			System.out.println("Le fichier demandé est \"" + fichier + "\"");
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
		File f = new File("./" + fichier);
		if(f.exists()) {
			System.out.println("Le fichier existe.");
			FileInputStream fis = new FileInputStream(f);
			byte[] b = "HTTP/1.1 200 OK\r\n".getBytes();
			out.write(b);
			b = "Server: MonServeur\r\n".getBytes();
			out.write(b);
			String text = "Content-Length: "+f.length()+"\r\n";
			b = text.getBytes();
			out.write(b);
			text = "Content-Type: "+ new MimetypesFileTypeMap().getContentType(f) +"\r\n";
			b = text.getBytes();
			out.write(b);
			text = "\r\n";
			b = text.getBytes();
			out.write(b);
			
			byte[] databytes = new byte[1024];
			int count;
			while ((count = fis.read(databytes)) != -1)
				out.write(databytes,0,count);
		}
		else {
			String fichierHtml = "<html><head><title>Erreur 404</title></head><body><h1>Erreur 404</h1></body></html>";
			
			System.out.println("Le fichier n'existe pas.");
			byte[] b = "HTTP/1.1 404 Not Found\r\n".getBytes();
			out.write(b);
			b = "Server: MonServeur\r\n".getBytes();
			out.write(b);
			String text = "Content-Length: "+fichierHtml.getBytes().length+"\r\n";
			b = text.getBytes();
			out.write(b);
			text = "Content-Type: text/html\r\n";
			b = text.getBytes();
			out.write(b);
			b = "\r\n".getBytes();
			out.write(b);
			
			out.write(fichierHtml.getBytes());
		}
	}
	
	public void fermerConnexion() throws IOException {
		System.out.println("Fermeture de la connexion.");
		socket.close();
	}

	private Socket socket;
}
