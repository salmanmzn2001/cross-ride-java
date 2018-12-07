/**
 * 
 */
package com.crossover.techtrial.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author crossover
 *
 */
@Entity
@Table(name = "person")
public class Person implements Serializable {

	private static final long serialVersionUID = 7401548380514451401L;

	public Person() {
	}

	public Person(Long id, String name, String email, String registrationNumber) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.registrationNumber = registrationNumber;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	@Column(name = "name")
	@Size(min=2, max=240)
	@Pattern(regexp = "^[a-zA-Z ]+[-]*[a-zA-Z]+$")
	private String name;

	@NotEmpty
	@Email
	@Column(name = "email", nullable = false, unique = true)
	@Size(min=4, max=240)
	private String email;

	@NotEmpty
	@Column(name = "registration_number", nullable = false, unique = true)
	@Size(min=2, max=240)
	@Pattern(regexp = "[a-zA-Z0-9]*")
	private String registrationNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	
@Override
  public String toString() {
    return "Person [id=" + id + ", name=" + name + ", email=" + email + ", registrationNumber=" + registrationNumber + "]";
  }

}
