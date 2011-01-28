package grisu.control.events;

public class FolderCreatedEvent {

	private final String url;

	public FolderCreatedEvent(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

}
