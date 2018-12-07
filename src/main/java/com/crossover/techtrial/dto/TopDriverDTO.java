/**
 * 
 */
package com.crossover.techtrial.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author crossover
 *
 */
public class TopDriverDTO {

	/**
	 * Constructor for TopDriverDTO
	 * 
	 * @param name
	 * @param email
	 * @param totalRideDurationInSeconds
	 * @param maxRideDurationInSecods
	 * @param averageDistance
	 */
	public TopDriverDTO(String name, String email, Long totalRideDurationInSeconds, Long maxRideDurationInSecods,
			Double averageDistance) {
		this.setName(name);
		this.setEmail(email);
		this.setAverageDistance(averageDistance);
		this.setMaxRideDurationInSecods(maxRideDurationInSecods);
		this.setTotalRideDurationInSeconds(totalRideDurationInSeconds);
	}

	public TopDriverDTO() {

	}
	
	private String name;

	private String email;

	private Long totalRideDurationInSeconds=0L;

	private Long maxRideDurationInSecods=0L;

	private Double averageDistance=0.0;

	@JsonIgnore
	private Integer numberOfRides=0;
	
	@JsonIgnore
	private Long driverId;
	
	@JsonIgnore
	private Double totalDistance=0.0;

	public Double getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(Double totalDistance) {
		this.totalDistance = totalDistance;
	}

	public Integer getNumberOfRides() {
		return numberOfRides;
	}

	public void setNumberOfRides(Integer numberOfRides) {
		this.numberOfRides = numberOfRides;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getTotalRideDurationInSeconds() {
		return totalRideDurationInSeconds;
	}

	public void setTotalRideDurationInSeconds(Long totalRideDurationInSeconds) {
		this.totalRideDurationInSeconds = totalRideDurationInSeconds;
	}

	public Long getMaxRideDurationInSecods() {
		return maxRideDurationInSecods;
	}

	public void setMaxRideDurationInSecods(Long maxRideDurationInSecods) {
		this.maxRideDurationInSecods = maxRideDurationInSecods;
	}

	public Double getAverageDistance() {
		return averageDistance;
	}

	public void setAverageDistance(Double averageDistance) {
		this.averageDistance = averageDistance;
	}

}
