package grisu.frontend.view.swing.files.virtual.utils;

public interface IWorker<T> {

	public T doInBackground();

	public void done(T result);

}
