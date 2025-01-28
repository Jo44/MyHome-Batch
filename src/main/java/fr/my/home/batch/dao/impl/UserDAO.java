package fr.my.home.batch.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import fr.my.home.batch.bean.User;
import fr.my.home.batch.dao.HibernateDAO;
import fr.my.home.batch.exception.FonctionnalException;
import fr.my.home.batch.exception.TechnicalException;
import fr.my.home.batch.tool.HibernateUtil;
import fr.my.home.batch.tool.properties.Settings;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;

/**
 * Classe UserDAO
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public class UserDAO implements HibernateDAO<User> {

	/**
	 * Attributs
	 */

	private static final String USERDAO_GET_BY_INACTIVE = Settings.getStringProperty("user.get.by.inactive");
	private static final String USERDAO_GET_BY_REINIT_TOKEN_OR_DATE = Settings.getStringProperty("user.get.by.reinit.token.or.date");
	private static final String USERDAO_GET_BY_REMEMBER_ME_TOKEN_NOT_NULL = Settings.getStringProperty("user.get.by.remember.me.token.not.null");

	/**
	 * Constructeur
	 */
	public UserDAO() {}

	/**
	 * Récupère la liste de tous les utilisateurs inactifs, ou exception fonctionnelle si aucun utilisateur
	 * 
	 * @return List<User>
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	public List<User> getAllInactiveUsers() throws FonctionnalException, TechnicalException {
		List<User> listUser = new ArrayList<User>();
		Session session = HibernateUtil.getInstance().openSession();
		Query<User> query = session.createQuery(USERDAO_GET_BY_INACTIVE, User.class);
		try {
			listUser = query.getResultList();
		} catch (NoResultException nre) {
			throw new FonctionnalException("Aucun utilisateur inactif");
		} finally {
			HibernateUtil.getInstance().validateSession(session);
		}
		return listUser;
	}

	/**
	 * Récupère la liste de tous les utilisateurs ayant un token et/ou une date de ré-initialisation, ou exception fonctionnelle si aucun utilisateur
	 * 
	 * @return List<User>
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	public List<User> getAllUsersWithReinitTokenOrDate() throws FonctionnalException, TechnicalException {
		List<User> listUser = new ArrayList<User>();
		Session session = HibernateUtil.getInstance().openSession();
		Query<User> query = session.createQuery(USERDAO_GET_BY_REINIT_TOKEN_OR_DATE, User.class);
		try {
			listUser = query.getResultList();
		} catch (NoResultException nre) {
			throw new FonctionnalException("Aucun utilisateur ayant de token de ré-initialisation");
		} finally {
			HibernateUtil.getInstance().validateSession(session);
		}
		return listUser;
	}

	/**
	 * Récupère la liste de tous les utilisateurs ayant un token de 'Remember Me' et une date de modification de plus de 1 an, ou exception
	 * fonctionnelle si aucun utilisateur
	 * 
	 * @return List<User>
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	public List<User> getAllUsersWithRememberMeToken() throws FonctionnalException, TechnicalException {
		List<User> listUser = new ArrayList<User>();
		Session session = HibernateUtil.getInstance().openSession();
		Query<User> query = session.createQuery(USERDAO_GET_BY_REMEMBER_ME_TOKEN_NOT_NULL, User.class);
		try {
			listUser = query.getResultList();
		} catch (NoResultException nre) {
			throw new FonctionnalException("Aucun utilisateur ayant de token de 'Remember Me'");
		} finally {
			HibernateUtil.getInstance().validateSession(session);
		}
		return listUser;
	}

	/**
	 * Ajoute un nouvel utilisateur en base, ou exception fonctionnelle si impossible
	 * 
	 * @param user
	 * @param FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public void add(User user) throws FonctionnalException, TechnicalException {
		Session session = HibernateUtil.getInstance().openSession();
		try {
			session.persist(user);
			session.flush();
			HibernateUtil.getInstance().validateSession(session);
		} catch (PersistenceException pe) {
			throw new FonctionnalException("Impossible d'ajouter l'utilisateur");
		} finally {
			if (session != null && session.isOpen()) {
				HibernateUtil.getInstance().closeSession(session);
			}
		}
	}

	/**
	 * Met à jour un utilisateur, ou exception fonctionnelle impossible (e-mail indisponible ?)
	 * 
	 * @param user
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public void update(User user) throws FonctionnalException, TechnicalException {
		Session session = HibernateUtil.getInstance().openSession();
		try {
			session.merge(user);
			HibernateUtil.getInstance().validateSession(session);
		} catch (PersistenceException pe) {
			throw new FonctionnalException("Impossible de mettre à jour l'utilisateur");
		} finally {
			if (session != null && session.isOpen()) {
				HibernateUtil.getInstance().closeSession(session);
			}
		}
	}

	/**
	 * Supprime un utilisateur, ou exception fonctionnelle si impossible
	 * 
	 * @param user
	 * @throws FonctionnalException
	 * @throws TechnicalException
	 */
	@Override
	public void delete(User user) throws FonctionnalException, TechnicalException {
		Session session = HibernateUtil.getInstance().openSession();
		try {
			session.remove(user);
			HibernateUtil.getInstance().validateSession(session);
		} catch (PersistenceException pe) {
			throw new FonctionnalException("Impossible de supprimer l'utilisateur");
		} finally {
			if (session != null && session.isOpen()) {
				HibernateUtil.getInstance().closeSession(session);
			}
		}
	}

}
