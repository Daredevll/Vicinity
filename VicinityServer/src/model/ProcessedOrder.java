package model;

public class ProcessedOrder extends Order{
	
	private boolean isConfirmed;

	public ProcessedOrder(String cutumerId,
			String restaurantId,
			String comment,
			int peopleCount,
			String hour,
			String date,
			boolean isConfirmed,
			String restaurantName) {
		super(cutumerId, restaurantId, comment, peopleCount, hour, date, restaurantName);
		
		this.isConfirmed = isConfirmed;
		
	}

	public boolean isConfirmed() {
		return isConfirmed;
	}
	
}
