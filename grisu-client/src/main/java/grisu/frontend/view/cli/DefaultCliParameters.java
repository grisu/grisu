package grisu.frontend.view.cli;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class DefaultCliParameters extends GrisuCliParameters {

	@Parameter(description = "Other params")
	private List<String> other = new ArrayList<String>();

	public List<String> getOtherParams() {
		return other;
	}

}
