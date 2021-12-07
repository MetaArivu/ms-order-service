package com.order.adapter.entities;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "order_details")
public class OrderDetails extends BaseEntity {

	private String cartId;
	private String customerId;
	private double total;
	private boolean paymentStatus;

	private List<LinkedHashMap> lineItems;

	@JsonIgnore
	@Transient
	private String authorization;

	public OrderDetails() {
		
	}
	
	public OrderDetails(String cartId, String customerId, double total, boolean paymentStatus,
			List<LinkedHashMap> lineItems) {
		super();
		this.cartId = cartId;
		this.customerId = customerId;
		this.total = total;
		this.paymentStatus = paymentStatus;
	}

	public OrderDetails(String cartId, String customerId, double total, boolean paymentStatus, String authorization,
			List<LinkedHashMap> lineItems) {
		super();
		this.cartId = cartId;
		this.customerId = customerId;
		this.total = total;
		this.paymentStatus = paymentStatus;
		this.authorization = authorization;
	}

	public static class Builder {
		private String cartId;
		private String customerId;
		private double total;
		private boolean paymentStatus;
		private String authorization;
		private List<LinkedHashMap> lineItems;

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

		public Builder lineItems(List<LinkedHashMap> lineItems) {
			this.lineItems = lineItems;
			return this;
		}

		public OrderDetails build() {
			return new OrderDetails(cartId, customerId, total, paymentStatus, authorization, lineItems);
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

	public List<LinkedHashMap> getLineItems() {
		return lineItems;
	}

	public void addLineItems(List<LinkedHashMap> lineItems) {
		this.lineItems = lineItems;
	}

	@Override
	public String toString() {
		return id + "|" + cartId + "|" + customerId + "|" + total + "|" + paymentStatus;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
