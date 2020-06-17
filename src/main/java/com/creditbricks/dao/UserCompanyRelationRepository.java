package com.creditbricks.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.creditbricks.model.Company;
import com.creditbricks.model.User;
import com.creditbricks.model.UserCompanyRelation;

@Repository
public interface UserCompanyRelationRepository extends CrudRepository<UserCompanyRelation, Long> {

	UserCompanyRelation findByUserId(long id);
	UserCompanyRelation findByUser(User id);
	UserCompanyRelation findByCompany(Company compnay);
}
