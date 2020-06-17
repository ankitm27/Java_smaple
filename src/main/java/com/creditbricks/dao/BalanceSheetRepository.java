package com.creditbricks.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.creditbricks.model.BalanceSheet;
import com.creditbricks.model.Company;

@Repository
public interface BalanceSheetRepository extends CrudRepository<BalanceSheet, Long> {
	
	BalanceSheet findByCompanyAndName(Company company, String name);

}
