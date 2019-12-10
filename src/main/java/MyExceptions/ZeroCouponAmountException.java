package MyExceptions;

@SuppressWarnings("serial")
public class ZeroCouponAmountException extends Exception {
	
	public ZeroCouponAmountException(String msg) {
		super(msg);
	}

}
