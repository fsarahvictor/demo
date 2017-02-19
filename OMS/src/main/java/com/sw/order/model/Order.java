package com.sw.order.model;

import java.util.List;

public class Order 
{
	int orderId;
	String orderName;
	String status;
	List<OrderLine> orderLines;
	
	public Order() {}
	
	public Order(String name, List<OrderLine> orderLines) {
		this.orderName = name;
		this.orderLines = orderLines;
	}
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	
	public String getOrderName() {
		return orderName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public List<OrderLine> getOrderLines() {
		return orderLines;
	}
	public void setOrderLines(List<OrderLine> orderLines) {
		this.orderLines = orderLines;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("OrderName:" + orderName + " OrderStatus:" + status);
		return stringBuilder.toString();
	}
	
	public String detail()
	{
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(toString());
		List<OrderLine> orderLines = getOrderLines();
		for (OrderLine line : orderLines) {
			stringBuilder.append("\n");
			stringBuilder.append(line.toString());
		}
		return stringBuilder.toString();
	}
}
