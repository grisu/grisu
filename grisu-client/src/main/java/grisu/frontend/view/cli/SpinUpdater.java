package grisu.frontend.view.cli;

import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;

public class SpinUpdater extends TimerTask {

	private String message = null;
	private int i = 0;

	public SpinUpdater(String message) {
		this.message = message;
	}

	@Override
	public void run() {
		String msg = null;
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(message)) {
			msg = CliHelpers.indeterminateProgressStrings[i];
		} else {
			msg = message + " " + CliHelpers.indeterminateProgressStrings[i];
		}
		CliHelpers.writeToTerminal(msg);

		i = i + 1;
		if (i >= CliHelpers.indeterminateProgressStrings.length) {
			i = 0;
		}
	}

	public void setMessage(String message) {
		this.message = message;
	}

}