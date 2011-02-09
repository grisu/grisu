package grisu.control.events;

public class FileDeletedEvent {
	private final String url;

	public FileDeletedEvent(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

}
