package org.vpac.grisu.frontend.view.swing.files.virtual;

public interface IWorker<T> {

	public T doInBackground();

	public void done(T result);

}
