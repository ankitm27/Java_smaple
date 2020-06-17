package com.creditbricks.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.creditbricks.model.Voucher;
import com.creditbricks.model.VoucherItem;

@Repository
public interface VoucherItemRepository extends CrudRepository<VoucherItem, Long> {

	VoucherItem findAllByVoucherKey(String voucherKey);

	//VoucherItem findByVoucher(Voucher voucher);
	
	List<VoucherItem> findByVoucher(Voucher voucher);
	List<VoucherItem> findByVoucher_Id(long id);
	


}
