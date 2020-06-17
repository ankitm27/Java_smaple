package com.creditbricks.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.creditbricks.dao.BalanceSheetRepository;
import com.creditbricks.dao.CompanyRepository;
import com.creditbricks.dao.CustomerRepository;
import com.creditbricks.dao.LedgerRepository;
import com.creditbricks.dao.SalesAndCollectionRepository;
import com.creditbricks.dao.SettingRepository;
import com.creditbricks.dao.TimeLogRepository;
import com.creditbricks.dao.UserCompanyRelationRepository;
import com.creditbricks.dao.VoucherItemRepository;
import com.creditbricks.dao.VoucherRepository;
import com.creditbricks.model.Company;
import com.creditbricks.model.Customer;
import com.creditbricks.model.Voucher;
import com.creditbricks.model.VoucherItem;
import com.creditbricks.util.Message;
import com.creditbricks.util.SimpleVoucher;
import com.creditbricks.util.Util;

import static java.util.Collections.reverseOrder;

@CrossOrigin
@RestController
@RequestMapping("/")
public class ReveivableController {

	@Autowired
	BalanceSheetRepository balanceSheetRepository;
	@Autowired
	SalesAndCollectionRepository salesAndCollectionRepository;
	@Autowired
	LedgerRepository ledgerRepository;
	@Autowired
	VoucherRepository voucherRepository;
	@Autowired
	VoucherItemRepository voucherItemRepository;
	@Autowired
	TimeLogRepository timeLogRepository;
	@Autowired
	CompanyRepository companyRepository;
	@Autowired
	SettingRepository settingRepository;
	@Autowired
	UserCompanyRelationRepository userCompanyRelationRepository;
	@Autowired
	CustomerRepository customerRepository;
	private Object setSale;

	
	/**
	 * Recivable of cutomer for state
	 * 
	 */
	@PostMapping("/getReceivableStateListByDate/{state}/{subtype}/{startdate}/{enddate}")
	@CrossOrigin
	public Message getReceivableStateListByDate(@RequestBody Company company,
			@PathVariable(name = "state", value = "state", required = true) String state,
			@PathVariable(name = "subtype", value = "subtype", required = true) String subtype,
			@PathVariable(name = "startdate", value = "startdate", required = true) String startdate,
			@PathVariable(name = "enddate", value = "enddate", required = true) String enddate) {

		// UserCompanyRelation ucr =
		// userCompanyRelationRepository.findByUserId(user.getId());
		try {
			Message message = Util.returnResult(206, "List ", "", "");
			List<Voucher> voucherListSales;
			List<Voucher> voucherListCredit;

			double totalCost = 0;
			if (state.equalsIgnoreCase("All")) {
				voucherListSales = voucherRepository
						.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
								company, "Sales", Util.convertStrToDateYYYMMDD(startdate),
								Util.convertStrToDateYYYMMDD(enddate));
				voucherListCredit = voucherRepository
						.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
								company, "Receipt", Util.convertStrToDateYYYMMDD(startdate),
								Util.convertStrToDateYYYMMDD(enddate));

			} else {
				voucherListSales = voucherRepository
						.findAllByCompanyAndVoucherTypeAndStateAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
								company, "Sales", state, Util.convertStrToDateYYYMMDD(startdate),
								Util.convertStrToDateYYYMMDD(enddate));
				voucherListCredit = voucherRepository
						.findAllByCompanyAndVoucherTypeAndStateAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
								company, "Receipt", state, Util.convertStrToDateYYYMMDD(startdate),
								Util.convertStrToDateYYYMMDD(enddate));

			}
			List<SimpleVoucher> listToCreate = new ArrayList<SimpleVoucher>();
			System.out.println("voucherList Size >> " + voucherListSales.size());
			if (subtype.equalsIgnoreCase("customer")) {

				List<Object> finalList = new ArrayList<>();
				List<List<SimpleVoucher>> fullListSales = new ArrayList<>();
				Map<String, List<List<SimpleVoucher>>> cutomerMonthMapSales = new HashMap<>();
				List<SimpleVoucher> listToCreateSales = new ArrayList<SimpleVoucher>();

				List<List<SimpleVoucher>> fullListCredit = new ArrayList<>();
				Map<String, List<List<SimpleVoucher>>> cutomerMonthMapCredit = new HashMap<>();
				List<SimpleVoucher> listToCreateCredit = new ArrayList<SimpleVoucher>();

				// List<List<SimpleVoucher>> fullListReceivalble = new
				// ArrayList<>();
				Map<String, List<List<SimpleVoucher>>> cutomerMonthMapReceivalble = new HashMap<>();
				// List<SimpleVoucher> listToCreateReceivalble = new
				// ArrayList<SimpleVoucher>();
				List<List<SimpleVoucher>> totalListReceivalble = new ArrayList<>();
				// List<SimpleVoucher> listToTotalReceivalble = new
				// ArrayList<SimpleVoucher>();

				double totalCostSales = 0, totalCostCredit = 0;

				listToCreateSales = getCutomerList(voucherListSales,-1);
				fullListSales = getTotlaOfAll(voucherListSales, null,startdate, enddate);
				totalCostSales = fullListSales.get(fullListSales.size() - 1).get(0).getValue();
				cutomerMonthMapSales = getAllListOfCutomer(voucherListSales, listToCreateSales, totalCostSales,
						startdate, enddate);

				listToCreateCredit = getCutomerList(voucherListCredit,-1);
				fullListCredit = getTotlaOfAll(voucherListCredit,null, startdate, enddate);
				totalCostCredit = fullListCredit.get(fullListCredit.size() - 1).get(0).getValue();
				cutomerMonthMapCredit = getAllListOfCutomer(voucherListCredit, listToCreateCredit, totalCostCredit,
						startdate, enddate);

				for (Entry<String, List<List<SimpleVoucher>>> setSale : cutomerMonthMapSales.entrySet()) {
					String keySales = setSale.getKey();
					List<List<SimpleVoucher>> fullListofCustCredit = cutomerMonthMapCredit.get(keySales);
					List<List<SimpleVoucher>> fullListReceivalble = new ArrayList<>();
					for (int i = 0; i < fullListofCustCredit.size(); i++) {
						List<SimpleVoucher> creditList = fullListofCustCredit.get(i);
						List<SimpleVoucher> salesList = setSale.getValue().get(i);
						List<SimpleVoucher> listToCreateReceivalble = new ArrayList<SimpleVoucher>();

						for (int j = 0; j < salesList.size(); j++) {
							SimpleVoucher svSales = salesList.get(j);
							SimpleVoucher svCredit = creditList.get(j);

							SimpleVoucher smRe = new SimpleVoucher();
							smRe.setName(svSales.getName());
							smRe.setValue(Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue()));
							smRe.setCredit(svCredit.getValue());
							smRe.setSales(svSales.getValue());
							smRe.setReceivable((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
							smRe.setCustomer(customerRepository.findByName(svSales.getName()));
							listToCreateReceivalble.add(smRe);
						}
						fullListReceivalble.add(listToCreateReceivalble);
					}
					cutomerMonthMapReceivalble.put(keySales, fullListReceivalble);
				}

				for (int k = 0; k < fullListSales.size(); k++) {
					List<SimpleVoucher> totalTempSales = fullListSales.get(k);
					List<SimpleVoucher> totalTempCredit = fullListCredit.get(k);
					List<SimpleVoucher> listToTotalReceivalble = new ArrayList<SimpleVoucher>();

					for (int n = 0; n < totalTempSales.size(); n++) {
						SimpleVoucher svSales = totalTempSales.get(n);
						SimpleVoucher svCredit = totalTempCredit.get(n);
						SimpleVoucher svRec = new SimpleVoucher();
						svRec.setName(svSales.getName());
						svRec.setSales(svSales.getValue());
						svRec.setCredit(svCredit.getValue());
						svRec.setReceivable((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
						svRec.setValue((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
						listToTotalReceivalble.add(svRec);
					}
					totalListReceivalble.add(listToTotalReceivalble);
				}

				finalList.add(cutomerMonthMapReceivalble);
				finalList.add(totalListReceivalble);
				message.setContentList(finalList);
				//// End full list
			}else if (subtype.equalsIgnoreCase("product")) {

				List<Object> finalList = new ArrayList<>();
				List<List<SimpleVoucher>> fullListSales = new ArrayList<>();
				Map<String, List<List<SimpleVoucher>>> cutomerMonthMapSales = new HashMap<>();
				List<SimpleVoucher> listToCreateSales = new ArrayList<SimpleVoucher>();

				List<List<SimpleVoucher>> fullListCredit = new ArrayList<>();
				Map<String, List<List<SimpleVoucher>>> cutomerMonthMapCredit = new HashMap<>();
				List<SimpleVoucher> listToCreateCredit = new ArrayList<SimpleVoucher>();

				// List<List<SimpleVoucher>> fullListReceivalble = new
				// ArrayList<>();
				Map<String, List<List<SimpleVoucher>>> cutomerMonthMapReceivalble = new HashMap<>();
				// List<SimpleVoucher> listToCreateReceivalble = new
				// ArrayList<SimpleVoucher>();
				List<List<SimpleVoucher>> totalListReceivalble = new ArrayList<>();
				// List<SimpleVoucher> listToTotalReceivalble = new
				// ArrayList<SimpleVoucher>();

				double totalCostSales = 0, totalCostCredit = 0;

				listToCreateSales = getProductList(voucherListSales,-1);
				fullListSales = getTotlaOfAll(voucherListSales, null,startdate, enddate);
				totalCostSales = fullListSales.get(fullListSales.size() - 1).get(0).getValue();
				cutomerMonthMapSales = getAllListOfProduct(voucherListSales, listToCreateSales, totalCostSales,
						startdate, enddate);

				listToCreateCredit = getProductList(voucherListCredit,-1);
				fullListCredit = getTotlaOfAll(voucherListCredit, null,startdate, enddate);
				totalCostCredit = fullListCredit.get(fullListCredit.size() - 1).get(0).getValue();
				cutomerMonthMapCredit = getAllListOfProduct(voucherListCredit, listToCreateCredit, totalCostCredit,
						startdate, enddate);

				for (Entry<String, List<List<SimpleVoucher>>> setSale : cutomerMonthMapSales.entrySet()) {
					String keySales = setSale.getKey();
					List<List<SimpleVoucher>> fullListofCustCredit = cutomerMonthMapCredit.get(keySales);
					if(fullListofCustCredit!=null && !fullListofCustCredit.isEmpty() && fullListofCustCredit.size()>0){
					List<List<SimpleVoucher>> fullListReceivalble = new ArrayList<>();
					
					for (int i = 0; i < fullListofCustCredit.size(); i++) {
						List<SimpleVoucher> creditList = fullListofCustCredit.get(i);
						List<SimpleVoucher> salesList = setSale.getValue().get(i);
						List<SimpleVoucher> listToCreateReceivalble = new ArrayList<SimpleVoucher>();

						for (int j = 0; j < salesList.size(); j++) {
							SimpleVoucher svSales = salesList.get(j);
							SimpleVoucher svCredit = creditList.get(j);

							SimpleVoucher smRe = new SimpleVoucher();
							smRe.setName(svSales.getName());
							smRe.setValue(Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue()));
							smRe.setCredit(svCredit.getValue());
							smRe.setSales(svSales.getValue());
							smRe.setReceivable((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
							smRe.setCustomer(customerRepository.findByName(svSales.getName()));
							listToCreateReceivalble.add(smRe);
						}
						fullListReceivalble.add(listToCreateReceivalble);
					}
					
					cutomerMonthMapReceivalble.put(keySales, fullListReceivalble);
				}
				}

				for (int k = 0; k < fullListSales.size(); k++) {
					List<SimpleVoucher> totalTempSales = fullListSales.get(k);
					List<SimpleVoucher> totalTempCredit = fullListCredit.get(k);
					List<SimpleVoucher> listToTotalReceivalble = new ArrayList<SimpleVoucher>();

					for (int n = 0; n < totalTempSales.size(); n++) {
						SimpleVoucher svSales = totalTempSales.get(n);
						SimpleVoucher svCredit = totalTempCredit.get(n);
						SimpleVoucher svRec = new SimpleVoucher();
						svRec.setName(svSales.getName());
						svRec.setSales(svSales.getValue());
						svRec.setCredit(svCredit.getValue());
						svRec.setReceivable((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
						svRec.setValue((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
						listToTotalReceivalble.add(svRec);
					}
					totalListReceivalble.add(listToTotalReceivalble);
				}

				finalList.add(cutomerMonthMapReceivalble);
				finalList.add(totalListReceivalble);
				message.setContentList(finalList);
				//// End full list
			}

			return message;
		} catch (Exception e) {
			e.printStackTrace();
			return Util.returnResult(201, "There was a problem while saving information", e.getMessage(), null);
		}
	}

	@PostMapping("/getReceivableListByDate/{subtype}/{startdate}/{enddate}")
	@CrossOrigin
	public Message getReceivableListByDate(@RequestBody Company company,
			@PathVariable(name = "subtype", value = "subtype", required = true) String subtype,
			@PathVariable(name = "startdate", value = "startdate", required = true) String startdate,
			@PathVariable(name = "enddate", value = "enddate", required = true) String enddate) {

		// UserCompanyRelation ucr =
		// userCompanyRelationRepository.findByUserId(user.getId());
		try {
			List<Voucher> voucherListSales = voucherRepository
					.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(company,
							"sales", Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));

			List<Voucher> voucherListCredit = voucherRepository
					.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(company,
							"Receipt", Util.convertStrToDateYYYMMDD(startdate),
							Util.convertStrToDateYYYMMDD(enddate));

			Map<String, SimpleVoucher> listToCreateSales = getStateList(voucherListSales);
			Map<String, SimpleVoucher> listToCreateCredit = getStateList(voucherListCredit);
			List<SimpleVoucher> listToCreateReceiving = new ArrayList<>();
double totalCost=0;
			for (Entry<String, SimpleVoucher> salesSet : listToCreateSales.entrySet()) {
				SimpleVoucher sv = new SimpleVoucher();
				SimpleVoucher svCredit = listToCreateCredit.get(salesSet.getKey());
				SimpleVoucher svSales = salesSet.getValue();
				sv.setName(salesSet.getKey());
				sv.setReceivable(Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue()));
				//sv.setValue(Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue()));
				totalCost=totalCost+sv.getReceivable();
				listToCreateReceiving.add(sv);
			}
			Collections.sort(listToCreateReceiving);
			System.out.println("totalCost >> " + totalCost);
			final double totalCostOfAll = totalCost;
			System.out.println("totalCostOfAll >> " + totalCostOfAll);
			listToCreateReceiving.forEach(f -> f.setValue(((f.getReceivable() / totalCostOfAll) * 100) ));
			Message message = Util.returnResult(206, "List ", "", "");
			message.setContentList(listToCreateReceiving);
			return message;
		} catch (Exception e) {
			e.printStackTrace();
			return Util.returnResult(201, "There was a problem while saving information", e.getMessage(), null);
		}
	}
	
	@PostMapping("/getReceivableCustomerListByProductAndDate/{product}/{startdate}/{enddate}")
	@CrossOrigin
	public Message getReceivableCustomerListByProductAndDate(@RequestBody Company company,
			@PathVariable(name = "product", value = "product", required = true) String product,
			@PathVariable(name = "startdate", value = "startdate", required = true) String startdate,
			@PathVariable(name = "enddate", value = "enddate", required = true) String enddate) {

		// UserCompanyRelation ucr =
		// userCompanyRelationRepository.findByUserId(user.getId());
		try {
			Message message = Util.returnResult(206, "List ", "", "");
			List<Voucher> voucherListSales,voucherListCredit;

			voucherListSales = voucherRepository
					.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(company,
							"sales", Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));

			voucherListCredit = voucherRepository
					.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(company,
							"Receipt", Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));

			List<SimpleVoucher> listToCreateSales = getCustomerOnProduct(voucherListSales, product);
			List<SimpleVoucher> listToCreateCredit = getCustomerOnProduct(voucherListCredit, product);

			System.out.println("voucherList Size >> " + voucherListSales.size());
			double totalCostSale = 0,totalCostCredit = 0;

				List<Object> finalList = new ArrayList<>();
				List<List<SimpleVoucher>> fullListSales = new ArrayList<>();
				List<List<SimpleVoucher>> fullListCredit = new ArrayList<>();
				
				Map<String, List<List<SimpleVoucher>>> cutomerMonthMapSales = new HashMap<>();
				Map<String, List<List<SimpleVoucher>>> cutomerMonthMapCredit = new HashMap<>();
				
				fullListSales = getTotlaOfAll(voucherListSales, null,startdate, enddate);
				fullListCredit = getTotlaOfAll(voucherListCredit,null, startdate, enddate);
				
				totalCostSale=fullListSales.get(fullListSales.size()-1).get(0).getValue();
				totalCostCredit=fullListCredit.get(fullListCredit.size()-1).get(0).getValue();

				cutomerMonthMapSales=fullListCutomerProductWise(voucherListSales,listToCreateSales,product,startdate,enddate,totalCostSale);
				cutomerMonthMapCredit=fullListCutomerProductWise(voucherListCredit,listToCreateCredit,product,startdate,enddate,totalCostCredit);

				List<List<SimpleVoucher>> fullListReceivable = new ArrayList<>();
				Map<String, List<List<SimpleVoucher>>> cutomerMapReceivable = new HashMap<>();


				for (Entry<String, List<List<SimpleVoucher>>> setSale : cutomerMonthMapSales.entrySet()) {
					String keySales = setSale.getKey();
					List<List<SimpleVoucher>> fullListofCustCredit = cutomerMonthMapCredit.get(keySales);
					List<List<SimpleVoucher>> fullListReceivalble = new ArrayList<>();
					for (int i = 0; i < fullListofCustCredit.size(); i++) {
						List<SimpleVoucher> creditList = fullListofCustCredit.get(i);
						List<SimpleVoucher> salesList = setSale.getValue().get(i);
						List<SimpleVoucher> listToCreateReceivalble = new ArrayList<SimpleVoucher>();

						for (int j = 0; j < salesList.size(); j++) {
							SimpleVoucher svSales = salesList.get(j);
							SimpleVoucher svCredit = creditList.get(j);

							SimpleVoucher smRe = new SimpleVoucher();
							smRe.setName(svSales.getName());
							smRe.setValue(Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue()));
							smRe.setCredit(svCredit.getValue());
							smRe.setSales(svSales.getValue());
							smRe.setReceivable((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
							listToCreateReceivalble.add(smRe);
						}
						fullListReceivalble.add(listToCreateReceivalble);
					}
					cutomerMapReceivable.put(keySales, fullListReceivalble);
				}

				for (int k = 0; k < fullListSales.size(); k++) {
					List<SimpleVoucher> totalTempSales = fullListSales.get(k);
					List<SimpleVoucher> totalTempCredit = fullListCredit.get(k);
					List<SimpleVoucher> listToTotalReceivalble = new ArrayList<SimpleVoucher>();

					for (int n = 0; n < totalTempSales.size(); n++) {
						SimpleVoucher svSales = totalTempSales.get(n);
						SimpleVoucher svCredit = totalTempCredit.get(n);
						SimpleVoucher svRec = new SimpleVoucher();
						svRec.setName(svSales.getName());
						svRec.setSales(svSales.getValue());
						svRec.setCredit(svCredit.getValue());
						svRec.setReceivable((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
						svRec.setValue((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
						listToTotalReceivalble.add(svRec);
					}
					fullListReceivable.add(listToTotalReceivalble);
				}
				
				finalList.add(cutomerMapReceivable);
				finalList.add(fullListReceivable);
				message.setContentList(finalList);
				//// End full list
			

			return message;
		} catch (Exception e) {
			e.printStackTrace();
			return Util.returnResult(201, "There was a problem while saving information", e.getMessage(), null);
		}
	}
	
	@PostMapping("/getReceivableCustomerVoucherList/{customer}/{startdate}/{enddate}")
	@CrossOrigin
	public Message getReceivableCustomerVoucherList(@RequestBody Company company,
			@PathVariable(name = "customer", value = "customer", required = true) String customer,
			@PathVariable(name = "startdate", value = "startdate", required = true) String startdate,
			@PathVariable(name = "enddate", value = "enddate", required = true) String enddate){
		Message message = Util.returnResult(206, "List ", "", "");
		try {
			List<Voucher> voucherListSales,voucherListCredit;
			List<List<SimpleVoucher>> fullListSales = new ArrayList<>();
			List<List<SimpleVoucher>> fullListCredit = new ArrayList<>();
			voucherListSales = voucherRepository.findAllByCompanyAndVoucherTypeAndAccountNameAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
					 company,  "sales",  customer, Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));

			voucherListCredit =voucherRepository.findAllByCompanyAndVoucherTypeAndAccountNameAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
					 company,  "Receipt",  customer, Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));
			fullListSales = getTotlaOfAll(voucherListSales,null, startdate, enddate);
			fullListCredit = getTotlaOfAll(voucherListCredit,null, startdate, enddate);
			
			List<SimpleVoucher> receivableVoucher= new ArrayList<>();
			List<Object> finalList = new ArrayList<>();
			System.out.println("fullListSales Name "+fullListSales.get(fullListSales.size()-1).get(0).getName());
			System.out.println("fullListSales Value "+fullListSales.get(fullListSales.size()-1).get(0).getValue());

			System.out.println("fullListCredit Name "+fullListCredit.get(fullListCredit.size()-1).get(0).getName());
			System.out.println("fullListCredit Value "+fullListCredit.get(fullListCredit.size()-1).get(0).getValue());

			double totalReceivable=Math.abs(fullListSales.get(fullListSales.size()-1).get(0).getValue())-Math.abs(fullListCredit.get(fullListCredit.size()-1).get(0).getValue());
			System.out.println("totalReceivable "+totalReceivable);
			if(totalReceivable>0){
				double compareAmount=0;
			for(Voucher salesVoucher:voucherListSales){
				
				if(Math.abs(compareAmount)<totalReceivable){
					System.out.println("totalReceivable "+Math.abs(compareAmount)+" < "+totalReceivable);
				List<VoucherItem>  voucherItem=getVoucherItem(salesVoucher);
				double tempVAl=0;
				for(VoucherItem vi:voucherItem){
					compareAmount=compareAmount+vi.getAmount();
					tempVAl=tempVAl+vi.getAmount();
				}
				SimpleVoucher sv= new SimpleVoucher();
				sv.setName(salesVoucher.getVoucherNo());
				sv.setValue(tempVAl);
				sv.setSales(tempVAl);
				sv.setVoucherGuid(salesVoucher.getGuid());
				sv.setVoucherDate(Util.getVoucherDateNdMonth(salesVoucher));
				sv.setCustomer(customerRepository.findByName(salesVoucher.getAccountName()));
				receivableVoucher.add(sv);
				finalList.add(sv);
			}
			}
			}
			
			List<SimpleVoucher> listToCreateDay = Util.getSimpleVoucherDateList(startdate, enddate);

			List<SimpleVoucher> listToCreateMonth1 = Util.getSimpleVoucherMonthsList(startdate, enddate);
			List<SimpleVoucher> listToCreateTotalMonth1 = Util.getSimpleVoucherTotalMonthsList(startdate, enddate);
			List<SimpleVoucher> listToCreateQuater = Util.getSimpleVoucherQuaterList(startdate, enddate);
			List<SimpleVoucher> listToCreateYear = new ArrayList<SimpleVoucher>();
			List<SimpleVoucher> contribution = new ArrayList<SimpleVoucher>();
			List<List<SimpleVoucher>> customerFullList = new ArrayList<>();
		//	Message message = Util.returnResult(206, "List ", "", "");
			double percenContri = 0;
			double custTotal = 0;
			for (Voucher voucher : voucherList) {

				
				// Day
				boolean addedDay = false;
				for (SimpleVoucher sv1 : listToCreateDay) {
					if (sv1.getName().equals(Util.getVoucherDateNdMonth1(voucher))) {
						sv1.setValue(sv1.getValue() + getVoucherItemAmount(voucher));
						addedDay = true;
					}
				}
				if (!addedDay) {
					SimpleVoucher sv2 = new SimpleVoucher();
					sv2.setName(Util.getVoucherDateNdMonth1(voucher));
					sv2.setValue( getVoucherItemAmount(voucher));
					listToCreateDay.add(sv2);
				}
				// Month
				boolean addedMonth = false;
				for (SimpleVoucher sv3 : listToCreateMonth1) {
					if (sv3.getName().equals(Util.getVoucherMonth(voucher))) {
						sv3.setValue(sv3.getValue() + getVoucherItemAmount(voucher));
						addedMonth = true;
					}
				}
				if (!addedMonth) {
					SimpleVoucher sv4 = new SimpleVoucher();
					sv4.setName(Util.getVoucherMonth(voucher));
					sv4.setValue(sv4.getValue() + getVoucherItemAmount(voucher));
					listToCreateMonth1.add(sv4);
				}
				// Total Month
				boolean addedTotalMonth = false;
				for (SimpleVoucher sv3 : listToCreateTotalMonth1) {
					if (sv3.getName().equals(Util.getVoucherTotalMonth(voucher))) {
						sv3.setValue(sv3.getValue() + getVoucherItemAmount(voucher));
						addedTotalMonth = true;
					}
				}
				if (!addedTotalMonth) {
					SimpleVoucher sv4 = new SimpleVoucher();
					sv4.setName(Util.getVoucherTotalMonth(voucher));
					sv4.setValue(sv4.getValue() + getVoucherItemAmount(voucher));
					listToCreateTotalMonth1.add(sv4);
				}
				// Quater
				boolean addedQuater = false;
				for (SimpleVoucher sv5 : listToCreateQuater) {
					if (sv5.getName().equals(Util.getVoucherQuaterByDate(voucher))) {
						sv5.setValue(sv5.getValue() + getVoucherItemAmount(voucher));
						addedQuater = true;
					}
				}
				if (!addedQuater) {
					SimpleVoucher sv6 = new SimpleVoucher();
					sv6.setName(Util.getVoucherQuaterByDate(voucher));
					sv6.setValue(sv6.getValue() + getVoucherItemAmount(voucher));
					listToCreateQuater.add(sv6);
				}

				// yearly
				boolean addedYearly = false;

				for (SimpleVoucher sv7 : listToCreateYear) {
					if (sv7.getName().equals(Util.getYear(startdate) + "-" + Util.getYear(enddate))) {
						sv7.setValue(sv7.getValue() + getVoucherItemAmount(voucher));
						custTotal = sv7.getValue();
						addedYearly = true;
					}
				}

				if (!addedYearly) {
					SimpleVoucher sv1 = new SimpleVoucher();
					sv1.setName(Util.getYear(startdate) + "-" + Util.getYear(enddate));
					sv1.setValue(getVoucherItemAmount(voucher));
					listToCreateYear.add(sv1);

				}
		}
			
			customerFullList.add(listToCreateDay);
			customerFullList.add(listToCreateMonth1);
			customerFullList.add(listToCreateQuater);
			customerFullList.add(listToCreateYear);
			customerFullList.add(contribution);
			customerFullList.add(listToCreateTotalMonth1);
			
			Map<String, Object> cutomerMonthMap = new HashMap<>();
			cutomerMonthMap.put("tableData", finalList);
			cutomerMonthMap.put("chatData", customerFullList);
			//finalList.add(receivableVoucher);
			message.setContentList(cutomerMonthMap);
			//// End full list
		

		return message;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return message;
	}
	
	@PostMapping("/getReceivableCustomerVoucherListByProduct/{customer}/{prouct}/{startdate}/{enddate}")
	@CrossOrigin
	public Message getReceivableCustomerVoucherListByProduct(@RequestBody Company company,
			@PathVariable(name = "customer", value = "customer", required = true) String customer,
			@PathVariable(name = "prouct", value = "prouct", required = true) String prouct,
			@PathVariable(name = "startdate", value = "startdate", required = true) String startdate,
			@PathVariable(name = "enddate", value = "enddate", required = true) String enddate){
		Message message = Util.returnResult(206, "List ", "", "");
		try {
			List<Voucher> voucherListSales,voucherListCredit;
			List<List<SimpleVoucher>> fullListSales = new ArrayList<>();
			List<List<SimpleVoucher>> fullListCredit = new ArrayList<>();
			voucherListSales = voucherRepository.findAllByCompanyAndVoucherTypeAndAccountNameAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
					 company,  "sales",  customer, Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));

			voucherListCredit =voucherRepository.findAllByCompanyAndVoucherTypeAndAccountNameAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
					 company,  "Receipt",  customer, Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));
			fullListSales = getTotlaOfAll(voucherListSales,prouct, startdate, enddate);
			fullListCredit = getTotlaOfAll(voucherListCredit, prouct,startdate, enddate);
			
			List<SimpleVoucher> receivableVoucher= new ArrayList<>();
			List<Object> finalList = new ArrayList<>();
			System.out.println("fullListSales Name "+fullListSales.get(fullListSales.size()-1).get(0).getName());
			System.out.println("fullListSales Value "+fullListSales.get(fullListSales.size()-1).get(0).getValue());

			System.out.println("fullListCredit Name "+fullListCredit.get(fullListCredit.size()-1).get(0).getName());
			System.out.println("fullListCredit Value "+fullListCredit.get(fullListCredit.size()-1).get(0).getValue());

			double totalReceivable=Math.abs(fullListSales.get(fullListSales.size()-1).get(0).getValue())-Math.abs(fullListCredit.get(fullListCredit.size()-1).get(0).getValue());
			System.out.println("totalReceivable "+totalReceivable);
			if(totalReceivable>0){
				double compareAmount=0;
			for(Voucher salesVoucher:voucherListSales){
				
				if(Math.abs(compareAmount)<totalReceivable){
					System.out.println("totalReceivable "+Math.abs(compareAmount)+" < "+totalReceivable);
				List<VoucherItem>  voucherItem=getVoucherItem(salesVoucher);
				
				double tempVAl=0;
				boolean addFlag=false;
				for(VoucherItem vi:voucherItem){
					
					if(vi.getName().equalsIgnoreCase(prouct)){
					compareAmount=compareAmount+vi.getAmount();
					tempVAl=tempVAl+vi.getAmount();
					if(!addFlag){
						addFlag=true;
						}
					}
				}
				if(addFlag){
				SimpleVoucher sv= new SimpleVoucher();
				sv.setName(salesVoucher.getVoucherNo());
				sv.setValue(tempVAl);
				sv.setSales(tempVAl);
				sv.setVoucherGuid(salesVoucher.getGuid());
				sv.setVoucherDate(Util.getVoucherDateNdMonth(salesVoucher));
				sv.setCustomer(customerRepository.findByName(salesVoucher.getAccountName()));
				receivableVoucher.add(sv);
				finalList.add(sv);
				}
			}
			}
			}
			//finalList.add(receivableVoucher);
			message.setContentList(finalList);
			//// End full list
		

		return message;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return message;
	}
	
	@PostMapping("/getReceivableCustomerAging/{startdate}/{enddate}")
	@CrossOrigin
	public Message getReceivableCustomerAging(@RequestBody Company company,
			@PathVariable(name = "startdate", value = "startdate", required = true) String startdate,
			@PathVariable(name = "enddate", value = "enddate", required = true) String enddate){
		Message message = Util.returnResult(206, "List ", "", "");
		try {
			List<Voucher> voucherListSales,voucherFullListSales,voucherListCredit;
			List<List<SimpleVoucher>> fullListSales = new ArrayList<>();
			List<List<SimpleVoucher>> fullListCredit = new ArrayList<>();
			
			
			voucherFullListSales = voucherRepository
					.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
							company, "Sales", Util.convertStrToDateYYYMMDD(startdate),
							Util.convertStrToDateYYYMMDD(enddate));
			

			List<SimpleVoucher> customerListSales = getCutomerList(voucherFullListSales,-1);
			
			List<Object> finalList = new ArrayList<>();
			Map<String,Map<String,Double>> customerMap=new HashMap<>();
			double receivableTotal=0, overdueTotal=0,noDueTotal=0,day15Total=0,day30Total=0,day60Total=0,day90Total=0,dayGreater90Total=0;
			
			for(SimpleVoucher sv:customerListSales){
				
			Customer customerDetails=customerRepository.findByName(sv.getName());
			
			int creditLimit=customerDetails.getCreditAmount();
			int creditPeroid=customerDetails.getCreditPeroid();
			
			double overdue=0,noDue=0,day15=0,day30=0,day60=0,day90=0,dayGreater90=0;
			Map<String,Double> agingMap=new HashMap<>();
			
					
			voucherListSales = voucherRepository.findAllByCompanyAndVoucherTypeAndAccountNameAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
					 company,  "sales",  sv.getName(), Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));

			voucherListCredit =voucherRepository.findAllByCompanyAndVoucherTypeAndAccountNameAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
					 company,  "Receipt",  sv.getName(), Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));
			fullListSales = getTotlaOfAll(voucherListSales, null,startdate, enddate);
			fullListCredit = getTotlaOfAll(voucherListCredit,null, startdate, enddate);
			
			List<SimpleVoucher> receivableVoucher= new ArrayList<>();
			
			System.out.println("fullListSales Name "+fullListSales.get(fullListSales.size()-1).get(0).getName());
			System.out.println("fullListSales Value "+fullListSales.get(fullListSales.size()-1).get(0).getValue());

			System.out.println("fullListCredit Name "+fullListCredit.get(fullListCredit.size()-1).get(0).getName());
			System.out.println("fullListCredit Value "+fullListCredit.get(fullListCredit.size()-1).get(0).getValue());

			double totalReceivable=Math.abs(fullListSales.get(fullListSales.size()-1).get(0).getValue())-Math.abs(fullListCredit.get(fullListCredit.size()-1).get(0).getValue());
			System.out.println("totalReceivable "+totalReceivable);
			receivableTotal=receivableTotal+totalReceivable;
			if(totalReceivable>0){
				double compareAmount=0;
			for(Voucher salesVoucher:voucherListSales){
				
				if(Math.abs(compareAmount)<totalReceivable){
					System.out.println("totalReceivable "+Math.abs(compareAmount)+" < "+totalReceivable);
				List<VoucherItem>  voucherItem=getVoucherItem(salesVoucher);
				double tempVAl=0;
				for(VoucherItem vi:voucherItem){
					compareAmount=compareAmount+vi.getAmount();
					tempVAl=tempVAl+vi.getAmount();
				}
				SimpleVoucher sv1= new SimpleVoucher();
				sv1.setName(salesVoucher.getVoucherNo());
				sv1.setValue(tempVAl);
				sv1.setSales(tempVAl);
				receivableVoucher.add(sv1);
				Date vocherDate=salesVoucher.getDate();
				Date dayAfter = new Date(vocherDate.getTime() + TimeUnit.DAYS.toMillis( creditPeroid));
				System.out.println("dayAfter >>"+dayAfter.toString());
				Date currentDate = new Date();
			       long difference = dayAfter.getTime() - currentDate.getTime();
			       float daysBetween = (difference / (1000*60*60*24));
			       System.out.println("Number of Days between dates: "+daysBetween);
			       if(daysBetween>1){
			    	   noDue=tempVAl;
			       }else {
			    	   overdue=overdue+tempVAl;
			    	   if(-15<=daysBetween && daysBetween  >=0){
			    		   
			    		   day15=day15+tempVAl;
			    	   }else if(-30<=daysBetween && daysBetween  >=-15){
			    		   day30=day30+tempVAl;
			    	   }else if(-60<=daysBetween && daysBetween  >=-30){
			    		   day60=day60+tempVAl;
			    	   }else if(-90<=daysBetween && daysBetween  >=-60){
			    		   day90=day90+tempVAl;
			    	   }else {
			    		   dayGreater90=dayGreater90+tempVAl;
			    	   }
			       }
			}
			}
			agingMap.put("receivable",totalReceivable);
			agingMap.put("noDue",noDue);
			agingMap.put("overdue",overdue);
			agingMap.put("day15",day15);
			agingMap.put("day30",day30);
			agingMap.put("day60",day60);
			agingMap.put("day90",day90);
			agingMap.put("dayGreater90",dayGreater90);
			customerMap.put(sv.getName(), agingMap);
			
			noDueTotal=noDueTotal+noDue;
			overdueTotal=overdueTotal+overdue;
			day15Total=day15Total+day15;
			day30Total=day30Total+day30;
			day60Total=day60Total+day60;
			day90Total=day90Total+day90;
			dayGreater90Total=dayGreater90Total+dayGreater90;
			}
			
			//finalList.add(customerMap);
			//finalList.add(receivableVoucher);
		}
			Map<String,Double> agingMapTotal=new HashMap<>();
			agingMapTotal.put("receivable",receivableTotal);
			agingMapTotal.put("noDue",noDueTotal);
			agingMapTotal.put("overdue",overdueTotal);
			agingMapTotal.put("day15",day15Total);
			agingMapTotal.put("day30",day30Total);
			agingMapTotal.put("day60",day60Total);
			agingMapTotal.put("day90",day90Total);
			agingMapTotal.put("dayGreater90",dayGreater90Total);
			customerMap.put("Total Receivable", agingMapTotal);
			finalList.add(customerMap);

			message.setContentList(finalList);
			//// End full list
		

		return message;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return message;
	}

	
	@PostMapping("/getReceivableProductAging/{startdate}/{enddate}")
	@CrossOrigin
	public Message getReceivableProductAging(@RequestBody Company company,
			@PathVariable(name = "startdate", value = "startdate", required = true) String startdate,
			@PathVariable(name = "enddate", value = "enddate", required = true) String enddate){
		Message message = Util.returnResult(206, "List ", "", "");
		try {
			List<Voucher> voucherFullListSales,voucherListCredit;
			
			
			voucherFullListSales = voucherRepository
					.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
							company, "Sales", Util.convertStrToDateYYYMMDD(startdate),
							Util.convertStrToDateYYYMMDD(enddate));
			
			voucherListCredit =voucherRepository.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
					 company,  "Receipt", Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));
			
			//List<SimpleVoucher> productListSales = getProductList(voucherFullListSales);
			Map<String,SimpleVoucher> productMapSales = getProductMap(voucherFullListSales);
			System.out.println("productMapSales "+productMapSales.size());

			Map<String,SimpleVoucher> productMapCredit = getProductMap(voucherListCredit);
			System.out.println("productMapSales "+productMapCredit.size());
			List<Object> finalList = new ArrayList<>();
			Map<String,Map<String,Double>> customerMap=new HashMap<>();
			double receivableTotal=0, overdueTotal=0,noDueTotal=0,day15Total=0,day30Total=0,day60Total=0,day90Total=0,dayGreater90Total=0;
			int i=0;
			for(Entry<String, SimpleVoucher> setProduct:productMapSales.entrySet()){
			//for(SimpleVoucher sv:productListSales){
		
			double overdue=0,noDue=0,day15=0,day30=0,day60=0,day90=0,dayGreater90=0;
			Map<String,Double> agingMap=new HashMap<>();
			
			List<SimpleVoucher> receivableVoucher= new ArrayList<>();
			System.out.println("product Name>> "+setProduct.getValue().getName()+">> "+i);
			i++;
			double totalReceivable=Math.abs(productMapSales.get(setProduct.getValue().getName()).getValue())-Math.abs(productMapCredit.get(setProduct.getValue().getName()).getValue());
		//	System.out.println("totalReceivable "+totalReceivable);
			receivableTotal=receivableTotal+totalReceivable;
		//	System.out.println("receivableTotal "+receivableTotal);
			
			if(totalReceivable>0){
				double compareAmount=0;
			for(Voucher salesVoucher:voucherFullListSales){
				int creditLimit=0,creditPeroid=0;
				if(Math.abs(compareAmount)<totalReceivable){
			//		System.out.println("totalReceivable "+Math.abs(compareAmount)+" < "+totalReceivable);
				List<VoucherItem>  voucherItem=getVoucherItem(salesVoucher);
				double tempVAl=0;
				boolean addFlag=false;
				for(VoucherItem vi:voucherItem){
					if(vi.getName().trim().equalsIgnoreCase(setProduct.getValue().getName().trim())){
				//		System.out.println("Product Names "+vi.getName().trim()+" == "+setProduct.getValue().getName().trim());
					compareAmount=compareAmount+vi.getAmount();
					tempVAl=tempVAl+vi.getAmount();
					if(!addFlag){
					addFlag=true;
					System.out.println("Customer Name >"+salesVoucher.getName());
					Customer customerDetails=customerRepository.findByName(salesVoucher.getName());
					//System.out.println("Customer getCreditAmount >"+customerDetails.getCreditAmount());
					// creditLimit=customerDetails.getCreditAmount();
					 creditPeroid=customerDetails.getCreditPeroid();
					}
					}
				}
				if(addFlag){
				SimpleVoucher sv1= new SimpleVoucher();
				sv1.setName(salesVoucher.getVoucherNo());
				sv1.setValue(tempVAl);
				sv1.setSales(tempVAl);
				receivableVoucher.add(sv1);
				Date vocherDate=salesVoucher.getDate();
				Date dayAfter = new Date(vocherDate.getTime() + TimeUnit.DAYS.toMillis( creditPeroid));
				//System.out.println("dayAfter >>"+dayAfter.toString());
				Date currentDate = new Date();
			       long difference = dayAfter.getTime() - currentDate.getTime();
			       float daysBetween = (difference / (1000*60*60*24));
			     //  System.out.println("Number of Days between dates: "+daysBetween);
			       if(daysBetween>1){
			    	   noDue=tempVAl;
			       }else {
			    	   overdue=overdue+tempVAl;
			    	   if(-15<=daysBetween && daysBetween  >=0){
			    		   
			    		   day15=day15+tempVAl;
			    	   }else if(-30<=daysBetween && daysBetween  >=-15){
			    		   day30=day30+tempVAl;
			    	   }else if(-60<=daysBetween && daysBetween  >=-30){
			    		   day60=day60+tempVAl;
			    	   }else if(-90<=daysBetween && daysBetween  >=-60){
			    		   day90=day90+tempVAl;
			    	   }else {
			    		   dayGreater90=dayGreater90+tempVAl;
			    	   }
			       }
				} 
			}
			}
			
			agingMap.put("receivable",totalReceivable);
			agingMap.put("noDue",noDue);
			agingMap.put("overdue",overdue);
			agingMap.put("day15",day15);
			agingMap.put("day30",day30);
			agingMap.put("day60",day60);
			agingMap.put("day90",day90);
			agingMap.put("dayGreater90",dayGreater90);
			
			
			noDueTotal=noDueTotal+noDue;
			overdueTotal=overdueTotal+overdue;
			day15Total=day15Total+day15;
			day30Total=day30Total+day30;
			day60Total=day60Total+day60;
			day90Total=day90Total+day90;
			dayGreater90Total=dayGreater90Total+dayGreater90;
			
			}
			customerMap.put(setProduct.getValue().getName(), agingMap);
			finalList.add(customerMap);

			
			//finalList.add(receivableVoucher);
		}
			Map<String,Double> agingMapTotal=new HashMap<>();
			agingMapTotal.put("receivable",receivableTotal);
			agingMapTotal.put("noDue",noDueTotal);
			agingMapTotal.put("overdue",overdueTotal);
			agingMapTotal.put("day15",day15Total);
			agingMapTotal.put("day30",day30Total);
			agingMapTotal.put("day60",day60Total);
			agingMapTotal.put("day90",day90Total);
			agingMapTotal.put("dayGreater90",dayGreater90Total);
			customerMap.put("Total Receivable", agingMapTotal);
			finalList.add(customerMap);

			message.setContentList(finalList);
			//// End full list
		

		//return message;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return message;
	}
	
	
		@PostMapping("/getReceivableTop/{state}/{count}/{startdate}/{enddate}")
		@CrossOrigin
		public Message getReceivableTop(@RequestBody Company company,
				@PathVariable(name = "state", value = "state", required = true) String state,
				@PathVariable(name = "count", value = "count", required = true) int count,
				@PathVariable(name = "startdate", value = "startdate", required = true) String startdate,
				@PathVariable(name = "enddate", value = "enddate", required = true) String enddate) {

			// UserCompanyRelation ucr =
			// userCompanyRelationRepository.findByUserId(user.getId());
			try {
				Message message = Util.returnResult(206, "List ", "", "");
				List<Voucher> voucherListSales;
				List<Voucher> voucherListCredit;

				double totalCost = 0;
				if (state.equalsIgnoreCase("All")) {
					voucherListSales = voucherRepository
							.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
									company, "Sales", Util.convertStrToDateYYYMMDD(startdate),
									Util.convertStrToDateYYYMMDD(enddate));
					voucherListCredit = voucherRepository
							.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
									company, "Receipt", Util.convertStrToDateYYYMMDD(startdate),
									Util.convertStrToDateYYYMMDD(enddate));

				} else {
					voucherListSales = voucherRepository
							.findAllByCompanyAndVoucherTypeAndStateAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
									company, "Sales", state, Util.convertStrToDateYYYMMDD(startdate),
									Util.convertStrToDateYYYMMDD(enddate));
					voucherListCredit = voucherRepository
							.findAllByCompanyAndVoucherTypeAndStateAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
									company, "Receipt", state, Util.convertStrToDateYYYMMDD(startdate),
									Util.convertStrToDateYYYMMDD(enddate));

				}
				List<SimpleVoucher> listToCreate = new ArrayList<SimpleVoucher>();
				System.out.println("voucherList Size >> " + voucherListSales.size());
				//if (subtype.equalsIgnoreCase("customer")) {
				Map<String,Object> finalMap = new HashMap<>();
					List<Object> finalList = new ArrayList<>();
					List<List<SimpleVoucher>> fullListSales = new ArrayList<>();
					Map<String, List<List<SimpleVoucher>>> cutomerMonthMapSales = new HashMap<>();
					List<SimpleVoucher> listToCreateSales = new ArrayList<SimpleVoucher>();

					List<List<SimpleVoucher>> fullListCredit = new ArrayList<>();
					Map<String, List<List<SimpleVoucher>>> cutomerMonthMapCredit = new HashMap<>();
					List<SimpleVoucher> listToCreateCredit = new ArrayList<SimpleVoucher>();

					// List<List<SimpleVoucher>> fullListReceivalble = new
					// ArrayList<>();
					Map<String, List<List<SimpleVoucher>>> cutomerMonthMapReceivalble = new HashMap<>();
					// List<SimpleVoucher> listToCreateReceivalble = new
					// ArrayList<SimpleVoucher>();
					List<List<SimpleVoucher>> totalListReceivalble = new ArrayList<>();
					// List<SimpleVoucher> listToTotalReceivalble = new
					// ArrayList<SimpleVoucher>();

					double totalCostSales = 0, totalCostCredit = 0;

					listToCreateSales = getCutomerList(voucherListSales,count);
					fullListSales = getTotal(voucherListSales, null,startdate, enddate);
					totalCostSales = fullListSales.get(fullListSales.size() - 1).get(0).getValue();
					cutomerMonthMapSales = getMonthListOfCutomer(voucherListSales, listToCreateSales, totalCostSales,
							startdate, enddate);

					listToCreateCredit = getCutomerList(voucherListCredit,-1);
					fullListCredit = getTotal(voucherListCredit,null, startdate, enddate);
					totalCostCredit = fullListCredit.get(fullListCredit.size() - 1).get(0).getValue();
					cutomerMonthMapCredit = getMonthListOfCutomer(voucherListCredit, listToCreateCredit, totalCostCredit,
							startdate, enddate);

					for (Entry<String, List<List<SimpleVoucher>>> setSale : cutomerMonthMapSales.entrySet()) {
						String keySales = setSale.getKey();
						List<List<SimpleVoucher>> fullListofCustCredit = cutomerMonthMapCredit.get(keySales);
						List<List<SimpleVoucher>> fullListReceivalble = new ArrayList<>();
						for (int i = 0; i < fullListofCustCredit.size(); i++) {
							List<SimpleVoucher> creditList = fullListofCustCredit.get(i);
							List<SimpleVoucher> salesList = setSale.getValue().get(i);
							List<SimpleVoucher> listToCreateReceivalble = new ArrayList<SimpleVoucher>();

							for (int j = 0; j < salesList.size(); j++) {
								SimpleVoucher svSales = salesList.get(j);
								SimpleVoucher svCredit = creditList.get(j);

								SimpleVoucher smRe = new SimpleVoucher();
								smRe.setName(svSales.getName());
								smRe.setValue(Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue()));
								smRe.setCredit(svCredit.getValue());
								smRe.setSales(svSales.getValue());
								smRe.setReceivable((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
								smRe.setCustomer(customerRepository.findByName(svSales.getName()));
								listToCreateReceivalble.add(smRe);
							}
							fullListReceivalble.add(listToCreateReceivalble);
						}
						cutomerMonthMapReceivalble.put(keySales, fullListReceivalble);
					}

//					for (int k = 0; k < fullListSales.size(); k++) {
//						List<SimpleVoucher> totalTempSales = fullListSales.get(k);
//						List<SimpleVoucher> totalTempCredit = fullListCredit.get(k);
//						List<SimpleVoucher> listToTotalReceivalble = new ArrayList<SimpleVoucher>();
//
//						for (int n = 0; n < totalTempSales.size(); n++) {
//							SimpleVoucher svSales = totalTempSales.get(n);
//							SimpleVoucher svCredit = totalTempCredit.get(n);
//							SimpleVoucher svRec = new SimpleVoucher();
//							svRec.setName(svSales.getName());
//							svRec.setSales(svSales.getValue());
//							svRec.setCredit(svCredit.getValue());
//							svRec.setReceivable((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
//							svRec.setValue((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
//							listToTotalReceivalble.add(svRec);
//						}
//						totalListReceivalble.add(listToTotalReceivalble);
//					}

					
					//message.setContentList(finalList);
					//// End full list
				//}else if (subtype.equalsIgnoreCase("product")) {

					List<Object> finalList1 = new ArrayList<>();
					List<List<SimpleVoucher>> fullListSales1 = new ArrayList<>();
					Map<String, List<List<SimpleVoucher>>> cutomerMonthMapSales1 = new HashMap<>();
					List<SimpleVoucher> listToCreateSales1 = new ArrayList<SimpleVoucher>();

					List<List<SimpleVoucher>> fullListCredit1 = new ArrayList<>();
					Map<String, List<List<SimpleVoucher>>> cutomerMonthMapCredit1 = new HashMap<>();
					List<SimpleVoucher> listToCreateCredit1 = new ArrayList<SimpleVoucher>();

					// List<List<SimpleVoucher>> fullListReceivalble = new
					// ArrayList<>();
					Map<String, List<List<SimpleVoucher>>> cutomerMonthMapReceivalble1 = new HashMap<>();
					// List<SimpleVoucher> listToCreateReceivalble = new
					// ArrayList<SimpleVoucher>();
					List<List<SimpleVoucher>> totalListReceivalble1 = new ArrayList<>();
					// List<SimpleVoucher> listToTotalReceivalble = new
					// ArrayList<SimpleVoucher>();

					double totalCostSales1 = 0, totalCostCredit1 = 0;

					listToCreateSales1 = getProductList(voucherListSales,count);
					fullListSales1 = getTotal(voucherListSales, null,startdate, enddate);
					totalCostSales1 = fullListSales.get(fullListSales1.size() - 1).get(0).getValue();
					cutomerMonthMapSales1 = getMonthListOfProduct(voucherListSales, listToCreateSales1, totalCostSales1,
							startdate, enddate);

					listToCreateCredit1 = getProductList(voucherListCredit,-1);
					fullListCredit1 = getTotal(voucherListCredit, null,startdate, enddate);
					totalCostCredit1 = fullListCredit.get(fullListCredit1.size() - 1).get(0).getValue();
					cutomerMonthMapCredit1 = getMonthListOfProduct(voucherListCredit, listToCreateCredit1, totalCostCredit1,
							startdate, enddate);

					for (Entry<String, List<List<SimpleVoucher>>> setSale : cutomerMonthMapSales1.entrySet()) {
						String keySales = setSale.getKey();
						List<List<SimpleVoucher>> fullListofCustCredit = cutomerMonthMapCredit1.get(keySales);
						if(fullListofCustCredit!=null && !fullListofCustCredit.isEmpty() && fullListofCustCredit.size()>0){
						List<List<SimpleVoucher>> fullListReceivalble = new ArrayList<>();
						for (int i = 0; i < fullListofCustCredit.size(); i++) {
							List<SimpleVoucher> creditList = fullListofCustCredit.get(i);
							List<SimpleVoucher> salesList = setSale.getValue().get(i);
							List<SimpleVoucher> listToCreateReceivalble = new ArrayList<SimpleVoucher>();

							for (int j = 0; j < salesList.size(); j++) {
								SimpleVoucher svSales = salesList.get(j);
								SimpleVoucher svCredit = creditList.get(j);

								SimpleVoucher smRe = new SimpleVoucher();
								smRe.setName(svSales.getName());
								smRe.setValue(Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue()));
								smRe.setCredit(svCredit.getValue());
								smRe.setSales(svSales.getValue());
								smRe.setReceivable((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
								smRe.setCustomer(customerRepository.findByName(svSales.getName()));
								listToCreateReceivalble.add(smRe);
							}
							fullListReceivalble.add(listToCreateReceivalble);
						}
						cutomerMonthMapReceivalble1.put(keySales, fullListReceivalble);
						}
					}

//					for (int k = 0; k < fullListSales1.size(); k++) {
//						List<SimpleVoucher> totalTempSales = fullListSales.get(k);
//						List<SimpleVoucher> totalTempCredit = fullListCredit.get(k);
//						List<SimpleVoucher> listToTotalReceivalble = new ArrayList<SimpleVoucher>();
//
//						for (int n = 0; n < totalTempSales.size(); n++) {
//							SimpleVoucher svSales = totalTempSales.get(n);
//							SimpleVoucher svCredit = totalTempCredit.get(n);
//							SimpleVoucher svRec = new SimpleVoucher();
//							svRec.setName(svSales.getName());
//							svRec.setSales(svSales.getValue());
//							svRec.setCredit(svCredit.getValue());
//							svRec.setReceivable((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
//							svRec.setValue((Math.abs(svSales.getValue()) - Math.abs(svCredit.getValue())));
//							listToTotalReceivalble.add(svRec);
//						}
//						totalListReceivalble1.add(listToTotalReceivalble);
//					}
					
					//Aging 
					
					List<SimpleVoucher> customerListSales = getCutomerList(voucherListSales,-1);
					
					List<Object> finalList11 = new ArrayList<>();
					Map<String,Map<String,Double>> customerMapAging=new HashMap<>();
					double receivableTotal=0, overdueTotal=0,noDueTotal=0,day15Total=0,day30Total=0,day60Total=0,day90Total=0,dayGreater90Total=0;
					
					for(SimpleVoucher sv:customerListSales){
						
					Customer customerDetails=customerRepository.findByName(sv.getName());
					System.out.println("Customer "+sv.getName());
					//int creditLimit=customerDetails.getCreditAmount();
					int creditPeroid=customerDetails.getCreditPeroid();
					
					double overdue=0,noDue=0,day15=0,day30=0,day60=0,day90=0,dayGreater90=0;
					Map<String,Double> agingMap=new HashMap<>();
					
							
					List<Voucher> voucherListSales1 = voucherRepository.findAllByCompanyAndVoucherTypeAndAccountNameAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
							 company,  "sales",  sv.getName(), Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));

					List<Voucher> voucherListCredit1 =voucherRepository.findAllByCompanyAndVoucherTypeAndAccountNameAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
							 company,  "Receipt",  sv.getName(), Util.convertStrToDateYYYMMDD(startdate), Util.convertStrToDateYYYMMDD(enddate));
					List<List<SimpleVoucher>> fullListSales2 = getTotlaOfAll(voucherListSales1, null,startdate, enddate);
					List<List<SimpleVoucher>> fullListCredit2 = getTotlaOfAll(voucherListCredit1,null, startdate, enddate);
					
					List<SimpleVoucher> receivableVoucher= new ArrayList<>();
					
					System.out.println("fullListSales Name "+fullListSales2.get(fullListSales2.size()-1).get(0).getName());
					System.out.println("fullListSales Value "+fullListSales2.get(fullListSales2.size()-1).get(0).getValue());

					System.out.println("fullListCredit Name "+fullListCredit2.get(fullListCredit2.size()-1).get(0).getName());
					System.out.println("fullListCredit Value "+fullListCredit2.get(fullListCredit2.size()-1).get(0).getValue());

					double totalReceivable=Math.abs(fullListSales.get(fullListSales.size()-1).get(0).getValue())-Math.abs(fullListCredit.get(fullListCredit.size()-1).get(0).getValue());
					System.out.println("totalReceivable "+totalReceivable);
					receivableTotal=receivableTotal+totalReceivable;
					if(totalReceivable>0){
						double compareAmount=0;
					for(Voucher salesVoucher:voucherListSales){
						
						if(Math.abs(compareAmount)<totalReceivable){
							System.out.println("totalReceivable "+Math.abs(compareAmount)+" < "+totalReceivable);
						List<VoucherItem>  voucherItem=getVoucherItem(salesVoucher);
						double tempVAl=0;
						for(VoucherItem vi:voucherItem){
							compareAmount=compareAmount+vi.getAmount();
							tempVAl=tempVAl+vi.getAmount();
						}
						SimpleVoucher sv1= new SimpleVoucher();
						sv1.setName(salesVoucher.getVoucherNo());
						sv1.setValue(tempVAl);
						sv1.setSales(tempVAl);
						receivableVoucher.add(sv1);
						Date vocherDate=salesVoucher.getDate();
						Date dayAfter = new Date(vocherDate.getTime() + TimeUnit.DAYS.toMillis( creditPeroid));
						System.out.println("dayAfter >>"+dayAfter.toString());
						Date currentDate = new Date();
					       long difference = dayAfter.getTime() - currentDate.getTime();
					       float daysBetween = (difference / (1000*60*60*24));
					       System.out.println("Number of Days between dates: "+daysBetween);
					       if(daysBetween>1){
					    	   noDue=tempVAl;
					       }else {
					    	   overdue=overdue+tempVAl;
					    	   if(-15<=daysBetween && daysBetween  >=0){
					    		   
					    		   day15=day15+tempVAl;
					    	   }else if(-30<=daysBetween && daysBetween  >=-15){
					    		   day30=day30+tempVAl;
					    	   }else if(-60<=daysBetween && daysBetween  >=-30){
					    		   day60=day60+tempVAl;
					    	   }else if(-90<=daysBetween && daysBetween  >=-60){
					    		   day90=day90+tempVAl;
					    	   }else {
					    		   dayGreater90=dayGreater90+tempVAl;
					    	   }
					       }
					}
					}
					agingMap.put("receivable",totalReceivable);
					agingMap.put("noDue",noDue);
					agingMap.put("overdue",overdue);
					agingMap.put("day15",day15);
					agingMap.put("day30",day30);
					agingMap.put("day60",day60);
					agingMap.put("day90",day90);
					agingMap.put("dayGreater90",dayGreater90);
					customerMapAging.put(sv.getName(), agingMap);
					
					noDueTotal=noDueTotal+noDue;
					overdueTotal=overdueTotal+overdue;
					day15Total=day15Total+day15;
					day30Total=day30Total+day30;
					day60Total=day60Total+day60;
					day90Total=day90Total+day90;
					dayGreater90Total=dayGreater90Total+dayGreater90;
					}
					
					//finalList11.add(customerMapAging);
					//finalList.add(receivableVoucher);
				}
					Map<String,Double> agingMapTotal=new HashMap<>();
					agingMapTotal.put("receivable",receivableTotal);
					agingMapTotal.put("noDue",noDueTotal);
					agingMapTotal.put("overdue",overdueTotal);
					agingMapTotal.put("day15",day15Total);
					agingMapTotal.put("day30",day30Total);
					agingMapTotal.put("day60",day60Total);
					agingMapTotal.put("day90",day90Total);
					agingMapTotal.put("dayGreater90",dayGreater90Total);
					customerMapAging.put("Total Receivable", agingMapTotal);
					//finalList11.add(customerMapAging);

					//message.setContentList(finalList11);
					
					//end 
					
					finalList1.add(cutomerMonthMapReceivalble);
					//finalList.add(totalListReceivalble);
					finalMap.put("Customer", finalList1);
					finalList.add(cutomerMonthMapReceivalble1);
					//finalList1.add(totalListReceivalble1);
					finalMap.put("Product", finalList);
					
					finalList11.add(customerMapAging);
					finalMap.put("Aging", finalList11);
					message.setContentList(finalMap);
					//// End full list
				//}

				return message;
			} catch (Exception e) {
				e.printStackTrace();
				return Util.returnResult(201, "There was a problem while saving information", e.getMessage(), null);
			}
		}

	
		/// Helper Method
		public Map<String, List<List<SimpleVoucher>>> getAllListOfCutomer(List<Voucher> voucherListSales,
				List<SimpleVoucher> listOfCustomer, double totalCost, String startdate, String enddate) {
			Map<String, List<List<SimpleVoucher>>> cutomerMonthMap = new HashMap<>();
			for (SimpleVoucher svT : listOfCustomer) {
				List<SimpleVoucher> listToCreateDay = Util.getSimpleVoucherDateList(startdate, enddate);

				List<SimpleVoucher> listToCreateMonth1 = Util.getSimpleVoucherMonthsList(startdate, enddate);
				List<SimpleVoucher> listToCreateQuater = Util.getSimpleVoucherQuaterList(startdate, enddate);
				List<SimpleVoucher> listToCreateYear = new ArrayList<SimpleVoucher>();
				List<SimpleVoucher> contribution = new ArrayList<SimpleVoucher>();
				List<List<SimpleVoucher>> customerFullList = new ArrayList<>();
				double percenContri = 0;
				double custTotal = 0;
				for (Voucher voucher : voucherListSales) {

					if (voucher.getAccountName().equalsIgnoreCase(svT.getName())) {
						// Day
						boolean addedDay = false;
						for (SimpleVoucher sv1 : listToCreateDay) {
							if (sv1.getName().equals(Util.getVoucherDateNdMonth(voucher))) {
								sv1.setValue(sv1.getValue() + getVoucherItemAmount(voucher));
								addedDay = true;
							}
						}
						if (!addedDay) {
							SimpleVoucher sv2 = new SimpleVoucher();
							sv2.setName(Util.getVoucherDateNdMonth(voucher));
							sv2.setValue(sv2.getValue() + getVoucherItemAmount(voucher));
							listToCreateDay.add(sv2);
						}
						// Month
						boolean addedMonth = false;
						for (SimpleVoucher sv3 : listToCreateMonth1) {
							if (sv3.getName().equals(Util.getVoucherMonth(voucher))) {
								sv3.setValue(sv3.getValue() + getVoucherItemAmount(voucher));
								addedMonth = true;
							}
						}
						if (!addedMonth) {
							SimpleVoucher sv4 = new SimpleVoucher();
							sv4.setName(Util.getVoucherMonth(voucher));
							sv4.setValue(sv4.getValue() + getVoucherItemAmount(voucher));
							listToCreateMonth1.add(sv4);
						}
						// Quater
						boolean addedQuater = false;
						for (SimpleVoucher sv5 : listToCreateQuater) {
							if (sv5.getName().equals(Util.getVoucherQuaterByDate(voucher))) {
								sv5.setValue(sv5.getValue() + getVoucherItemAmount(voucher));
								addedQuater = true;
							}
						}
						if (!addedQuater) {
							SimpleVoucher sv6 = new SimpleVoucher();
							sv6.setName(Util.getVoucherQuaterByDate(voucher));
							sv6.setValue(sv6.getValue() + getVoucherItemAmount(voucher));
							listToCreateQuater.add(sv6);
						}

						// yearly
						boolean addedYearly = false;

						for (SimpleVoucher sv7 : listToCreateYear) {
							if (sv7.getName().equals(Util.getYear(startdate) + "-" + Util.getYear(enddate))) {
								sv7.setValue(sv7.getValue() + getVoucherItemAmount(voucher));
								custTotal = sv7.getValue();
								addedYearly = true;
							}
						}

						if (!addedYearly) {
							SimpleVoucher sv1 = new SimpleVoucher();
							sv1.setName(Util.getYear(startdate) + "-" + Util.getYear(enddate));
							sv1.setValue(getVoucherItemAmount(voucher));
							listToCreateYear.add(sv1);

						}

					}
				}
				System.out.print("custTotal >>" + custTotal);
				System.out.print("totalCost >>" + totalCost);
				percenContri = (custTotal / totalCost) * 100;
				System.out.print("percenContri >>" + percenContri);

				SimpleVoucher contriSV = new SimpleVoucher();
				contriSV.setName("% Contri");
				contriSV.setValue(percenContri);
				contribution.add(contriSV);

				// Calculate contribution of user
				// End
				customerFullList.add(listToCreateDay);
				customerFullList.add(listToCreateMonth1);
				customerFullList.add(listToCreateQuater);
				customerFullList.add(listToCreateYear);
				customerFullList.add(contribution);
				cutomerMonthMap.put(svT.getName(), customerFullList);
			}
			return cutomerMonthMap;
		}

		/// Helper Method
			public Map<String, List<List<SimpleVoucher>>> getMonthListOfCutomer(List<Voucher> voucherListSales,
					List<SimpleVoucher> listOfCustomer, double totalCost, String startdate, String enddate) {
				Map<String, List<List<SimpleVoucher>>> cutomerMonthMap = new HashMap<>();
				for (SimpleVoucher svT : listOfCustomer) {

					List<SimpleVoucher> listToCreateMonth1 = Util.getSimpleVoucherMonthsList(startdate, enddate);
					List<SimpleVoucher> listToCreateYear = new ArrayList<SimpleVoucher>();
					List<SimpleVoucher> contribution = new ArrayList<SimpleVoucher>();
					List<List<SimpleVoucher>> customerFullList = new ArrayList<>();
					double percenContri = 0;
					double custTotal = 0;
					for (Voucher voucher : voucherListSales) {

						if (voucher.getAccountName().equalsIgnoreCase(svT.getName())) {
							
							// Month
							boolean addedMonth = false;
							for (SimpleVoucher sv3 : listToCreateMonth1) {
								if (sv3.getName().equals(Util.getVoucherMonth(voucher))) {
									sv3.setValue(sv3.getValue() + getVoucherItemAmount(voucher));
									addedMonth = true;
								}
							}
							if (!addedMonth) {
								SimpleVoucher sv4 = new SimpleVoucher();
								sv4.setName(Util.getVoucherMonth(voucher));
								sv4.setValue(sv4.getValue() + getVoucherItemAmount(voucher));
								listToCreateMonth1.add(sv4);
							}
							

							// yearly
							boolean addedYearly = false;

							for (SimpleVoucher sv7 : listToCreateYear) {
								if (sv7.getName().equals(Util.getYear(startdate) + "-" + Util.getYear(enddate))) {
									sv7.setValue(sv7.getValue() + getVoucherItemAmount(voucher));
									custTotal = sv7.getValue();
									addedYearly = true;
								}
							}

							if (!addedYearly) {
								SimpleVoucher sv1 = new SimpleVoucher();
								sv1.setName(Util.getYear(startdate) + "-" + Util.getYear(enddate));
								sv1.setValue(getVoucherItemAmount(voucher));
								listToCreateYear.add(sv1);

							}

						}
					}
					System.out.print("custTotal >>" + custTotal);
					System.out.print("totalCost >>" + totalCost);
					percenContri = (custTotal / totalCost) * 100;
					System.out.print("percenContri >>" + percenContri);

					SimpleVoucher contriSV = new SimpleVoucher();
					contriSV.setName("% Contri");
					contriSV.setValue(percenContri);
					contribution.add(contriSV);

					// Calculate contribution of user
					// End
					customerFullList.add(listToCreateMonth1);
					//customerFullList.add(listToCreateYear);
					customerFullList.add(contribution);
					cutomerMonthMap.put(svT.getName(), customerFullList);
				}
				return cutomerMonthMap;
			}
		
		
	public List<List<SimpleVoucher>> getTotlaOfAll(List<Voucher> voucherListSales,String product, String startdate, String enddate) {
		List<Object> finalList = new ArrayList<>();
		List<List<SimpleVoucher>> fullList = new ArrayList<>();
		Map<String, List<List<SimpleVoucher>>> cutomerMonthMap = new HashMap<>();

		System.out.println("voucherList Size >> " + voucherListSales.size());
		List<SimpleVoucher> listToCreateD = Util.getSimpleVoucherDateList(startdate, enddate);
		for (Voucher voucher : voucherListSales) {
			boolean added = false;
			for (SimpleVoucher sv : listToCreateD) {
				if (sv.getName().equals(Util.getVoucherDateNdMonth(voucher))) {
					sv.setValue(sv.getValue() + (product!=null&& product.isEmpty()?getVoucherItemAmountByProduct(voucher,product): getVoucherItemAmount(voucher)));
					added = true;
				}
			}
			if (!added) {
				SimpleVoucher sv = new SimpleVoucher();
				sv.setName(Util.getVoucherDateNdMonth(voucher));
				sv.setValue(sv.getValue() +(product!=null&& product.isEmpty()?getVoucherItemAmountByProduct(voucher,product): getVoucherItemAmount(voucher)));
				listToCreateD.add(sv);
			}
		}
		fullList.add(listToCreateD);

		// if (subtype.equalsIgnoreCase("month")) {
		List<SimpleVoucher> listToCreateMonth = Util.getSimpleVoucherMonthsList(startdate, enddate);
		for (Voucher voucher : voucherListSales) {
			boolean added = false;
			for (SimpleVoucher sv : listToCreateMonth) {
				if (sv.getName().equals(Util.getVoucherMonth(voucher))) {
					sv.setValue(sv.getValue() + (product!=null&& product.isEmpty()?getVoucherItemAmountByProduct(voucher,product): getVoucherItemAmount(voucher)));
					added = true;
				}
			}
			if (!added) {
				SimpleVoucher sv = new SimpleVoucher();
				sv.setName(Util.getVoucherMonth(voucher));
				sv.setValue(sv.getValue() +(product!=null&& product.isEmpty()?getVoucherItemAmountByProduct(voucher,product): getVoucherItemAmount(voucher)));
				listToCreateMonth.add(sv);
			}
		}
		fullList.add(listToCreateMonth);
		// }
		// if (subtype.equalsIgnoreCase("quater")) {
		List<SimpleVoucher> listToCreateQ = Util.getSimpleVoucherQuaterList(startdate, enddate);
		for (Voucher voucher : voucherListSales) {
			boolean added = false;
			for (SimpleVoucher sv : listToCreateQ) {
				if (sv.getName().equals(Util.getVoucherQuaterByDate(voucher))) {
					sv.setValue(sv.getValue() + (product!=null&& product.isEmpty()?getVoucherItemAmountByProduct(voucher,product): getVoucherItemAmount(voucher)));
					added = true;
				}
			}
			if (!added) {
				SimpleVoucher sv = new SimpleVoucher();
				sv.setName(Util.getVoucherQuaterByDate(voucher));
				sv.setValue(sv.getValue() + (product!=null&& product.isEmpty()?getVoucherItemAmountByProduct(voucher,product): getVoucherItemAmount(voucher)));
				listToCreateQ.add(sv);
			}
		}
		fullList.add(listToCreateQ);
		// }
		// if (subtype.equalsIgnoreCase("yearly")) {
		List<SimpleVoucher> listToCreateY = new ArrayList<SimpleVoucher>();
		SimpleVoucher sv = new SimpleVoucher();
		sv.setName(Util.getYear(startdate) + "-" + Util.getYear(enddate));
		for (Voucher voucher : voucherListSales) {
			sv.setValue(sv.getValue() + (product!=null&& product.isEmpty()?getVoucherItemAmountByProduct(voucher,product): getVoucherItemAmount(voucher)));
		}
		listToCreateY.add(sv);
		fullList.add(listToCreateY);
		return fullList;
	}

	
	
	public List<List<SimpleVoucher>> getTotal(List<Voucher> voucherListSales,String product, String startdate, String enddate) {
		List<Object> finalList = new ArrayList<>();
		List<List<SimpleVoucher>> fullList = new ArrayList<>();
		
		// }
		// if (subtype.equalsIgnoreCase("yearly")) {
		List<SimpleVoucher> listToCreateY = new ArrayList<SimpleVoucher>();
		SimpleVoucher sv = new SimpleVoucher();
		sv.setName(startdate + "-" +enddate);
		for (Voucher voucher : voucherListSales) {
			sv.setValue(sv.getValue() + (product!=null&& product.isEmpty()?getVoucherItemAmountByProduct(voucher,product): getVoucherItemAmount(voucher)));
		}
		listToCreateY.add(sv);
		fullList.add(listToCreateY);
		return fullList;
	}

	
	public List<SimpleVoucher> getCutomerList(List<Voucher> voucherListSales,int count) {
		List<SimpleVoucher> listToCreate = new ArrayList<SimpleVoucher>();

		for (Voucher voucher : voucherListSales) {
			List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
			if (viList != null) {
				for (VoucherItem vi : viList) {
					boolean added = false;
					for (SimpleVoucher sv : listToCreate) {
						if (sv.getName().equals(voucher.getAccountName())) {
							sv.setValue(sv.getValue() + vi.getAmount());
							added = true;
						}
					}
					if (!added) {
						SimpleVoucher sv = new SimpleVoucher();
						sv.setName(voucher.getAccountName());
						sv.setValue(sv.getValue() + vi.getAmount());
						listToCreate.add(sv);
					}
				}
			}
		}
		Collections.sort(listToCreate);
		List<SimpleVoucher> finalListToCreate = new ArrayList<SimpleVoucher>();
		if(count>0 && listToCreate.size()>=count){
		for (int i=0;i<count;i++) {
			SimpleVoucher sv = listToCreate.get(i);
			finalListToCreate.add( sv);
		}
		}else{
			finalListToCreate=listToCreate;
		}
		return finalListToCreate;
	}

	
	public Map<String, SimpleVoucher> getStateList(List<Voucher> voucherList) {
		Map<String, SimpleVoucher> mapToCreate = new HashMap<>();

		List<SimpleVoucher> listToCreate = new ArrayList<>();

		System.out.println("voucherList Size >> " + voucherList.size());

		for (Voucher voucher : voucherList) {
			boolean added = false;
			for (SimpleVoucher sv : listToCreate) {
				if (sv.getName().equalsIgnoreCase(voucher.getState())) {
					sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
					added = true;
				}
			}
			if (!added) {
				SimpleVoucher sv = new SimpleVoucher();
				sv.setName(voucher.getState());
				sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
				listToCreate.add(sv);
			}
		}

		Collections.sort(listToCreate);

		for (SimpleVoucher sv : listToCreate) {
			mapToCreate.put(sv.getName(), sv);
		}
		return mapToCreate;
	}

	
	public Map<String, SimpleVoucher> getProductMap(List<Voucher> voucherList) {
		Map<String, SimpleVoucher> mapToCreate = new HashMap<>();

		List<SimpleVoucher> listToCreate = new ArrayList<>();
		listToCreate.clear();
		for (Voucher voucher : voucherList) {
			List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
			if (viList != null) {
				for (VoucherItem vi : viList) {
					boolean added = false;
					for (SimpleVoucher sv : listToCreate) {
						if (sv.getName().equals(vi.getName())) {
							sv.setValue(sv.getValue() + vi.getAmount());
							added = true;
						}
					}
					if (!added) {
						SimpleVoucher sv = new SimpleVoucher();
						sv.setName(vi.getName());
						sv.setValue(sv.getValue() + vi.getAmount());
						listToCreate.add(sv);
					}
				}
			}
		}
		System.out.println("listToCreate Size >> "+listToCreate.size());
		Collections.sort(listToCreate);

		for (SimpleVoucher sv : listToCreate) {
			mapToCreate.put(sv.getName(), sv);
		}
		System.out.println("mapToCreate Size >> "+mapToCreate.size());
		
		return mapToCreate;
	}
	
	
	public List< SimpleVoucher> getProductList(List<Voucher> voucherList,int count) {
		Map<String, SimpleVoucher> mapToCreate = new HashMap<>();

		List<SimpleVoucher> listToCreate = new ArrayList<>();
		for (Voucher voucher : voucherList) {
			List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
			if (viList != null) {
				for (VoucherItem vi : viList) {
					boolean added = false;
					for (SimpleVoucher sv : listToCreate) {
						if (sv.getName().equals(vi.getName())) {
							sv.setValue(sv.getValue() + vi.getAmount());
							added = true;
						}
					}
					if (!added) {
						SimpleVoucher sv = new SimpleVoucher();
						sv.setName(vi.getName());
						sv.setValue(sv.getValue() + vi.getAmount());
						listToCreate.add(sv);
					}
				}
			}
		}
		Collections.sort(listToCreate);

		List<SimpleVoucher> finalListToCreate = new ArrayList<>();
		if(count>0 && listToCreate.size()>=count){
		for(int i=0;i<count;i++){
			finalListToCreate.add(listToCreate.get(i));
		}
		}else{
			finalListToCreate=listToCreate;
		}
		return listToCreate;
	}
	
	
	
	public Map<String, List<List<SimpleVoucher>>> getAllListOfProduct(List<Voucher> voucherList,
			List<SimpleVoucher> listOfProduct, double totalCost, String startdate, String enddate){
		
		Map<String, List<List<SimpleVoucher>>> productMap = new HashMap<>();

		for (SimpleVoucher svT : listOfProduct) {
			// svT.getName();
			// List<SimpleVoucher> listToCreateDay = new
			// ArrayList<SimpleVoucher>();
			List<SimpleVoucher> listToCreateDay = Util.getSimpleVoucherDateList(startdate, enddate);

			// List<SimpleVoucher> listToCreateMonth = new
			// ArrayList<SimpleVoucher>();
			List<SimpleVoucher> listToCreateMonth = Util.getSimpleVoucherMonthsList(startdate, enddate);
			// List<SimpleVoucher> listToCreateQuater = new
			// ArrayList<SimpleVoucher>();
			List<SimpleVoucher> listToCreateQuater = Util.getSimpleVoucherQuaterList(startdate, enddate);
			List<SimpleVoucher> listToCreateYear = new ArrayList<SimpleVoucher>();
			List<List<SimpleVoucher>> customerFullList = new ArrayList<>();

			for (Voucher voucher : voucherList) {

				List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
				if (viList != null) {
					for (VoucherItem vi : viList) {
						if (vi.getName().equalsIgnoreCase(svT.getName())) {

							// Day
							boolean addedDay = false;
							for (SimpleVoucher sv : listToCreateDay) {
								if (sv.getName().equals(Util.getVoucherDateNdMonth(voucher))) {
									sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
									addedDay = true;
								}
							}
							if (!addedDay) {
								SimpleVoucher sv = new SimpleVoucher();
								sv.setName(Util.getVoucherDateNdMonth(voucher));
								sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
								listToCreateDay.add(sv);
							}
							// Month
							boolean addedMonth = false;
							for (SimpleVoucher sv : listToCreateMonth) {
								if (sv.getName().equals(Util.getVoucherMonth(voucher))) {
									sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
									addedMonth = true;
								}
							}
							if (!addedMonth) {
								SimpleVoucher sv = new SimpleVoucher();
								sv.setName(Util.getVoucherMonth(voucher));
								sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
								listToCreateMonth.add(sv);
							}
							// Quater
							boolean addedQuater = false;
							for (SimpleVoucher sv : listToCreateQuater) {
								if (sv.getName().equals(Util.getVoucherQuaterByDate(voucher))) {
									sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
									addedQuater = true;
								}
							}
							if (!addedQuater) {
								SimpleVoucher sv = new SimpleVoucher();
								sv.setName(Util.getVoucherQuaterByDate(voucher));
								sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
								listToCreateQuater.add(sv);
							}

							boolean addedYearly = false;
							for (SimpleVoucher sv : listToCreateYear) {
								if (sv.getName().equalsIgnoreCase(
										Util.getYear(startdate) + "-" + Util.getYear(enddate))) {
									sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
									addedYearly = true;
								}
							}

							if (!addedYearly) {
								SimpleVoucher sv1 = new SimpleVoucher();
								sv1.setName(Util.getYear(startdate) + "-" + Util.getYear(enddate));
								sv1.setValue(getVoucherItemAmount(voucher));
								listToCreateYear.add(sv1);
							}

						}
					}
				}
				// End
			}
			customerFullList.add(listToCreateDay);
			customerFullList.add(listToCreateMonth);
			customerFullList.add(listToCreateQuater);
			customerFullList.add(listToCreateYear);

			productMap.put(svT.getName(), customerFullList);
		}
		return productMap;
	}

	public Map<String, List<List<SimpleVoucher>>> getMonthListOfProduct(List<Voucher> voucherList,
			List<SimpleVoucher> listOfProduct, double totalCost, String startdate, String enddate){
		
		Map<String, List<List<SimpleVoucher>>> productMap = new HashMap<>();

		for (SimpleVoucher svT : listOfProduct) {
			// svT.getName();
			// List<SimpleVoucher> listToCreateDay = new
			// ArrayList<SimpleVoucher>();
			List<SimpleVoucher> listToCreateDay = Util.getSimpleVoucherDateList(startdate, enddate);

			// List<SimpleVoucher> listToCreateMonth = new
			// ArrayList<SimpleVoucher>();
			List<SimpleVoucher> listToCreateMonth = Util.getSimpleVoucherMonthsList(startdate, enddate);
			// List<SimpleVoucher> listToCreateQuater = new
			// ArrayList<SimpleVoucher>();
			List<SimpleVoucher> listToCreateQuater = Util.getSimpleVoucherQuaterList(startdate, enddate);
			List<SimpleVoucher> listToCreateYear = new ArrayList<SimpleVoucher>();
			List<SimpleVoucher> contribution = new ArrayList<SimpleVoucher>();
			List<List<SimpleVoucher>> customerFullList = new ArrayList<>();
			double custTotal=0;
			for (Voucher voucher : voucherList) {

				List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
				if (viList != null) {
					for (VoucherItem vi : viList) {
						if (vi.getName().equalsIgnoreCase(svT.getName())) {

							// Month
							boolean addedMonth = false;
							for (SimpleVoucher sv : listToCreateMonth) {
								if (sv.getName().equals(Util.getVoucherMonth(voucher))) {
									sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
									addedMonth = true;
								}
							}
							if (!addedMonth) {
								SimpleVoucher sv = new SimpleVoucher();
								sv.setName(Util.getVoucherMonth(voucher));
								sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
								listToCreateMonth.add(sv);
							}
							

							boolean addedYearly = false;
							for (SimpleVoucher sv : listToCreateYear) {
								if (sv.getName().equalsIgnoreCase(
										Util.getYear(startdate) + "-" + Util.getYear(enddate))) {
									sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
									custTotal = sv.getValue();
									addedYearly = true;
								}
							}

							if (!addedYearly) {
								SimpleVoucher sv1 = new SimpleVoucher();
								sv1.setName(Util.getYear(startdate) + "-" + Util.getYear(enddate));
								sv1.setValue(getVoucherItemAmount(voucher));
								listToCreateYear.add(sv1);
							}

						}
					}
				}
				// End
			}
			System.out.print("custTotal >>" + custTotal);
			System.out.print("totalCost >>" + totalCost);
			double percenContri = (custTotal / totalCost) * 100;
			System.out.print("percenContri >>" + percenContri);

			SimpleVoucher contriSV = new SimpleVoucher();
			contriSV.setName("% Contri");
			contriSV.setValue(percenContri);
			contribution.add(contriSV);

			
			customerFullList.add(listToCreateMonth);
			//customerFullList.add(listToCreateYear);
			customerFullList.add(contribution);
			productMap.put(svT.getName(), customerFullList);
		}
		return productMap;
	}
	
	
	public List<SimpleVoucher> getCustomerOnProduct(List<Voucher> voucherList,String product){
		List<SimpleVoucher> listToCreate = new ArrayList<SimpleVoucher>();
		for (Voucher voucher : voucherList) {
			List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
			if (viList != null) {
				for (VoucherItem vi : viList) {
					if (vi.getName().equalsIgnoreCase(product)) {
						boolean added = false;
						for (SimpleVoucher sv : listToCreate) {
							if (sv.getName().equals(voucher.getAccountName())) {
								sv.setValue(sv.getValue() + vi.getAmount());
								added = true;
							}
						}
						if (!added) {
							SimpleVoucher sv = new SimpleVoucher();
							sv.setName(voucher.getAccountName());
							sv.setValue(sv.getValue() + vi.getAmount());
							listToCreate.add(sv);
						}
					}
				}
			}
		}
		return listToCreate;
	}

	public Map<String, List<List<SimpleVoucher>>> fullListCutomerProductWise(List<Voucher> voucherList,List<SimpleVoucher> listToCreate,String product,String startdate,String enddate
			,double totalCost){
		Map<String, List<List<SimpleVoucher>>> cutomerMonthMap = new HashMap<>();
		for (SimpleVoucher svT : listToCreate) {
			double custTotalCost=0;
			// svT.getName();
			// List<SimpleVoucher> listToCreateDay = new
			// ArrayList<SimpleVoucher>();
			List<SimpleVoucher> listToCreateDay = Util.getSimpleVoucherDateList(startdate, enddate);

			// List<SimpleVoucher> listToCreateMonth = new
			// ArrayList<SimpleVoucher>();
			List<SimpleVoucher> listToCreateMonth = Util.getSimpleVoucherMonthsList(startdate, enddate);
			// List<SimpleVoucher> listToCreateQuater = new
			// ArrayList<SimpleVoucher>();
			List<SimpleVoucher> listToCreateQuater = Util.getSimpleVoucherQuaterList(startdate, enddate);
			List<SimpleVoucher> listToCreateYear = new ArrayList<SimpleVoucher>();
			List<SimpleVoucher> listToContri = new ArrayList<SimpleVoucher>();
			List<List<SimpleVoucher>> customerFullList = new ArrayList<>();

			for (Voucher voucher : voucherList) {
				List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
				if (viList != null) {
					for (VoucherItem vi : viList) {
						if (vi.getName().equalsIgnoreCase(product)
								&& voucher.getAccountName().equalsIgnoreCase(svT.getName())) {

							// Day
							boolean addedDay = false;
							for (SimpleVoucher sv : listToCreateDay) {
								if (sv.getName().equals(Util.getVoucherDateNdMonth(voucher))) {
									sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
									addedDay = true;
								}
							}
							if (!addedDay) {
								SimpleVoucher sv = new SimpleVoucher();
								sv.setName(Util.getVoucherDateNdMonth(voucher));
								sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
								listToCreateDay.add(sv);
							}
							// Month
							boolean addedMonth = false;
							for (SimpleVoucher sv : listToCreateMonth) {
								if (sv.getName().equals(Util.getVoucherMonth(voucher))) {
									sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
									addedMonth = true;
								}
							}
							if (!addedMonth) {
								SimpleVoucher sv = new SimpleVoucher();
								sv.setName(Util.getVoucherMonth(voucher));
								sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
								listToCreateMonth.add(sv);
							}
							// Quater
							boolean addedQuater = false;
							for (SimpleVoucher sv : listToCreateQuater) {
								if (sv.getName().equals(Util.getVoucherQuaterByDate(voucher))) {
									sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
									addedQuater = true;
								}
							}
							if (!addedQuater) {
								SimpleVoucher sv = new SimpleVoucher();
								sv.setName(Util.getVoucherQuaterByDate(voucher));
								sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
								listToCreateQuater.add(sv);
							}

							boolean addedYearly = false;
							for (SimpleVoucher sv : listToCreateYear) {
								if (sv.getName()
										.equals(Util.getYear(startdate) + "-" + Util.getYear(enddate))) {
									sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
									addedYearly = true;
									custTotalCost = sv.getValue();
								}
							}
							if (!addedYearly) {
								SimpleVoucher sv1 = new SimpleVoucher();
								sv1.setName(Util.getYear(startdate) + "-" + Util.getYear(enddate));
								sv1.setValue(getVoucherItemAmount(voucher));
								listToCreateYear.add(sv1);

							}

						}
					}
				}
			}

			SimpleVoucher svContri = new SimpleVoucher();
			svContri.setName("% Contri");
			svContri.setValue(((custTotalCost / totalCost) * 100));
			listToContri.add(svContri);
			customerFullList.add(listToCreateDay);
			customerFullList.add(listToCreateMonth);
			customerFullList.add(listToCreateQuater);
			customerFullList.add(listToCreateYear);
			customerFullList.add(listToContri);

			cutomerMonthMap.put(svT.getName(), customerFullList);
		}
		return cutomerMonthMap;
	}

	private List<VoucherItem> getVoucherItem(Voucher voucher) {
		return voucherItemRepository.findByVoucher(voucher);
	}
	
	private double getVoucherItemAmount(Voucher voucher) {
		try {
			double totalVoucherAmount = 0;
			List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
			for (VoucherItem vi : viList) {
				totalVoucherAmount = totalVoucherAmount + vi.getAmount();
			}
			return totalVoucherAmount;
		} catch (Exception e) {
			return 0;
		}
	}
	
	private double getVoucherItemAmountByProduct(Voucher voucher,String product) {
		try {
			double totalVoucherAmount = 0;
			List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
			for (VoucherItem vi : viList) {
				if(vi.getName().equalsIgnoreCase(product)){
				totalVoucherAmount = totalVoucherAmount + vi.getAmount();
				}
			}
			return totalVoucherAmount;
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	@PostMapping("/getReceivableListStateWiseByDate/{type}/{state}/{subtype}/{startdate}/{enddate}")
	@CrossOrigin
	public Message getReceivableListStateWiseByDate(@RequestBody Company company,
			@PathVariable(name = "type", value = "type", required = true) String type,
			@PathVariable(name = "state", value = "state", required = true) String state,
			@PathVariable(name = "subtype", value = "subtype", required = true) String subtype,
			@PathVariable(name = "startdate", value = "startdate", required = true) String startdate,
			@PathVariable(name = "enddate", value = "enddate", required = true) String enddate) {

		// UserCompanyRelation ucr =
		// userCompanyRelationRepository.findByUserId(user.getId());
		try {
			Message message = Util.returnResult(206, "List ", "", "");
			List<Voucher> voucherList;
			double totalCost = 0;
			if (state.equalsIgnoreCase("All")) {
				voucherList = voucherRepository
						.findAllByCompanyAndVoucherTypeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
								company, type, Util.convertStrToDateYYYMMDD(startdate),
								Util.convertStrToDateYYYMMDD(enddate));

			} else {
				voucherList = voucherRepository
						.findAllByCompanyAndVoucherTypeAndStateAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(
								company, type, state, Util.convertStrToDateYYYMMDD(startdate),
								Util.convertStrToDateYYYMMDD(enddate));
			}
			List<SimpleVoucher> listToCreate = new ArrayList<SimpleVoucher>();
			System.out.println("voucherList Size >> " + voucherList.size());
			if (subtype.equalsIgnoreCase("product")) {
				List<Object> finalList = new ArrayList<>();
				List<List<SimpleVoucher>> fullList = new ArrayList<>();
				Map<String, List<List<SimpleVoucher>>> cutomerMonthMap = new HashMap<>();

				for (Voucher voucher : voucherList) {
					List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
					if (viList != null) {
						for (VoucherItem vi : viList) {
							boolean added = false;
							for (SimpleVoucher sv : listToCreate) {
								if (sv.getName().equals(vi.getName())) {
									sv.setValue(sv.getValue() + vi.getAmountAgainst());
									added = true;
								}
							}
							if (!added) {
								SimpleVoucher sv = new SimpleVoucher();
								sv.setName(vi.getName());
								sv.setValue(sv.getValue() + vi.getAmountAgainst());
								listToCreate.add(sv);
							}
						}
					}
				}
				System.out.println("voucherList Size >> " + voucherList.size());
				List<SimpleVoucher> listToCreateD = Util.getSimpleVoucherDateList(startdate, enddate);
				for (Voucher voucher : voucherList) {
					boolean added = false;
					for (SimpleVoucher sv : listToCreateD) {
						if (sv.getName().equals(Util.getVoucherDateNdMonth(voucher))) {
							sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
							added = true;
						}
					}
					if (!added) {
						SimpleVoucher sv = new SimpleVoucher();
						sv.setName(Util.getVoucherDateNdMonth(voucher));
						sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
						listToCreateD.add(sv);
					}
				}
				fullList.add(listToCreateD);

				// if (subtype.equalsIgnoreCase("month")) {
				List<SimpleVoucher> listToCreateMonth = Util.getSimpleVoucherMonthsList(startdate, enddate);
				for (Voucher voucher : voucherList) {
					boolean added = false;
					for (SimpleVoucher sv : listToCreateMonth) {
						if (sv.getName().equals(Util.getVoucherMonth(voucher))) {
							sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
							added = true;
						}
					}
					if (!added) {
						SimpleVoucher sv = new SimpleVoucher();
						sv.setName(Util.getVoucherMonth(voucher));
						sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
						listToCreateMonth.add(sv);
					}
				}
				fullList.add(listToCreateMonth);
				// }
				//////////////Total of Monthly ///////////
				List<SimpleVoucher> listToCreateTotalMonth = Util.getSimpleVoucherTotalMonthsList(startdate, enddate);
				for (Voucher voucher : voucherList) {
					boolean added = false;
					for (SimpleVoucher sv : listToCreateTotalMonth) {
						if (sv.getName().equals(Util.getVoucherTotalMonth(voucher))) {
							sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
							added = true;
						}
					}
					if (!added) {
						SimpleVoucher sv = new SimpleVoucher();
						sv.setName(Util.getVoucherTotalMonth(voucher));
						sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
						listToCreateTotalMonth.add(sv);
					}
				}
				fullList.add(listToCreateTotalMonth);
				///////////End - Total of Monthly//////
				// if (subtype.equalsIgnoreCase("quater")) {
				List<SimpleVoucher> listToCreateQ = Util.getSimpleVoucherQuaterList(startdate, enddate);
				for (Voucher voucher : voucherList) {
					boolean added = false;
					for (SimpleVoucher sv : listToCreateQ) {
						if (sv.getName().equals(Util.getVoucherQuaterByDate(voucher))) {
							sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
							added = true;
						}
					}
					if (!added) {
						SimpleVoucher sv = new SimpleVoucher();
						sv.setName(Util.getVoucherQuaterByDate(voucher));
						sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
						listToCreateQ.add(sv);
					}
				}
				fullList.add(listToCreateQ);
				// }
				// if (subtype.equalsIgnoreCase("yearly")) {
				List<SimpleVoucher> listToCreateY = new ArrayList<SimpleVoucher>();
				SimpleVoucher svY = new SimpleVoucher();
				svY.setName(Util.getYear(startdate) + "-" + Util.getYear(enddate));
				for (Voucher voucher : voucherList) {
					svY.setValue(svY.getValue() + getReceiptItemAmount(voucher));
				}
				totalCost = svY.getValue();
				listToCreateY.add(svY);
				fullList.add(listToCreateY);
				//
				// fullList.add(listToCreate);
				// Start Customer total Sales month wise
				for (SimpleVoucher svT : listToCreate) {
					// svT.getName();
					// List<SimpleVoucher> listToCreateDay = new
					// ArrayList<SimpleVoucher>();
					List<SimpleVoucher> listToCreateDay = Util.getSimpleVoucherDateList(startdate, enddate);

					// List<SimpleVoucher> listToCreateMonth = new
					// ArrayList<SimpleVoucher>();
					List<SimpleVoucher> listToCreateM = Util.getSimpleVoucherMonthsList(startdate, enddate);
					List<SimpleVoucher> listToCreateTotalMonth1 = Util.getSimpleVoucherTotalMonthsList(startdate, enddate);

					// List<SimpleVoucher> listToCreateQuater = new
					// ArrayList<SimpleVoucher>();
					List<SimpleVoucher> listToCreateQuater = Util.getSimpleVoucherQuaterList(startdate, enddate);
					List<SimpleVoucher> listToCreateYear = new ArrayList<SimpleVoucher>();
					List<List<SimpleVoucher>> customerFullList = new ArrayList<>();
					List<SimpleVoucher> contribution = new ArrayList<SimpleVoucher>();

					double percenContri = 0;
					double prodTotal = 0;
					for (Voucher voucher : voucherList) {

						List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
						if (viList != null) {
							for (VoucherItem vi : viList) {
								if (vi.getName().equalsIgnoreCase(svT.getName())) {

									// Day
									boolean addedDay = false;
									for (SimpleVoucher sv : listToCreateDay) {
										if (sv.getName().equals(Util.getVoucherDateNdMonth(voucher))) {
											sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
											addedDay = true;
										}
									}
									if (!addedDay) {
										SimpleVoucher sv = new SimpleVoucher();
										sv.setName(Util.getVoucherDateNdMonth(voucher));
										sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
										listToCreateDay.add(sv);
									}
									// Month
									boolean addedMonth = false;
									for (SimpleVoucher sv : listToCreateMonth) {
										if (sv.getName().equals(Util.getVoucherMonth(voucher))) {
											sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
											addedMonth = true;
										}
									}
									if (!addedMonth) {
										SimpleVoucher sv = new SimpleVoucher();
										sv.setName(Util.getVoucherMonth(voucher));
										sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
										listToCreateMonth.add(sv);
									}
									// Total Month
									boolean addedTotalMonth = false;
									for (SimpleVoucher sv3 : listToCreateTotalMonth1) {
										if (sv3.getName().equals(Util.getVoucherTotalMonth(voucher))) {
											sv3.setValue(sv3.getValue() + getVoucherItemAmount(voucher));
											addedTotalMonth = true;
										}
									}
									if (!addedTotalMonth) {
										SimpleVoucher sv4 = new SimpleVoucher();
										sv4.setName(Util.getVoucherTotalMonth(voucher));
										sv4.setValue(sv4.getValue() + getVoucherItemAmount(voucher));
										listToCreateTotalMonth1.add(sv4);
									}
									// Quater
									boolean addedQuater = false;
									for (SimpleVoucher sv : listToCreateQuater) {
										if (sv.getName().equals(Util.getVoucherQuaterByDate(voucher))) {
											sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
											addedQuater = true;
										}
									}
									if (!addedQuater) {
										SimpleVoucher sv = new SimpleVoucher();
										sv.setName(Util.getVoucherQuaterByDate(voucher));
										sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));

										listToCreateQuater.add(sv);
									}

									boolean addedYearly = false;
									for (SimpleVoucher sv : listToCreateYear) {
										if (sv.getName().equalsIgnoreCase(
												Util.getYear(startdate) + "-" + Util.getYear(enddate))) {
											sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
											prodTotal = sv.getValue();
											addedYearly = true;
										}
									}

									if (!addedYearly) {
										SimpleVoucher sv1 = new SimpleVoucher();
										sv1.setName(Util.getYear(startdate) + "-" + Util.getYear(enddate));
										sv1.setValue(getReceiptItemAmount(voucher));
										listToCreateYear.add(sv1);
									}

								}
							}
						}
						// End
					}
					System.out.print("custTotal >>" + prodTotal);
					System.out.print("totalCost >>" + totalCost);
					percenContri = (prodTotal / totalCost) * 100;
					System.out.print("percenContri >>" + percenContri);

					SimpleVoucher contriSV = new SimpleVoucher();
					contriSV.setName("% Contri");
					contriSV.setValue(percenContri);
					contribution.add(contriSV);
					customerFullList.add(listToCreateDay);
					customerFullList.add(listToCreateMonth);
					customerFullList.add(listToCreateQuater);
					customerFullList.add(listToCreateYear);
					customerFullList.add(contribution);
					customerFullList.add(listToCreateTotalMonth1);

					cutomerMonthMap.put(svT.getName(), customerFullList);
				}

				// }
				finalList.add(cutomerMonthMap);
				finalList.add(fullList);
				message.setContentList(finalList);
				///
				// message.setContentList(listToCreate);
			} else if (subtype.equalsIgnoreCase("customer")) {

				List<Object> finalList = new ArrayList<>();
				List<List<SimpleVoucher>> fullList = new ArrayList<>();
				Map<String, List<List<SimpleVoucher>>> cutomerMonthMap = new HashMap<>();
				Map<String, SimpleVoucher> cutomerMapContri = new HashMap<>();
				Map<String, List<List<SimpleVoucher>>> cutomerMap = new LinkedHashMap<>();

				for (Voucher voucher : voucherList) {
					List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
					if (viList != null) {
						for (VoucherItem vi : viList) {
							boolean added = false;
							for (SimpleVoucher sv : listToCreate) {
								if (sv.getName().equals(voucher.getAccountName())) {
									sv.setValue(sv.getValue() + vi.getAmountAgainst());
									added = true;
								}
							}
							if (!added) {
								SimpleVoucher sv = new SimpleVoucher();
								sv.setName(voucher.getAccountName());
								sv.setValue(sv.getValue() + vi.getAmountAgainst());
								Customer customer = customerRepository.findByName(voucher.getAccountName());
								sv.setCustomer(customer);
								listToCreate.add(sv);
							}
						}
					}
				}

				System.out.println("voucherList Size >> " + voucherList.size());
				List<SimpleVoucher> listToCreateD = Util.getSimpleVoucherDateList(startdate, enddate);
				for (Voucher voucher : voucherList) {
					boolean added = false;
					for (SimpleVoucher sv : listToCreateD) {
						if (sv.getName().equals(Util.getVoucherDateNdMonth(voucher))) {
							sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
							added = true;
						}
					}
					if (!added) {
						SimpleVoucher sv = new SimpleVoucher();
						sv.setName(Util.getVoucherDateNdMonth(voucher));
						sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
						listToCreateD.add(sv);
					}
				}
				fullList.add(listToCreateD);

				// if (subtype.equalsIgnoreCase("month")) {
				List<SimpleVoucher> listToCreateMonth = Util.getSimpleVoucherMonthsList(startdate, enddate);
				for (Voucher voucher : voucherList) {
					boolean added = false;
					for (SimpleVoucher sv : listToCreateMonth) {
						if (sv.getName().equals(Util.getVoucherMonth(voucher))) {
							sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
							added = true;
						}
					}
					if (!added) {
						SimpleVoucher sv = new SimpleVoucher();
						sv.setName(Util.getVoucherMonth(voucher));
						sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
						listToCreateMonth.add(sv);
					}
				}
				fullList.add(listToCreateMonth);
				// }
	//////////////Total of Monthly ///////////
					List<SimpleVoucher> listToCreateTotalMonth = Util.getSimpleVoucherTotalMonthsList(startdate, enddate);
					for (Voucher voucher : voucherList) {
						boolean added = false;
						for (SimpleVoucher sv : listToCreateTotalMonth) {
							if (sv.getName().equals(Util.getVoucherTotalMonth(voucher))) {
								sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
								added = true;
							}
						}
						if (!added) {
							SimpleVoucher sv = new SimpleVoucher();
							sv.setName(Util.getVoucherTotalMonth(voucher));
							sv.setValue(sv.getValue() + getVoucherItemAmount(voucher));
							listToCreateTotalMonth.add(sv);
						}
					}
					fullList.add(listToCreateTotalMonth);
				// if (subtype.equalsIgnoreCase("quater")) {
				List<SimpleVoucher> listToCreateQ = Util.getSimpleVoucherQuaterList(startdate, enddate);
				for (Voucher voucher : voucherList) {
					boolean added = false;
					for (SimpleVoucher sv : listToCreateQ) {
						if (sv.getName().equals(Util.getVoucherQuaterByDate(voucher))) {
							sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
							added = true;
						}
					}
					if (!added) {
						SimpleVoucher sv = new SimpleVoucher();
						sv.setName(Util.getVoucherQuaterByDate(voucher));
						sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
						listToCreateQ.add(sv);
					}
				}
				fullList.add(listToCreateQ);
				// }
				// if (subtype.equalsIgnoreCase("yearly")) {
				List<SimpleVoucher> listToCreateY = new ArrayList<SimpleVoucher>();
				SimpleVoucher sv = new SimpleVoucher();
				sv.setName(Util.getYear(startdate) + "-" + Util.getYear(enddate));
				for (Voucher voucher : voucherList) {
					sv.setValue(sv.getValue() + getReceiptItemAmount(voucher));
				}
				totalCost = sv.getValue();
				listToCreateY.add(sv);
				fullList.add(listToCreateY);
				// }

				for (SimpleVoucher svT : listToCreate) {
					List<SimpleVoucher> listToCreateDay = Util.getSimpleVoucherDateList(startdate, enddate);
					List<SimpleVoucher> listToCreateTotalMonth1 = Util.getSimpleVoucherTotalMonthsList(startdate, enddate);
					List<SimpleVoucher> listToCreateMonth1 = Util.getSimpleVoucherMonthsList(startdate, enddate);
					List<SimpleVoucher> listToCreateQuater = Util.getSimpleVoucherQuaterList(startdate, enddate);
					List<SimpleVoucher> listToCreateYear = new ArrayList<SimpleVoucher>();
					List<SimpleVoucher> contribution = new ArrayList<SimpleVoucher>();
					List<List<SimpleVoucher>> customerFullList = new ArrayList<>();
					double percenContri = 0;
					double custTotal = 0;
					for (Voucher voucher : voucherList) {

						if (voucher.getAccountName().equalsIgnoreCase(svT.getName())) {
							// Day
							boolean addedDay = false;
							for (SimpleVoucher sv1 : listToCreateDay) {
								if (sv1.getName().equals(Util.getVoucherDateNdMonth(voucher))) {
									sv1.setValue(sv1.getValue() + getReceiptItemAmount(voucher));
									addedDay = true;
								}
							}
							if (!addedDay) {
								SimpleVoucher sv2 = new SimpleVoucher();
								sv2.setName(Util.getVoucherDateNdMonth(voucher));
								sv2.setValue(sv.getValue() + getReceiptItemAmount(voucher));
								listToCreateDay.add(sv2);
							}
							// Month
							boolean addedMonth = false;
							for (SimpleVoucher sv3 : listToCreateMonth1) {
								if (sv3.getName().equals(Util.getVoucherMonth(voucher))) {
									sv3.setValue(sv3.getValue() + getReceiptItemAmount(voucher));
									addedMonth = true;
								}
							}
							if (!addedMonth) {
								SimpleVoucher sv4 = new SimpleVoucher();
								sv4.setName(Util.getVoucherMonth(voucher));
								sv4.setValue(sv4.getValue() + getReceiptItemAmount(voucher));
								listToCreateMonth1.add(sv4);
							}
							// Total Month
							boolean addedTotalMonth = false;
							for (SimpleVoucher sv3 : listToCreateTotalMonth1) {
								if (sv3.getName().equals(Util.getVoucherTotalMonth(voucher))) {
									sv3.setValue(sv3.getValue() + getVoucherItemAmount(voucher));
									addedTotalMonth = true;
								}
							}
							if (!addedTotalMonth) {
								SimpleVoucher sv4 = new SimpleVoucher();
								sv4.setName(Util.getVoucherTotalMonth(voucher));
								sv4.setValue(sv4.getValue() + getVoucherItemAmount(voucher));
								listToCreateTotalMonth1.add(sv4);
							}
							// Quater
							boolean addedQuater = false;
							for (SimpleVoucher sv5 : listToCreateQuater) {
								if (sv5.getName().equals(Util.getVoucherQuaterByDate(voucher))) {
									sv5.setValue(sv5.getValue() + getReceiptItemAmount(voucher));
									addedQuater = true;
								}
							}
							if (!addedQuater) {
								SimpleVoucher sv6 = new SimpleVoucher();
								sv6.setName(Util.getVoucherQuaterByDate(voucher));
								sv6.setValue(sv6.getValue() + getReceiptItemAmount(voucher));
								listToCreateQuater.add(sv6);
							}

							// yearly
							boolean addedYearly = false;

							for (SimpleVoucher sv7 : listToCreateYear) {
								if (sv7.getName().equals(Util.getYear(startdate) + "-" + Util.getYear(enddate))) {
									sv7.setValue(sv7.getValue() + getReceiptItemAmount(voucher));
									custTotal = sv7.getValue();
									addedYearly = true;
								}
							}

							if (!addedYearly) {
								SimpleVoucher sv1 = new SimpleVoucher();
								sv1.setName(Util.getYear(startdate) + "-" + Util.getYear(enddate));
								sv1.setValue(getReceiptItemAmount(voucher));
								listToCreateYear.add(sv1);

							}

						}
					}
					//System.out.println("custTotal >>" + custTotal);
					//System.out.println("totalCost >>" + totalCost);
					percenContri = (custTotal / totalCost) * 100;
					//System.out.println("percenContri >>" + percenContri);

					SimpleVoucher contriSV = new SimpleVoucher();
					contriSV.setName("% Contri");
					contriSV.setValue(percenContri);
					contribution.add(contriSV);

					List<SimpleVoucher> cutomerSV = new ArrayList<>();
					cutomerSV.add(svT);
					// Calculate contribution of user
					// End
					customerFullList.add(listToCreateDay);
					customerFullList.add(listToCreateMonth1);
					customerFullList.add(listToCreateQuater);
					customerFullList.add(listToCreateYear);
					customerFullList.add(contribution);
					customerFullList.add(cutomerSV);
					customerFullList.add(listToCreateTotalMonth1);
					cutomerMapContri.put(svT.getName(), contriSV);
					cutomerMonthMap.put(svT.getName(), customerFullList);
				}
				
				cutomerMapContri.entrySet()
				.forEach(System.out::println);
				
				Map<String, SimpleVoucher> result = cutomerMapContri.entrySet()
						  .stream()
						  .sorted(reverseOrder(Map.Entry.comparingByValue()))
						  .collect(Collectors.toMap(
						    Map.Entry::getKey, 
						    Map.Entry::getValue, 
						    (oldValue, newValue) -> oldValue, LinkedHashMap::new));
				
//				cutomerMapContri.entrySet()
//				  .stream()
//				  .sorted(Map.Entry.<String, SimpleVoucher>comparingByValue())
//				  .forEach(System.out::println);
				System.out.println("><><><><><><><><><><><><><><><>><><><><>><><><>>><>><>><>");
				result.entrySet()
				.forEach(System.out::println);
				
				for(Entry<String, SimpleVoucher> s :result.entrySet()) {
					System.out.println("><><><>><>"+s.getKey());
					cutomerMap.put(s.getKey(),cutomerMonthMap.get(s.getKey()));
				}
	       // System.out.println("><><><><><><"+entryList);
//				System.out.println("><><><><><><><><><><><><><><><Cust  >><><><><>><><><>>><>><>><>");
//				cutomerMap.entrySet().
//				.forEach(System.out::println);
//				
				//finalList.add(cutomerMonthMap);
				finalList.add(cutomerMap);
				finalList.add(fullList);
				message.setContentList(finalList);
				//// End full list
			}

			return message;
		} catch (Exception e) {
			e.printStackTrace();
			return Util.returnResult(201, "There was a problem while Receipt information", e.getMessage(), null);
		}
	}
	
	
	private Comparator<? super Entry<String, Double>> comparingByValue() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object toMap(Object object, Object object2, Object object3, Object object4) {
		// TODO Auto-generated method stub
		return null;
	}

	// function to sort hashmap by values 
