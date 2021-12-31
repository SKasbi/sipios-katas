package com.sipios.refactoring.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {
    
    // Compute discount for customer
    static double costumerDicount(@RequestBody Body b){
        double d;
        if (b.getType().equals("STANDARD_CUSTOMER")) {
            d = 1;
        } else if (b.getType().equals("PREMIUM_CUSTOMER")) {
            d = 0.9;
        } else if (b.getType().equals("PLATINUM_CUSTOMER")) {
            d = 0.5;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return d;
    }
    
    // Compute total amount depending on the types and quantity of product and
    // if we are in winter or summer discounts periods
    static double totalAmount(@RequestBody Body b){
        double p = 0;
        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(date);
        
        if (
            (cal.get(Calendar.DAY_OF_MONTH) < 15 &&
             cal.get(Calendar.DAY_OF_MONTH) > 5 && 
             cal.get(Calendar.MONTH) == 5) 
            || 
            (cal.get(Calendar.DAY_OF_MONTH) < 15 &&
            cal.get(Calendar.DAY_OF_MONTH) > 5 &&
            cal.get(Calendar.MONTH) == 0)
            ) {    
            for (Item it : b.getItems()) {
                if (it.getType().equals("TSHIRT")) {
                    p += 30 * it.getNb() * costumerDicount(b);
                } else if (it.getType().equals("DRESS")) {
                    p += 50 * it.getNb() * 0.8 * costumerDicount(b);
                } else if (it.getType().equals("JACKET")) {
                    p += 100 * it.getNb() * 0.9 * costumerDicount(b);
                } 
            }
        } else {
            for (Item it : b.getItems()) {
                if (it.getType().equals("TSHIRT")) {
                    p += 30 * it.getNb() *costumerDicount(b);
                } else if (it.getType().equals("DRESS")) {
                    p += 50 * it.getNb() *costumerDicount(b);
                } else if (it.getType().equals("JACKET")) {
                    p += 100 * it.getNb() * costumerDicount(b);
                }
            }
            
        }
        return p;
    }

    @PostMapping
    public String getPrice(@RequestBody Body b) {
        
        if (b.getItems() == null)
        return "0";  
        
        Double price = totalAmount(b);
       
        try {
            if (b.getType().equals("STANDARD_CUSTOMER") && price > 200) {
                    throw new Exception("Price (" + price + ") is too high for standard customer");
            } else if (b.getType().equals("PREMIUM_CUSTOMER") && price > 800) {
                    throw new Exception("Price (" + price + ") is too high for premium customer");
            } else if (b.getType().equals("PLATINUM_CUSTOMER") && price > 2000) {
                    throw new Exception("Price (" + price + ") is too high for platinum customer");
            } 
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return String.valueOf(price);
    }
}

class Body {

    private Item[] items;
    private String type;

    public Body(Item[] is, String t) {
        this.items = is;
        this.type = t;
    }

    public Body() {}

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

    public Item() {}

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
