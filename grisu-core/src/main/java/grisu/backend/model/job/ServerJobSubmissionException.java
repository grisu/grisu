package grisu.backend.model.job;

public class ServerJobSubmissionException extends Exception {

	public ServerJobSubmissionException(Exception e) {
		super(e);
	}

	public ServerJobSubmissionException(String message) {
		super(message);
	}

	public ServerJobSubmissionException(String message, Exception e) {
		super(message, e);
	}

}
