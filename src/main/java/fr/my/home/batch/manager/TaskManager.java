package fr.my.home.batch.manager;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.my.home.batch.exception.FonctionnalException;
import fr.my.home.batch.exception.TechnicalException;
import fr.my.home.batch.task.InactiveUserTask;
import fr.my.home.batch.task.ReInitUserTask;
import fr.my.home.batch.task.RememberMeUserTask;
import fr.my.home.batch.tool.GlobalTools;
import fr.my.home.batch.tool.properties.Settings;

/**
 * Manager qui prends en charge le lancement des tâches du batch
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public class TaskManager {

	/**
	 * Attributs
	 */

	private static final Logger logger = LogManager.getLogger(TaskManager.class);
	private static final String EMAIL_TARGET = Settings.getStringProperty("email.target");
	private static final String DB_ERROR = Settings.getStringProperty("error.db");
	private static final String EMAIL_SUCCESS = Settings.getStringProperty("email.success");
	private static boolean inactiveUserTaskToday = false;
	private static boolean reInitUserTaskToday = false;
	private static boolean rememberMeUserTaskToday = false;

	/**
	 * Vérifie si une tâche du batch doit être lancée en fonction de la date et de l'heure actuelle
	 */
	public void checkSchedule() {

		// Récupère la date / heure actuelle
		Calendar now = Calendar.getInstance();
		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		int hourOfDay = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);

		// Ré-initialise les variables TaskToday (pour exécution 1 fois / jour max)
		// -> Tous les jours entre 00h15 et 00h30
		if (hourOfDay == 0 && minute >= 15 && minute <= 30) {
			if (inactiveUserTaskToday) {
				// Ré-initialise la variable 'inactiveUserTaskToday'
				inactiveUserTaskToday = false;
			}
			if (reInitUserTaskToday) {
				// Ré-initialise la variable 'reInitUserTaskToday'
				reInitUserTaskToday = false;
			}
			if (rememberMeUserTaskToday) {
				// Ré-initialise la variable 'rememberMeUserTaskToday'
				rememberMeUserTaskToday = false;
			}
		}

		// Batch de suppression utilisateur non activé après 72h
		// -> Tous les jours entre 6h et 6h30
		// TEST
		// if (!inactiveUserTaskToday) {
		// RUN
		if (!inactiveUserTaskToday && hourOfDay >= 6 && hourOfDay < 7 && minute >= 0 && minute < 30) {
			// Lancement du batch
			inactiveUserTask();
			// Met à jour la variable pour limiter à 1 fois / jour
			inactiveUserTaskToday = true;
		}

		// Batch de suppression des infos de ré-initialisation après 24h
		// -> Tous les jours entre 6h30 et 7h
		// TEST
		// if (!reInitUserTaskToday) {
		// RUN
		if (!reInitUserTaskToday && hourOfDay >= 6 && hourOfDay < 7 && minute >= 30 && minute < 60) {
			// Lancement du batch
			reInitUserTask();
			// Met à jour la variable pour limiter à 1 fois / jour
			reInitUserTaskToday = true;
		}

		// Batch de suppression de l'info du 'Remember Me' après 1an (+ email de notification hebdomadaire)
		// -> Tous les Lundi entre 7h et 7h30 (Lundi = dayOfWeek 2)
		// TEST
		// if (!rememberMeUserTaskToday) {
		// RUN
		if (!rememberMeUserTaskToday && dayOfWeek == 2 && hourOfDay >= 7 && hourOfDay < 8 && minute >= 0 && minute < 30) {
			// Lancement du batch
			rememberMeUserTask();
			// Met à jour la variable pour limiter à 1 fois / jour
			rememberMeUserTaskToday = true;
			// Envoi un email de notification de l'état général du batch
			notificationEmail();
		}

	}

	/**
	 * Lance la tâche de suppression des utilisateurs non activés après 72h
	 */
	private void inactiveUserTask() {
		logger.info("Lancement de la tâche de suppression des utilisateurs non activés après 72h ..");

		InactiveUserTask inactiveUserTask = new InactiveUserTask();
		boolean status = false;

		try {
			// Créer un nouveau job
			inactiveUserTask.init();
			// Exécute le job
			status = inactiveUserTask.execute();
			// Clôture le job
			inactiveUserTask.finish(status);
		} catch (FonctionnalException fex) {
			logger.error(fex.getMessage());
		} catch (TechnicalException tex) {
			logger.error(DB_ERROR);
		}
	}

	/**
	 * Lance la tâche de suppression des infos de ré-initialisation des utilisateurs après 24h
	 */
	private void reInitUserTask() {
		logger.info("Lancement de la tâche de suppression des infos de ré-initialisation des utilisateurs après 24h ..");

		ReInitUserTask reInitUserTask = new ReInitUserTask();
		boolean status = false;

		try {
			// Créer un nouveau job
			reInitUserTask.init();
			// Exécute le job
			status = reInitUserTask.execute();
			// Clôture le job
			reInitUserTask.finish(status);
		} catch (FonctionnalException fex) {
			logger.error(fex.getMessage());
		} catch (TechnicalException tex) {
			logger.error(DB_ERROR);
		}
	}

	/**
	 * Lance la tâche de suppression de l'info du 'Remember Me' des utilisateurs après 1an
	 */
	private void rememberMeUserTask() {
		logger.info("Lancement de la tâche de suppression de l'info du 'Remember Me' des utilisateurs après 1an ..");

		RememberMeUserTask rememberMeUserTask = new RememberMeUserTask();
		boolean status = false;

		try {
			// Créer un nouveau job
			rememberMeUserTask.init();
			// Exécute le job
			status = rememberMeUserTask.execute();
			// Clôture le job
			rememberMeUserTask.finish(status);
		} catch (FonctionnalException fex) {
			logger.error(fex.getMessage());
		} catch (TechnicalException tex) {
			logger.error(DB_ERROR);
		}
	}

	/**
	 * Envoi un email de notification de l'état général du batch à l'administrateur
	 */
	private void notificationEmail() {
		// Récupère la date/heure de lancement de l'application
		Calendar launchDate = GlobalTools.getLaunchDate();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
		String date = dateFormat.format(launchDate.getTime());
		String hour = hourFormat.format(launchDate.getTime());
		// Récupère le nombre de jour depuis lancement
		long offsetDay = ChronoUnit.DAYS.between(launchDate.toInstant(), Calendar.getInstance().toInstant());
		// Prépare le contenu de l'email
		String content = EMAIL_SUCCESS;
		content = content.replace("{0}", date);
		content = content.replace("{1}", hour);
		content = content.replace("{2}", String.valueOf(offsetDay));
		// Envoi de l'email de notification
		GlobalTools.sendEmail(EMAIL_TARGET, "Batch my-home.ovh", content);
	}

}
