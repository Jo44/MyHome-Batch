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
 * Tâche de base de données 'RememberMe User' qui effectue un traitement en base pour supprimer l'info du 'Remember Me' des utilisateurs après 1an
 * (reader / process / writer)
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public class RememberMeUserTaskDB implements BatchTaskDB {

	/**
	 * Attributs
	 */

	private static final Logger logger = LogManager.getLogger(RememberMeUserTaskDB.class);
	private UserDAO userDAO = null;

	/**
	 * Constructeur
	 */
	public RememberMeUserTaskDB() {
		userDAO = new UserDAO();
	}

	/**
	 * Récupère la liste des utilisateurs ayant un token de 'RememberMe'
	 * 
	 * @return rememberMeListUser
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public List<User> reader() throws FonctionnalException, TechnicalException {
		logger.info("Récupération des utilisateurs ayant un token de 'RememberMe'");

		// Récupère la liste des utilisateurs ayant un token de 'RememberMe' à vérifier
		List<User> rememberMeListUser = userDAO.getAllUsersWithRememberMeToken();
		return rememberMeListUser;
	}

	/**
	 * Vérifie si l'utilisateur possède un token de 'RememberMe' expiré
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

					// Vérifie si l'utilisateur possède un token de 'Remember Me' et une date de mise à jour
					if (user.getRememberMeToken() != null && !user.getRememberMeToken().trim().isEmpty() && user.getLastUpdate() != null) {

						// Vérifie si le délai est expiré (+ de 1an)
						// now > limitDate
						// limitDate = Update + 1 an
						Calendar cal = Calendar.getInstance();
						Timestamp now = new Timestamp(cal.getTimeInMillis());

						// Détermine la date limite
						cal.setTimeInMillis(user.getLastUpdate().getTime());
						cal.add(Calendar.YEAR, 1);
						Timestamp limitDate = new Timestamp(cal.getTimeInMillis());

						// Si timestamp expiré
						if (now.after(limitDate)) {
							needChange = true;
						} else {
							logger.info("Le token de 'Remember Me' est encore valide !");
						}
					}

					// Si besoin de modification, modifie l'objet puis l'ajoute à la liste des utilisateurs traités
					if (needChange) {

						// Modifie l'objet
						user.setRememberMeToken(null);

						logger.info("Suppression du token 'Remember Me' pour l'utilisateur < " + user.getName() + " > demandée");
						processedListUser.add(user);
					}
				}
			}
		}
		return processedListUser;
	}

	/**
	 * Met à jour les utilisateurs ayant un token de 'RememberMe' expiré
	 * 
	 * @param listUser
	 * @return processed
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public int writer(List<User> listUser) throws FonctionnalException, TechnicalException {
		logger.info("Mise à jour des utilisateurs ayant un token de 'RememberMe' expiré");

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
