package com.creditbricks.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.creditbricks.model.Company;
import com.creditbricks.model.TimeLog;

@Repository
public interface TimeLogRepository extends CrudRepository<TimeLog, Long> {

	List<TimeLog> findAllByCompany(Company company);	
}
