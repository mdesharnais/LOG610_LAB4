package dns;

/******************************************************
 Laboratoire #3 : Programmation d'un serveur DNS
 
 Cours :             LOG610
 Session :           Hiver 2007
 Groupe :            01
 Projet :            Laboratoire #3
 �tudiant(e)(s) :    Maxime Bouchard
 Code(s) perm. :     BOUM24028309
 
 Professeur :        Michel Lavoie 
 Nom du fichier :    AnswerRecorder.java
 Date cr�e :         2007-03-10
 Date dern. modif.   X
 *******************************************************/
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/***
 * Cette classe est utilis� pour enregistrer une r�ponse
 * dans le fichier texte en provenance d'un Server DNS autre.
 * @author Max
 *
 */

public class AnswerRecorder extends DataStorageAccess {

	private FileWriter writerFichierSource = null;
	private String adresseIP = null;
	private String hostname = null;
	private String filename = null;
	private String uneligne = null;
	
	/**
	 * Construteur par default
	 *
	 */
	public AnswerRecorder(){
		
	}
	
	/**
	 * Constructeur
	 * @param filename
	 * @param hostname
	 * @param adresseIP
	 */
	public AnswerRecorder(String filename,String hostname,String adresseIP){
		this.adresseIP = adresseIP;
		this.hostname = hostname;
		this.filename = filename;
	}
	
	/**
	 * Construteur
	 * @param filename
	 */
	public AnswerRecorder(String filename){
		this.filename = filename;
	}
	
	public void StartRecord(String hostname,String adresseIP){

		try {
			writerFichierSource = new FileWriter(filename,true);
			writerFichierSource.write(hostname + " " + adresseIP);
			writerFichierSource.write("\r\n");
			writerFichierSource.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
