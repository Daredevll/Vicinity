package model;

public class SMS {
	
	private String phone;
	private String code;
	
	public SMS(String phone, String code) {
		super();
		this.phone = phone;
		this.code = code;
	}

	public String getPhone() {
		return phone;
	}

	public String getCode() {
		return code;
	}
}
