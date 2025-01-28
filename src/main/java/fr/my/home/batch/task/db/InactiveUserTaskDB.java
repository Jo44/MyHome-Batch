package fr.my.home.batch.task.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.my.home.batch.bean.User;
import fr.my.home.batch.dao.impl.UserDAO;
import fr.my.home.batch.exception.FonctionnalException;
import fr.my.home.batch.exception.TechnicalException;
import fr.my.home.batch.task.db.impl.BatchTaskDB;

/**
 * Tâche de base de données 'Inactive User' qui effectue un traitement en base pour supprimer les utilisateurs toujours inactifs 72h après leurs
 * inscriptions (reader / process / writer)
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public class InactiveUserTaskDB implements BatchTaskDB {

	/**
	 * Attributs
	 */

	private static final Logger logger = LogManager.getLogger(InactiveUserTaskDB.class);
	private UserDAO userDAO = null;

	/**
	 * Constructeur
	 */
	public InactiveUserTaskDB() {
		userDAO = new UserDAO();
	}

	/**
	 * Récupère la liste des utilisateurs non actifs
	 * 
	 * @return inactiveListUser
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public List<User> reader() throws FonctionnalException, TechnicalException {
		logger.info("Récupération des utilisateurs non actifs");

		// Récupère la liste des utilisateurs non actifs à vérifier
		List<User> inactiveListUser = userDAO.getAllInactiveUsers();
		return inactiveListUser;
	}

	/**
	 * Vérifie si l'utilisateur non actif est expiré
	 * 
	 * @param listUser
	 * @return processedListUser
	 */
	@Override
	public List<User> process(List<User> listUser) {

		List<User> processedListUser = new ArrayList<User>();

		// Si la liste n'est pas vide, pour chaque utilisateur
		if (listUser != null && !listUser.isEmpty()) {
			for (User user : listUser) {
				// Si l'utilisateur n'est pas null
				if (user != null) {

					logger.info("Vérification de l'utilisateur < " + user.getName() + " > ...");
					boolean needRemove = false;

					// Vérifie si l'utilisateur possède un token de validation et/ou une date d'inscription
					if ((user.getValidationToken() != null && !user.getValidationToken().trim().isEmpty()) | user.getInscriptionDate() != null) {
						// Si le timestamp est présent
						if (user.getInscriptionDate() != null) {

							// Vérifie si le délai est expiré (+ de 72h)
							// now > limitDate
							// limitDate = reInitTokenDate + 72 heures
							Calendar cal = Calendar.getInstance();
							Timestamp now = new Timestamp(cal.getTimeInMillis());

							// Détermine la date limite
							cal.setTimeInMillis(user.getInscriptionDate().getTime());
							cal.add(Calendar.DAY_OF_MONTH, 3);
							Timestamp limitDate = new Timestamp(cal.getTimeInMillis());

							// Si expiré ou token absent
							if (now.after(limitDate) || user.getValidationToken() == null || user.getValidationToken().trim().isEmpty()) {
								needRemove = true;
							} else {
								logger.info("Le token de validation est encore valide !");
							}
						} else {
							// Le timestamp n'est pas présent donc efface l'utilisateur
							needRemove = true;
						}
					}

					// Si besoin de suppression, ajoute l'objet à la liste des utilisateurs traités
					if (needRemove) {
						logger.info("Suppression de l'utilisateur < " + user.getName() + " > demandée");
						processedListUser.add(user);
					}
				}
			}
		}
		return processedListUser;
	}

	/**
	 * Supprime les utilisateurs non actifs expirés
	 * 
	 * @param listUser
	 * @return processed
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public int writer(List<User> listUser) throws FonctionnalException, TechnicalException {
		logger.info("Suppression des utilisateurs non actifs expirés");

		int processed = 0;
		// Pour chaque utilisateur traité
		if (listUser != null && !listUser.isEmpty()) {
			for (User user : listUser) {
				// Supprime l'utilisateur de la base
				userDAO.delete(user);
				// Incrément le compteur
				processed++;
			}
		}
		// Renvoi le nombre d'entrée traitée
		return processed;
	}

}
