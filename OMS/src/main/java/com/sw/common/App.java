package com.sw.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sw.order.dao.OrderDAO;

public class App 
{
    public static void main( String[] args )
    {
    	ApplicationContext context = new ClassPathXmlApplicationContext("Sw-Orders.xml");
    	 
        OrderDAO orderDAO = (OrderDAO) context.getBean("orderDAO");
        try {
	        orderDAO.run();
        } catch (Exception e) {
        	System.out.println("System has an error exception:" + e);
        }        
    }
}
