package com.sw.order.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.sw.order.dao.OrderDAO;
import com.sw.order.model.Order;
import com.sw.order.model.OrderLine;
import com.sw.order.model.Product;

public class JdbcOrderDAO implements OrderDAO
{
	private DataSource dataSource;
	boolean outOfOrders = false;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void addOrder(Order order)
	{
		System.out.println("\nADDING ORDER:" + order.detail());
		addOrder(order.getOrderName());
		order = getOrder(order);
		int orderId = order.getOrderId();
		
		List<OrderLine> lines = order.getOrderLines();
		for (int i = 0; i < lines.size(); i++) {
			OrderLine orderLine = lines.get(i);
			orderLine.setOrderId(orderId);
			addOrderLine(orderLine);
		}
	}
	
	public void addOrderLine(OrderLine orderLine)
	{
		String sql = "INSERT INTO sw_order_line " +
				"(sw_order_id, order_line_qty, sw_product_id) VALUES (?, ?, " +
				"(SELECT product_id from sw_product WHERE product_name=?))";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, orderLine.getOrderId());
			ps.setInt(2, orderLine.getQtyOrdered());
			ps.setString(3, orderLine.getProductName());
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
			
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public void addOrder(String orderName)
	{
		String sql = "INSERT INTO sw_order " +
				"(order_name) VALUES (?)";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, orderName);
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
			
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public void updateOrder(Order order)
	{
		String sql = "UPDATE sw_order SET order_status = ?" +
				" WHERE order_id=?";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, order.getStatus());
			ps.setInt(2, order.getOrderId());
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
			
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public Order getOrder(Order order){
		
		String sql = "SELECT * FROM sw_order WHERE order_name = ?";
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, order.getOrderName());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				order.setOrderId(rs.getInt("order_id"));
			}
			rs.close();
			ps.close();
			return order;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public List<Order> getOrdersByStatus(String status)
	{
		List<Order> orders = new ArrayList<Order>();
		
		String sql = "SELECT order_id, order_name FROM sw_order WHERE order_status = ?";
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, status);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Order order = new Order();
				order.setOrderId(rs.getInt("order_id"));
				order.setOrderName(rs.getString("order_name"));
				orders.add(order);
			}
			rs.close();
			ps.close();
			orders = getOrderLines(orders);
			return orders;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public List<Order> getOrders()
	{
		List<Order> orders = new ArrayList<Order>();
		
		String sql = "SELECT order_id, order_name, order_status FROM sw_order";
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Order order = new Order();
				order.setOrderId(rs.getInt("order_id"));
				order.setOrderName(rs.getString("order_name"));
				order.setStatus(rs.getString("order_status"));
				orders.add(order);
			}
			rs.close();
			ps.close();
			orders = getOrderLines(orders);
			return orders;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public List<Order> getOrderLines(List<Order> orders)
	{
		String sql = "SELECT order_line_id, order_line_qty, order_line_qty_allct, order_line_qty_bckord, product_name " +
		"FROM sw_order_line, sw_product WHERE " +
		"sw_product.product_id = sw_order_line.sw_product_id AND sw_order_id = ?";
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			for (int i = 0; i < orders.size(); i++) {
				Order order = orders.get(i);
				List<OrderLine> orderLines = new ArrayList<OrderLine>();
				order.setOrderLines(orderLines);
				
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, order.getOrderId());
	
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					OrderLine orderLine = new OrderLine();
					orderLine.setOrderId(order.getOrderId());
					orderLine.setOrderLineId(rs.getInt("order_line_id"));
					orderLine.setQtyOrdered(rs.getInt("order_line_qty"));
					orderLine.setQtyBackOrdered(rs.getInt("order_line_qty_bckord"));
					orderLine.setQtyAllocated(rs.getInt("order_line_qty_allct"));
					orderLine.setProductName(rs.getString("product_name"));
					orderLines.add(orderLine);
				}
				rs.close();
				ps.close();
			}
			
			return orders;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public void updateOrderLine(OrderLine orderLine)
	{
		String sql = "UPDATE sw_order_line SET order_line_status = ?, order_line_qty_allct = ?, " +
			" order_line_qty_bckord = ? WHERE order_line_id=?";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, orderLine.getStatus());
			ps.setInt(2, orderLine.getQtyAllocated());
			ps.setInt(3, orderLine.getQtyBackOrdered());
			ps.setInt(4,  orderLine.getOrderLineId());
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
			
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public Product getProductByName(String productName)
	{
		String sql = "SELECT product_id, product_status, product_qty, " +
		"product_qty_bckord FROM sw_product WHERE product_name = ?";
		
		Connection conn = null;
		Product product = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, productName);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				product = new Product();
				product.setProductName(productName);
				product.setProductId(rs.getInt("product_id"));
				product.setQty(rs.getInt("product_qty"));
				product.setQtyBackOrdered(rs.getInt("product_qty_bckord"));
				product.setStatus(rs.getString("product_status"));

			}
			rs.close();
			ps.close();
			
			return product;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public boolean hasInventory()
	{
		String sql = "SELECT SUM(product_qty) AS total FROM sw_product";
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			int qty = 0;
			if (rs.next()) {
				qty = rs.getInt("total");
			}
			rs.close();
			ps.close();
			
			if (qty > 0)
				return true;
			return false;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public void updateProduct(Product product)
	{
		String sql = "UPDATE sw_product SET product_status = ?, product_qty = ?" +
				" WHERE product_id=?";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, product.getStatus());
			ps.setInt(2, product.getQty());
			ps.setInt(3,  product.getProductId());
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
			
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public List<Order> fulfilOrders(List<Order> orders)
	{
		// get pending orders by order of placement
		for (int i = 0; i < orders.size(); i++) {

			Order order = orders.get(i);
			List<OrderLine> lines = order.getOrderLines();
			System.out.println("\nFULFILLING ORDER:" + order.detail());
			
			boolean backOrdered = false;
			for (int j = 0; j < lines.size(); j++) {
				
				OrderLine line = lines.get(j);
				int orderQty = line.getQtyOrdered();
				String productName = line.getProductName();
				
				Product product = getProductByName(productName);
				
				int productQty = product.getQty();
				if (productQty >= orderQty) {
					product.setQty(productQty - orderQty);
					updateProduct(product);
					
					line.setStatus("ALLOCATED");
					line.setQtyAllocated(orderQty);
					updateOrderLine(line);
					
				} else {
					product.setStatus("BACKORDERED");
					product.setQtyBackOrdered(orderQty);
					updateProduct(product);
					
					line.setStatus("BACKORDERED");
					line.setQtyBackOrdered(orderQty);
					updateOrderLine(line);
					backOrdered = true;
				}
			}
			if (backOrdered) {
				order.setStatus("BACKORDERED");
				updateOrder(order);
			} else {
				order.setStatus("COMPLETE");
				updateOrder(order);
			}
			
		}
		return orders;
	}
	
	public void printOrders()
	{
		List<Order> orders = getOrders();
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\nPRINTING ALL ORDERS:\n\n");
		for(Order order : orders) {
			stringBuilder.append(order.toString());
			List<OrderLine> orderLines = order.getOrderLines();
			for (OrderLine line : orderLines) {
				stringBuilder.append("\n");
				stringBuilder.append(line.toString());
			}
			stringBuilder.append("\n\n");

		}
		System.out.println(stringBuilder.toString());
	}
	
	public static void preEnter(OrderDAO orderDAO)
	{
		List<OrderLine> orderLines1 = new ArrayList<OrderLine>();
        orderLines1.add(new OrderLine("A", 1));
        orderLines1.add(new OrderLine("C", 1));
        Order order1 = new Order("1", orderLines1);
        orderDAO.addOrder(order1);
        
        List<OrderLine> orderLines2 = new ArrayList<OrderLine>();
        orderLines2.add(new OrderLine("E", 5));
        Order order2 = new Order("2", orderLines2);
        orderDAO.addOrder(order2);
        
        List<OrderLine> orderLines3 = new ArrayList<OrderLine>();
        orderLines3.add(new OrderLine("D", 4));
        Order order3 = new Order("3", orderLines3);
        orderDAO.addOrder(order3);
        
        List<OrderLine> orderLines4 = new ArrayList<OrderLine>();
        orderLines4.add(new OrderLine("A", 1));
        orderLines4.add(new OrderLine("C", 1));
        Order order4 = new Order("4", orderLines4);
        orderDAO.addOrder(order4);
        
        List<OrderLine> orderLines5 = new ArrayList<OrderLine>();
        orderLines5.add(new OrderLine("B", 3));
        Order order5 = new Order("5", orderLines5);
        orderDAO.addOrder(order5);
        
        List<OrderLine> orderLines6 = new ArrayList<OrderLine>();
        orderLines6.add(new OrderLine("D", 4));
        Order order6 = new Order("6", orderLines6);
        orderDAO.addOrder(order6);
	}
	
	public class OrderGenerator implements Runnable {
		 private Thread t;
		 private String threadName;
		 
		 OrderGenerator(String name) {
			 threadName = name;
		 }
		 public void start () {
		      System.out.println("Starting " +  threadName );
		      if (t == null) {
		         t = new Thread (this, threadName);
		         t.start ();
		      }
		   }
		 
		 public void interrupt() { t.interrupt();}
		 
		public void run() {
			Integer orderName = 1;
			double random1, random2, random3;
			String[] products = {"A","B","C","D","E"};
			int maxProducts = 5;
			int maxQty = 2;
			int maxLines = 2;
			int maxSleepTime = 2;
			
			try {
				while (true) {
					List<OrderLine> orderLines = new ArrayList<OrderLine>();
					
					random1 = Math.random();
					int numLines = (int) Math.ceil(random1*maxLines);
					
					for (int i = 0; i < numLines; i++) {
						random2 = Math.random();
						int productIndex = (int) Math.floor(random2*maxProducts);
						if (productIndex == maxProducts)
							productIndex--;
						 
						random3 = Math.random();
						int orderQty = (int) Math.ceil(maxQty * random3);
						orderLines.add(new OrderLine(products[productIndex], orderQty));
					}
					
					Order order = new Order(orderName.toString(), orderLines);
					addOrder(order);
					orderName++;
					
					int sleepSecs = (int) Math.ceil(random1*maxSleepTime);
	
					Thread.sleep(1000*sleepSecs);
				}
			} catch (InterruptedException e) {
				System.out.println("Exiting thread");
				return;
			} catch (Exception e) {
				System.out.println("Exception:" + e + " Exiting");
				return;
			}
			
		}
	}
	
	private OrderGenerator generator = null;
	public void startGeneratingOrders()
	throws Exception
	{
		generator = new OrderGenerator("OrderGenerator");
		generator.start();
	}
	
	public void run()
	throws Exception
	{
		startGeneratingOrders();
		processOrders();
		printOrders();
	}
	
	public void processOrders()
	throws Exception
	{
		while (true) {
			boolean hasInventory = hasInventory();
			if (!hasInventory)
				break;
			List<Order> orders = getOrdersByStatus(new String("NEW"));
			fulfilOrders(orders);
			
			Thread.sleep(2*1000);
		}

		System.out.println("Out of Inventory\n");
		generator.interrupt();

		
	}
	
}




