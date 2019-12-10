package model.remote;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonSetter;

import model.Coupon;

public class RemoteCoupon extends Coupon {

	@JsonSetter(value = "startDate")
	public void setStartDate(String dateStr) {
		DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate parsedStartDate = LocalDate.parse(dateStr, dtFormatter);
		setStartDate(parsedStartDate);
	}

	@JsonSetter(value = "endDate")
	public void setEndDate(String dateStr) {
		DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate parsedEndDate = LocalDate.parse(dateStr, dtFormatter);
		setEndDate(parsedEndDate);
	}

}
