package MyExceptions;

@SuppressWarnings("serial")
public class MalfunctionReturnConnectionException extends Exception {

	/**
	 * @param message
	 */
	public MalfunctionReturnConnectionException(String message) {
		super(message);
	}

}
