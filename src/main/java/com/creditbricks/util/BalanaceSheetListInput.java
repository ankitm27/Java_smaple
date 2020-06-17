package com.creditbricks.util;

import java.util.List;

import com.creditbricks.model.BalanceSheet;
import com.creditbricks.model.Company;
import com.creditbricks.model.Customer;
import com.creditbricks.model.SalesAndCollection;



public class BalanaceSheetListInput {
	private SettingDO user;
	private Company company;
	private List<BalanceSheet> balanceSheets;
	private List<SalesAndCollection> salesAndCollectionList;
	private List<VoucherItems> voucherList;
	private List<Customer> customerList;
	
	private float totalSales = 0;
	public float getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(float totalSales) {
		this.totalSales = totalSales;
	}

	public float getTotalCollection() {
		return totalCollection;
	}

	public void setTotalCollection(float totalCollection) {
		this.totalCollection = totalCollection;
	}

	private float totalCollection = 0;
	



	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<BalanceSheet> getBalanceSheets() {
		return balanceSheets;
	}

	public void setBalanceSheets(List<BalanceSheet> balanceSheets) {
		this.balanceSheets = balanceSheets;
	}

	public SettingDO getUser() {
		return user;
	}

	public void setUser(SettingDO user) {
		this.user = user;
	}

	public List<SalesAndCollection> getSalesAndCollectionList() {
		return salesAndCollectionList;
	}

	public void setSalesAndCollectionList(List<SalesAndCollection> salesAndCollectionList) {
		this.salesAndCollectionList = salesAndCollectionList;
	}

	public List<VoucherItems> getVoucherList() {
		return voucherList;
	}

	public void setVoucherList(List<VoucherItems> voucherList) {
		this.voucherList = voucherList;
	}

	public List<Customer> getCustomerList() {
		return customerList;
	}

	public void setCustomerList(List<Customer> customerList) {
		this.customerList = customerList;
	}


	
	
	
	
	
}
