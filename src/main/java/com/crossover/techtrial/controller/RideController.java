/**
 * 
 */
package com.crossover.techtrial.controller;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.service.RideService;

/**
 * RideController for Ride related APIs.
 * 
 * @author crossover
 *
 */
@RestController
public class RideController {

	@Autowired
	private RideService rideService;

	@PostMapping(path = "/api/ride")
	public ResponseEntity<Ride> createNewRide(@Valid @RequestBody Ride ride) {
		return ResponseEntity.ok(rideService.save(ride));
	}

	@GetMapping(path = "/api/ride/{ride-id}")
	public ResponseEntity<Ride> getRideById(@PathVariable(name = "ride-id", required = true) Long rideId) {
		Ride ride = rideService.findById(rideId);
		if (ride != null)
			return ResponseEntity.ok(ride);
		return ResponseEntity.notFound().build();
	}

	/**
	 * This API returns the top 5 drivers with their email,name, total minutes,
	 * maximum ride duration in minutes. Only rides that starts and ends within the
	 * mentioned durations should be counted. Any rides where either start or
	 * endtime is outside the search, should not be considered.
	 * 
	 * DONT CHANGE METHOD SIGNATURE AND RETURN TYPES
	 * 
	 * @return
	 */
	
	/*
	 * Requirement not clear, variables Names in TopDriverDTO says I need to return time in seconds ( which I am not supposed to change as per above instruction) but above mentioned explanation said in minutes.
	 * I am not changing variable names and will return in seconds.
	 */
	@GetMapping(path = "/api/top-rides")
	public ResponseEntity<List<TopDriverDTO>> getTopDriver(@Valid @RequestParam(value = "max", defaultValue = "5") Long count,
			@RequestParam(value = "startTime", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
			@RequestParam(value = "endTime", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime) {
		List<TopDriverDTO> topDrivers = rideService.getTopDrivers(startTime, endTime, count);
		if(isSafe(topDrivers)) {
			return ResponseEntity.ok(topDrivers);
		}
		return ResponseEntity.notFound().build();
	}
	
	private <T> boolean isSafe(Collection<T> list) {
		return !(list == null || list.isEmpty());
	}
}
