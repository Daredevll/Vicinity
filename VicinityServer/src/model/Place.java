package model;

public class Place {
	private String userId;
	private String placeId;
	private String phone;
	private String confirmationCode;
	private boolean isConfirmed;
	
	public Place(String userId, String placeId, String phone, String confirmationCode, boolean isConfirmed) {
		super();
		this.userId = userId;
		this.placeId = placeId;
		this.phone = phone;
		this.confirmationCode = confirmationCode;
		this.isConfirmed = isConfirmed;
	}

	public String getUserId() {
		return userId;
	}

	public String getPlaceId() {
		return placeId;
	}

	public String getPhone() {
		return phone;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}

	public boolean isConfirmed() {
		return isConfirmed;
	}
	
	
}
