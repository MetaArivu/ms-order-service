package com.order.adapter.entities;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.order.adapter.dto.LineItems;

@Document(collection = "order_details")
public class OrderDetails extends BaseEntity {

	private String orderNo;
	private java.util.Date orderDate;
	private String cartId;
	private String customerId;
	private double total;
	private boolean paymentStatus;
	private String paymentId;
	private int stage;
	
	private List<LineItems> lineItems;

	@JsonIgnore
	@Transient
	private String authorization;

	public OrderDetails() {

	}

	public OrderDetails(String cartId, String customerId, double total, boolean paymentStatus,
			List<LineItems> lineItems, int stage) {
		super();
		this.cartId = cartId;
		this.customerId = customerId;
		this.total = total;
		this.paymentStatus = paymentStatus;
		this.stage = stage;
	}

	public OrderDetails(String cartId, String customerId, double total, boolean paymentStatus, String authorization,
			List<LineItems> lineItems,  int stage) {
		super();
		this.orderNo = System.currentTimeMillis() + "";
		this.orderDate = new java.util.Date();
		this.cartId = cartId;
		this.customerId = customerId;
		this.total = total;
		this.paymentStatus = paymentStatus;
		this.lineItems = lineItems;
		this.authorization = authorization;
		this.stage = stage;
	}

	public static class Builder {
		private String cartId;
		private String customerId;
		private double total;
		private boolean paymentStatus;
		private String authorization;
		private List<LineItems> lineItems;
		private int stage;
		
		public Builder cartId(String cartId) {
			this.cartId = cartId;
			return this;
		}

		public Builder customerId(String customerId) {
			this.customerId = customerId;
			return this;
		}

		public Builder total(double total) {
			this.total = total;
			return this;
		}

		public Builder paymentStatus(boolean paymentStatus) {
			this.paymentStatus = paymentStatus;
			return this;
		}

		public Builder authorization(String authorization) {
			this.authorization = authorization;
			return this;
		}

		public Builder lineItems(List<LineItems> lineItems) {
			this.lineItems = lineItems;
			return this;
		}
		
		public Builder stage(int stage) {
			this.stage = stage;
			return this;
		}

		public OrderDetails build() {
			return new OrderDetails(cartId, customerId, total, paymentStatus, authorization, lineItems, stage);
		}

	}

	public static Builder build() {
		return new Builder();
	}

	public String getCartId() {
		return cartId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public double getTotal() {
		return total;
	}

	public boolean isPaymentStatus() {
		return paymentStatus;
	}

	public String getAuthorization() {
		return authorization;
	}

	public List<LineItems> getLineItems() {
		return lineItems;
	}

	public void addLineItems(List<LineItems> lineItems) {
		this.lineItems = lineItems;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public java.util.Date getOrderDate() {
		return orderDate;
	}

	public OrderDetails paymentId(String paymentId) {
		this.paymentId = paymentId;
		return this;
	}
	
	public OrderDetails cartId(String cartId) {
		this.cartId = cartId;
		return this;
	}
	
	public OrderDetails authorization(String authorization) {
		this.authorization = authorization;
		return this;
	}

	public OrderDetails paymentStatus(boolean status) {
		this.paymentStatus = status;
		if(this.paymentStatus) {
			this.stage = 2;
		}
		return this;
	}
	
	public int getStage() {
		return stage;
	}

	@Override
	public String toString() {
		return id + "|" + cartId + "|" + customerId + "|" + total + "|" + paymentStatus +"|"+lineItems;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
