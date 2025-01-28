package fr.my.home.batch.tool;

import java.util.Calendar;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.my.home.batch.tool.properties.Settings;

/**
 * Classe regroupant différents outils (send Email / stock launchDate)
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public class GlobalTools {

	/**
	 * Attributs
	 */

	private static final Logger logger = LogManager.getLogger(GlobalTools.class);
	private static final String SMTP_HOSTNAME = Settings.getStringProperty("smtp.hostname");
	private static final int SMTP_PORT = Settings.getIntProperty("smtp.port");
	private static final boolean SMTP_SSL = Settings.getBooleanProperty("smtp.ssl");
	private static final String SMTP_USER = Settings.getStringProperty("smtp.user");
	private static final String SMTP_PASS = Settings.getStringProperty("smtp.pass");
	private static Calendar launchDate;

	/**
	 * Envoi un mail en utilisant Apache Commons Email
	 * 
	 * @param target
	 * @param subject
	 * @param message
	 */
	public static void sendEmail(String target, String subject, String message) {
		try {
			Email email = new SimpleEmail();
			email.setHostName(SMTP_HOSTNAME);
			email.setSmtpPort(SMTP_PORT);
			email.setSSLOnConnect(SMTP_SSL);
			email.setAuthenticator(new DefaultAuthenticator(SMTP_USER, SMTP_PASS));
			email.setSSLOnConnect(true);
			email.setFrom(SMTP_USER);
			email.setSubject(subject);
			email.setMsg(message);
			email.addTo(target);
			email.send();
			logger.info("Email correctement envoyé à " + target);
		} catch (EmailException ee) {
			ee.printStackTrace();
			logger.error("Erreur lors de l'envoi de l'email à " + target);
		}
	}

	/**
	 * Getter / Setter
	 */
	public static Calendar getLaunchDate() {
		return launchDate;
	}

	public static void setLaunchDate(Calendar calendar) {
		launchDate = calendar;
	}

}
