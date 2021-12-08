package com.order.adapter.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

public class ShoppingCart {

	private String customerId;
	private String eventType;
	private List<LineItems> lineItems = new ArrayList<LineItems>();
 
	

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public List<LineItems> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<LineItems> lineItems) {
		this.lineItems = lineItems;
	}

	@JsonIgnore
	public boolean isInCheckOutStage() {
		return this.eventType != null && this.eventType.equalsIgnoreCase("CHECKOUT");
	}
	
	@JsonIgnore
	public double getTotal() {
		double total = 0;
		List<LineItems> lineItems = this.getLineItems();
		if(lineItems!=null) {
			for (LineItems lineItems2 : lineItems) {
				total = total + lineItems2.getTotalPrice();
			}
			 
		}
		return total;
	}

	public String toJson() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{'error':" + e.getMessage() + "'}";
		}

	}

	@Override
	public String toString() {
		return toJson();
	}

	public static ShoppingCart parse(String v) {
		try {
			return new ObjectMapper().readValue(v, ShoppingCart.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

}
