package com.creditbricks.model;

import com.creditbricks.model.TimeLog;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class SalesAndCollection {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;

	private float creditAmt;

	private float debitAmt;

	public TimeLog getTimeLog() {
		return timeLog;
	}

	public void setTimeLog(TimeLog timeLog) {
		this.timeLog = timeLog;
	}

	@OneToOne
	private TimeLog timeLog;

	@OneToOne
	private Company company;

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




	public float getCreditAmt() {
		return creditAmt;
	}

	public void setCreditAmt(float creditAmt) {
		this.creditAmt = creditAmt;
	}

	public float getDebitAmt() {
		return debitAmt;
	}

	public void setDebitAmt(float debitAmt) {
		this.debitAmt = debitAmt;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
