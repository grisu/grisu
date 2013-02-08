package grisu.frontend.model.job;

/**
 * An exception that is thrown if something goes wrong with a job.
 * 
 * @author Markus Binsteiner
 * 
 */
public class JobException extends RuntimeException {

	private final GrisuJob jo;

	public JobException(final GrisuJob jo, final String message) {
		super(message);
		this.jo = jo;
	}

	public JobException(final GrisuJob grisuJob, final String string,
			final Exception e) {
		super(string, e);
		this.jo = grisuJob;
	}

	public final GrisuJob getJobObject() {
		return this.jo;
	}

}
