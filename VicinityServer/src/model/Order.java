package model;

public class Order {

	private String cutumerId;
	private String restaurantId;
	private String comment;
	private int peopleCount;
	private String hour;
	private String date;
	private String restaurantName;
	
	public Order(
			String cutumerId,
			String restaurantId,
			String comment,
			int peopleCount,
			String hour,
			String date,
			String restaurantName) {
		super();
		
		this.cutumerId = cutumerId;
		this.restaurantId = restaurantId;
		this.comment = comment;
		this.peopleCount = peopleCount;
		this.hour = hour;
		this.date = date;
		this.restaurantName = restaurantName;
	}
	
	public String getCutumerId() {
		return cutumerId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public String getComment() {
		return comment;
	}

	public int getPeopleCount() {
		return peopleCount;
	}

	public String getHour() {
		return hour;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public String getRestaurantName(){
		return this.restaurantName;
	}
}
