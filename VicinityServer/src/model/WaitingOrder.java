package model;

public class WaitingOrder extends Order {

	public WaitingOrder(
			String cutumerId,
			String restaurantId,
			String comment,
			int peopleCount,
			String hour,
			String date,
			String restaurantName) {
		super(cutumerId, restaurantId, comment, peopleCount, hour, date, restaurantName);
		
	}
}
