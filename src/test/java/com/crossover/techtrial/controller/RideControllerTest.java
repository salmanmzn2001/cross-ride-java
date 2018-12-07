package com.crossover.techtrial.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.PersonRepository;
import com.crossover.techtrial.repositories.RideRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RideControllerTest {
	
	MockMvc mockMvc;

	@Mock
	private Ride rideController;

	@Autowired
	private TestRestTemplate template;

	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private RideRepository rideRepository;

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(rideController).build();
	}

	private HttpEntity<Object> getHttpEntity(Object body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<Object>(body, headers);
	}

	@Test
	public void testRideShouldBeCreated() throws Exception {
		Long p1Id = createPerson("test","khush6@gmail.com","khush6");
		Long p2Id = createPerson("test","khush7@gmail.com","khush7");
		String jsonRequest= "{\"startTime\":\"2018-11-01T01:10:00\",\"endTime\":\"2018-11-01T01:20:00\",\"distance\":2.5,\"driver\":{\"id\":\""+p1Id+"\"},\"rider\":{\"id\":\""+p2Id+"\"}}";
		HttpEntity<Object> ride = getHttpEntity(jsonRequest);
		ResponseEntity<Ride> response = template.postForEntity("/api/ride", ride, Ride.class);
		deleteRideAndPerson(response.getBody().getId(),p1Id,p2Id);
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testRideShouldBeFound() throws Exception {
		Long p1Id = createPerson("test","khush8@gmail.com","khush8");
		Long p2Id = createPerson("test","khush9@gmail.com","khush9");
		Long rId = createRide(p1Id,p2Id);
		String url = "/api/ride/"+ rId;	
		ResponseEntity<Ride> response = template.getForEntity(url, Ride.class);
		deleteRideAndPerson(rId,p1Id,p2Id);
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testRideShouldNotBeFound() throws Exception {
		String url = "/api/ride/0";	
		ResponseEntity<Ride> response = template.getForEntity(url, Ride.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void testTopDriversShouldBeFound() throws Exception {
		Long p1Id = createPerson("test","khush10@gmail.com","khush10");
		Long p2Id = createPerson("test","khush11@gmail.com","khush11");
		Long rId = createRide(p1Id,p2Id);
		String url = "/api/top-rides?startTime=2018-11-01T01:00:00&endTime=2018-11-01T02:20:00";
		ResponseEntity<String>  response = template.exchange(url,HttpMethod.GET, null,String.class);	
		deleteRideAndPerson(rId,p1Id,p2Id);
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testTopDriversShouldNotBeFound() throws Exception {
		String url = "/api/top-rides?startTime=2019-11-01T01:00:00&endTime=2019-11-01T02:20:00";	
		ResponseEntity<Ride> response = template.getForEntity(url, Ride.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void testDateFormatError() throws Exception {
		String url = "/api/top-rides?startTime=-11-01T01:00:00&endTime=2019-11-01T02:20:00";	
		ResponseEntity<Ride> response = template.getForEntity(url, Ride.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testMissingFieldError() throws Exception {
		String url = "/api/top-rides?startTime=-11-01T01:00:00";	
		ResponseEntity<Ride> response = template.getForEntity(url, Ride.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testIncorrectDateFormatShouldNotBeAccepted() throws Exception {
		String jsonRequest= "{\"startTime\":\"201-11-0101:10:00\",\"endTime\":\"2018-11-01T01:20:00\",\"distance\":2.5,\"driver\":{\"id\":\"p1Id\"},\"rider\":{\"id\":\"p2Id\"}}";
		HttpEntity<Object> ride = getHttpEntity(jsonRequest);
		ResponseEntity<Ride> response = template.postForEntity("/api/ride", ride, Ride.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testDistanceShouldNotBeZero() throws Exception {
		String jsonRequest= "{\"startTime\":\"2018-11-01T01:10:00\",\"endTime\":\"2018-11-01T01:20:00\",\"distance\":0.0,\"driver\":{\"id\":\"p1Id\"},\"rider\":{\"id\":\"p2Id\"}}";
		HttpEntity<Object> ride = getHttpEntity(jsonRequest);
		ResponseEntity<Ride> response = template.postForEntity("/api/ride", ride, Ride.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testDriverAndRiderIdShouldNotBeSame() throws Exception {
		String jsonRequest= "{\"startTime\":\"2018-11-01T01:10:00\",\"endTime\":\"2018-11-01T01:20:00\",\"distance\":2.5,\"driver\":{\"id\":\"1\"},\"rider\":{\"id\":\"1\"}}";
		HttpEntity<Object> ride = getHttpEntity(jsonRequest);
		ResponseEntity<Ride> response = template.postForEntity("/api/ride", ride, Ride.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testMethodNotSupportedError() throws Exception {
		String url = "/api/top-rides?startTime=2018-11-01T01:00:00&endTime=2018-11-01T02:20:00";
		ResponseEntity<String>  response = template.exchange(url,HttpMethod.POST, null,String.class);	
		Assert.assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
	}
	
	@Test
	public void testStartTimeAfterEndTimeError() throws Exception {
		String jsonRequest= "{\"startTime\":\"2019-11-01T01:10:00\",\"endTime\":\"2018-11-01T01:20:00\",\"distance\":2.5,\"driver\":{\"id\":\""+1+"\"},\"rider\":{\"id\":\""+2+"\"}}";
		HttpEntity<Object> ride = getHttpEntity(jsonRequest);
		ResponseEntity<Ride> response = template.postForEntity("/api/ride", ride, Ride.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testMissingDriverIdError() throws Exception {
		String jsonRequest= "{\"startTime\":\"2018-11-01T01:10:00\",\"endTime\":\"2018-11-01T01:20:00\",\"distance\":2.5,\"driver\":{\"id\":\"p1Id\"}}";
		HttpEntity<Object> ride = getHttpEntity(jsonRequest);
		ResponseEntity<Ride> response = template.postForEntity("/api/ride", ride, Ride.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testMissingRideIdError() throws Exception {
		String jsonRequest= "{\"startTime\":\"2018-11-01T01:10:00\",\"endTime\":\"2018-11-01T01:20:00\",\"distance\":2.5,\"rider\":{\"id\":\"p1Id\"}}";
		HttpEntity<Object> ride = getHttpEntity(jsonRequest);
		ResponseEntity<Ride> response = template.postForEntity("/api/ride", ride, Ride.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testInvalidJsonFormatError() throws Exception {
		String jsonRequest= "{\"startTime\":\"2018-11-01T01:10:00\"\"endTime\":\"2018-11-01T01:20:00\",\"distance\":2.5\"rider\":{\"id\":\"p1Id\"}}";
		HttpEntity<Object> ride = getHttpEntity(jsonRequest);
		ResponseEntity<Ride> response = template.postForEntity("/api/ride", ride, Ride.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	private Long createPerson(String name, String email, String registrationNumber) {
		Person p = new Person(null,name,email,registrationNumber);
		return personRepository.save(p).getId();
	}
	
	private Long createRide(Long driverId, Long riderId) {
		Person driver = new Person(driverId,null,null,null);
		Person rider = new Person(riderId,null,null,null);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	    LocalDateTime startTime = LocalDateTime.parse("2018-11-01T01:10:00", formatter);
	    LocalDateTime endTime = LocalDateTime.parse("2018-11-01T01:20:00", formatter);
		Ride r = new Ride(null,startTime,endTime,2.5,driver,rider);
		return rideRepository.save(r).getId();
	}

	private void deleteRideAndPerson(Long rId, Long p1Id, Long p2Id) {
		rideRepository.deleteById(rId);	
		personRepository.deleteById(p1Id);
		personRepository.deleteById(p2Id);
	}

}
