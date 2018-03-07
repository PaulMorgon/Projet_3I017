package base_de_donnees;

import java.util.GregorianCalendar;

import org.bson.types.ObjectId;
import org.json.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

/**
 * Classe contenant les méthodes statiques pour gérer les commentaires de messages avec notre base de données MongDB.
 *
 */
public class CommentTools
{
	/**
	 * Ajoute un commentaire à un message.
	 * @param auteurId L'id de l'auteur du commentaire (ID dans MySQL).
	 * @param idMessage L'id du message commenté (ID dans MongoDB).
	 * @param commentaire Le contenu du commentaire.
	 */
	public static void addComment(String auteurId , String idMessage, String commentaire)
	{
		DBCollection msg = DataBase.getMongoCollection("Message");
		BasicDBObject comments = new BasicDBObject();
		GregorianCalendar c = new GregorianCalendar();
		comments.put("content", commentaire);
		comments.put("date", c.getTime());
		comments.put("idAuthor",auteurId);
		
		ObjectId objectId = genererObjectId();
		comments.put("id_comment", objectId);
		
		BasicDBObject content = new BasicDBObject();
		content.put("comments", comments);
		
		BasicDBObject push = new BasicDBObject();
		push.put("$push", content);
		
		BasicDBObject cond = new BasicDBObject();
		cond.put("_id", new ObjectId(idMessage));
		
		System.out.println("condition : " + cond);
		System.out.println("push : " + push);
		
		msg.update(cond , push);
	}
	
	/**
	 * Liste les commentaires d'un message.
	 * @param idMessage L'id du message (ID dans MongoDB).
	 * @return Une liste des commentaires.
	 */
	public static JSONArray listComment(String idMessage)
	{
		System.err.println("Error listComment : not yet implemented !");
		
		return null;
	}
	
	/**
	 * Supprime un commentaire.
	 * @param idCommentaire L'id du commentaire à supprimer (ID dans MongoDB).
	 */
	public static void removeComment(String idCommentaire)
	{
		DBCollection msg = DataBase.getMongoCollection("Message");
		BasicDBObject query = new BasicDBObject();
		query.put("id_comment", new ObjectId(idCommentaire));
		System.out.println("Query : " +query);
		msg.remove(query);
	}
	
	public static ObjectId genererObjectId()
	{
		DBCollection msg = DataBase.getMongoCollection("Message");
		int taille = 1;
		ObjectId id = new ObjectId();
		while(taille > 0)
		{
			BasicDBObject query = new BasicDBObject();
			query.put("id_comment", new BasicDBObject("$exists", true).put("$ne", id));
			System.out.println(query);
			DBCursor cursor = msg.find(query);
			System.out.println("Taille curseur : " + cursor.size());
			taille = cursor.size();
		}
		return id;
	}
}