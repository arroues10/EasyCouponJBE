package MyExceptions;

@SuppressWarnings("serial")
public class CouponNotExistsException extends Exception {

	public CouponNotExistsException(String msg) {
		super(msg);
	}

}
