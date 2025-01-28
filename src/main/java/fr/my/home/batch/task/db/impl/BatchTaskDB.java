package fr.my.home.batch.task.db.impl;

import java.util.List;

import fr.my.home.batch.bean.User;
import fr.my.home.batch.exception.FonctionnalException;
import fr.my.home.batch.exception.TechnicalException;

/**
 * Interface des tâches de base de données du batch qui déclare les 3 méthodes reader / process / writer
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public interface BatchTaskDB {

	/**
	 * Récupère la liste des utilisateurs à traiter
	 * 
	 * @return List<User>
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	public List<User> reader() throws FonctionnalException, TechnicalException;

	/**
	 * Effectue le traitement sur la liste fournie
	 * 
	 * @param List<User>
	 * @return List<User>
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	public List<User> process(List<User> listUser);

	/**
	 * Met à jour la liste des utilisateurs traités et renvoi le nombre d'utilisateur traité
	 * 
	 * @param List<User>
	 * @return processed
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	public int writer(List<User> listUser) throws FonctionnalException, TechnicalException;

}
