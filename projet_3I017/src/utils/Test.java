package utils;
import java.sql.Time;

import org.bson.types.*;

import base_de_donnees.CommentTools;
import base_de_donnees.UserTools;
import service.UserServices;

public class Test 
{
	/**
	 * Classe contenant le main pour les tests en local.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// Les infos de l'utilisateur
		String login = "tata";
		String pwd = "mot de passe de toto";
		String prenom = "Jason";
		String nom = "Statham";
		String mail = "toto@chips.fr";
		String loginFriend = "tata";
		int root = 1;
		int idUser = base_de_donnees.UserTools.getIdUserFromLogin(loginFriend); 
		int id1;
		int id2;
		
		/*
		System.out.println(service.UserServices.createUser(login, pwd, prenom, nom, mail).toString());
		id1 = base_de_donnees.UserTools.getIdUser(login);
		id2= base_de_donnees.UserTools.getIdUser("titi");
		//System.out.println("Insérer connexion : " + base_de_donnees.UserTools.insererConnexion(login, pwd, root) + "\n");
		
		//String key = base_de_donnees.UserTools.getKey(id2);
		//System.out.println("isConnexion : " + base_de_donnees.UserTools.isConnection(key) + "\n");

		//System.out.println("search friend friend : " + base_de_donnees.UserTools.searchUserByLogin("t"));
		
		//String id_msg= "5a8d9299e4b0893a2034300b";
		
		//base_de_donnees.MessageTools.addMessage(""+id1, "à oilp");
		//base_de_donnees.MessageTools.removeMessage(id_msg);
		//System.out.println("Liste des messages : \n" + base_de_donnees.MessageTools.listMessage(""+id, false, 2).toString() + "\n");
		*/
		
		/*String login2 = "Jordan";
		String pwd2 = "mot de passe de jordan";
		String prenom2 = "Jord";
		String nom2 = "An";
		String mail2 = "jordan@legume.fr";
		String loginFriend2 = "tata";
		
		// On crée Jordan
		System.out.println(service.UserServices.createUser(login2, pwd2, prenom2, nom2, mail2).toString());
		
		int idJordan = base_de_donnees.UserTools.getIdUser(login2);
		int idTata = base_de_donnees.UserTools.getIdUser("tata");
		String keyJordan=base_de_donnees.UserTools.getKey(idJordan);
		
		// Jordan se connecte en non-root
		System.out.println(service.UserServices.login(login2, pwd2, ""+0).toString());
		
		//Jordan se fait un ami
		System.out.println(service.FriendServices.addFriend(""+idJordan, ""+idTata, keyJordan).toString());
		
		//Jordan parle
		base_de_donnees.MessageTools.addMessage(""+idJordan, "jordan1");
		base_de_donnees.MessageTools.addMessage(""+idJordan, "jordan2");
		
		int idToto = base_de_donnees.UserTools.getIdUser("toto");
		
		//Jordan se fait un autre ami !
		System.out.println("add friend : "+base_de_donnees.UserTools.addFriend(""+idJordan, ""+idToto));
		
		//Jordan regarde les messages de ses potos
		String amis = base_de_donnees.UserTools.listFriendString(""+idJordan);
		System.out.println(amis);
		System.out.println("Liste des messages : \n" + service.MessageServices.listMessage(keyJordan, ""+idJordan, ""+0, ""+0, amis).toString() + "\n");
		
		
		//base_de_donnees.MessageTools.removeMessage("");
		
		//Jordan t'es la ?
		//System.out.println("isConnexion : " + base_de_donnees.UserTools.isConnection(keyJordan) + "\n");
		
		// Jordan va jouer au basket
		//service.UserServices.logout(keyJordan);*/
		
		
		
		//System.out.println(UserTools.updateImage("aaaaa", "jordan va  a la plage !! :)"));
		//System.out.println("1 : " + UserTools.insererConnexion("aaaaa", "console.log(\"jason\")", 0));
		//System.out.println("2 : " + UserTools.insererConnexion("aaaaa", "console.log(%22jason%22)", 0));
		
		
		//UserServices.sendRecoveryPassword("fodzakaria69@gmail.com" , "fodzakaria69@gmail.com");
		//CommentTools.removeComment("5adc6a26d399bea0e7cba0e8","5adc6a5bd399bea0e7cba0e9");
		System.out.println(UserServices.ChangePwd("4e5b03661","12345678","12345678" , "03c5530a94634635b851889a5ef739be"));
		//UserTools.setNewPwd("7", UserTools.generateNewPwd());
		// Test des commentaires
		//System.out.println(service.UserServices.createUser("totot", "totototo", "to", "to", "to@to.to.com").toString());
		/*System.out.println(service.UserServices.login("totot", "4b3304b4d", "0"));
		
		int idToto = base_de_donnees.UserTools.getIdUserFromLogin("totot");
		service.UserServices.login("totot", "totototo", ""+0);
		String keyToto = base_de_donnees.UserTools.getKey(idToto);*/
		
		//service.MessageServices.addMessage(keyToto, "Vive Jason");
		//String idMessage = "5aa181b246ec75cfec8b7f47";
		
		//System.out.println("cle toto : " + keyToto);
		//System.out.println(service.CommentServices.addComment(keyToto, idMessage, "8000 fois d'accord avec toi, vive Jason !"));
		//System.out.println("Liste des commentaires : " + service.CommentServices.listComment(keyToto, idMessage));
		//System.out.println(service.CommentServices.removeComment(keyToto, "5aa185d346ec3aa8bc39d8be"));
		
		//System.out.println("Envoi de mail : " + service.UserServices.sendRecoveryPassword(keyToto));
	}

}
