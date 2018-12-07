/**
 * 
 */
package com.crossover.techtrial.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "ride")
public class Ride implements Serializable {

	public Ride() {
	}

	public Ride(Long id, LocalDateTime startTime, LocalDateTime endTime, Double distance, Person driver, Person rider) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.distance = distance;
		this.driver = driver;
		this.rider = rider;
	}

	private static final long serialVersionUID = 9097639215351514001L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@PastOrPresent
	@Column(name = "start_time")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime startTime;

	@NotNull
	@PastOrPresent
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Positive
	@Column(name = "distance")
	@Digits(integer = 6, fraction = 2)
	private Double distance;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "driver_id", referencedColumnName = "id")
	Person driver;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "rider_id", referencedColumnName = "id")
	Person rider;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Person getDriver() {
		return driver;
	}

	public void setDriver(Person driver) {
		this.driver = driver;
	}

	public Person getRider() {
		return rider;
	}

	public void setRider(Person rider) {
		this.rider = rider;
	}

	@Override
	public String toString() {
		return "Ride [id=" + id + ", startTime=" + startTime + ", endTime=" + endTime + ", distance=" + distance
				+ ", driver=" + driver + ", rider=" + rider + "]";
	}

}
