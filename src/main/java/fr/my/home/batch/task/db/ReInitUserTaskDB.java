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
 * Tâche de base de données 'ReInit User' qui effectue un traitement en base pour supprimer les infos de ré-initialisation des utilisateurs après 24h
 * (reader / process / writer)
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public class ReInitUserTaskDB implements BatchTaskDB {

	/**
	 * Attributs
	 */

	private static final Logger logger = LogManager.getLogger(ReInitUserTaskDB.class);
	private UserDAO userDAO = null;

	/**
	 * Constructeur
	 */
	public ReInitUserTaskDB() {
		userDAO = new UserDAO();
	}

	/**
	 * Récupère la liste des utilisateurs ayant des informations de ré-initialisation
	 * 
	 * @return reInitListUser
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public List<User> reader() throws FonctionnalException, TechnicalException {
		logger.info("Récupération des utilisateurs ayant des informations de ré-initialisation");

		// Récupère la liste des utilisateurs ayant des informations de ré-initialisation à vérifier
		List<User> reInitListUser = userDAO.getAllUsersWithReinitTokenOrDate();
		return reInitListUser;
	}

	/**
	 * Vérifie si l'utilisateur possède des informations de ré-initialisation expirées
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
					boolean needChange = false;

					if ((user.getReInitToken() != null && !user.getReInitToken().trim().isEmpty()) | user.getReInitDate() != null) {
						// Si le timestamp est présent
						if (user.getReInitDate() != null) {

							// Vérifie si le délai est expiré (+ de 24h)
							// now > limitDate
							// limitDate = reInitTokenDate + 24 heures
							Calendar cal = Calendar.getInstance();
							Timestamp now = new Timestamp(cal.getTimeInMillis());

							// Détermine la date limite
							cal.setTimeInMillis(user.getReInitDate().getTime());
							cal.add(Calendar.DAY_OF_MONTH, 1);
							Timestamp limitDate = new Timestamp(cal.getTimeInMillis());

							// Si timestamp expiré ou token absent
							if (now.after(limitDate) || user.getReInitToken() == null || user.getReInitToken().trim().isEmpty()) {
								needChange = true;
							} else {
								logger.info("Le token de ré-initialisation est encore valide !");
							}
						} else {
							// Le timestamp n'est pas présent donc efface les données de ré-initialisation
							needChange = true;
						}
					}

					// Si besoin de modification, modifie l'objet puis l'ajoute à la liste des utilisateurs traités
					if (needChange) {

						// Modifie l'objet
						user.setReInitToken(null);
						user.setReInitDate(null);

						logger.info("Suppression des informations de ré-initialisation pour l'utilisateur < " + user.getName() + " > demandée");
						processedListUser.add(user);
					}
				}
			}
		}
		return processedListUser;
	}

	/**
	 * Met à jour les utilisateurs ayant des informations de ré-initialisation expirées
	 * 
	 * @param listUser
	 * @return processed
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public int writer(List<User> listUser) throws FonctionnalException, TechnicalException {
		logger.info("Mise à jour des utilisateurs ayant des informations de ré-initialisation expirées");

		int processed = 0;
		// Pour chaque utilisateur traité
		if (listUser != null && !listUser.isEmpty()) {
			for (User user : listUser) {
				// Modifie l'utilisateur de la base
				userDAO.update(user);
				// Incrément le compteur
				processed++;
			}
		}
		// Renvoi le nombre d'entrée traitée
		return processed;
	}

}
