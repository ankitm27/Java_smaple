package com.creditbricks.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.creditbricks.model.BalanceSheet;
import com.creditbricks.model.Company;
import com.creditbricks.model.Product;;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
	Product findByCompanyAndProductName(Company company, String name);

	List<Product> findByCompany(Company company);

}
