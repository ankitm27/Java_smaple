package com.creditbricks.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;



@Entity
public class Setting implements Serializable  {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private long id;
	
	private String currencyType;
	
	
	private Date startDate;
	
//	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
//	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	
	
	private int targetYearly;
	
	private int userId;
	
	private int janTarget;
	private int febTarget;
	private int marTarget;
	private int aprTarget;
	private int mayTarget;
	private int junTarget;
	private int julTarget;
	private int augTarget;
	private int sptTarget;
	private int octTarget;
	private int novTarget;
	private int decTarget;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getTargetYearly() {
		return targetYearly;
	}

	public void setTargetYearly(int targetYearly) {
		this.targetYearly = targetYearly;
	}

	public int getJanTarget() {
		return janTarget;
	}

	public void setJanTarget(int janTarget) {
		this.janTarget = janTarget;
	}

	public int getFebTarget() {
		return febTarget;
	}

	public void setFebTarget(int febTarget) {
		this.febTarget = febTarget;
	}

	public int getMarTarget() {
		return marTarget;
	}

	public void setMarTarget(int marTarget) {
		this.marTarget = marTarget;
	}

	public int getAprTarget() {
		return aprTarget;
	}

	public void setAprTarget(int aprTarget) {
		this.aprTarget = aprTarget;
	}

	public int getMayTarget() {
		return mayTarget;
	}

	public void setMayTarget(int mayTarget) {
		this.mayTarget = mayTarget;
	}

	public int getJunTarget() {
		return junTarget;
	}

	public void setJunTarget(int junTarget) {
		this.junTarget = junTarget;
	}

	public int getJulTarget() {
		return julTarget;
	}

	public void setJulTarget(int julTarget) {
		this.julTarget = julTarget;
	}

	public int getAugTarget() {
		return augTarget;
	}

	public void setAugTarget(int augTarget) {
		this.augTarget = augTarget;
	}

	public int getSptTarget() {
		return sptTarget;
	}

	public void setSptTarget(int sptTarget) {
		this.sptTarget = sptTarget;
	}

	public int getOctTarget() {
		return octTarget;
	}

	public void setOctTarget(int octTarget) {
		this.octTarget = octTarget;
	}

	public int getNovTarget() {
		return novTarget;
	}

	public void setNovTarget(int novTarget) {
		this.novTarget = novTarget;
	}

	public int getDecTarget() {
		return decTarget;
	}

	public void setDecTarget(int decTarget) {
		this.decTarget = decTarget;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
	
}
