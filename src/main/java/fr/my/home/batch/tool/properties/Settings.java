package fr.my.home.batch.tool.properties;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe en charge de récupérer en mémoire les paramètres de l'application via un fichier 'settings.properties'
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public class Settings {

	/**
	 * Attributs
	 */

	private static final Logger logger = LogManager.getLogger(Settings.class);
	private static Properties properties;

	/**
	 * Charge le fichier 'settings.properties'
	 */
	static {
		try {
			properties = new Properties();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			properties.load(classLoader.getResourceAsStream("settings.properties"));
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Retourne la valeur (String)
	 * 
	 * @param key
	 * @return String
	 */
	public static String getStringProperty(String key) {
		String parametre = properties.getProperty(key, null);
		return parametre;
	}

	/**
	 * Retourne la valeur (int)
	 * 
	 * @param key
	 * @return int
	 */
	public static int getIntProperty(String key) {
		String parametreStr = properties.getProperty(key, null);
		int parametre = Integer.parseInt(parametreStr);
		return parametre;
	}

	/**
	 * Retourne la valeur (long)
	 * 
	 * @param key
	 * @return long
	 */
	public static long getLongProperty(String key) {
		String parametreStr = properties.getProperty(key, null);
		long parametre = Long.parseLong(parametreStr);
		return parametre;
	}

	/**
	 * Retourne la valeur (boolean)
	 * 
	 * @param key
	 * @return boolean
	 */
	public static boolean getBooleanProperty(String key) {
		String parametreStr = properties.getProperty(key, null);
		boolean parametre = Boolean.parseBoolean(parametreStr);
		return parametre;
	}

}
