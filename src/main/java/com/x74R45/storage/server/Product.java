package com.x74R45.storage.server;

import java.math.BigDecimal;

import org.json.simple.JSONObject;

public class Product {
	
	private int id;
	private String name;
	private String description;
	private String maker;
	private int amount;
	private BigDecimal price;
	private int categoryId;
	
	public Product(int id, String name, String description, String maker, int amount, BigDecimal price, int categoryId) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.maker = maker;
		this.amount = amount;
		this.price = price;
		this.categoryId = categoryId;
	}
	
	public Product(int id, String name, String description, String maker, int amount, String price, int categoryId) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.maker = maker;
		this.amount = amount;
		this.price = new BigDecimal(price);
		this.categoryId = categoryId;
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
	
	public String getMaker() {
		return maker;
	}
	
	public void setMaker(String maker) {
		this.maker = maker;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (amount != other.amount)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (maker == null) {
			if (other.maker != null)
				return false;
		} else if (!maker.equals(other.maker))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", description=" + description + ", maker=" + maker
				+ ", amount=" + amount + ", price=" + price + ']';
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject res = new JSONObject();
		res.put("product_id", getId());
		res.put("product_name", getName());
		res.put("product_description", getDescription());
		res.put("product_maker", getMaker());
		res.put("product_amount", getAmount());
		res.put("product_price", getPrice());
		res.put("category_id", getCategoryId());
		return res;
	}
	
	public boolean isValid() {
		if (getAmount() >= 0 && getPrice().doubleValue() >= 0 && getName().length() > 0)
			return true;
		return false;
	}
}