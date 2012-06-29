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
 Nom du fichier :    UDPAnswerPacketCreator.java
 Date cr�e :         2007-03-10
 Date dern. modif.   X
 *******************************************************/
public class UDPAnswerPacketCreator {
	
	int longueur;
	byte[] Answerpacket;
	
	public int getLongueur(){
		return longueur;
	}
	
	public byte[] getAnswrpacket(){
		return Answerpacket;
	}
	
	public UDPAnswerPacketCreator(){
		
	}
	
	public byte[] CreateAnswerPacket(byte[] Qpacket,String adrr){
		
		
		System.out.println("Le packet QUERY recu");
		
		for(int i = 0;i < Qpacket.length;i++){
			if(i%16 == 0){
				System.out.println("\r");
			}
			System.out.print(Integer.toHexString(Qpacket[i] & 0xff).toString() + " ");
		}
		System.out.println("\r");
		
		//copie les informations dans un tableau qui est utilis� de buffer
		//durant la modification du packet
		byte[] Querypacket = new byte[1024];
		for(int i = 0; i < Qpacket.length; i++){
			Querypacket[i] = Qpacket[i];
		}
		
		
		//Conversion de l'adresse IP de String � byte
		adrr = adrr.replace("."," ");
		String[] adr = adrr.split(" ");
		byte part1 = 0;
		byte part2 = 0;
		byte part3 = 0;
		byte part4 = 0;
		part1 = (byte)(Integer.parseInt(adr[0]) & 0xff);
		part2 = (byte)(Integer.parseInt(adr[1]) & 0xff);
		part3 = (byte)(Integer.parseInt(adr[2]) & 0xff);
		part4 = (byte)(Integer.parseInt(adr[3]) & 0xff);
		
		System.out.println("Construction du packet ANSWER");
		
		//modification de l'identifiant
		Querypacket[0] = (byte)Qpacket[0];
		Querypacket[1] = (byte)Qpacket[1];
		
		//modification des param�tres
		//Active le champ reponse dans l'en-t�te
		Querypacket[2] = (byte) 0x81;
		Querypacket[3] = (byte) 0x80;
		Querypacket[4] = (byte) 0x00;
		Querypacket[5] = (byte) 0x01;
		Querypacket[6] = (byte) 0x00;
		Querypacket[7] = (byte) 0x01;
		Querypacket[8] = (byte) 0x00;
		
		//Serveur authority --> 0 il n'y a pas de serveur d'autorit�
		Querypacket[9] = (byte) 0x00;
		
		Querypacket[10] = (byte) 0x00;
		Querypacket[11] = (byte) 0x00;

		//Lecture de l'hostname
		//ici comme on ne connait pas la grandeur que occupe le nom de domaine
		//nous devons rechercher l'index pour pouvoir placer l'adresse IP � la bonne endroit
		//dans le packet
		
		int nbchar = Querypacket[12];
		String hostName = "";
		int index = 13;
		
		while(nbchar != 0){
			
			while(nbchar > 0) {
				hostName = hostName + String.valueOf(Character.toChars(Querypacket[index]));
			index++;
			nbchar--;
			}
			hostName = hostName + ".";
			
			nbchar = Querypacket[index];
			index++;
		}
		//System.out.println(hostName);       
		index = index - 1;
    

		//Identification de la class
		Querypacket[index + 1] = (byte)0x00;
		Querypacket[index + 2] = (byte)0x01;
		Querypacket[index + 3] = (byte)0x00;
		Querypacket[index + 4] = (byte)0x01;
		
		Querypacket[index + 5] = (byte) (0xC0);
		Querypacket[index + 6] = (byte) (0x0C);
		Querypacket[index + 7] = (byte) (0x00);
		Querypacket[index + 8] = (byte) 0x01;
		Querypacket[index + 9] = (byte) 0x00;
		Querypacket[index + 10] = (byte) 0x01;
		Querypacket[index + 11] = (byte) 0x00;
		Querypacket[index + 12] = (byte) 0x01;
		
		//time to life
		Querypacket[index + 13] = (byte)0x1a;
		Querypacket[index + 14] = (byte) (0x6c);
		Querypacket[index + 15] = (byte) (0x00);
		
		//Grace a l'index de possion, nous somme en mesure
		//de faire l'injection de l'adresse IP dans le packet
		//et ce � la bonne endroit
		Querypacket[index + 16] = 0x04;
		Querypacket[index + 17] = (byte) (part1 & 0xff);
		Querypacket[index + 18] = (byte) (part2 & 0xff);
		Querypacket[index + 19] = (byte) (part3 & 0xff);
		Querypacket[index + 20] = (byte) (part4 & 0xff);
		
		longueur = index + 20 + 1;
		
		Answerpacket = new byte[this.longueur];
		
		for(int i = 0; i < Answerpacket.length; i++){
			Answerpacket[i] = Querypacket[i];
		}
		System.out.println("Identifiant: 0x" + Integer.toHexString(Answerpacket[0] & 0xff) + Integer.toHexString(Answerpacket[1] & 0xff));
		System.out.println("parametre: 0x" + Integer.toHexString(Answerpacket[2] & 0xff) + Integer.toHexString(Answerpacket[3] & 0xff));
		System.out.println("question: 0x" + Integer.toHexString(Answerpacket[4] & 0xff) + Integer.toHexString(Answerpacket[5] & 0xff));
		System.out.println("reponse: 0x" + Integer.toHexString(Answerpacket[6] & 0xff) + Integer.toHexString(Answerpacket[7] & 0xff));
		System.out.println("autorite: 0x" + Integer.toHexString(Answerpacket[8] & 0xff) + Integer.toHexString(Answerpacket[9] & 0xff));
		System.out.println("info complementaire: 0x" + Integer.toHexString(Answerpacket[10] & 0xff) + Integer.toHexString(Answerpacket[11] & 0xff));
		
		for(int i = 0;i < Answerpacket.length;i++){
			if(i%16 == 0){
				System.out.println("\r");
			}
			System.out.print(Integer.toHexString(Answerpacket[i] & 0xff).toString() + " ");
		}
		System.out.println("\r");
		
		
		return Answerpacket;
	}
}