package fr.my.home.batch.tool.properties;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe en charge de récupérer en mémoire les messages des mails de l'application via des fichiers 'emails.properties'
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public class EmailMessages {

	/**
	 * Attributs
	 */

	private static final Logger logger = LogManager.getLogger(EmailMessages.class);
	private static Properties properties;

	/**
	 * Charge le fichier 'emails.properties' + localisation
	 */
	static {
		try {
			properties = new Properties();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			properties.load(classLoader.getResourceAsStream("emails.properties"));
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Retourne la valeur
	 * 
	 * @param key
	 * @return String
	 */
	public static String getProperty(String key) {
		String parametre = "";
		parametre = properties.getProperty(key, null);
		return parametre;
	}

}
