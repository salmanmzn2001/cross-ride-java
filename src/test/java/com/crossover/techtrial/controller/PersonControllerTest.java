/**
 * 
 */
package com.crossover.techtrial.controller;

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
import com.crossover.techtrial.repositories.PersonRepository;

/**
 * @author Khushhal
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PersonControllerTest {

	MockMvc mockMvc;

	@Mock
	private PersonController personController;

	@Autowired
	private TestRestTemplate template;

	@Autowired
	private PersonRepository personRepository;

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
	}

	private HttpEntity<Object> getHttpEntity(Object body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<Object>(body, headers);
	}

	@Test
	public void testPanelShouldBeRegistered() throws Exception {
		HttpEntity<Object> person = getHttpEntity("{\"name\":\"test\",\"email\":\"khush@gmail.com\",\"registrationNumber\":\"khush\"}");
		ResponseEntity<Person> response = template.postForEntity("/api/person", person, Person.class);
		// Delete this user
		deletePerson(response.getBody().getId());
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testNameShouldNotBeAlphanumeric() throws Exception {
		HttpEntity<Object> person = getHttpEntity("{\"name\":\"test123\",\"email\":\"test@gmail.com\",\"registrationNumber\":\"test\"}");
		ResponseEntity<Person> response = template.postForEntity("/api/person", person, Person.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testEmailShouldNotBeDuplicate() throws Exception {
		Long id = createPerson("test", "same@gmail.com", "khush1");
		HttpEntity<Object> person = getHttpEntity("{\"name\":\"test\",\"email\":\"same@gmail.com\",\"registrationNumber\":\"test\"}");
		ResponseEntity<Person> response = template.postForEntity("/api/person", person, Person.class);
		deletePerson(id);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testRegistrationNumberShouldNotBeDuplicate() throws Exception {
		Long id = createPerson("test", "khush2@gmail.com", "same");
		HttpEntity<Object> person = getHttpEntity("{\"name\":\"test\",\"email\":\"khush3@gmail.com\",\"registrationNumber\":\"same\"}");
		ResponseEntity<Person> response = template.postForEntity("/api/person", person, Person.class);
		deletePerson(id);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testGetAllPersonShouldBeFound() throws Exception {
		Long id = createPerson("test", "khush4@gmail.com", "khush4");
		ResponseEntity<String>  response = template.exchange("/api/person",HttpMethod.GET, null,String.class);
		deletePerson(id);
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testPersonShouldBeFound() throws Exception {
		Long id = createPerson("test", "khush5@gmail.com", "khush5");
		String url = "/api/person/"+ id;
		ResponseEntity<Person> response = template.getForEntity(url, Person.class);
		deletePerson(id);
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testDataShouldNotBeFound() throws Exception {		
		ResponseEntity<Person> response = template.getForEntity("/api/person/0", Person.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	private Long createPerson(String name, String email, String registrationNumber) {
		Person p = new Person(null,name,email,registrationNumber);
		return personRepository.save(p).getId();
	}

	private void deletePerson(Long id) {
		personRepository.deleteById(id);		
	}
}
