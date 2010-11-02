package org.vpac.grisu.frontend.view.swing.files.groups;

public interface SwingWorkerFactory<T, V> {

	public org.jdesktop.swingworker.SwingWorker<T, V> getInstance(
			final IWorker<T> worker);

}
