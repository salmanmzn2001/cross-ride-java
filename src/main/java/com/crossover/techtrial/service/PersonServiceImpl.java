/**
 * 
 */
package com.crossover.techtrial.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crossover.techtrial.exceptions.ValidateException;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.repositories.PersonRepository;

/**
 * @author crossover
 *
 */
@Service
public class PersonServiceImpl implements PersonService {

	@Autowired
	private PersonRepository personRepository;

	@Override
	public List<Person> getAll() {
		List<Person> personList = new ArrayList<>();
		personRepository.findAll().forEach(personList::add);
		return personList;

	}

	@Override
	public Person save(Person p) {
		validateRequest(p);
		return personRepository.save(p);
	}

	@Override
	public Person findById(Long personId) {
		Optional<Person> dbPerson = personRepository.findById(personId);
		return dbPerson.orElse(null);
	}

	private Person findByRegistrationNumber(String registrationNumber) {
		Optional<Person> dbPerson = personRepository.findByRegistrationNumber(registrationNumber);
		return dbPerson.orElse(null);
	}
	
	private Person findByEmail(String email) {
		Optional<Person> dbPerson = personRepository.findByEmail(email);
		return dbPerson.orElse(null);
	}
	
	private void validateRequest(Person p) {
		
		if (isRigistrationNotUnique(p.getRegistrationNumber())) {
			returnError("Registration Number : " + p.getRegistrationNumber() + " already exist in Database.");
		}

		if (isEmailNotUnique(p.getEmail())) {
			returnError(p.getEmail() + " is already registed. Please use different email address.");
		}

	}

	private boolean isEmailNotUnique(String email) {
		Person dbPerson2 =findByEmail(email);
		return dbPerson2 != null && dbPerson2.getEmail().equals(email);
	}

	private boolean isRigistrationNotUnique(String registrationNumber) {
		Person dbPerson1 =findByRegistrationNumber(registrationNumber);
		return dbPerson1 != null && dbPerson1.getRegistrationNumber().equals(registrationNumber);
	}

	private void returnError(String message) {
		throw ValidateException.of().message(message).build();
	}
	

}
