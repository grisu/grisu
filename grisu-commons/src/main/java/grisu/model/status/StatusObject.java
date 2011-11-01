package grisu.model.status;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.StatusException;
import grisu.model.dto.DtoActionStatus;

import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusObject {

	public interface Listener {
		public void statusMessage(ActionStatusEvent event);
	}

	static final Logger myLogger = LoggerFactory.getLogger(StatusObject.class
			.getName());

	public static StatusObject wait(ServiceInterface si, String handle) {
		try {
			return waitForActionToFinish(si, handle, 10, true, false);
		} catch (final Exception e) {
			// TODO logging
			return null;
		}
	}

	public static StatusObject waitForActionToFinish(ServiceInterface si,
			String handle, int recheckIntervalInSeconds, boolean exitIfFailed,
			boolean sendStatusEvent) throws InterruptedException,
			StatusException {

		final StatusObject temp = new StatusObject(si, handle);
		temp.waitForActionToFinish(recheckIntervalInSeconds, exitIfFailed,
				sendStatusEvent);

		return temp;
	}

	private final ServiceInterface si;
	private final String handle;

	private volatile Thread t = null;

	private Vector<Listener> listeners;

	private DtoActionStatus lastStatus;
	private double lastPercentage = 0;
	private boolean taskFinished = false;

	private final CountDownLatch finished = new CountDownLatch(1);

	public StatusObject(ServiceInterface si, String handle) {
		this(si, handle, (Vector) null);
	}

	public StatusObject(ServiceInterface si, String handle, Listener l) {
		this(si, handle, (Vector) null);
		addListener(l);
	}

	public StatusObject(ServiceInterface si, String handle,
			Vector<Listener> listeners) {
		this.si = si;
		this.handle = handle;
		this.listeners = listeners;

		if (listeners != null) {
			for (final Listener l : listeners) {
				addListener(l);
			}
		}
	}

	synchronized public void addListener(Listener l) {
		if (listeners == null) {
			listeners = new Vector();
		}
		listeners.addElement(l);
	}

	private synchronized void createWaitThread(final int waitTime) {

		if (t == null) {
			t = new Thread() {
				@Override
				public void run() {
					while (!getStatus().isFinished()) {
						try {
							myLogger.debug("Waiting for task {} to finish...",
									handle);
							Thread.sleep(waitTime * 1000);
						} catch (final InterruptedException e) {
							myLogger.error("Status wait interrupted.", e);
							break;
						}
					}
					finished.countDown();
				}
			};
			t.setName("Wait thread for status: " + handle);
			t.start();
		}
	}

	private void fireEvent(ActionStatusEvent message) {
		if ((listeners != null) && !listeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector targets;
			synchronized (this) {
				targets = (Vector) listeners.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			final Enumeration e = targets.elements();
			while (e.hasMoreElements()) {
				final Listener l = (Listener) e.nextElement();
				l.statusMessage(message);
			}
		}
	}

	public synchronized DtoActionStatus getStatus() {

		if (taskFinished) {
			return lastStatus;
		}
		myLogger.debug("Checking status for: " + handle);
		lastStatus = si.getActionStatus(handle);
		if ((taskFinished != lastStatus.isFinished())
				|| (lastPercentage != lastStatus.percentFinished())) {
			lastPercentage = lastStatus.percentFinished();
			taskFinished = lastStatus.isFinished();
			fireEvent(new ActionStatusEvent(lastStatus));
		}
		myLogger.debug("Status for " + handle + ": "
				+ lastStatus.percentFinished() + " %" + " / finished: "
				+ lastStatus.isFinished());

		return lastStatus;
	}

	synchronized public void removeListener(Listener l) {
		if (listeners == null) {
			listeners = new Vector<Listener>();
		}
		listeners.removeElement(l);
	}

	public void waitForActionToFinish(int recheckIntervalInSeconds,
			boolean exitIfFailed, boolean sendStatusEvent)
					throws InterruptedException, StatusException {
		waitForActionToFinish(recheckIntervalInSeconds, exitIfFailed,
				sendStatusEvent, null);
	}

	public void waitForActionToFinish(int recheckIntervalInSeconds,
			boolean exitIfFailed, boolean sendStatusEvent,
			String statusMessagePrefix) throws InterruptedException,
			StatusException {

		lastStatus = si.getActionStatus(handle);

		if (lastStatus == null) {

			throw new StatusException("Can't find status with handle "
					+ this.handle);
		}

		if (lastStatus.isFinished()) {
			return;
		}

		createWaitThread(recheckIntervalInSeconds);

		try {
			finished.await();
		} catch (InterruptedException e) {
			myLogger.error("Waiting for status " + handle
					+ " to finish interrupted.", e);
		}




		return;

	}

}
