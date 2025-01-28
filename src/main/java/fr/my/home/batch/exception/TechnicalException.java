package fr.my.home.batch.exception;

/**
 * Classe modèle d'une exception de type technique
 * 
 * @author Jonathan
 * @version 1.1
 * @since 15/01/2025
 */
public class TechnicalException extends Exception {

	/**
	 * Attributs
	 */

	private static final long serialVersionUID = 930448801449184468L;
	private String message;

	/**
	 * Constructeur
	 */
	public TechnicalException(String message) {
		this.message = message;
	}

	/**
	 * Getters/Setters
	 */

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
