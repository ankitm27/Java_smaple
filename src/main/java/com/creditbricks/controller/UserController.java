package com.creditbricks.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.hibernate.query.criteria.internal.predicate.IsEmptyPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.creditbricks.dao.BalanceSheetRepository;
import com.creditbricks.dao.CompanyRepository;
import com.creditbricks.dao.CustomerRepository;
import com.creditbricks.dao.LedgerRepository;
import com.creditbricks.dao.PincodeRepository;
import com.creditbricks.dao.ProductRepository;
import com.creditbricks.dao.SalesAndCollectionRepository;
import com.creditbricks.dao.SettingRepository;
import com.creditbricks.dao.TimeLogRepository;
import com.creditbricks.dao.UserCompanyRelationRepository;
import com.creditbricks.dao.UserRepository;
import com.creditbricks.dao.VoucherItemRepository;
import com.creditbricks.dao.VoucherRepository;
import com.creditbricks.model.BalanceSheet;
import com.creditbricks.model.Company;
import com.creditbricks.model.Customer;
import com.creditbricks.model.Ledger;
import com.creditbricks.model.Pincode;
import com.creditbricks.model.Product;
import com.creditbricks.model.SalesAndCollection;
import com.creditbricks.model.Setting;
import com.creditbricks.model.TimeLog;
import com.creditbricks.model.User;
import com.creditbricks.model.UserCompanyRelation;
import com.creditbricks.model.Voucher;
import com.creditbricks.model.VoucherItem;
import com.creditbricks.util.BalanaceSheetListInput;
import com.creditbricks.util.LedgerListInput;
import com.creditbricks.util.Message;
import com.creditbricks.util.ProductModel;
import com.creditbricks.util.SimpleVoucher;
import com.creditbricks.util.SettingDO;
import com.creditbricks.util.Util;
import com.creditbricks.util.VoucherItems;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.creditbricks.util.impl.EmailServiceImpl;


@CrossOrigin
@RestController
@RequestMapping("/")
public class UserController {

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
	ProductRepository productRepository;

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	PincodeRepository pincodeRepository;


	private EmailServiceImpl sendMail;
	
	

		

	@PostMapping("/SendPasscode")
	@CrossOrigin
	public String SendPasscode(@RequestBody User user) {

		User user1 = userRepository.findById(user.getId()).get();
		if (user1 != null) {
			String random = randomAlphaNumeric(10);
			//user1.setSecurityStamp(random);
			userRepository.save(user1);
			sendPasscode(random, user.getEmail());
			return returnResult(207, new Gson().toJson(new User()), "");

		} else {

			return returnResult(209, "Email does not exist", "");
		}

	}
	
	@PostMapping("/resetPassword")
	@CrossOrigin
	public String resetPasswordWithPasscode(@RequestBody User user) {

		User user1 = userRepository.findByEmail(user.getEmail());
		if (user1 != null) {
			String random = randomAlphaNumeric(10);
			//user1.setSecurityStamp(random);
			user1.setPassword(generateHash(random));
			userRepository.save(user1);
			EmailServiceImpl.sendSimpleMessageWithProperties(user.getEmail(), "Password", "your password is:"+random);
//			sendPasscode(random, user.getEmail());
			return returnResult(207, new Gson().toJson(new User()), "");

		} else {

			return returnResult(209, "Email does not exist", "");
		}

	}
	
	
	@PostMapping("/sendCreditLink")
	@CrossOrigin
	public String sendCreditLink(@RequestBody SettingDO setting) {

		User user1 = userRepository.findById(setting.getId()).get();
		if (user1 != null) {
			
			EmailServiceImpl.sendSimpleMessageWithProperties(setting.getMailToEmail(), setting.getMailSubject(), setting.getMailMessage());
//			sendPasscode(random, user.getEmail());
			return returnResult(207, new Gson().toJson(user1), "");

		} else {

			return returnResult(209, "User does not exist", "");
		}

	}

