package MyExceptions;

@SuppressWarnings("serial")
public class NoSuchCompanyException extends Exception {
	
	public NoSuchCompanyException(String message) {
		super(message);
	}

}
