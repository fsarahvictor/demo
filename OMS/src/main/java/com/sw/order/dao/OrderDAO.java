package com.sw.order.dao;


import com.sw.order.model.Order;

public interface OrderDAO 
{
	public void addOrder(Order order);

	public void processOrders() throws Exception;
	
	public void run() throws Exception;
}




