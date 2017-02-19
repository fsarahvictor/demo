package com.sw.order.model;

public class Product
{
	int productId;
	String productName;
	
	String status;
	int qty;
	int qtyBackOrdered;
	
	public Product() {}
	
	public Product(String productName, int qty) {
		this.productName = productName;
		this.qty = qty;
	}
	
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public int getQtyBackOrdered() {
		return qtyBackOrdered;
	}
	public void setQtyBackOrdered(int qtyBackOrdered) {
		this.qtyBackOrdered = qtyBackOrdered;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		return stringBuilder.toString();
	}
	
	
}
