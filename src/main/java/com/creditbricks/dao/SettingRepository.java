package com.creditbricks.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.creditbricks.model.Setting;
import com.creditbricks.model.UserCompanyRelation;

@Repository
public interface SettingRepository extends CrudRepository<Setting, Long> {

Setting findById(long id);
}
