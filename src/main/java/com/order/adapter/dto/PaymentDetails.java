package com.order.adapter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.adapter.entities.BaseEntity;

public class PaymentDetails {

	private String id;
	private String cartId;
	private String customerId;
	private double total;
	private boolean status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCartId() {
		return cartId;
	}

	public void setCartId(String cartId) {
		this.cartId = cartId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isValid() {
		return (this.cartId != null && this.customerId != null && this.total > 0);
	}

	@Override
	public String toString() {
		return cartId + "|" + customerId + "|" + total + "|" + status;
	}

	public static String invalidMsg() {
		return "Please enter valid payment details";
	}

	public static PaymentDetails parse(String value) {
		try {
			return new ObjectMapper().readValue(value, PaymentDetails.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return null;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
