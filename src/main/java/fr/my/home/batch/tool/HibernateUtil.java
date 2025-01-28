package fr.my.home.batch.tool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import fr.my.home.batch.exception.TechnicalException;
import fr.my.home.batch.tool.properties.EmailMessages;
import fr.my.home.batch.tool.properties.Settings;
import jakarta.persistence.PersistenceException;

/**
 * Classe HibernateUtil permettant d'établir une session Hibernate
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
public class HibernateUtil {

	/**
	 * Attributs
	 */

	private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
	private static HibernateUtil instance;
	private static SessionFactory sessionFactory;
	private int nbTentative = 0;
	private static final String EMAIL_TARGET = Settings.getStringProperty("email.target");

	/**
	 * Constructeur
	 */
	private HibernateUtil() {
		// Initialisation de la SessionFactory
		initSessionFactory();
	}

	/**
	 * Initialisation de la SessionFactory
	 */
	private void initSessionFactory() {
		StandardServiceRegistry registry = null;
		try {
			logger.info("Initialisation Hibernate ...");
			registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
			MetadataSources sources = new MetadataSources(registry);
			Metadata metadata = sources.getMetadataBuilder().build();
			sessionFactory = metadata.getSessionFactoryBuilder().build();
		} catch (Exception ex) {
			logger.error("Erreur lors de l'initialisation Hibernate : " + ex.getMessage());
			if (registry != null) {
				StandardServiceRegistryBuilder.destroy(registry);
			}
		}
	}

	/**
	 * Retourne une session Hibernate prête à l'emploi (transaction débutée)
	 * 
	 * @return Session
	 * @throws TechnicalException
	 */
	public Session openSession() throws TechnicalException {
		Session session = sessionFactory.openSession();
		logger.debug("Session Hibernate débutée ..");
		try {
			session.beginTransaction();
		} catch (JDBCConnectionException jdbcex) {
			nbTentative++;
			logger.error("Erreur #" + nbTentative + " lors de l'ouverture d'une nouvelle transaction.");
			session = openSessionRetry(session);
		}
		// Renvoi exception si impossible de débuter une transaction
		if (session == null || session.getTransaction() == null || session.getTransaction().getStatus() != TransactionStatus.ACTIVE) {
			throw new TechnicalException("Impossible de débuter la transaction !");
		}
		logger.debug("Transaction débutée ..");
		// Reset du nombre de tentative
		nbTentative = 0;
		return session;
	}

	/**
	 * Râfraichis la SessionFactory et essaye de nouveau d'ouvrir une nouvelle session
	 * 
	 * @param session
	 * @return Session
	 * @throws TechnicalException
	 */
	private Session openSessionRetry(Session session) throws TechnicalException {
		if (nbTentative < 3) {
			logger.info("Nouvelle tentative en cours ..");
			// Fermeture de la session
			session.close();
			// Ré-initialisation de la factory
			initSessionFactory();
			// Ré-ouverture de la session
			session = openSession();
		} else {
			// Envoi de l'email de notification de l'erreur en fonction de la langue de l'administrateur
			GlobalTools.sendEmail(EMAIL_TARGET, EmailMessages.getProperty("error.database.object"),
					EmailMessages.getProperty("error.database.content"));
			throw new TechnicalException("Impossible de débuter la transaction apres " + nbTentative + " tentatives.. Abandon.");
		}
		return session;
	}

	/**
	 * Valide la transaction (ou capte PersistenceException (Rollback automatique)) et clôture la session
	 * 
	 * @param session
	 * @throws PersistenceException
	 */
	public void validateSession(Session session) throws PersistenceException {
		if (session != null && session.getTransaction() != null) {
			try {
				session.flush();
				session.getTransaction().commit();
				logger.debug("Transaction commit.");
			} catch (PersistenceException pe) {
				logger.debug("Transaction roll-back.");
				throw pe;
			} finally {
				session.close();
				logger.debug("Session Hibernate clôturée.");
			}
		}
	}

	/**
	 * Annule la transaction et clôture la session
	 * 
	 * @param session
	 * @throws PersistenceException
	 */
	public void closeSession(Session session) throws PersistenceException {
		if (session != null) {
			if (session.getTransaction() != null) {
				session.getTransaction().rollback();
				logger.debug("Transaction roll-back.");
			}
			session.close();
			logger.debug("Session Hibernate clôturée.");
		}
	}

	/**
	 * Test la connection Hibernate, et renvoi son status boolean
	 * 
	 * @return boolean
	 */
	public boolean testConnection() {
		logger.info("Test de connexion Hibernate :");
		boolean ready = false;
		Session session = null;
		try {
			session = sessionFactory.openSession();
			if (session != null && session.isConnected()) {
				session.beginTransaction();
				ready = true;
				logger.info("Test de connexion Hibernate effectué avec succès.");
			}
		} catch (HibernateException he) {
			logger.error("Test de connexion Hibernate échoué !");
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return ready;
	}

	/**
	 * Récupère l'unique instance de HibernateUtil (Singleton)
	 * 
	 * @return DatabaseAccess
	 */
	public static synchronized HibernateUtil getInstance() {
		if (instance == null) {
			instance = new HibernateUtil();
		}
		return instance;
	}

}
