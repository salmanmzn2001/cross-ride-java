/**
 * 
 */
package com.crossover.techtrial.repositories;

import com.crossover.techtrial.model.Ride;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * @author crossover
 *
 */
@RestResource(exported = false)
public interface RideRepository extends CrudRepository<Ride, Long> {

	List<Ride> findByStartTimeBetweenAndRiderIdEquals( LocalDateTime startTime, LocalDateTime endTime, Long riderId);
	List<Ride> findByEndTimeBetweenAndRiderIdEquals( LocalDateTime startTime, LocalDateTime endTime, Long riderId);
	List<Ride> findAllByStartTimeBetweenAndEndTimeBetween( LocalDateTime startTime1, LocalDateTime endTime1, LocalDateTime startTime2, LocalDateTime endTime2);
}
