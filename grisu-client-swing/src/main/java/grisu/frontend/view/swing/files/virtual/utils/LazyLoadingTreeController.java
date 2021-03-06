package grisu.frontend.view.swing.files.virtual.utils;

import java.util.concurrent.ExecutionException;

import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LazyLoadingTreeController implements TreeWillExpandListener {

	public static class DefaultWorkerFactory implements
			SwingWorkerFactory<MutableTreeNode[], Object> {

		public SwingWorker<MutableTreeNode[], Object> getInstance(
				final IWorker<MutableTreeNode[]> worker) {
			final SwingWorker<MutableTreeNode[], Object> myWorker = new SwingWorker<MutableTreeNode[], Object>() {

				@Override
				protected MutableTreeNode[] doInBackground() throws Exception {
					return worker.doInBackground();
				}

				@Override
				protected void done() {
					try {
						worker.done(get());
					} catch (final InterruptedException e) {
						myLogger.error(e.getLocalizedMessage(), e);
					} catch (final ExecutionException e) {
						myLogger.error(e.getLocalizedMessage(), e);
					} catch (final Exception ex) {
						myLogger.error(ex.getLocalizedMessage(), ex);
					}

				}
			};
			return myWorker;
		}

	}

	static final Logger myLogger = LoggerFactory
			.getLogger(TreeWillExpandListener.class.getName());

	private SwingWorkerFactory<MutableTreeNode[], ?> workerFactory = new DefaultWorkerFactory();
	/** Tree Model */
	private final DefaultTreeModel model;

	// private final JTree tree;

	public LazyLoadingTreeController(DefaultTreeModel model) {
		this.model = model;
	}

	/**
	 * Default constructor
	 *
	 * @param model
	 *            Tree model
	 */
	public LazyLoadingTreeController(JTree tree) {
		// this.tree = tree;
		this.model = (DefaultTreeModel) tree.getModel();
	}

	/**
	 *
	 * @return a new Loading please wait node
	 */
	protected MutableTreeNode createLoadingNode() {
		return new DefaultMutableTreeNode(
				VirtualFileSystemBrowserTreeRenderer.LOADING_STRING, false);
	}

	/**
	 * Create worker that will load the nodes
	 *
	 * @param tree
	 *            the tree
	 * @return the newly created SwingWorker
	 */
	protected SwingWorker<MutableTreeNode[], ?> createSwingWorker(
			final LazyLoadingTreeNode node) {
		return getWorkerFactory().getInstance(getWorkerInterface(node));
	}

	/**
	 * If the Node is not already loaded
	 *
	 * @param node
	 * @param model
	 */
	public void expandNode(final LazyLoadingTreeNode node,
			final DefaultTreeModel model) {
		if (node.areChildrenLoaded()) {
			return;
		}
		node.setChildren(createLoadingNode());
		final SwingWorker<MutableTreeNode[], ?> worker = createSwingWorker(node);
		worker.execute();
	}

	public DefaultTreeModel getModel() {
		return model;
	}

	/**
	 *
	 * @return
	 */
	public SwingWorkerFactory<MutableTreeNode[], ?> getWorkerFactory() {
		if (workerFactory == null) {
			workerFactory = new DefaultWorkerFactory();
		}
		return workerFactory;
	}

	protected IWorker<MutableTreeNode[]> getWorkerInterface(
			final LazyLoadingTreeNode node) {
		return new IWorker<MutableTreeNode[]>() {

			public MutableTreeNode[] doInBackground() {
				return node.loadChildren(model);
			}

			public void done(MutableTreeNode[] nodes) {

				if (nodes == null) {
					final DefaultMutableTreeNode temp = (DefaultMutableTreeNode) node
							.getChildAt(0);
					temp.setUserObject("Exception/Can't access...");
					model.nodeChanged(temp);
					return;
				}

				if (nodes.length == 0) {
					try {
						if (node.getChildCount() > 0) {
							final DefaultMutableTreeNode temp = (DefaultMutableTreeNode) node
									.getChildAt(0);
							model.removeNodeFromParent(temp);
						}

						model.nodeChanged(node);
					} catch (final Exception e) {
						myLogger.error(e.getLocalizedMessage(), e);
					}

				} else {
					node.setAllowsChildren((nodes != null)
							&& (nodes.length > 0));
					node.setChildren(nodes);

					// if (nodes.length == 1) {
					// DefaultMutableTreeNode tmp = (DefaultMutableTreeNode)
					// node
					// .getChildAt(0);
					//
					// TreePath path = new TreePath(tmp.getPaths());
					// tree.expandPath(path);
					// }
				}
			}
		};
	}

	// public void setModel(DefaultTreeModel model) {
	// this.model = model;
	// }

	public void setWorkerFactory(
			SwingWorkerFactory<MutableTreeNode[], ?> workerFactory) {
		this.workerFactory = workerFactory;
	}

	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		// Do nothing on collapse.
	}

	/**
	 * Invoked whenever a node in the tree is about to be expanded. If the Node
	 * is a LazyLoadingTreeNode load it's children in a SwingWorker
	 */
	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		final TreePath path = event.getPath();
		final Object lastPathComponent = path.getLastPathComponent();
		if (lastPathComponent instanceof LazyLoadingTreeNode) {
			final LazyLoadingTreeNode lazyNode = (LazyLoadingTreeNode) lastPathComponent;
			expandNode(lazyNode, model);
		}
	}
}
