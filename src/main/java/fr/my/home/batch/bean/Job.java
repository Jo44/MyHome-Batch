package fr.my.home.batch.bean;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Bean Job
 * 
 * @author Jonathan
 * @version 1.0
 * @since 18/01/2025
 */
@Entity
@Table(name = "jobs")
public class Job {

	/**
	 * Attributs
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "processed", nullable = false)
	private int processed;
	@Column(name = "finished", nullable = false)
	private boolean finished;
	@Column(name = "status", nullable = false)
	private boolean status;
	@Column(name = "start_time", nullable = false)
	private Timestamp start_time;
	@Column(name = "finish_time", nullable = true)
	private Timestamp finish_time;

	/**
	 * Constructeur
	 */
	public Job() {}

	/**
	 * Constructeur
	 * 
	 * @param name
	 * @param processed
	 * @param finished
	 * @param status
	 * @param startTime
	 * @param finishTime
	 */
	public Job(String name, int processed, boolean finished, boolean status, Timestamp start_time, Timestamp finish_time) {
		this();
		this.name = name;
		this.processed = processed;
		this.finished = finished;
		this.status = status;
		this.start_time = start_time;
		this.finish_time = finish_time;
	}

	/**
	 * Getters / Setters
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

	public int getProcessed() {
		return processed;
	}

	public void setProcessed(int processed) {
		this.processed = processed;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Timestamp getStartTime() {
		return start_time;
	}

	public void setStartTime(Timestamp start_time) {
		this.start_time = start_time;
	}

	public Timestamp getFinishTime() {
		return finish_time;
	}

	public void setFinishTime(Timestamp finish_time) {
		this.finish_time = finish_time;
	}

}
