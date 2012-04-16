package grisu.backend.model;

public interface RemoteFileTransferObject {

	public String getId();

	public Exception getPossibleException();

	public String getPossibleExceptionMessage();

	public boolean isFailed();

	public void joinFileTransfer();

	public void startTransfer(boolean waitForTransferToFinish);

	/**
	 * An external method to verify whether source and target are clones of each
	 * other.
	 * 
	 * Seems to be necessary since sometimes gridftp doesn't throw an error even
	 * though the transfer failed.
	 * 
	 * @return whether the transfer produced a clone of the source file(s)
	 */
	public boolean verifyTransferSuccess();

}
