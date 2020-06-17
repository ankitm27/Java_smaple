package com.creditbricks.model;

import com.creditbricks.model.TimeLog;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class BalanceSheet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;

	private String bssubamt;

	private String bsmainamt;

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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getBssubamt() {
		return bssubamt;
	}

	public void setBssubamt(String bssubamt) {
		this.bssubamt = bssubamt;
	}

	public String getBsmainamt() {
		return bsmainamt;
	}

	public void setBsmainamt(String bsmainamt) {
		this.bsmainamt = bsmainamt;
	}

}
