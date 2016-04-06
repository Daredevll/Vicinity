package model;

public class WaitingOrder {

	private String cutumerId;
	private String restaurantId;
	private String comment;
	private int peopleCount;
	private String hour;
	
	public WaitingOrder(String cutumerId, String restaurantId, String comment, int peopleCount, String hour) {
		super();
		this.cutumerId = cutumerId;
		this.restaurantId = restaurantId;
		this.comment = comment;
		this.peopleCount = peopleCount;
		this.hour = hour;
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
}
