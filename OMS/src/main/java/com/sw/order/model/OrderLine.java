package com.sw.order.model;

public class OrderLine
{
	int orderLineId;
	int orderId;
	int productId;
	String productName;
	
	String status;
	int qtyOrdered;
	int qtyAllocated;
	int qtyBackOrdered;
	
	public OrderLine() {}
	
	public OrderLine(String productName, int qtyOrdered) {
		this.productName = productName;
		this.qtyOrdered = qtyOrdered;
	}
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	
	public int getOrderLineId() {
		return orderLineId;
	}
	public void setOrderLineId(int orderLineId) {
		this.orderLineId = orderLineId;
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
	
	public int getQtyOrdered() {
		return qtyOrdered;
	}
	public void setQtyOrdered(int qtyOrdered) {
		this.qtyOrdered = qtyOrdered;
	}
	
	public int getQtyAllocated() {
		return qtyAllocated;
	}
	public void setQtyAllocated(int qtyAllocated) {
		this.qtyAllocated = qtyAllocated;
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
		stringBuilder.append("OrderLineId:" + orderLineId);
		stringBuilder.append(" Product:" + productName);
		stringBuilder.append(" QtyOrdered:" + qtyOrdered);
		stringBuilder.append(" QtyAllocated:" + qtyAllocated);
		stringBuilder.append(" QtyBackOrdered:" + qtyBackOrdered);
		return stringBuilder.toString();
	}
	
	
}
