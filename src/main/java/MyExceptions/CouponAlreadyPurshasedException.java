package MyExceptions;

@SuppressWarnings("serial")
public class CouponAlreadyPurshasedException extends Exception {
	
	public CouponAlreadyPurshasedException(String msg) {
		super(msg);
	}

}
