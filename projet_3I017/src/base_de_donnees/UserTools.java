package base_de_donnees;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mysql.jdbc.StringUtils;

import utils.Data;
/**
 * Classe contenant les méthodes statiques permettant à un utilisateur d'intéragir avec la base de données MySQL.
 *
 */
public class UserTools 
{
	/**
	 * Teste si l'utilisateur existe dans la bd avec son login.
	 * @param login : le login de l'utilisateur.
	 * @return True si l'utilisateur existe dans la base de données. False sinon.
	 */
	public static boolean userExistsLogin (String login)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM user WHERE login= \""+login+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return true;
			return false;
		} 
		catch (Exception e) 
		{
			System.err.println("Error userExists : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Teste si l'utilisateur existe dans la bd avec son id.
	 * @param id L'id de l'utilisateur dans la base de données.
	 * @return True si l'utilisateur existe dans la base de données. False sinon.
	 */
	
	public static boolean userExistsId (int id)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM user WHERE id = \""+id+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return true;
			return false;
		} 
		catch (Exception e) 
		{
			System.err.println("Error userExists : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Vérifie si un mail est présent dans la base de données MySQL.
	 * @param mail Le mail à vérifier.
	 * @return L'id sous si le mail existe, -1 sinon.
	 */
	public static int mailExists (String mail)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT id FROM user WHERE mail = \""+mail+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return rs.getInt(1);
			return -1;
		} 
		catch (Exception e) 
		{
			System.err.println("Error mailExists : " + e.getMessage());
			return -1;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Ajouter un utilisateur dans la base de données.
	 * @param login Le login de l'utilisateur.
	 * @param pwd Le mot de passe de l'utilisateur.
	 * @param prenom Le prenom de l'utilisateur.
	 * @param nom Le nom de l'utilisateur.
	 * @param email L'e-mail de l'utilisateur.
	 * @return boolean True si l'utilisateur à été ajouté dans la base de données. False sinon.
	 */
	
	public static boolean insererUser(String login , String pwd , String prenom , String nom , String email)
	{
		Connection c = null;
		try
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "INSERT INTO user(login, pwd, nom, prenom, mail) VALUES(\""+login+"\", PASSWORD(\""+pwd+"\"), \""+nom+"\", \""+prenom+"\", \""+email+"\");";
			System.out.println("insertion : " + query);
			int res = st.executeUpdate(query);
			 
			if (res == 0)
				return false;
			else
				return true;
		}
		catch (Exception e)
		{
			System.err.println("Error insererUser : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Inserer une connexion si l'utilisateur n'est pas connecté. S'il est déjà connecté, on met à jour les données de la connexion. 
	 * @param login Le login de l'utilisateur.
	 * @param pwd Le mot de passe de l'utilisateur.
	 * @return la clé de la connection. Null si la connexion n'a pû être insérée.
	 */
	public static String insererConnexion(String login , String pwd, int root)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			if(checkPwd(login, pwd))
			{
				int idUser = getIdUserFromLogin(login);
				if(hasSession(idUser))		// Il a déja une session, il faut mettre à jour la date
				{
					if(UserTools.updateDateSessionById(idUser))
						return UserTools.getKey(idUser);
					else
						return null;
				}
				else
				{
					String skey ="";
					while (keyExists(skey = generate_key()))
					{
						//tkt bro ça marche
					}
					String query = "INSERT INTO session(id_user , skey , root) VALUES(\""+idUser+"\", \""+skey+"\", \""+root+"\");";
					int res = st.executeUpdate(query);
					if (res == 0)
						return null;
					else
						return skey;
				}
			}
			else
			{
				return null;
			}
		}
		catch (Exception e)
		{
			System.err.println("Error insererConnecion : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Mettre a jour les données de connexion de l'utilisateur à partir de sa clé.
	 * @param idUser L'id de l'utilisateur.
	 * @return True si les données ont pû être mises à jour. False sinon.
	 */
	public static boolean updateDateSessionById(int idUser)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "UPDATE session SET sdate = NOW() WHERE id_user = \""+idUser+"\";";
			if(st.executeUpdate(query) == 0)
				return false;
			return true;
		} 
		catch (Exception e) 
		{
			System.err.println("Error updateDateSessionById : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Mettre a jour les données de connexion de l'utilisateur à partir de sa clé.
	 * @param key La clé de session de l'utilisateur.
	 * @return True si les données ont pû être mises à jour. False sinon.
	 */
	public static boolean updateDateSessionByKey(String key)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "UPDATE session SET sdate = NOW() WHERE skey = \""+key+"\";";
			if(st.executeUpdate(query) == 0)
				return false;
			return true;
		} 
		catch (Exception e) 
		{
			System.err.println("Error updateDateSessionByKey : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Obtenir la clé de connexion de l'utilisateur a partir de son id.
	 * @param idUser L'id de l'utilisateur.
	 * @return La clé de connexion, ou null en cas de problème.
	 */
	public static String getKey(int idUser)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT skey FROM session WHERE id_user = \""+idUser+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
			{
				return rs.getString(1);
			}
			return null;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getKey : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Vérifie si l'utilisateur est connecté a partir de son id.
	 * @param idUser L'id de l'utilisateur.
	 * @return True si l'utilisateur est connecté. False sinon.
	 */
	public static boolean hasSession(int idUser)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM session WHERE id_user = \""+idUser+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
			{
				return true;
			}
			return false;
		} 
		catch (Exception e) 
		{
			System.err.println("Error hasSession : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Verifie si la clé passée en paramètre est deja utilisée.
	 * @param key La clé que l'on doit vérifier.
	 * @return True si la clé existe déjà dans la base de données. False sinon.
	 */
	public static boolean keyExists(String key)
	{
	
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM session WHERE skey= \""+key+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
			{
				return true;
			}
			return false;
		} 
		catch (Exception e) 
		{
			System.err.println("Error keyExists : " + e.getMessage());
			return true;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Vérifie la correspondance (login,pwd).
	 * @param login Le login à vérifier.
	 * @param pwd Le mot de passe à vérifier.
	 * @return True si le couple existe dans la base de données. False sinon.
	 */
	public static boolean checkPwd(String login , String pwd)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM user WHERE login = \""+login+"\" AND pwd = PASSWORD(\""+pwd+"\");";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
			{
				return true;
			}
			return false;
		} 
		catch (Exception e) 
		{
			System.err.println("Error checkPwd : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Obtenir le login de l'utilisateur à partir de sa clé.
	 * @param key La clé.
	 * @return Le login de l'utilisateur. False en cas d'erreur avec la base de données.
	 */
	public static String getIdUserFromKey(String key)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT id_user FROM session WHERE skey= \""+key+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return rs.getString(1);
			else
				return null;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getLogin : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Obtenir l'id de l'utilisateur à partir de son login. 
	 * @param login Le login de l'utilisateur.
	 * @return l'id du user, ou -1 en cas de problème avec la base de données.
	 */
	public static int getIdUserFromLogin(String login)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT id FROM user WHERE login= \""+login+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return rs.getInt(1);
			else
				return -1;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getIdUser : " + e.getMessage());
			return -1;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Ajouter une entrée dans la table Friend (ajouter ami).
	 * @param idUser L'id de l'utilisateur qui souhaite ajouter un ami.
	 * @param idFriend L'id de l'ami à ajouter.
	 * @return True si l'ajout s'est effectué sans problème. False sinon.
	 */
	public static boolean addFriend(String idUser, String idFriend)
	{
		Connection c = null;
		try
		{
			if(!areFriends(idUser, idFriend))
			{
				c = DataBase.getMySQLConnection();
				Statement st = c.createStatement();
				String query = "INSERT INTO friend(id_user, id_friend) VALUES(\""+idUser+"\", \""+idFriend+"\");";
				int res = st.executeUpdate(query); 
				if (res == 0)
					return false;
				else
					return true;
			}
			else
				return false;
		}
		catch (Exception e)
		{
			System.err.println("Error addFriend : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Supprimer un ami dans la table Friend.
	 * @param idUser L'id de l'utilisateur qui souhaite supprimer un ami.
	 * @param idFriend L'id de l'ami à supprimer.
	 * @return True si la suppression s'est effectuée sans problème. False sinon.
	 */

	public static boolean removeFriend(String idUser, String idFriend)
	{
		Connection c = null;
		try
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "DELETE FROM friend WHERE id_user=\""+idUser+"\" AND id_friend =\""+idFriend+"\";";
			int res = st.executeUpdate(query); 
			if (res == 0)
				return false;
			else
				return true;
		}
		catch (Exception e)
		{
			System.err.println("Error RemoveFriend : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Lister les amis d'un utilisateur (les IDs).
	 * @param idUser L'id de l'utilisateur
	 * @return La liste des amis de l'utilisateur.
	 */
	public static List<Integer> listFriendId(String idUser)
	{
		Connection c = null;
		try
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT id_friend FROM friend WHERE id_user=\""+idUser+"\";";
			ResultSet cursor = st.executeQuery(query);
			List<Integer> friendArray = new ArrayList<Integer>();
			while(cursor.next())
				friendArray.add(cursor.getInt("id_friend"));
			
			return friendArray;
		}
		catch (Exception e)
		{
			System.err.println("Error listFriend : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Lister les amis d'un utilisateur (les logins).
	 * @param idUser L'id de l'utilisateur
	 * @return La liste des amis de l'utilisateur.
	 */
	public static List<String> listFriendLogin(String idUser)
	{
		Connection c = null;
		try
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT user.login FROM user, friend WHERE friend.id_user = "+idUser+" AND user.id = friend.id_friend;";
			ResultSet cursor = st.executeQuery(query);
			List<String> friendArray = new ArrayList<String>();
			while(cursor.next())
				friendArray.add(cursor.getString(1));
			
			return friendArray;
		}
		catch (Exception e)
		{
			System.err.println("Error listFriend : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Test si une connexion existe avec la clé "key". Si necessaire, la date de la clé peut être mise à jour.
	 * @param key La clé à tester
	 * @return boolean True si la clé existe. False sinon.
	 */
	public static boolean isConnection(String key)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT sdate FROM session WHERE skey= \""+key+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())		// Il faut vérifier depuis quand est posée la clé de connexion
			{
				Timestamp tempsSession = rs.getTimestamp(1);
				Timestamp maintenant = new Timestamp(new GregorianCalendar().getTimeInMillis());
				if((maintenant.getTime() - tempsSession.getTime()) >= utils.Data.DUREE_AVANT_DECO)		// Le temps est dépassé
				{
					if (!isRoot(key))		// L'utilisateur n'est pas root, on le déconnecte
					{
						removeConnection(key);
						return false;
					}
					else
						return true;
				}
				else if(!isRoot(key))						// Le temps n'est pas dépassé, et l'utilisateur n'est pas root
					return updateDateSessionByKey(key);		// On met donc à jour la date de sa session
				
				else										// Le temps n'est pas dépassé, et l'utilisateur est root
					return true;
			}
			else
				return false;
		} 
		catch (Exception e) 
		{
			System.err.println("Error isConnexion : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Supprimer une connexion.
	 * @param key La clé à supprimer.
	 * @return boolean True si la clé à été supprimée avec succès. False sinon.
	 */
	public static boolean removeConnection(String key)
	{
		Connection c = null;
		try
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			
			String query = "DELETE FROM session WHERE skey=\""+key+"\" ;";
			st.executeUpdate(query);
			
			return true;
		
		}
		catch (Exception e)
		{
			System.err.println("Error removeConnecion : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	

	/**
	 * Générer une clé de connexion de 32 caractères.
	 * @return la clé de connexion.
	 */
	private static String generate_key()
	{
		String key = UUID.randomUUID().toString().replaceAll("-", "");		//Génére une clé de 32 octets.
		return key;
	}
	
	/**
	 * Vérifie si une connexion est root.
	 * @param key La clé de connexion
	 * @return true si la connexion est root, false sinon.
	 */
	private static boolean isRoot(String key)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT root FROM session WHERE skey= \""+key+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())		// Il faut vérifier depuis quand est posé la clé de connexion
			{
				int root = rs.getInt(1);
				if(root == 0)
					return false;
				else
					return true;
			}
			else
				return false;
		} 
		catch (Exception e) 
		{
			System.err.println("Error isRoot : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Recherche la liste des utilisateur dont le login ressemble à celui donnée en paramètre.
	 * @param login Le login recherché
	 * @return Une liste des login ressemblant à celui passé en paramètre.
	 */
	public static JSONArray searchUserByLogin(String login)
	{
		Connection c = null;
		try
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM user WHERE login LIKE \"%"+login+"%\";";
			ResultSet cursor = st.executeQuery(query);
			JSONArray userArray = new JSONArray();
			while(cursor.next())
			{
				JSONObject json = new JSONObject();
				json.put("user_id",cursor.getString("id"));
				json.put("login",cursor.getString("login"));
				userArray.put(json);
			}
			
			return userArray;
		}
		catch (Exception e)
		{
			System.err.println("Error User_Search : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Liste tous les amis d'un utilisateur sous forme d'une chaine de caractère.
	 * @param idUser L'id de l'utilisateur dans la base de données MySQL.
	 * @return La liste des amis de l'utilisateur sous la forme suivante : id_ami1-id_ami2-id_ami3-...
	 */
	public static String listFriendString(String idUser)
	{
		
		Connection c = null;
		try
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM friend WHERE id_user=\""+idUser+"\";";
			ResultSet cursor = st.executeQuery(query);
			String amis = "";
			while(cursor.next())
				amis += cursor.getString(3) + "-";
			
			return amis;
		}
		catch (Exception e)
		{
			System.err.println("Error listFriend string: " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Vérifie si deux utilisateurs sont amis.
	 * @param idUser Le premier utilisateur.
	 * @param idFriend Le second utilisateur.
	 * @return True s'ils sont amis, false sinon.
	 */
	public static boolean areFriends(String idUser, String idFriend)
	{
		Connection c = null;
		try
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT * FROM friend WHERE id_user=\""+idUser+"\" AND id_friend=\""+idFriend+"\";";
			ResultSet cursor = st.executeQuery(query);
			JSONArray friendArray = new JSONArray();
			if(cursor.next())
			{
				return true;
			}
			
			return false;
		}
		catch (Exception e)
		{
			System.err.println("Error areFriend : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Récupérer le login de l'utilisateur à partir de son id.
	 * @param id L'id de l'utilisateur dans la base de données MySQL.
	 * @return Le login de l'utilisateur, NULL en cas d'erreur.
	 */
	public static String getLoginFromId(String id)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT login FROM user WHERE id = "+id+";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return rs.getString(1);
			else
				return null;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getLoginFromId : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Récupère l' e-mail d'un utilisateur.
	 * @param id L'id de l'utilisateur dans la base de données MySQL.
	 * @return L'e-mail de l'utilisateur.
	 */
	public static String getMailFromId(String id)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT mail FROM user WHERE id = "+id+";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return rs.getString(1);
			else
				return null;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getMailFromId : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Récupère le mot de passe d'un utilisateur
	 * @param id L'id de l'utilisateur dans la base de données MySQL.
	 * @return Le nouveau mot de passe.
	 */
	public static String getPassword(String id)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT pwd FROM user WHERE id = \""+id+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return rs.getString(1);
			return null;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getPassword : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Met à jour le mot de passe d'un utilisateur.
	 * @param id L'id de l'utilisateur dans la base de données MySQL.
	 * @param newPwd Le nouveau mot de passe.
	 * @return Un booléen indiquant la bonne execution ou non de l'envoi de mail.
	 */
	public static boolean setNewPwd(String id, String newPwd)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "UPDATE user SET pwd = PASSWORD(\""+newPwd+"\") WHERE id = \""+id+"\";";
			if(st.executeUpdate(query) == 0)
				return false;
			return true;
		} 
		catch (Exception e) 
		{
			System.err.println("Error setNewPwd : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Rétablie l'ancien mot de passe d'un utilisateur.
	 * @param id L'id de l'utilisateur dans la base de données MySQL.
	 * @param formerPwd L'ancien mot de passe à rétablir.
	 * @return Un booléen indiquant la bonne execution ou non de l'envoi de mail. 
	 */
	public static boolean setFormerPwd(String id, String formerPwd)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "UPDATE user SET pwd = \""+formerPwd+"\" WHERE id = \""+id+"\";";
			if(st.executeUpdate(query) == 0)
				return false;
			return true;
		} 
		catch (Exception e) 
		{
			System.err.println("Error setFormerPwd : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Envoie par mail un nouveau mot de passe.
	 * @param key La clé de connexion.
	 * @return Un booléen indiquant la bonne execution ou non de l'envoi de mail.
	 */
	public static boolean sendRecoveryPassword(int id, String mail)
	{
		String idStr = Integer.toString(id);
		String formerPwd = getPassword(idStr);
		if (formerPwd == null)		// On a pas pu récupérer le mot de passe
			return false;
		else
		{	
			// Envoi du mail
			final String username = Data.MAIL_ADDRESS;
			final String password = Data.MAIL_PASSWORD
					;
			final String newPwd = generateNewPwd();
			if(!setNewPwd(idStr, newPwd))		// erreur lors de la mise en place du nouveau mot de passe
			{
				setFormerPwd(idStr, formerPwd);		// On rétablie le précedent mot de passe
				return false;
			}
			else
			{
				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.port", "587");
				props.put("mail.smtp.ssl.trust", "smtp.gmail.com");


				
				Session session = Session.getInstance(props, new javax.mail.Authenticator()
				{
					protected PasswordAuthentication getPasswordAuthentication()
					{
						return new PasswordAuthentication(username, password);
					}
				  });

				try
				{
					Message message = new MimeMessage(session);
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
					message.setSubject("Password recovery");
					message.setText("Your new password is : \"" + newPwd +"\".");
					
					Transport.send(message);
					
					return true;
					
				}
				catch (MessagingException e)
				{
					System.err.println("sendRecoveryPassword : " + e.getMessage());
					
					setFormerPwd(idStr, formerPwd);
					return false;
				}

			}
		}
	}
	
	/**
	 * Génére un nouveau mot de passe de 10 caractères.
	 * @return Le nouveau mot de passe généré.
	 */
	public static String generateNewPwd()
	{
		String pwd = UUID.randomUUID().toString().replaceAll("-", "");		//Génére une clé de 32 octets.
		pwd = pwd.substring(0, 9);
		System.out.println(pwd);
		return pwd;
	}

	public static HashMap<Integer , String> getAllLogins()
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT id , login FROM user";
			ResultSet rs = st.executeQuery(query);
			
			HashMap<Integer , String> logins = new HashMap<Integer, String>();
			while(rs.next())
				//logins.add(rs.getString(1));
				logins.put(rs.getInt(1) , rs.getString(2));
			System.out.println(logins);
			
			return logins;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getAllLogins : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}

	public static boolean updateImage(String login, String pathImage)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "UPDATE user SET image = \""+pathImage+"\" WHERE login = \""+login+"\";";
			if(st.executeUpdate(query) == 0)
				return false;
			return true;
		} 
		catch (Exception e) 
		{
			System.err.println("Error updateImage : " + e.getMessage());
			return false;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}

	public static String getPathUserFromLogin(String login)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT image FROM user WHERE login = \""+login+"\";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return rs.getString(1);
			else
				return null;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getPathFromLogin : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	/**
	 * Renvoie une hashmap contenant les statistiques de l'application.
	 * @return une hashmap contenant les statistiques de l'application.
	 */
	public static HashMap<Integer, Double> listStats(int idUser)
	{
		HashMap<Integer, Double> res = new HashMap<Integer, Double>();
		Double tmp = getNbUser();
		if (tmp != null)
			res.put(Data.CLE_NB_UTIL, tmp);
		
		tmp = getNbUserCo();
		if (tmp != null)
			res.put(Data.CLE_NB_UTIL_CO, tmp);
		
		tmp = getNbMsg();
		if (tmp != null)
			res.put(Data.CLE_NB_MSG, tmp);
		
		if (idUser != -1)
		{
			tmp = getNbFriend(idUser);
			if (tmp != null)
				res.put(Data.CLE_NB_FRIEND, tmp);
			
			tmp = getNbOwnedMsg(idUser);
			if (tmp != null)
				res.put(Data.CLE_NB_OWNED_MSG, tmp);
			
			tmp = getNbFollowers(idUser);
			if (tmp != null)
				res.put(Data.CLE_NB_FOLLOWERS, tmp);
			
		}
		
		
		return res;
	}

	/**
	 * Retourne le nombre de message posté par l'utilisateur.
	 * @param idUser L'id de l'utilisateur connecté.
	 * @return Le nombre de message posté par l'utilisateur.
	 */
	private static Double getNbOwnedMsg(int idUser)
	{
		DBCollection msg = DataBase.getMongoCollection("Message");
		
		BasicDBObject cond = new BasicDBObject();
		cond.put("user_id", String.valueOf(idUser));
		
		return (double) msg.count(cond);
	}

	/**
	 * Retourne le nombre d'ami de l'utilisateur connecté.
	 * @param idUser L'id de l'utilisateur connecté.
	 * @return Le nombre d'ami de l'utilisateur connecté.
	 */
	private static Double getNbFriend(int idUser)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT count(*) FROM friend where id_user = "+idUser+";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return rs.getDouble(1);
			else
				return null;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getNbFriend : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}

	/**
	 * Retourne le nombre de message présent dans la base de données Mongo.
	 * @return Le nombre de message présent dans la base de données Mongo.
	 */
	private static Double getNbMsg()
	{
		DBCollection msg = DataBase.getMongoCollection("Message");
		
		return (double) msg.count();
	}

	/**
	 * Retourne le nombre d'utilisateur actuellement connecté.
	 * @return Le nombre d'utilisateur actuellement connecté.
	 */
	private static Double getNbUserCo()
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT count(*) FROM session ;";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return rs.getDouble(1);
			else
				return null;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getNbUserCo : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}

	/**
	 * Retourne le nombre total d'utilisateur inscrit à l'application.
	 * @return Le nombre total d'utilisateur inscrit à l'application.
	 */
	private static Double getNbUser()
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT count(*) FROM user ;";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return rs.getDouble(1);
			else
				return null;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getNbUser : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
	
	
	private static Double getNbFollowers(int idUser)
	{
		Connection c = null;
		try 
		{
			c = DataBase.getMySQLConnection();
			Statement st = c.createStatement();
			String query = "SELECT count(*) FROM friend where id_friend = "+idUser+";";
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				return rs.getDouble(1);
			else
				return null;
		} 
		catch (Exception e) 
		{
			System.err.println("Error getNbFriend : " + e.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if ((!DBStatic.mysql_pooling) && (c != null))
					c.close();
			}
			catch(SQLException e)
			{
				System.err.println("Error closing connexion : " + e.getMessage());
			}
		}
	}
}
