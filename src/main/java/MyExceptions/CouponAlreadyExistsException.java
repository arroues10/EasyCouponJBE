package MyExceptions;

@SuppressWarnings("serial")
public class CouponAlreadyExistsException extends Exception {
	public CouponAlreadyExistsException(String msg) {
		super(msg);
	}
}
