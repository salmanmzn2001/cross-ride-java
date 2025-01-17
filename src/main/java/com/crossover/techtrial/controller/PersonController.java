/**
 * 
 */
package com.crossover.techtrial.controller;

import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.service.PersonService;

/**
 * @author crossover
 * This is my First change.
 */

@RestController
public class PersonController {

	@Autowired
	PersonService personService;

	@PostMapping(path = "/api/person")
	public ResponseEntity<Person> register(@Valid @RequestBody Person p) {
		return ResponseEntity.ok(personService.save(p));
	}

	@GetMapping(path = "/api/person")
	public ResponseEntity<List<Person>> getAllPersons() {
		List<Person> personList = personService.getAll();
		if(isSafe(personList)) {
			return ResponseEntity.ok(personList);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping(path = "/api/person/{person-id}")
	public ResponseEntity<Person> getPersonById(@PathVariable(name = "person-id", required = true) Long personId) {
		Person person = personService.findById(personId);
		if (person != null) {
			return ResponseEntity.ok(person);
		}
		return ResponseEntity.notFound().build();
	}

	private <T> boolean isSafe(Collection<T> list) {
		return !(list == null || list.isEmpty());
	}

}
