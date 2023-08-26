package com.qortex.inventory.common.utils;

import com.qortex.inventory.common.exception.ValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public DateUtil() {
		super();
	}

	private static final String IST_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
	private static final String KYC_DATE_TIME_FORMAT = "yyyy-MM-dd";

	public static Date getDateByString(String dateString) {
		if (org.springframework.util.StringUtils.isEmpty(dateString)) {
			return null;
		}
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(IST_DATE_TIME_FORMAT);
			date = sdf.parse(dateString);
		} catch (ParseException ex) {
		}
		return date;
	}

	public static String getStringValByDate(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(IST_DATE_TIME_FORMAT);
		String dateString = simpleDateFormat.format(date);
		return dateString;
	}

	public static String getKYCStringValByDate(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(KYC_DATE_TIME_FORMAT);
		String dateString = simpleDateFormat.format(date);
		return dateString;
	}

	public static Date getCurrentDate() throws ValidationException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(IST_DATE_TIME_FORMAT);
		String dateString = simpleDateFormat.format(new Date());
		return getDateByString(dateString);
	}

	public static String getCurrentDateAsString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(IST_DATE_TIME_FORMAT);
		return simpleDateFormat.format(new Date());
	}
}
