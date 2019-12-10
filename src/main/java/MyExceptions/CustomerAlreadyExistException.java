package MyExceptions;

@SuppressWarnings("serial")
public class CustomerAlreadyExistException extends Exception {
	
	public CustomerAlreadyExistException(String msg) {
		super(msg);
	}

}
