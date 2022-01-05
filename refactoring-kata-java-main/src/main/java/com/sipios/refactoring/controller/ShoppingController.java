package com.sipios.refactoring.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {
	private static final String STANDARD_CUSTOMER = "STANDARD_CUSTOMER";
	private static final String PREMIUM_CUSTOMER = "PREMIUM_CUSTOMER";
	private static final String PLATINUM_CUSTOMER = "PLATINUM_CUSTOMER";
	private static final String TSHIRT_CLOTHES = "PLATINUM_CUSTOMER";
	private static final String DRESS_CLOTHES = "PLATINUM_CUSTOMER";
	private static final String JACKET_CLOTHES = "PLATINUM_CUSTOMER";
	
	private static final int STANDARD_LIMIT_PRICE = 200;
	private static final int PREMIMIUM_LIMIT_PRICE = 800;
	private static final int PLATINUM_LIMIT_PRICE = 2000;

	private static final int DRESS_PRICE = 30;
	private static final int TSHIRT_PRICE = 50;
	private static final int JACKET_PRICE = 100;


	private static final double DRESS_DISCOUNT = 0.8;
	private static final double JACKET_DICOUNT = 0.9;
	
	private Logger logger = LoggerFactory.getLogger(ShoppingController.class);


	private static double getCustomerDiscount(@RequestBody Body b) {
		double d;
		if (b.getType().equals(STANDARD_CUSTOMER)) {
			d = 1;
		} else if (b.getType().equals(PREMIUM_CUSTOMER)) {
			d = 0.9;
		} else if (b.getType().equals(PLATINUM_CUSTOMER)) {
			d = 0.5;
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		return d;
	}

	
	// Compute total amount depending on the types and quantity of product and
	// if we are in winter or summer discounts periods
	private static double getDiscountedPrice(@RequestBody Body b) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
		cal.setTime(date);
		double price = 0;

		if (b.getItems() == null) {
			return 0;
		}

		for (int i = 0; i < b.getItems().length; i++) {
			Item it = b.getItems()[i];
		if (
			!(
			cal.get(Calendar.DAY_OF_MONTH) < 15 &&
			cal.get(Calendar.DAY_OF_MONTH) > 5 && 
			cal.get(Calendar.MONTH) == 5)
			&& 
			!(
			cal.get(Calendar.DAY_OF_MONTH) < 15 &&
			cal.get(Calendar.DAY_OF_MONTH) > 5 &&
			cal.get(Calendar.MONTH) == 0)) 
			{
				if (it.getType().equals(TSHIRT_CLOTHES)) {
					price += TSHIRT_PRICE * it.getNb() * getCustomerDiscount(b);
				} else if (it.getType().equals(DRESS_CLOTHES)) {
					price += DRESS_PRICE * it.getNb() * getCustomerDiscount(b);
				} else if (it.getType().equals(JACKET_CLOTHES)) {
					price += JACKET_PRICE * it.getNb() * getCustomerDiscount(b);
				}
			} else {
				if (it.getType().equals(TSHIRT_CLOTHES)) {
					price += TSHIRT_PRICE * it.getNb() * getCustomerDiscount(b);
				} else if (it.getType().equals(DRESS_CLOTHES)) {
					price += DRESS_PRICE * it.getNb() * DRESS_DISCOUNT *  getCustomerDiscount(b);
				} else if (it.getType().equals(JACKET_CLOTHES)) {
					price += JACKET_PRICE * it.getNb() * JACKET_DICOUNT * getCustomerDiscount(b);
				}
			}
		}
		return price;
	}
	@PostMapping
	public String getPrice(@RequestBody Body b) {
		double p = 0;
		
		p = getDiscountedPrice(b);
		
		try {
			if (b.getType().equals(STANDARD_CUSTOMER) && p > STANDARD_LIMIT_PRICE)
					throw new Exception(String.format("Price %d is too high for standard customer", p));
			else if (b.getType().equals(PREMIUM_CUSTOMER) && p > PREMIMIUM_LIMIT_PRICE)
					throw new Exception(String.format("Price %d is too high for premium customer", p));
			else if (b.getType().equals(PLATINUM_CUSTOMER) && p > PLATINUM_LIMIT_PRICE)
					throw new Exception(String.format("Price %d is too high for platinum customer", p));
			if (p == 0)
				throw new Exception(String.format("No items contains discount impossible"));
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

		return String.valueOf(p);
	}
}

class Body {

	private Item[] items;
	private String type;

	public Body(Item[] is, String t) {
		this.items = is;
		this.type = t;
	}

	public Body() {
	}

	public Item[] getItems() {
		return items;
	}

	public void setItems(Item[] items) {
		this.items = items;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

class Item {

	private String type;
	private int nb;

	public Item() {
	}

	public Item(String type, int quantity) {
		this.type = type;
		this.nb = quantity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNb() {
		return nb;
	}

	public void setNb(int nb) {
		this.nb = nb;
	}
}