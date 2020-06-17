package com.creditbricks.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Company implements Serializable {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private long id;
	private String name;
	private String companyUniqueId;
	private String state;
	private LocalDateTime lastSync;
	

	public String getCompanyUniqueId() {
		return companyUniqueId;
	}

	public void setCompanyUniqueId(String companyUniqueId) {
		this.companyUniqueId = companyUniqueId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getLastSync() {
		return lastSync;
	}

	public void setLastSync(LocalDateTime lastSync) {
		this.lastSync = lastSync;
	}

	
	

	


	
	
	
	
	
	
}
