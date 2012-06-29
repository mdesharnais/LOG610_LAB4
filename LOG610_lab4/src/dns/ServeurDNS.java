package dns;

import java.io.File;
import java.io.IOException;

/******************************************************
 Laboratoire #3 : Programmation d'un serveur DNS
 
 Cours :             LOG610
 Session :           Hiver 2007
 Groupe :            01
 Projet :            Laboratoire #3
 �tudiant(e)(s) :    Maxime Bouchard
 Code(s) perm. :     BOUM24028309
 
 Professeur :        Michel Lavoie 
 Nom du fichier :    Application.java
 Date cr�e :         2007-03-10
 Date dern. modif.   X
 *******************************************************/
/**
 * Application principale qui lance les autres processus
 * @author Max
 *
 */
public class ServeurDNS {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//logo
		System.out.println("--------------------------------------");
		System.out.println("Serveur DNS");
		System.out.println("Ecole de Technologie Superieures (ETS)");
		System.out.println("LOG610 - R�seau de t�l�communication");
		System.out.println("R�alis� par Maxime Bouchard");
		System.out.println("--------------------------------------");
		
		if (args.length == 0) {
			System.out.println("Usage: "
					+"[addresse DNS] <Fichier DNS> <TrueFalse/Redirection seulement>");
			System.out.println("Pour lister la table: "
					+"showtable <Fichier DNS>");
			System.out.println("Pour lancer par defaut, tapper : default");
			System.exit(1);
		}
		
		QueryFinder QF = new QueryFinder();
		UDPReceiver UDPR = new UDPReceiver();
		File f = null;
		
		UDPR.setport(53);
		
		//Application des configurations par default
		if(args[0].equals("default")){
			if (args.length <= 1) {
			UDPR.setSERVER_DNS("10.162.8.51");
			f = new File("DNSFILE.TXT");
			if(f.exists()){
				UDPR.setDNSFile("DNSFILE.TXT");
			}
			else{
				try {
					f.createNewFile();
					UDPR.setDNSFile("DNSFILE.TXT");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			UDPR.setRedirectionSeulement(false);
			//lancement du thread
			UDPR.start();
			}
			else{
				System.out.print("Cette commande n'a pas d'autre argument");
			}
		}
                else{
		
		 if(args[0].equals("showtable")){
			if (args.length == 2) {
			f = new File(args[1]);
			if(f.exists()){
				UDPR.setDNSFile(args[1]);
			}
			else{
				try {
					f.createNewFile();
					UDPR.setDNSFile(args[1]);
				} catch (IOException e) {
					e.printStackTrace();
				}
				QF.listCorrespondingTable();
				
			}
			}
			else{
				System.out.println("vous n'avez pas indique le nom du fichier");
			}
		  }
		  else{
			if (args.length == 3) {
			UDPR.setSERVER_DNS(args[0]);
			f = new File(args[1]);
			if(f.exists()){
				UDPR.setDNSFile(args[1]);
			}
			else{
				try {
					f.createNewFile();
					UDPR.setDNSFile(args[1]);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(args[2].equals("false")){
					UDPR.setRedirectionSeulement(false);
				}
				else{
					UDPR.setRedirectionSeulement(true);
				}
			}
			//lancement du thread
			UDPR.start();
		        }
			else{
				System.out.println("Un argument est manquant!");
			}
	 	 }
	     }
	}
}
