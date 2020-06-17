package com.creditbricks.util;

import java.util.List;

import com.creditbricks.model.Voucher;
import com.creditbricks.model.VoucherItem;

public class VoucherItems {
	private Voucher voucher;
	private List<VoucherItem> voucherItems;

	public Voucher getVoucher() {
		return voucher;
	}
	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}
	public List<VoucherItem> getVoucherItems() {
		return voucherItems;
	}
	public void setVoucherItems(List<VoucherItem> voucherItems) {
		this.voucherItems = voucherItems;
	}


}
