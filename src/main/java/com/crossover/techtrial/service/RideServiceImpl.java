/**
 * 
 */
package com.crossover.techtrial.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.exceptions.ValidateException;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.RideRepository;

/**
 * @author crossover
 *
 */
@Service
public class RideServiceImpl implements RideService {

	@Autowired
	private RideRepository rideRepository;

	@Autowired
	private PersonService personService;

	@Override
	public Ride save(Ride ride) {
		validateRequest(ride);
		return rideRepository.save(ride);
	}

	@Override
	public Ride findById(Long rideId) {
		Optional<Ride> optionalRide = rideRepository.findById(rideId);
		return optionalRide.orElse(null);
	}
	
	@Override
	public List<TopDriverDTO> getTopDrivers(LocalDateTime startTime, LocalDateTime endTime, Long count){
		List<Ride> rideList = rideRepository.findAllByStartTimeBetweenAndEndTimeBetween(startTime, endTime, startTime, endTime);
		List<TopDriverDTO> driversDataList = processDriversData(rideList);
		return topDrivers(driversDataList, count);	
	}
	
	private List<TopDriverDTO> topDrivers(List<TopDriverDTO> driversDataList, Long count) {
		return safe(driversDataList).stream().sorted(Comparator.comparing(TopDriverDTO::getTotalRideDurationInSeconds).reversed()).collect(Collectors.toList()).subList(0, min(driversDataList.size(),count.intValue()));
	}

	private int min(int size, int count) {		
		return size<count?size:count;
	}

	private List<TopDriverDTO> processDriversData(List<Ride> rideList) {
		
		List<Ride> sortedRideByDriverId = safe(rideList).stream().sorted(Comparator.comparing(r->r.getDriver().getId())).collect(Collectors.toList());
		
		List<TopDriverDTO> driverList = new ArrayList<>();
		AtomicInteger rCount = new AtomicInteger(0);
		AtomicInteger i = new AtomicInteger(-1);
		
		safe(sortedRideByDriverId).forEach(r->{
			
			if(driverList.isEmpty() || !r.getDriver().getId().equals(driverList.get(i.get()).getDriverId())) {
				TopDriverDTO driverDto = new TopDriverDTO();
				driverList.add(driverDto);
				i.getAndIncrement();
				rCount.set(0);
			}		
			
			setDriverData(r, driverList.get(i.get()), rCount.incrementAndGet());
		});
		
		return driverList;
	}
	
	private void setDriverData(Ride ride, TopDriverDTO driver, int rCount) {
		
		driver.setDriverId(ride.getDriver().getId());
		driver.setName(ride.getDriver().getName());
		driver.setEmail(ride.getDriver().getEmail());
		driver.setTotalRideDurationInSeconds(calculateTotalRideDuration(ride,driver));
		driver.setMaxRideDurationInSecods(calculateMaxRideDuration(ride,driver));
		driver.setTotalDistance(calculateTotalDistance(ride,driver));
		driver.setNumberOfRides(rCount);
		driver.setAverageDistance(calculateAverageDistance(driver));		
	}

	private Double calculateAverageDistance(TopDriverDTO driver) {
		return driver.getTotalDistance()/driver.getNumberOfRides();
	}

	private Double calculateTotalDistance(Ride ride, TopDriverDTO driver) {
		return driver.getTotalDistance()+ ride.getDistance();
	}

	private Long calculateMaxRideDuration(Ride ride, TopDriverDTO driver) {
		Long rideDuration = calculateDuration(ride.getStartTime(), ride.getEndTime());
		return rideDuration>driver.getMaxRideDurationInSecods()?rideDuration:driver.getMaxRideDurationInSecods();
	}

	private Long calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
		return Duration.between(startTime, endTime).getSeconds();
	}

	private Long calculateTotalRideDuration(Ride ride, TopDriverDTO driver) {
		return driver.getTotalRideDurationInSeconds()+calculateDuration(ride.getStartTime(), ride.getEndTime());
	}
	
	private  <T> Collection<T> safe(Collection<T> list) {
	    return Optional.ofNullable(list).orElseGet(Collections::emptyList);
	}

	private <T> boolean isSafe(Collection<T> list) {
		return !(list == null || list.isEmpty());
	}

	private void validateRequest(Ride ride) {
		if (isStartTimeAfterEndTime(ride)) {
			returnError("Invalid Request. startTime should not be after endTime.");
		}
		if (null == ride.getDriver().getId()) {
			returnError("Mandatory field missing. Field : driver.Id.");
		}

		if (null == ride.getRider().getId()) {
			returnError("Mandatory field missing. Field : rider.Id.");
		}

		if (isDriverNotRegistered(ride)) {
			returnError("Driver Id : " + ride.getDriver().getId()
					+ " is not Registered with us. Please use valid Driver Id.");
		}

		if (isRiderNotRegistered(ride)) {
			returnError("Rider Id : " + ride.getRider().getId()
					+ " is not Registered  with us. Please use valid Rider Id.");
		}

		if (isDriverAndRiderIdNotDifferent(ride)) {
			returnError("Rider Id and Driver Id must not be same.");
		}

		/*
		 * It is clear from requirement that a driver can have overlapping rides. But
		 * not clear for riders. Logically a rider should not have overlapping riders
		 * unlesss he is booking for someone else. With this assumption, I am
		 * considering overlapping rides not allowed for rides.
		 */

		if (isRideOverlappingForRider(ride)) {
			returnError(
					"Logical Validation Error : A Rider can not have overlapping rides. An entry is already present for Rider Id : "
							+ ride.getRider().getId() + " in same time frame.");
		}
	}

	private boolean isStartTimeAfterEndTime(Ride ride) {
		return ride.getStartTime().isAfter(ride.getEndTime());
	}

	private boolean isDriverAndRiderIdNotDifferent(Ride ride) {
		return ride.getDriver().getId().equals(ride.getRider().getId());
	}

	private boolean isRideOverlappingForRider(Ride ride) {
		List<Ride> rideList = findRiderOverlappingRides(ride.getStartTime(), ride.getEndTime(),
				ride.getRider().getId());
		return !rideList.isEmpty();
	}
	
	private List<Ride> findRiderOverlappingRides(LocalDateTime startTime, LocalDateTime endTime, Long riderId) {
		List<Ride> rideList = rideRepository.findByStartTimeBetweenAndRiderIdEquals(startTime, endTime, riderId);
		if (!isSafe(rideList))
			rideList = rideRepository.findByEndTimeBetweenAndRiderIdEquals(startTime, endTime, riderId);
		return Optional.ofNullable(rideList).orElseGet(Collections::emptyList);
	}

	private boolean isRiderNotRegistered(Ride ride) {
		Person dbPerson = personService.findById(ride.getRider().getId());
		if (dbPerson != null) {
			ride.setRider(dbPerson);
		}
		return dbPerson == null;
	}

	private boolean isDriverNotRegistered(Ride ride) {
		Person dbPerson = personService.findById(ride.getDriver().getId());
		if (dbPerson != null) {
			ride.setDriver(dbPerson);
		}
		return dbPerson == null;
	}

	private void returnError(String message) {
		throw ValidateException.of().message(message).build();
	}

}
