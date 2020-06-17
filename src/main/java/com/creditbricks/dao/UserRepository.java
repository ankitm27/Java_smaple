package com.creditbricks.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.creditbricks.model.Company;
import com.creditbricks.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	User findById(String id);
	User findByEmail(String email);
	User findByLoginIdAndPassword(String loginId,String password);
	User findByEmailAndPassword(String email,String password);
	User findByPhoneNumberAndPassword(String phoneNumber,String password);
	List<User> findByEmailOrPhoneNumber(String email,String phoneNumber);
	User findByAndroidId(String androidId);
	
}