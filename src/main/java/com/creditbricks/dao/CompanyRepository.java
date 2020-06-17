package com.creditbricks.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.creditbricks.model.Company;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {

	Company findByCompanyUniqueId(String companyUniqueId);
	
	
}