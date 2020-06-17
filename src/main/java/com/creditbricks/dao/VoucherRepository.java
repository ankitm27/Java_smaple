package com.creditbricks.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.creditbricks.model.Company;
import com.creditbricks.model.Voucher;
import java.util.Date;

@Repository
public interface VoucherRepository extends CrudRepository<Voucher, Long> {

	Voucher findByGuid(String guid);

	List<Voucher> findAllByCompany(Company company);

	List<Voucher> findAllByCompanyAndVoucherType(Company company, String voucherType);

	List<Voucher> findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
			Company company, String voucherType, Date startDate, Date endDate);

	List<Voucher> findAllByCompanyAndVoucherTypeAndStateAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
			Company company, String voucherType, String state, Date startDate, Date endDate);

	List<Voucher> findAllByCompanyAndVoucherTypeAndStateAndAccountNameAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
			Company company, String voucherType, String state, String accountName, Date startDate, Date endDate);

	List<Voucher> findAllByCompanyAndVoucherTypeAndAccountNameAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
			Company company, String voucherType, String accountName, Date startDate, Date endDate);
	
	List<Voucher> findAllByCompanyAndVoucherTypeAndAccountNameAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
			Company company, String voucherType, String accountName, Date startDate, Date endDate);

	List<Voucher> findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
			Company company, String voucherType, Date startDate, Date endDate);
	
	List<Voucher> findAllByCompanyAndVoucherNoAndAccountName(
			Company company, String voucherNo, String accountName);
}
