package fr.my.home.batch.task;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.my.home.batch.bean.Job;
import fr.my.home.batch.bean.User;
import fr.my.home.batch.dao.impl.JobDAO;
import fr.my.home.batch.exception.FonctionnalException;
import fr.my.home.batch.exception.TechnicalException;
import fr.my.home.batch.task.db.RememberMeUserTaskDB;
import fr.my.home.batch.task.impl.BatchTask;

/**
 * Tâche 'RememberMe User' qui effectue un traitement pour supprimer l'info du 'Remember Me' des utilisateurs après 1an (init / execute / finish)
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public class RememberMeUserTask implements BatchTask {

	/**
	 * Attributs
	 */

	private static final Logger logger = LogManager.getLogger(RememberMeUserTask.class);
	private RememberMeUserTaskDB rememberMeUserTaskDB = null;
	private JobDAO jobDAO = null;
	private Job job = null;
	private int processed = 0;

	/**
	 * Constructeur
	 */
	public RememberMeUserTask() {
		rememberMeUserTaskDB = new RememberMeUserTaskDB();
		jobDAO = new JobDAO();
	}

	/**
	 * Initialisation du job
	 * 
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public void init() throws FonctionnalException, TechnicalException {
		logger.info("Créer un nouveau job");

		// Récupère la date / heure actuelle
		Calendar now = Calendar.getInstance();
		// Création d'un nouveau job
		job = new Job("RememberMe User", processed, false, false, new Timestamp(now.getTimeInMillis()), null);
		// Enregistrement du job
		jobDAO.add(job);
	}

	/**
	 * Exécution du job
	 * 
	 * @return status
	 * @throws TechnicalException
	 */
	@Override
	public boolean execute() throws TechnicalException {
		logger.info("Exécute le job");

		// Tâche base de données
		boolean status = false;
		List<User> listUser = null;
		try {
			// Récupère la liste des utilisateurs ayant un token de 'RememberMe'
			listUser = rememberMeUserTaskDB.reader();
			// Détermine quels utilisateurs doivent être modifiés
			listUser = rememberMeUserTaskDB.process(listUser);
		} catch (FonctionnalException fex) {
			logger.info(fex.getMessage());
		} finally {
			// Si la liste traitée n'est pas vide
			if (listUser != null && !listUser.isEmpty()) {
				try {
					// Stock le total puis met à jour les utilisateurs sélectionnés
					processed = rememberMeUserTaskDB.writer(listUser);
					logger.info("Nombre d'utilisateur mis à jour : " + String.valueOf(processed));
					// Détermine le status du job
					status = true;
				} catch (FonctionnalException fex) {
					logger.error(fex.getMessage());
					status = false;
				}
			} else {
				logger.info("Aucun utilisateur à mettre à jour");
				status = false;
			}
		}
		return status;
	}

	/**
	 * Clôture du job
	 * 
	 * @param status
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public void finish(boolean status) throws FonctionnalException, TechnicalException {
		logger.info("Clôture le job");

		// Récupère la date / heure actuelle
		Calendar now = Calendar.getInstance();
		// Met à jour le job en fonction du status
		job.setProcessed(processed);
		job.setFinished(true);
		job.setStatus(status);
		job.setFinishTime(new Timestamp(now.getTimeInMillis()));
		// Enregistrement du job
		jobDAO.update(job);
	}

}
