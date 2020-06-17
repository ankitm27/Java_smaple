package com.creditbricks.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.creditbricks.model.Company;
import com.creditbricks.model.Customer;
import com.creditbricks.model.Voucher;
import java.util.Date;


@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

	Customer findById(long id);

	List<Customer> findByCompany(Company company);
	Customer findByName(String name);

	Customer findByCompanyAndName(Company company, String name);
	

}
