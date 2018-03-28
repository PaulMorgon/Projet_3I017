package service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import base_de_donnees.UserTools;
import utils.Data;
import utils.ErrorJSON;
import utils.ServiceTools;

/**
 * Classe contenant tous les services en lien avec la gestion d'utilisateur.
 *
 */
public class UserServices 
{
	/**
	 * Permet à un utilisateur de se connecter.
	 * @param login Le login de l'utilisateur.
	 * @param pwd Le mot de passe de l'utilsiateur.
	 * @param root Un booléen pour indiquer si l'utilisateur devra etre déconnecté à partir d'un certain délai.
	 * @return Un objet JSON indiquant le résultat de l'opération.
	 */
	public static JSONObject login(String login, String pwd, String root) 
	{
		if(login==null || pwd == null || root == null)
			return ErrorJSON.defaultJsonError(Data.MESSAGE_MISSING_PARAMETERS, Data.CODE_MISSING_PARAMETERS);
		
		int myRoot = Integer.parseInt(root);
		if ((myRoot != 0) && (myRoot != 1))
			myRoot = 0;
		
		if(!base_de_donnees.UserTools.userExistsLogin(login))
			return ErrorJSON.defaultJsonError(Data.MESSAGE_USER_DOES_NOT_EXIST, Data.CODE_USER_DOES_NOT_EXIST);
		if(!base_de_donnees.UserTools.checkPwd(login,pwd))
			return ErrorJSON.defaultJsonError(Data.MESSAGE_INCORRECT_LOGIN_PASSWORD, Data.CODE_INCORRECT_LOGIN_PASSWORD);
		String key = base_de_donnees.UserTools.insererConnexion(login, pwd, myRoot);
		if (key == null)
			return ErrorJSON.defaultJsonError(Data.MESSAGE_ERROR_DB, Data.CODE_ERROR_DB);
		else
		{
			try
			{
				return ServiceTools.serviceAccepted().put("key", key);
			}
			catch (JSONException e) 
			{
				return ErrorJSON.defaultJsonError(Data.MESSAGE_ERROR_JSON, Data.CODE_ERROR_JSON);
			}
		}	
	}
	
	/**
	 * Déconnecte un utilisateur.
	 * @param key Le clé de connexion de l'utilisateur.
	 * @return Un objet JSON indiquant le résultat de l'opération.
	 */
	public static JSONObject logout(String key)
	{
		if(key == null)
			return ErrorJSON.defaultJsonError(Data.MESSAGE_MISSING_PARAMETERS, Data.CODE_MISSING_PARAMETERS);
		if(!UserTools.isConnection(key))		// Utilisateur non connecté
			return ErrorJSON.defaultJsonError(Data.MESSAGE_NOT_CONNECTED, Data.CODE_NOT_CONNECTED);
		
		if(UserTools.removeConnection(key))		//On enleve la connextion
			return ServiceTools.serviceAccepted();
		else
			return ErrorJSON.defaultJsonError(Data.MESSAGE_ERROR_DB, Data.CODE_ERROR_DB);
	}
	
	/**
	 * Ajoute un utilisateur à la base de données MySQL.
	 * @param login Le login de l'utilisateur.
	 * @param pwd Le mot de passe de l'utilisateur.
	 * @param prenom Le prénom de l'utilisateur.
	 * @param nom Le nom de l'utilisateur.
	 * @param email L'e-mail de l'utilisateur.
	 * @return Un objet JSON indiquant le résultat de l'opération.
	 */
	public static JSONObject createUser (String login , String pwd , String prenom , String nom , String email)
	{
		if(login==null || pwd == null || prenom == null || nom == null || email == null)
			return ErrorJSON.defaultJsonError(Data.MESSAGE_MISSING_PARAMETERS, Data.CODE_MISSING_PARAMETERS);
		
		if(login.length() < 5 || pwd.length() < 8)
			return ErrorJSON.defaultJsonError(Data.MESSAGE_LENGTH_PARAMETER, Data.CODE_LENGTH_PARAMETER);
		
		if(base_de_donnees.UserTools.userExistsLogin(login))
			return ErrorJSON.defaultJsonError(Data.MESSAGE_USER_ALREADY_EXISTS, Data.CODE_USER_ALREADY_EXISTS);
		
		boolean res = base_de_donnees.UserTools.insererUser(login , pwd , prenom , nom , email);
		if (res)
			return ServiceTools.serviceAccepted();
		else
			return ErrorJSON.defaultJsonError(Data.MESSAGE_ERROR_DB, Data.CODE_ERROR_DB);
	}
	
	/**
	 * 
	 * @param login Le login de l'utilisateur
	 * @param key
	 * @return
	 */
	public static JSONObject searchUserByLogin(String login, String key)
	{
		if(login == null || key == null)
			return ErrorJSON.defaultJsonError(Data.MESSAGE_MISSING_PARAMETERS, Data.CODE_MISSING_PARAMETERS);
		if(!base_de_donnees.UserTools.isConnection(key))
			return ErrorJSON.defaultJsonError(Data.MESSAGE_NOT_CONNECTED, Data.CODE_NOT_CONNECTED);
		JSONArray liste = UserTools.searchUserByLogin(login);
		if(liste == null)
			return ErrorJSON.defaultJsonError(Data.MESSAGE_ERROR_DB, Data.CODE_ERROR_DB);
		
		try
		{
			return ServiceTools.serviceAccepted().put("list_search_user", liste);
		}
		catch (JSONException e)
		{
			System.err.println("Error search User By Login : " + e.getMessage());
			return ErrorJSON.defaultJsonError(Data.MESSAGE_ERROR_JSON, Data.CODE_ERROR_JSON);
		}
	}
	
	public static JSONObject sendRecoveryPassword(String key, String mail1, String mail2)
	{
		if (key == null || mail1 == null || mail2 == null)
			return ErrorJSON.defaultJsonError(Data.MESSAGE_MISSING_PARAMETERS, Data.CODE_MISSING_PARAMETERS);
		if(!base_de_donnees.UserTools.isConnection(key))
			return ErrorJSON.defaultJsonError(Data.MESSAGE_NOT_CONNECTED, Data.CODE_NOT_CONNECTED);
		if (!mail1.equals(mail2))
			return ErrorJSON.defaultJsonError(Data.MESSAGE_MAIL_MATCHING_ERROR, Data.CODE_MAIL_MATCHING_ERROR);
		boolean res = UserTools.sendRecoveryPassword(key);
		if(res)
			return ServiceTools.serviceAccepted();
		else
			return ErrorJSON.defaultJsonError(Data.MESSAGE_ERROR_DB, Data.CODE_ERROR_DB);
		
	}
}
