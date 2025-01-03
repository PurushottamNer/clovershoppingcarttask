package com.clover.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Catalog {

	@Id
	private String productId;
	private String productName;
	@Column(length = 1000)
	private String productDescription;
	private Double price;
	private String currency;
	private String category; // e.g., Electronics, Clothing
	private Double averageRating; // Average rating out of 5
	private Integer reviewCount; // Number of reviews
	private String brand;
	private String modelNumber;
	private String dimensions; // Product dimensions, e.g., "15.6 x 10.2 x 0.8 inches"
	private String weight;
	private String color;
	private String warranty;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedAt;
	private boolean isActive;

}
