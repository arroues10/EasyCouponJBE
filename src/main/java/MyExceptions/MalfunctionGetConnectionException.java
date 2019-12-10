package MyExceptions;

@SuppressWarnings("serial")
public class MalfunctionGetConnectionException extends Exception {

	/**
	 * @param message
	 */
	public MalfunctionGetConnectionException(String message) {
		super(message);
	}

}
