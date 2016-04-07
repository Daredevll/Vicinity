package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Place;
import model.ProcessedOrder;
import model.SMS;
import model.WaitingOrder;

public class DBManager {

	private static DBManager instance;
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://" + System.getenv("OPENSHIFT_MYSQL_DB_HOST") + 
			":" + System.getenv("OPENSHIFT_MYSQL_DB_PORT") + "/vicinity";
	static final String DB_NAME = "vicinity";

	// Database credentials
	static final String USER = "admin9zEAlhG";
	static final String PASS = "dkV1sNlRQ5M6";
	Connection conn = null;

	private DBManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException se) {
			se.printStackTrace();
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
	//TODO fix add orders
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
			
			sql = "INSERT INTO _waiting_orders("
					+ "custumer_id,"
					+ " restaurant_id,"
					+ " people_count,"
					+ " comment,"
					+ " time,"
					+ " date,"
					+ "restaurant_name) VALUES " +
			"('" + waitingOrder.getCutumerId() + "', '" + 
			waitingOrder.getRestaurantId() + "', " + 
			waitingOrder.getPeopleCount() + ", '" + 
			waitingOrder.getComment() +"', '" + 
			waitingOrder.getHour() + "', '" + 
			waitingOrder.getDate() + "', '" +
			waitingOrder.getRestaurantName() + "');";
			
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
			
			sql = "INSERT INTO _processed_orders("
					+ "custumer_id,"
					+ " restaurant_id,"
					+ " people_count,"
					+ " comment,"
					+ " time,"
					+ " date,"
					+ " isConfirmed,"
					+ "restaurant_name) VALUES " +
			"('" + processedOrder.getCutumerId() + "', '" + 
			processedOrder.getRestaurantId() + "'," + 
			processedOrder.getPeopleCount() + ", '" + 
			processedOrder.getComment() +"', '" + 
			processedOrder.getHour() + "', '" + 
			processedOrder.getDate() + "'," + 
			processedOrder.isConfirmed() + ",'"
			+ processedOrder.getRestaurantName() + "');";
			
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

	public void addPlace(Place place){
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
			
			sql = "INSERT INTO _place(user_id,"
					+ " restaurant_id,"
					+ " phone,"
					+ " confirmation_code,"
					+ " isConfirmed) VALUES " +
			"('" + place.getUserId() + "', '" + place.getPlaceId() + "', '" + place.getPhone() +"', " + place.getConfirmationCode() + ", " + place.isConfirmed() + ");";
			
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

	public void addUser(String userId){
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

			sql = "INSERT INTO _customers(user_id) VALUES " +
			"('" + userId + "');";
			
			stmt.executeUpdate(sql);
			conn.commit();
		} 
		catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
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
	
	public void addSMS(SMS sms){
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

			sql = "INSERT INTO _sms(code, phone) VALUES " +
			"('" + sms.getCode() + "', '" +
			sms.getPhone() + "');";
			
			stmt.executeUpdate(sql);
			conn.commit();
		} 
		catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
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

	public SMS getSMS(){
		String sql = "SELECT * FROM _sms;";
		
		Statement stmt = null;
		
		try{
			stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				String phone = rs.getString("phone");
				String code = rs.getString("code");
				
				this.deleteSMS(phone, code);
				return new SMS(phone, code);
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
		
		return null;
	}
	
	public ArrayList<WaitingOrder> getWaitingOrder(String restaurantId) {
		ArrayList<WaitingOrder> orders = new ArrayList<WaitingOrder>();
		
		String sql = "SELECT * FROM _waiting_orders WHERE restaurant_id='" + restaurantId + "';";
		
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
						rs.getString("time"),
						rs.getString("date"),
						rs.getString("restaurant_name")));
			}
			
			this.deleteWaitingorder(rs.getInt("id"));
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
		//Delete entry
	}
	
	public ArrayList<ProcessedOrder> getProcessedOrder(String custumerId) {
		ArrayList<ProcessedOrder> orders = new ArrayList<ProcessedOrder>();
		
		String sql = "SELECT * FROM _processed_orders WHERE custumer_id='" + custumerId + "';";
		
		Statement stmt = null;
		
		try{
			stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				
				orders.add(new ProcessedOrder(
						rs.getString("custumer_id"),
						rs.getString("restaurant_id"),
						rs.getString("comment"),
						rs.getInt("people_count"),
						rs.getString("time"),
						rs.getString("date"),
						rs.getBoolean("isConfirmed"),
						rs.getString("restaurant_name")));
				
				this.deleteProcessedOrder(rs.getInt("id"));
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
		//Delete entry
	}
	
	public Place getPlaceBuUser(String userid){
		String sql = "SELECT * FROM _place WHERE user_id='" + userid + "';";
		Place place = null;
		
		Statement stmt = null;
		
		try{
			stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				place = new Place(
						rs.getString("user_id"),
						rs.getString("restaurant_id"),
						rs.getString("phone"),
						rs.getString("confirmation_code"),
						rs.getBoolean("isConfirmed"));
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
		
		return place;
	}

	public String getUser(String userid){
		String sql = "SELECT * FROM _customers WHERE user_id='" + userid + "';";
		
		Statement stmt = null;
		String id = null;
		
		try{
			stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				id = rs.getString("user_id");
				System.out.println("User: " + id);
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
		
		return id;
	}

	public void updatePlace(String userId, String code){
		String sql = "UPDARE _place SET isConfirmed=" + true + "WHERE user_id ='" + userId + "' AND confirmation_code='" + code + "';";
		
		try {
			Statement stmt = conn.prepareStatement(sql);
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteSMS(String phone, String code){
		String sql = "DELETE FROM _processed_orders WHERE phone ='" + phone + "'AND code ='" + code + "';";
		
		try {
			Statement stmt = conn.prepareStatement(sql);
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteProcessedOrder(int id){
		String sql = "DELETE FROM _processed_orders WHERE id =" + id + ";";
		
		try {
			Statement stmt = conn.prepareStatement(sql);
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteWaitingorder(int id){
		String sql = "DELETE FROM _waiting_orders WHERE id =" + id + ";";
		
		try {
			Statement stmt = conn.prepareStatement(sql);
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createTables() throws SQLException {
		conn.setAutoCommit(false);
		
		Statement stmt = null;
		
		try{
			stmt = conn.createStatement();
			
			String sql = "USE "+DB_NAME+";";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS _waiting_orders("
					+ "id INTEGER AUTO_INCREMENT PRIMARY KEY,"
					+ "custumer_id TEXT,"
					+ "restaurant_id TEXT,"
					+ "people_count INTEGER,"
					+ "comment TEXT,"
					+ "time VARCHAR(25)," 
					+ "date VARCHAR(25),"
					+ "restaurant_name VARCHAR(50));";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS _processed_orders("
					+ "id INTEGER AUTO_INCREMENT PRIMARY KEY,"
					+ "custumer_id TEXT,"
					+ "restaurant_id TEXT,"
					+ "people_count INTEGER,"
					+ "comment TEXT,"
					+ "time VARCHAR(25),"
					+ "date VARCHAR(25),"
					+ "restaurant_name VARCHAR(50),"
					+ "isConfirmed BOOLEAN);";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS _place("
					+ "user_id TEXT,"
					+ "restaurant_id TEXT,"
					+ "phone VARCHAR(50),"
					+ "confirmation_code TEXT,"
					+ "isConfirmed BOOLEAN);";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS _customers("
					+ "user_id TEXT);";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS _sms("
					+ "code VARCHAR(50),"
					+ "phone VARCHAR(50));";
			stmt.executeUpdate(sql);
			
			conn.commit();
		} 
		catch (SQLException e) {
			e.printStackTrace();
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
