package MyExceptions;

@SuppressWarnings("serial")
public class CustomerAlreadyExistsException extends Exception {
	public CustomerAlreadyExistsException(String msg) {
		super(msg);
	}
}
