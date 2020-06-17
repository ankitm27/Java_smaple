package com.creditbricks.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
public class Customer implements Serializable {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private long id;
	
	private long userId;
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	private String name;
	
	private String pincode;
	
	private String address;
		
	private String state;
	private long accountNo;
	private String bankName;
	private String IFSCCode;
	private String BranchName;
	private String bankAddress;
	
	private String panapplicablefrom;
	public String getPartygstin() {
		return partygstin;
	}

	public void setPartygstin(String partygstin) {
		this.partygstin = partygstin;
	}

	public String getTaxtype() {
		return taxtype;
	}

	public void setTaxtype(String taxtype) {
		this.taxtype = taxtype;
	}

	public String getGstregistrationtype() {
		return gstregistrationtype;
	}

	public void setGstregistrationtype(String gstregistrationtype) {
		this.gstregistrationtype = gstregistrationtype;
	}

	private String partygstin;
	private String taxtype;
	private String gstregistrationtype;
	
	private int creditAmount = 0;

	private int creditPeroid = 0;
	
	public TimeLog getTimeLog() {
		return timeLog;
	}

	public void setTimeLog(TimeLog timeLog) {
		this.timeLog = timeLog;
	}

	@OneToOne
	private TimeLog timeLog;

	@OneToOne(fetch = FetchType.EAGER)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Company company;
	
	
	private LocalDateTime lastSync;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(int creditAmount) {
		this.creditAmount = creditAmount;
	}

	public int getCreditPeroid() {
		return creditPeroid;
	}

	public void setCreditPeroid(int creditperoid) {
		this.creditPeroid = creditperoid;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public LocalDateTime getLastSync() {
		return lastSync;
	}

	public void setLastSync(LocalDateTime lastSync) {
		this.lastSync = lastSync;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getPanapplicablefrom() {
		return panapplicablefrom;
	}

	public void setPanapplicablefrom(String panapplicablefrom) {
		this.panapplicablefrom = panapplicablefrom;
	}

	public long getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(long accountNo) {
		this.accountNo = accountNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIFSCCode() {
		return IFSCCode;
	}

	public void setIFSCCode(String iFSCCode) {
		IFSCCode = iFSCCode;
	}

	public String getBranchName() {
		return BranchName;
	}

	public void setBranchName(String branchName) {
		BranchName = branchName;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Customer [name ="+ name +"]"; 
	}
	
	
	
}
