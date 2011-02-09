package grisu.frontend.view.swing.files.virtual.utils;

import javax.swing.SwingWorker;

public interface SwingWorkerFactory<T, V> {

	public SwingWorker<T, V> getInstance(final IWorker<T> worker);

}
