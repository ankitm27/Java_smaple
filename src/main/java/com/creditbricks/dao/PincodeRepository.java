package com.creditbricks.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.creditbricks.model.Pincode;

@Repository
public interface PincodeRepository extends CrudRepository<Pincode, Long> {

	Pincode findById(long id);

	Pincode findByPincode(String pincode);
}
