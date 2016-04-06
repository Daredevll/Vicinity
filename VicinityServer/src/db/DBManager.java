package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.ProcessedOrder;
import model.WaitingOrder;

public class DBManager {

	private static DBManager instance;
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://" + System.getenv("OPENSHIFT_MYSQL_DB_HOST") + 
			":" + System.getenv("OPENSHIFT_MYSQL_DB_PORT") + "/vicinity";
	static final String DB_NAME = "vicinity";

	// Database credentials
	static final String USER = "";
	static final String PASS = "";
	Connection conn = null;

	private DBManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException se) {
			se.printStackTrace();
			throw new ArithmeticException(se.getMessage());
		} catch (ClassNotFoundException se) {
			se.printStackTrace();
		}
		createDB();
		try {
			createTables();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createDB(){
		Statement stmt = null;
		
		try{
			stmt = conn.createStatement();
			
			String sql;
			sql = "	CREATE DATABASE IF NOT EXISTS " + DB_NAME +";";
			stmt.executeUpdate(sql);
			sql = "	USE " + DB_NAME +";";
			stmt.executeUpdate(sql);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		} finally{
//			try {
//				stmt.close();
//			} catch (SQLException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	
	public void addWaitingOrder(WaitingOrder waitingOrder){
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Statement stmt = null;
		
		try{
			stmt = conn.createStatement();
			
			String sql = "USE "+DB_NAME+";";
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO _waiting_orders(custumer_id, restaurant_id, people_count, comment, time) VALUES " +
			"('" + waitingOrder.getCutumerId() + "', '" + waitingOrder.getRestaurantId() + "', " + waitingOrder.getPeopleCount() + ", '" + waitingOrder.getComment() +"', '" + waitingOrder.getHour() + "');";
			
			stmt.executeUpdate(sql);
			conn.commit();
		} 
		catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		try {
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addProcessedOrder(ProcessedOrder processedOrder){
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Statement stmt = null;
		
		try{
			stmt = conn.createStatement();
			
			String sql = "USE "+DB_NAME+";";
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO _processed_orders(custumer_id, restaurant_id, comment, isConfirmed) VALUES " +
			"('" + processedOrder.getCutumerId() + "', '" + processedOrder.getRestaurantId() + "', '" + processedOrder.getComment() +"', " + processedOrder.getComment() + ");";
			
			stmt.executeUpdate(sql);
			conn.commit();
		} 
		catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		try {
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList<WaitingOrder> getWaitingOrder(String restaurantId) {
		ArrayList<WaitingOrder> orders = new ArrayList<>();
		
		String sql = "SELECT id FROM _waiting_orders WHERE restaurant_id='" + restaurantId + "';";
		
		Statement stmt = null;
		
		try{
			stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				orders.add(new WaitingOrder(
						rs.getString("custumer_id"),
						rs.getString("restaurant_id"),
						rs.getString("comment"),
						rs.getInt("people_count"),
						rs.getString("time")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
//			try {
//				stmt.close();
//			} catch (SQLException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		return orders;
	}
	
	private ArrayList<ProcessedOrder> getProcessedOrder(String custumerId) {
		ArrayList<ProcessedOrder> orders = new ArrayList<>();
		
		String sql = "SELECT id FROM _processed_orders WHERE custumer_id='" + custumerId + "';";
		
		Statement stmt = null;
		
		try{
			stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				orders.add(new ProcessedOrder(
						rs.getString("custumer_id"),
						rs.getString("restaurant_id"),
						rs.getString("comment"),
						rs.getBoolean("isConfirmed")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
//			try {
//				stmt.close();
//			} catch (SQLException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		return orders;
	}

	private void createTables() throws SQLException {
		conn.setAutoCommit(false);
		
		Statement stmt = null;
		
		try{
			stmt = conn.createStatement();
			
			String sql = "USE "+DB_NAME+";";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE _waiting_orders("
					+ "custumer_id TEXT,"
					+ "restaurant_id TEXT,"
					+ "people_count INTEGER,"
					+ "comment TEXT,"
					+ "time VARCHAR(50));";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE _processed_orders("
					+ "custumer_id TEXT,"
					+ "restaurant_id TEXT,"
					+ "comment TEXT,"
					+ "isConfirmed BOOLEAN);";
			stmt.executeUpdate(sql);
			
			conn.commit();
		} 
		catch (SQLException e) {
			conn.rollback();
		}finally{
//			stmt.close();
		}
		conn.setAutoCommit(true);
	}

	public static DBManager getInstance() {
		if (instance == null)
			instance = new DBManager();
		return instance;
	}

}
