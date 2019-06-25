package com.x74R45.storage.server;

import java.math.BigDecimal;

import org.json.simple.JSONObject;

public class Category {

	private int id;
	private String name;
	private String description;
	private BigDecimal totalPrice;
	
	public Category(int id, String name, String description, BigDecimal totalPrice) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.totalPrice = totalPrice;
	}
	
	public Category(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.totalPrice = new BigDecimal("0.00");
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (totalPrice == null) {
			if (other.totalPrice != null)
				return false;
		} else if (!totalPrice.equals(other.totalPrice))
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject res = new JSONObject();
		res.put("category_id", getId());
		res.put("category_name", getName());
		res.put("category_description", getDescription());
		res.put("category_total_price", getTotalPrice());
		return res;
	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name 
				+ ", description=" + description + ", totalPrice=" + totalPrice + ']';
	}
}