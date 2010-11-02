package org.vpac.grisu.frontend.view.swing.files.groups;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.jdesktop.swingworker.SwingWorker;

public class SwingDemo implements Runnable {

	public static class VisualSwingWorkerFactory implements
			SwingWorkerFactory<MutableTreeNode[], Object> {

		private final Component father;

		public VisualSwingWorkerFactory(Component father) {
			this.father = father;
		}

		public SwingWorker<MutableTreeNode[], Object> getInstance(
				final IWorker<MutableTreeNode[]> worker) {
			VisualSwingWorker<MutableTreeNode[], Object> vWorker = new VisualSwingWorker<MutableTreeNode[], Object>(
					father, "Tree Loading ...", "Loading in Tree", false) {

				@Override
				protected void done() {
					try {
						worker.done(get());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				protected MutableTreeNode[] lockedDoInBackground()
						throws Exception {
					return worker.doInBackground();
				}
			};
			return vWorker;
		}

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new SwingDemo());
	}

	public void run() {
		final JTree tree = new JTree();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root",
				true);
		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		for (int i = 0; i < 5; i++) {
			rootNode.add(new FindChildrenTreeNode("Node " + i, model));
		}
		tree.setModel(model);
		final LazyLoadingTreeController controller = new LazyLoadingTreeController(
				model);
		tree.addTreeWillExpandListener(controller);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(new JScrollPane(tree), BorderLayout.CENTER);
		final JCheckBox useVisualSwingWorkerCheck = new JCheckBox(
				"Can't expand 2 Nodes at a time");
		useVisualSwingWorkerCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (useVisualSwingWorkerCheck.isSelected()) {
					controller.setWorkerFactory(new VisualSwingWorkerFactory(
							tree));
				} else {
					controller.setWorkerFactory(null);
				}
			}
		});
		JPanel northPanel = new JPanel();
		northPanel.add(useVisualSwingWorkerCheck);
		frame.add(northPanel, BorderLayout.NORTH);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
