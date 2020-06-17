package com.creditbricks.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.creditbricks.model.Company;
import com.creditbricks.model.SalesAndCollection;

@Repository
public interface SalesAndCollectionRepository extends CrudRepository<SalesAndCollection, Long> {
	
	SalesAndCollection findByCompanyAndName(Company company, String name);

	List<SalesAndCollection>  findByCompany(Company company);

}
