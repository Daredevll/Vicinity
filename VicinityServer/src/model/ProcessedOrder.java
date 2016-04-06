package model;

public class ProcessedOrder {
	
	private String cutumerId;
	private String restaurantId;
	private String comment;
	private boolean isConfirmed;
	
	public ProcessedOrder(String cutumerId, String restaurantId, String comment, boolean isConfirmed) {
		super();
		this.cutumerId = cutumerId;
		this.restaurantId = restaurantId;
		this.comment = comment;
		this.isConfirmed = isConfirmed;
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

	public boolean isConfirmed() {
		return isConfirmed;
	}
	
	
	
}
