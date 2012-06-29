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
 Nom du fichier :    UDPReceiver.java
 Date cr�e :         2007-03-10
 Date dern. modif.   X
 *******************************************************/
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;


/**
 * Cette classe permet la r�ception d'un paquet UDP sur le port de r�ception
 * UDP/DNS. Elle analyse le paquet et extrait le hostname
 * 
 * Il s'agit d'un Thread qui �coute en permanance 
 * pour ne pas affecter le d�roulement du programme
 * @author Max
 *
 */


@SuppressWarnings("deprecation")
public class UDPReceiver extends Thread {
	/**
	 * Les champs d'un Packet UDP
	 * --------------------------
	 * En-t�te (12 octects)
	 * Question : l'adresse demand�
	 * R�ponse : l'adresse IP
	 * Autorit� : info sur le serveur d'autorit�
	 * Additionnel : information suppl�mentaire
	 */
	
	/**
	 * D�finition de l'En-t�te d'un Packet UDP
	 * ---------------------------------------
	 * Identifiant Param�tres
	 * QDcount Ancount
	 * NScount ARcount
	 * 
	 *� identifiant est un entier permettant d�identifier la requete.
	 *� parametres contient les champs suivant :
	 *	� QR (1 bit) : indique si le message est une question (0) ou une reponse (1).
	 *	� OPCODE (4 bits) : type de la requete (0000 pour une requete simple).
	 *	� AA (1 bit) : le serveur qui a fourni la reponse a-t�il autorite sur le domaine?
	 *	� TC (1 bit) : indique si le message est tronque.
	 *	� RD (1 bit) : demande d�une requete recursive.
	 *	� RA (1 bit) : indique que le serveur peut faire une demande recursive.
	 *	� UNUSED, AD, CD (1 bit chacun) : non utilises.
	 *	� RCODE (4 bits) : code de retour. 0 : OK, 1 : erreur sur le format de la requete, 2: probleme du serveur,
	 *    3 : nom de domaine non trouve (valide seulement si AA), 4 : requete non supportee, 5 : le serveur refuse
	 *    de repondre (raisons de s�ecurite ou autres).
	 * � QDCount : nombre de questions.
	 * � ANCount, NSCount, ARCount : nombre d�entrees dans les champs �Reponse�, �Autorite�, �Additionnel�.
	 */
	
	/**
	 * Les champs Reponse, Autorite, Additionnel sont tous representes de la meme maniere :
	 *
	 * � Nom (16 bits) : Pour eviter de recopier la totalite du nom, on utilise des offsets. Par exemple si ce champ
	 *   vaut C0 0C, cela signifie qu�on a un offset (C0) de 12 (0C) octets. C�est-a-dire que le nom en clair se trouve
	 *   au 12eme octet du message.
	 * � Type (16 bits) : idem que pour le champ Question.
	 * � Class (16 bits) : idem que pour le champ Question.
	 * � TTL (32 bits) : dur�ee de vie de l�entr�ee.
	 * � RDLength (16 bits): nombre d�octets de la zone RDData.
	 * � RDData (RDLength octets) : reponse
	 */
	
	private DataInputStream d = null;
	protected final static int BUF_SIZE = 1024;
	protected String SERVER_DNS = null;
	protected int port = 53;  // port de r�ception
	private String DomainName = "none";
	private String DNSFile = null;
	private String adrIP = null;
	private boolean RedirectionSeulement = false;
	private String adresseIP = null;
	
	public void setport(int p) {
		this.port = p;
	}
	
	public void setRedirectionSeulement(boolean b){
		this.RedirectionSeulement = b;
	}
	
	public String gethostNameFromPacket(){
		return DomainName;
	}
	
	public String getAdrIP(){
		return adrIP;
	}
	
	private void setAdrIP(String ip){
		adrIP = ip;
	}
	
	public void sethostNameFromPacket(String hostname){
		this.DomainName = hostname;
	}
	
	public String getSERVER_DNS(){
		return SERVER_DNS;
	}
	
	public void setSERVER_DNS(String server_dns){
		this.SERVER_DNS = server_dns;
	}
	
	public void UDPReceiver(String SERVER_DNS,int Port) {
		this.SERVER_DNS = SERVER_DNS;
		this.port = Port;
	}
	
	public void setDNSFile(String filename){
		DNSFile = filename;
	}
	
	public void run(){

		try{
			
			//*Creation d'un socket UDP
			DatagramSocket clientSocket = new DatagramSocket(port);
			List<byte[]> listeID = new ArrayList<byte[]>();
			
			//*Boucle infinie de recpetion
			while(true){
				
				//*Reception d'un paquet UDP via le socket
				byte[] receiveData = new byte[BUF_SIZE];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				
				//*Creation d'un DataInputStream ou ByteArrayInputStream pour manipuler les bytes du paquet			
				ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
				
				//*Lecture et sauvegarde des deux premier bytes, qui specifie l'identifiant
				byte[] id = new byte[2];
				bais.read(id);
				
				//*Lecture et sauvegarde du huitieme byte, qui specifie le nombre de reponse dans le message 
				int ancount = 0;
				for(int i=0; i<6; ++i)
					ancount = bais.read();
				
				//*Dans le cas d'une reponse
				if(ancount == 1) {

					//*Lecture du Query Domain name, a partir du 13 byte
					//*Sauvegarde du Query Domain name
					for(int i=0; i<5; ++i)
						ancount = bais.read();
					
					int entete = ancount;
					String queryDomainName = "";
					while(entete != 0) {
						for(int i=0; i<entete; i++)
							queryDomainName += Character.toChars(bais.read());
						queryDomainName += ".";
						entete = bais.read();
					}
					
					
					//*Passe par dessus Query Type et Query Class
					for(int i=0; i<4; ++i)
						ancount = bais.read();
					//*Passe par dessus les premiers champs du ressource record pour arriver au ressource data
					//*qui contient l'adresse IP associe au hostname (dans le fond saut de 16 bytes)
					for(int i=0; i<16; ++i)
						ancount = bais.read();
					
					//*Capture de l'adresse IP
					
					for(int i=0; i<4; ++i)
						ancount = bais.read();
					//*Ajouter la correspondance dans le fichier seulement si une seule
					//*reponse dans le message DNS (cette apllication ne traite que ce cas)
					
					
					//*Faire parvenir le paquet reponse au demandeur original, ayant emis une requete 
					//*avec cet identifiant
				}	

				//*Dans le cas d'une requete
				else if (ancount == 0) {
					//*Lecture du Query Domain name, a partir du 13 byte
					int queryDomainName = 0;
					for(int i=0; i<5; ++i)
						ancount = bais.read();
					//*Sauvegarde du Query Domain name
					
					//*Sauvegarde de l'adresse, du port et de l'identifiant de la requete
					

					//*Si le mode est redirection seulement
					
						//*Rediriger le paquet vers le serveur DNS
					
					//*Sinon
						
						//*Rechercher l'adresse IP associe au Query Domain name dans le fichier de 
						//*correspondance de ce serveur
					
						//*Si la correspondance n'est pas trouvee
							
							//*Rediriger le paquet vers le serveur DNS
					
						//*Sinon
							//*Creer le paquet de reponse a l'aide du UDPAnswerPaquetCreator
					
							//*Placer ce paquet dans le socket
					
							//*Envoyer le paquet
				}
			}
		}catch(Exception e){
			System.err.println("Probl�me � l'ex�cution :");
			e.printStackTrace(System.err);
		}	
	}
}
