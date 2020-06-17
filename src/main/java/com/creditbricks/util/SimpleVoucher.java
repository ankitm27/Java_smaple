package com.creditbricks.util;

import com.creditbricks.model.Customer;

public class SimpleVoucher implements Comparable< SimpleVoucher > {
	public String name ="";
	public double value = 0;
	public String voucherGuid ="";
	public String voucherDate="";
	public double sales = 0;
	public double credit = 0;
	public double receivable = 0;
	public double creditLimit = 0;
	public double creditUtilize = 0;
	public String address ="";
	public String dueDate="";
	public float arrearDays=0;
	public int credirPeroid=0;
	public String refNumber="";
	public String againrefNumber="";
	public String methodOfAdj="";
	public double amountAgainst=0;
	Customer customer;
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVoucherGuid() {
		return voucherGuid;
	}
	public void setVoucherGuid(String voucherGuid) {
		this.voucherGuid = voucherGuid;
	}
	public String getVoucherDate() {
		return voucherDate;
	}
	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}
	public double getReceivable() {
		return receivable;
	}
	public void setReceivable(double receivable) {
		this.receivable = receivable;
	}
	public double getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(double creditLimit) {
		this.creditLimit = creditLimit;
	}
	public double getCreditUtilize() {
		return creditUtilize;
	}
	public void setCreditUtilize(double creditUtilize) {
		this.creditUtilize = creditUtilize;
	}
	public double getSales() {
		return sales;
	}
	public void setSales(double sales) {
		this.sales = sales;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	
	 public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	
	
	public String getRefNumber() {
		return refNumber;
	}
	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	
	public float getArrearDays() {
		return arrearDays;
	}
	public void setArrearDays(float arrearDays) {
		this.arrearDays = arrearDays;
	}
	public int getCredirPeroid() {
		return credirPeroid;
	}
	public void setCredirPeroid(int credirPeroid) {
		this.credirPeroid = credirPeroid;
	}
	
	
	public String getAgainrefNumber() {
		return againrefNumber;
	}
	public void setAgainrefNumber(String againrefNumber) {
		this.againrefNumber = againrefNumber;
	}
	public String getMethodOfAdj() {
		return methodOfAdj;
	}
	public void setMethodOfAdj(String methodOfAdj) {
		this.methodOfAdj = methodOfAdj;
	}
	
	
	public double getAmountAgainst() {
		return amountAgainst;
	}
	public void setAmountAgainst(double amountAgainst) {
		this.amountAgainst = amountAgainst;
	}
	@Override
	    public String toString() {
	        return "SimpleVoucher [Value=" + value + "]" +" [Name=" + name + "]";
	    }
	 
	    @Override
	    public int compareTo(SimpleVoucher o) {
	    	Integer compareVal=(int)o.getValue();
	    	Integer realVal=(int)this.getValue();
	    	
	    	return realVal.compareTo(compareVal);
	    }
	

}
