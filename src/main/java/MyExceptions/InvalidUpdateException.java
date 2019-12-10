package MyExceptions;

@SuppressWarnings("serial")
public class InvalidUpdateException extends Exception {
	public InvalidUpdateException(String msg) {
		super(msg);
	}
}
