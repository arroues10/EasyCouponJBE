package common;

public enum CouponCategory {
	
	TRAVELING(1), FOOD(2), ELECTRICITY(3), HEALTH(4), SPORTS(5), CAMPING(6), FASHION(7), STUDIES(8), MECHANIC(9);
	
	private int value;
	
	private CouponCategory(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
