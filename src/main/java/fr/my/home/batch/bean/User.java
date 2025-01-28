package fr.my.home.batch.bean;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Bean User
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
@Entity
@Table(name = "users")
public class User {

	/**
	 * Attributs
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "name", nullable = false, unique = true)
	private String name;
	@Column(name = "pass", nullable = false)
	private String pass;
	@Column(name = "email", nullable = false, unique = true)
	private String email;
	@Column(name = "remember_me_token", nullable = true)
	private String remember_me_token;
	@Column(name = "validation_token", nullable = false)
	private String validation_token;
	@Column(name = "active", nullable = false)
	private boolean active;
	@Column(name = "reinit_token", nullable = true)
	private String reinit_token;
	@Column(name = "reinit_date", nullable = true)
	private Timestamp reinit_date;
	@Column(name = "access_token", nullable = true)
	private String access_token;
	@Column(name = "last_log_date", nullable = true)
	private Timestamp last_log_date;
	@Column(name = "inscription_date", nullable = false)
	private Timestamp inscription_date;
	@Column(name = "last_update", nullable = true)
	private Timestamp last_update;

	/**
	 * Constructeur
	 */
	public User() {}

	/**
	 * Constructeur
	 * 
	 * @param name
	 * @param pass
	 * @param email
	 * @param remember_me_token
	 * @param validation_token
	 * @param active
	 * @param reinit_token
	 * @param reinit_date
	 * @param access_token
	 * @param last_log_date
	 * @param inscription_date
	 * @param last_update
	 */
	public User(String name, String pass, String email, String remember_me_token, String validation_token, boolean active, String reinit_token,
			Timestamp reinit_date, String access_token, Timestamp last_log_date, Timestamp inscription_date, Timestamp last_update) {
		this.name = name;
		this.pass = pass;
		this.email = email;
		this.remember_me_token = remember_me_token;
		this.validation_token = validation_token;
		this.active = active;
		this.reinit_token = reinit_token;
		this.reinit_date = reinit_date;
		this.access_token = access_token;
		this.last_log_date = last_log_date;
		this.inscription_date = inscription_date;
		this.last_update = last_update;
	}

	/**
	 * Getters/Setters
	 */

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRememberMeToken() {
		return remember_me_token;
	}

	public void setRememberMeToken(String remember_me_token) {
		this.remember_me_token = remember_me_token;
	}

	public String getValidationToken() {
		return validation_token;
	}

	public void setValidationToken(String validation_token) {
		this.validation_token = validation_token;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getReInitToken() {
		return reinit_token;
	}

	public void setReInitToken(String reinit_token) {
		this.reinit_token = reinit_token;
	}

	public Timestamp getReInitDate() {
		return reinit_date;
	}

	public void setReInitDate(Timestamp reinit_date) {
		this.reinit_date = reinit_date;
	}

	public String getAccessToken() {
		return access_token;
	}

	public void setAccessToken(String access_token) {
		this.access_token = access_token;
	}

	public Timestamp getLastLogDate() {
		return last_log_date;
	}

	public void setLastLogDate(Timestamp last_log_date) {
		this.last_log_date = last_log_date;
	}

	public Timestamp getInscriptionDate() {
		return inscription_date;
	}

	public void setInscriptionDate(Timestamp inscription_date) {
		this.inscription_date = inscription_date;
	}

	public Timestamp getLastUpdate() {
		return last_update;
	}

	public void setLastUpdate(Timestamp update) {
		this.last_update = update;
	}

}
