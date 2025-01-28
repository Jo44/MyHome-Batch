package fr.my.home.batch.dao;

import fr.my.home.batch.exception.FonctionnalException;
import fr.my.home.batch.exception.TechnicalException;

/**
 * Interface HibernateDAO qui déclare les 3 méthodes génériques CREATE / UPDATE / DELETE
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public interface HibernateDAO<T> {

	/**
	 * Ajoute un nouvel objet
	 * 
	 * @param object
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	public void add(T object) throws FonctionnalException, TechnicalException;

	/**
	 * Met à jour un objet
	 * 
	 * @param object
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	public void update(T object) throws FonctionnalException, TechnicalException;

	/**
	 * Supprime un objet
	 * 
	 * @param object
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	public void delete(T object) throws FonctionnalException, TechnicalException;

}
