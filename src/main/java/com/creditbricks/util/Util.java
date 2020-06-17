package com.creditbricks.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.creditbricks.model.Company;
import com.creditbricks.model.TimeLog;
import com.creditbricks.model.Voucher;
import com.creditbricks.model.VoucherItem;

public class Util {

	public static java.sql.Date convertStrToDateYYYMMDD(String inputdate) {
		java.sql.Date sqlStartDate = null;
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date utildate = sdf1.parse(inputdate);
			System.out.println("utildate >> " + utildate);
			sqlStartDate = new java.sql.Date(utildate.getTime());
			System.out.println("sqlStartDate >> " + sqlStartDate);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return sqlStartDate;
	}

	public static String getYear(String inputdate) {
		String year = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date utildate = sdf1.parse(inputdate);
			//System.out.println("utildate >> " + utildate);
			java.sql.Date sqlStartDate = new java.sql.Date(utildate.getTime());
			//System.out.println("sqlStartDate >> " + sqlStartDate);
			year = formatter.format(sqlStartDate);

		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return year;
	}

	public static List<Date> getListOfDate(String str_date, String end_date) {
		List<Date> dates = new ArrayList<Date>();
		DateFormat formatter;
		try {
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate;

			startDate = (Date) formatter.parse(str_date);

			Date endDate = (Date) formatter.parse(end_date);
			long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
			long endTime = endDate.getTime(); // create your endtime here,
												// possibly using Calendar or
												// Date
			long curTime = startDate.getTime();
			while (curTime <= endTime) {
				dates.add(new Date(curTime));
				curTime += interval;
			}
			for (int i = 0; i < dates.size(); i++) {
				Date lDate = (Date) dates.get(i);
				String ds = formatter.format(lDate);
				//System.out.println(" Date is ..." + ds);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dates;
	}

	public static List<String> getListOfMonth(String str_date, String end_date) {
		List<String> months = new ArrayList<>();
		DateFormat formatter;
		try {
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat formater = new SimpleDateFormat("MMM-yy");

			Calendar beginCalendar = Calendar.getInstance();
			Calendar finishCalendar = Calendar.getInstance();
			beginCalendar.setTime(formatter.parse(str_date));
			finishCalendar.setTime(formatter.parse(end_date));

			while (beginCalendar.before(finishCalendar)) {
				// add one month to date per loop
				String date = formater.format(beginCalendar.getTime()).toUpperCase();
				//System.out.println(date);
				months.add(date);
				beginCalendar.add(Calendar.MONTH, 1);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return months;
	}
	
	
	public static List<String> getListOfQuater(String str_date, String end_date) {
		 ArrayList<String> quarters = new ArrayList<String>();
		try {
			//String fromDate = "01-Jan-2015";
           // String toDate = "01-Apr-2015";

           
          //  DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dfYY = new SimpleDateFormat("yy");

            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(str_date));

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(df.parse(end_date));

            while (cal1.getTime().after(cal.getTime()) || cal1.getTime().equals(cal.getTime())){
                int month = cal.get(Calendar.MONTH) + 1;

                int quarter = month % 3 == 0 ? (month / 3) : (month / 3) + 1;
					if(quarter==1){
					   quarters.add("JAN-MAR," + dfYY.format(cal.getTime()));
					}else if(quarter==2){
						 quarters.add("APR-JUN," + dfYY.format(cal.getTime()));
					}else if(quarter==3){
						 quarters.add("JUL-SEP," + dfYY.format(cal.getTime()));
					}else if(quarter==4){
						 quarters.add("OCT-DEC," + dfYY.format(cal.getTime()));
					}
                cal.add(Calendar.MONTH, 3);
            }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return quarters;
	}
	
	public static String getQuaterOfDate(String str_date) {
		 String quarters = "";
		try {
			//String fromDate = "01-Jan-2015";
          // String toDate = "01-Apr-2015";

          
           DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
           DateFormat dfYY = new SimpleDateFormat("yy");

           Calendar cal = Calendar.getInstance();
           cal.setTime(df.parse(str_date));

          
          // while (cal1.getTime().after(cal.getTime()) || cal1.getTime().equals(cal.getTime())){
               int month = cal.get(Calendar.MONTH) + 1;
               int quarter = month % 3 == 0 ? (month / 3) : (month / 3) + 1;
              // quarters="Q" + quarter + "-" + dfYY.format(cal.getTime());
              // int quarter = month % 3 == 0 ? (month / 3) : (month / 3) + 1;
				if(quarter==1){
				   quarters="JAN-MAR," + dfYY.format(cal.getTime());
				}else if(quarter==2){
					 quarters="APR-JUN," + dfYY.format(cal.getTime());
				}else if(quarter==3){
					 quarters="JUL-SEP," + dfYY.format(cal.getTime());
				}else if(quarter==4){
					 quarters="OCT-DEC," + dfYY.format(cal.getTime());
				}
               cal.add(Calendar.MONTH, 3);
         //  }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return quarters;
	}
	
	
	public static String getVoucherMonth(Voucher voucher) {
		// SimpleDateFormat formatter = new SimpleDateFormat("MMM");
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-yy");
		return formatter.format(voucher.getDate()).toUpperCase();
	}

	public static String getVoucherQuater(Voucher voucher) {
		// SimpleDateFormat formatter = new SimpleDateFormat("MMM");
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-yy");
		String quaterStr = "";
		String[] quater = new String[] { "Apr-Jun", "Jul-Sep", "Oct-Dec", "Jan-Mar" };
		ArrayList<String> q1 = new ArrayList<String>(Arrays.asList("Apr", "May", "Jun"));
		ArrayList<String> q2 = new ArrayList<String>(Arrays.asList("Jul", "Aug", "Sep"));
		ArrayList<String> q3 = new ArrayList<String>(Arrays.asList("Oct", "Nov", "Dec"));
		ArrayList<String> q4 = new ArrayList<String>(Arrays.asList("Jan", "Feb", "Mar"));
		if (q1.contains(formatter.format(voucher.getDate()))) {
			quaterStr = quater[0];
		} else if (q2.contains(formatter.format(voucher.getDate()))) {
			quaterStr = quater[1];
		} else if (q3.contains(formatter.format(voucher.getDate()))) {
			quaterStr = quater[2];
		} else if (q4.contains(formatter.format(voucher.getDate()))) {
			quaterStr = quater[3];
		}
		return quaterStr;
	}

	public static String getVoucherQuaterByDate(Voucher voucher) {
		// SimpleDateFormat formatter = new SimpleDateFormat("MMM");
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

		return Util.getQuaterOfDate(formatter.format(voucher.getDate()));
	}

	public static String getVoucherDateNdMonth(Voucher voucher) {
		// SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM");
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
		return formatter.format(voucher.getDate());
	}
	
	public static String getVoucherDateNdMonth1(Voucher voucher) {
		// SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String output="";
		java.util.Date utildate;
		try {
			 utildate = sdf.parse(voucher.getStringDate());
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
				output= formatter.format(utildate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	

	

	public static Date parseDateFromString(String input) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			java.util.Date utildate = sdf.parse(input);
			//System.out.println("utildate >> " + utildate);
			Date sqlStartDate = new java.sql.Date(utildate.getTime());
		//	//System.out.println("sqlStartDate >> " + sqlStartDate);
			return sqlStartDate;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static List<SimpleVoucher> getSimpleVoucherDateList(String startDate, String endDate) {
		List<SimpleVoucher> list = new ArrayList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
		List<Date> dateList = Util.getListOfDate(startDate, endDate);
		for (Date date : dateList) {
			String formatedDate = formatter.format(date);
			SimpleVoucher sv = new SimpleVoucher();
			sv.setName(formatedDate);
			list.add(sv);
		}
		return list;
	}

	public static List<SimpleVoucher> getSimpleVoucherMonthsList(String startDate, String endDate) {
		List<SimpleVoucher> list = new ArrayList<>();
		// SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
		List<String> dateList = Util.getListOfMonth(startDate, endDate);
		for (String month : dateList) {
			// String formatedDate= formatter.format(date);
			SimpleVoucher sv = new SimpleVoucher();
			sv.setName(month);
			list.add(sv);
		}
		return list;
	}

	public static List<SimpleVoucher> getSimpleVoucherQuaterList(String startDate, String endDate) {
		List<SimpleVoucher> list = new ArrayList<>();
		// SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
		List<String> dateList = Util.getListOfQuater(startDate, endDate);
		for (String month : dateList) {
			// String formatedDate= formatter.format(date);
			SimpleVoucher sv = new SimpleVoucher();
			sv.setName(month);
			list.add(sv);
		}
		return list;
	}

	public static Message returnResult(int error, String result, String exception, String content) {

		Message message = new Message();
		message.setCode(error);
		message.setContent(content);
		message.setMessage(result);
		message.setDescription(exception);
		return message;

	}
	public static List<SimpleVoucher> getSimpleVoucherTotalMonthsList(String startDate, String endDate) {
		List<SimpleVoucher> list = new ArrayList<>();
		// SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
		List<String> dateList = Util.getListOfTotalMonth(startDate, endDate);
		for (String month : dateList) {
			// String formatedDate= formatter.format(date);
			//System.out.println("month >>>>>>>>>>>>>>>>>>>>>>>"+month);
			SimpleVoucher sv = new SimpleVoucher();
			sv.setName(month);
			list.add(sv);
		}
		return list;
	}
	public static String getVoucherTotalMonth(Voucher voucher) {
		// SimpleDateFormat formatter = new SimpleDateFormat("MMM");
		SimpleDateFormat formatter = new SimpleDateFormat("MMM");
		return formatter.format(voucher.getDate()).toUpperCase();
	}
	
	public static String getVoucherTotalDay(Voucher voucher) {
		// SimpleDateFormat formatter = new SimpleDateFormat("MMM");
		SimpleDateFormat formatter = new SimpleDateFormat("dd");
		System.out.println(voucher.getDate()+" >> formatter.format(voucher.getDate()) >>>>>>>>>>>>>>>>>>>>>>>"+formatter.format(voucher.getDate()));
		return formatter.format(voucher.getDate());
	}
	
	public static String getVoucherTotalDay1(Voucher voucher) {
		// SimpleDateFormat formatter = new SimpleDateFormat("MMM");
		//SimpleDateFormat formatter = new SimpleDateFormat("dd");
		System.out.println(voucher.getStringDate()+" >> formatter.format(voucher.getDate()) >>>>>>>>>>>>>>>>>>>>>>>"+voucher.getStringDate().substring(6, 8));
		return voucher.getStringDate().substring(6, 8);
	}
	
	public static List<String> getListOfTotalMonth(String str_date, String end_date) {
		List<String> months = new ArrayList<>();
		DateFormat formatter;
		try {
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat formater = new SimpleDateFormat("MMM");

			Calendar beginCalendar = Calendar.getInstance();
			Calendar finishCalendar = Calendar.getInstance();
			beginCalendar.setTime(formatter.parse(str_date));
			finishCalendar.setTime(formatter.parse(end_date));

			while (beginCalendar.before(finishCalendar)) {
				// add one month to date per loop
				String date = formater.format(beginCalendar.getTime()).toUpperCase();
				//System.out.println(date);
				if(!months.contains(date)){
				months.add(date);
				}
				//System.out.println("date >>>>>>>>>>>>>>>>>>>>>>>"+date);
				
				beginCalendar.add(Calendar.MONTH, 1);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return months;
	}
	
}