package service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import base_de_donnees.CommentTools;
import base_de_donnees.MessageTools;
import base_de_donnees.UserTools;
import utils.Data;
import utils.ErrorJSON;
import utils.ServiceTools;

/**
 * Classe contenant les services des commentaires de messages.
 *
 */
public class CommentServices
{
	/**
	 * Service d'ajout de commentaire à un message. 
	 * @param key La clé de connexion
	 * @param idMessage L'id du message.
	 * @param commentaire Le commentaire à ajouter.
	 * @return Un objet JSON indiquant le résultat de l'opération.
	 */
	public static JSONObject addComment(String key, String idMessage, String commentaire)
	{
		if(key == null || idMessage == null || commentaire == null)
			return ErrorJSON.defaultJsonError(Data.MESSAGE_MISSING_PARAMETERS, Data.CODE_MISSING_PARAMETERS);
		try
		{
			if(!base_de_donnees.UserTools.isConnection(key))
				return ErrorJSON.defaultJsonError(Data.MESSAGE_NOT_CONNECTED, Data.CODE_NOT_CONNECTED);
			
			String idAuteur = UserTools.getIdUserFromKey(key);
			CommentTools.addComment(idAuteur , idMessage, commentaire);
			
			return ServiceTools.serviceAccepted().put("added_comment", commentaire);
		}
		catch(JSONException e)
		{
			return ErrorJSON.defaultJsonError(Data.MESSAGE_ERROR_JSON, Data.CODE_ERROR_JSON);
		}
	}
	
	/**
	 * Service pour lister les commentaires d'un message. 
	 * @param key La clé de connexion
	 * @param idMessage L'id du message.
	 * @return Un objet JSON indiquant le résultat de l'opération.
	 */
	public static JSONObject listComment(String key, String idMessage)
	{
		if(key == null || idMessage == null)
			return ErrorJSON.defaultJsonError(Data.MESSAGE_MISSING_PARAMETERS, Data.CODE_MISSING_PARAMETERS);
		try
		{
			if(!base_de_donnees.UserTools.isConnection(key))
				return ErrorJSON.defaultJsonError(Data.MESSAGE_NOT_CONNECTED, Data.CODE_NOT_CONNECTED);
			
			JSONArray listeCommentaire = CommentTools.listComment(idMessage);
			
			return ServiceTools.serviceAccepted().put("comments", listeCommentaire);
		}
		catch(JSONException e)
		{
			return ErrorJSON.defaultJsonError(Data.MESSAGE_ERROR_JSON, Data.CODE_ERROR_JSON);
		}
	}
	
	/**
	 * Service de suppression de commentaire. 
	 * @param key La clé de connexion
	 * @param idCommentaire L'id du commentaire.
	 * @return Un objet JSON indiquant le résultat de l'opération.
	 */
	public static JSONObject removeComment(String key, String idCommentaire)
	{
		if(key == null || idCommentaire == null)
			return ErrorJSON.defaultJsonError(Data.MESSAGE_MISSING_PARAMETERS, Data.CODE_MISSING_PARAMETERS);
		
		if(!base_de_donnees.UserTools.isConnection(key))
			return ErrorJSON.defaultJsonError(Data.MESSAGE_NOT_CONNECTED, Data.CODE_NOT_CONNECTED);
		
		return ServiceTools.serviceAccepted();
	}
}
