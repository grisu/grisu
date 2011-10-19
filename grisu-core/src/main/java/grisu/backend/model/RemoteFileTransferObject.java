package grisu.backend.model;

public interface RemoteFileTransferObject {

	public String getId();

	public Exception getPossibleException();

	public String getPossibleExceptionMessage();

	public boolean isFailed();

	public void joinFileTransfer();

	public void startTransfer(boolean waitForTransferToFinish);

}
