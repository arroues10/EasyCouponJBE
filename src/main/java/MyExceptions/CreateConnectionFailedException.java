package MyExceptions;

@SuppressWarnings("serial")
public class CreateConnectionFailedException extends Exception {
	
	public CreateConnectionFailedException(String message) {
		super(message);
	}

}