	@PostMapping("/ChangePassword")
	@CrossOrigin
	public String ChangePassword(@RequestBody User user) {

		User user1 = userRepository.findById(user.getId()).get();
		if (user1 != null) {
			user1.setPassword(generateHash(user.getPassword()));
			userRepository.save(user1);
			return returnResult(206, new Gson().toJson(user), "");

		} else {

			return returnResult(209, "Passcode is wrong", "");
		}

	}

	// Create a new Note
	@PostMapping("/signup")
	@Transactional
	@CrossOrigin
	public String createUser(@Valid @RequestBody User user) {
//		this.sendPasscode("ranjeet", "ranjeetnityanand@gmail.com");
//		int userCod=25000;
//		String code="00000";
		try {
			System.out.println("email"+user.getEmail());
					if (!org.springframework.util.StringUtils.isEmpty(user.getEmail())
							&& checkEmailRegex(user.getEmail())) {
						System.out.println("EMailId" + user.getEmail());
						System.err.println(user.getPhoneNumber());
						if (!checkExist(user.getEmail(), (int) user.getId(), user.getPhoneNumber())) {
							System.out.println("true");
							User userTemp=userRepository.findByAndroidId(user.getAndroidId());
							System.out.println("userTemp id "+userTemp);
							if(userTemp==null ) {
								System.out.println("check");
								
								System.out.println(user.toString()+" "+ user.getPassword()+ " "+ user.getAndroidId());
							String random = randomAlphaNumeric(10);
							user.setPasscode(random);
							user.setPassword(generateHash(user.getPassword()));
							user.setLoginId(user.getName());
//					
							User usersaved = userRepository.save(user);
							
							
					//		EmailServiceImpl.sendSimpleMessageWithProperties(user.getEmail(), "Password", "your password is:"+random);
							
							return returnResult(206, new Gson().toJson(usersaved), "");
							}else {
								return returnResult(207, "This mobile is align to diffrent user.", "");
							}
							
							
						} else {
							return returnResult(207, "User already exist", "");
						}
					} else {
						return returnResult(207, "Email Format is not Correct", "");
					}
			
		} catch (Exception e) {
			return returnResult(200, "Error in process", e.toString());
		}
	}

	

	
	@PostMapping("/signin/{type}")
	@CrossOrigin
	public String getByUsernameAndPassword(@RequestBody User user,
			@PathVariable(name = "type", value = "type", required = true) String type) {
		
	
		user.setPassword(generateHash(user.getPassword()));
		User result = null;
		if(!isEmpty(user.getLoginId())) {
			result =  userRepository.findByLoginIdAndPassword(user.getLoginId(), user.getPassword());
		if(isEmpty(result)) {
			result =  userRepository.findByEmailAndPassword(user.getLoginId(), user.getPassword());
		
		if(isEmpty(result)) {
			result =  userRepository.findByPhoneNumberAndPassword(user.getLoginId(), user.getPassword());
		}
		}
		}
		if (result != null) {
			System.err.println("Android Key"+result.getAndroidId());
			if(type.equalsIgnoreCase("A")) {
			if(result.getAndroidId()!=null && !result.getAndroidId().isEmpty() && !result.getAndroidId().equalsIgnoreCase("null") ) {
				 if(result.getAndroidId().equalsIgnoreCase(user.getAndroidId())) {
					 return returnResult(206, new Gson().toJson(result), "");
				 }else {
					 return returnResult(201, "Android Key is not Matched", result.getEmail());
				 }
			 }else {
				result.setAndroidId(user.getAndroidId());
				userRepository.save(result);
			 }
			}else if(type.equalsIgnoreCase("W")){
			return returnResult(206, new Gson().toJson(result), "");
			}else {
				return returnResult(200, "URL is not correct", "");
			}
			 return returnResult(206, new Gson().toJson(result), "");
		} else {
			return returnResult(200, "Username or password is not correct", "");
		}
	}
	
	
	