//    public static HashMap<String, List<List<SimpleVoucher>>> sortByValue(HashMap<String, List<List<SimpleVoucher>>> hm) 
//    { 
//        // Create a list from elements of HashMap 
//        List<Map.Entry<String, List<List<SimpleVoucher>>> > list = 
//               new LinkedList<Map.Entry<String, List<List<SimpleVoucher>>> >(hm.entrySet()); 
//  
//        // Sort the list 
//        Collection(<List<SimpleVoucher>) list=map.values();
//        Collection<List<List<SimpleVoucher>>> students = map.values();
//        List<Student> list = new ArrayList<>(students);
//        Collections.sort(list, new MyComparator());
//        
//        Collections.sort(list, new Comparator<Map.Entry<String, List<List<SimpleVoucher>>> >() { 
//            public int compare(Map.Entry<String, Integer> o1,Map.Entry<String, Integer> o2) 
//            { 
//                return (o1.getValue()).compareTo(o2.getValue()); 
//            } 
//        }); 
//          
//        // put data from sorted list to hashmap  
//        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>(); 
//        for (Map.Entry<String, Integer> aa : list) { 
//            temp.put(aa.getKey(), aa.getValue()); 
//        } 
//        return temp; 
//    } 
	private double getReceiptItemAmount(Voucher voucher) {
		try {
			double totalVoucherAmount = 0;
			List<VoucherItem> viList = voucherItemRepository.findByVoucher(voucher);
			for (VoucherItem vi : viList) {
				totalVoucherAmount = totalVoucherAmount + vi.getAmountAgainst();
			}
			return totalVoucherAmount;
		} catch (Exception e) {
			return 0;
		}
	}
}
