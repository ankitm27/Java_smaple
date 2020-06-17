package com.creditbricks.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.creditbricks.model.Company;
import com.creditbricks.model.Ledger;

@Repository
public interface LedgerRepository extends CrudRepository<Ledger, Long> {
	Ledger findByCompanyAndName(Company company, String name);

	List<Ledger> findByCompany(Company company);
}
