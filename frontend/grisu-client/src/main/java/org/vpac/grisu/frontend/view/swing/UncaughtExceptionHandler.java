package org.vpac.grisu.frontend.view.swing;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	private void logException(Thread t, Throwable e) {
		// todo: start a thread that sends an email, or write to a log file, or
		// send a JMS message...whatever
	}

	private void showException(Thread t, Throwable e) {
		String msg = String.format("Unexpected problem on thread %s: %s",
				t.getName(), e.getMessage());

		logException(t, e);

		// note: in a real app, you should locate the currently focused frame
		// or dialog and use it as the parent. In this example, I'm just passing
		// a null owner, which means this dialog may get buried behind
		// some other screen.
		JOptionPane.showMessageDialog(null, msg);
	}

	public void uncaughtException(final Thread t, final Throwable e) {

		e.printStackTrace();

		if (SwingUtilities.isEventDispatchThread()) {
			showException(t, e);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					showException(t, e);
				}
			});
		}
	}

}