	@PostMapping("/getUserInfoByUserId")
	@CrossOrigin
	public String getUserInfoByUserId(@RequestBody User user) {
		User result = userRepository.findById(user.getId()).get();
		if (result != null) {
			result.setPassword("");
			return returnResult(206, new Gson().toJson(result), "");
		} else {
			return returnResult(200, "Wrong id", "");
		}
	}

	

	public Boolean checkExist(String email, int userId,String phoneNumber) {

		List<User> user = userRepository.findByEmailOrPhoneNumber(email,phoneNumber);
		System.out.println(user.toString()+"user");
		user.forEach(System.out::println);
//		if (user != null) {
//			System.out.println("user.getId()==userId " + user.getId() + "==" + userId);
			
			if(user.size()>0) {
				return true;
			}
			else {
				System.out.println("false");
				return false;
			}
//			if (user.getId() == userId) {
//				return false;
//			}
//			if(user.getPhoneNumber().equals(phoneNumber)) {
//				return false;
//			}
//			else {
//				return true;
//			}
			
//		} else {
//			return false;
//		}

	}
	


	private static String generateHash(String input) {
		StringBuilder hash = new StringBuilder();

		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] hashedBytes = sha.digest(input.getBytes());
			char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			for (int idx = 0; idx < hashedBytes.length; ++idx) {
				byte b = hashedBytes[idx];
				hash.append(digits[(b & 0xf0) >> 4]);
				hash.append(digits[b & 0x0f]);
			}
		} catch (NoSuchAlgorithmException e) {
			// handle error here.
		}

		return hash.toString();
	}

	public boolean sendPasscode(String passcode, String email) {
		try {
			sendMail.sendSimpleMessage(email, "Welcome to Audit", "This is your passcode: " + passcode);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public String returnResult(int error, String result, String exception) {
		if (error != 206) {
			Message message = new Message();
			message.setCode(error);
			message.setMessage(result);
			message.setDescription(exception);
			String gson = new Gson().toJson(message);
			return gson;
		} else {
			JsonObject json = (JsonObject) new JsonParser().parse(result);
			json.addProperty("code", error);
			return json.toString();
		}

	}

	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static String randomAlphaNumeric(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}
	private static final String NUMERIC_VALUE = "0123456789";

	public static String randomNumeric(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * NUMERIC_VALUE.length());
			builder.append(NUMERIC_VALUE.charAt(character));
		}
		return builder.toString();
	}

	boolean checkEmailRegex(String email) {

		String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(email).matches();
	}
	
	
	private boolean isEmpty(Object input){
		return ObjectUtils.isEmpty(input);
		
	}
	
	
	
//	============================================================================================================================
	private static String createDateInString() {
		Instant nowUtc = Instant.now();
		ZoneId asiaKolkata = ZoneId.of("Asia/Kolkata");
		ZonedDateTime nowAsiaKolkata = ZonedDateTime.ofInstant(nowUtc, asiaKolkata);
		String str="";
		if(nowAsiaKolkata.getDayOfMonth()<10 && nowAsiaKolkata.getMonthValue() < 10) {
		str=("0"+nowAsiaKolkata.getDayOfMonth() +"-"+"0"+ nowAsiaKolkata.getMonthValue() +"-"+ nowAsiaKolkata.getYear());
		return str;
		}
		else if(nowAsiaKolkata.getDayOfMonth() < 10 && nowAsiaKolkata.getMonthValue() >= 10 ) {
		str=("0"+nowAsiaKolkata.getDayOfMonth() +"-"+nowAsiaKolkata.getMonthValue() +"-"+ nowAsiaKolkata.getYear());
		return str;
		}
		else if(nowAsiaKolkata.getDayOfMonth() >= 10 && nowAsiaKolkata.getMonthValue() < 10) {
			str=(nowAsiaKolkata.getDayOfMonth() +"-"+"0"+ nowAsiaKolkata.getMonthValue() +"-"+ nowAsiaKolkata.getYear());
			return str;
		}
		else {
			str=(nowAsiaKolkata.getDayOfMonth() +"-"+nowAsiaKolkata.getMonthValue() +"-"+ nowAsiaKolkata.getYear());
			return str;
		}
		
	}
}
