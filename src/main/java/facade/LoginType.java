package facade;

public enum LoginType {
	ADMIN(1),
	COMPANY(2),
	CUSTOMER(3);
	
	private final int value;
	
	private LoginType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
