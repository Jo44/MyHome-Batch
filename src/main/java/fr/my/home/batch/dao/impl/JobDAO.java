package fr.my.home.batch.dao.impl;

import org.hibernate.Session;

import fr.my.home.batch.bean.Job;
import fr.my.home.batch.dao.HibernateDAO;
import fr.my.home.batch.exception.FonctionnalException;
import fr.my.home.batch.exception.TechnicalException;
import fr.my.home.batch.tool.HibernateUtil;
import jakarta.persistence.PersistenceException;

/**
 * Classe JobDAO
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public class JobDAO implements HibernateDAO<Job> {

	/**
	 * Constructeur
	 */
	public JobDAO() {}

	/**
	 * Ajoute un nouveau job en base, ou exception fonctionnelle si impossible
	 * 
	 * @param job
	 * @param FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public void add(Job job) throws FonctionnalException, TechnicalException {
		Session session = HibernateUtil.getInstance().openSession();
		try {
			session.persist(job);
			session.flush();
			HibernateUtil.getInstance().validateSession(session);
		} catch (PersistenceException pe) {
			throw new FonctionnalException("Impossible d'ajouter le job");
		} finally {
			if (session != null && session.isOpen()) {
				HibernateUtil.getInstance().closeSession(session);
			}
		}

	}

	/**
	 * Met à jour un job, ou exception fonctionnelle si impossible
	 * 
	 * @param job
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public void update(Job job) throws FonctionnalException, TechnicalException {
		Session session = HibernateUtil.getInstance().openSession();
		try {
			session.merge(job);
			HibernateUtil.getInstance().validateSession(session);
		} catch (PersistenceException pe) {
			throw new FonctionnalException("Impossible de mettre à jour le job");
		} finally {
			if (session != null && session.isOpen()) {
				HibernateUtil.getInstance().closeSession(session);
			}
		}
	}

	/**
	 * Supprime un job, ou exception fonctionnelle si impossible
	 * 
	 * @param job
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public void delete(Job job) throws FonctionnalException, TechnicalException {
		Session session = HibernateUtil.getInstance().openSession();
		try {
			session.remove(job);
			HibernateUtil.getInstance().validateSession(session);
		} catch (PersistenceException pe) {
			throw new FonctionnalException("Impossible de supprimer le job");
		} finally {
			if (session != null && session.isOpen()) {
				HibernateUtil.getInstance().closeSession(session);
			}
		}
	}

}
