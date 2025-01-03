package com.clover.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Order1 {

	@Id
	private String orderId;
	private String customerId;

	@OneToMany(cascade = CascadeType.ALL)
	private List<OrderItem> orderItems;
	private Double totalAmount;
	private String paymentStatus;
	private String orderStatus; // (PENDING, SHIPPED, DELIVERED, CANCELLED)

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deliveryDate;

	private String shippingAddress;
	private String paymentMethod;
	private String trackingNumber;

	private Boolean isActive;

}
