package grisu.frontend.view.swing.files.virtual.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public abstract class VisualSwingWorker<T, V> extends SwingWorker<T, V> {

	/**
	 * Waiting dialog displaying a simple
	 * 
	 * @author Thierry LEFORT 15 f�vr. 08
	 * 
	 */
	private class WaitingDialog extends JDialog implements
			PropertyChangeListener {

		private final JProgressBar progressBar = new JProgressBar();

		private WaitingDialog(Component father, String title, String message,
				boolean allowCancel) {
			super(JOptionPane.getFrameForComponent(father), title, true);
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			progressBar.setIndeterminate(true);
			setLayout(new BorderLayout());
			JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
			messagePanel.add(new JLabel(message));
			add(messagePanel, BorderLayout.NORTH);
			JPanel progressPanel = new JPanel();
			progressPanel.add(progressBar);
			add(progressPanel, BorderLayout.CENTER);
			if (allowCancel) {
				addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						cancel(true);
					}
				});
				JButton cancelButton = new JButton(
						UIManager.getString("FileChooser.cancelButtonText"));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancel(true);
					}
				});
				JPanel southPanel = new JPanel();
				southPanel.add(cancelButton);
				add(southPanel, BorderLayout.SOUTH);
			}
			pack();
			// setSize(new Dimension(200, 150));
			setLocationRelativeTo(father);

		}

		public void propertyChange(PropertyChangeEvent evt) {
			if ("progress".equals(evt.getPropertyName())) {
				if (progressBar.isIndeterminate()) {
					progressBar.setIndeterminate(false);
					progressBar.setMinimum(0);
					progressBar.setMaximum(100);
				}
				progressBar.setValue((Integer) evt.getNewValue());
			}
			if ("state".equals(evt.getPropertyName())) {
				if (StateValue.DONE.equals(evt.getNewValue())) {
					dispose();
				}
			}
		}

	}

	private final Component father;
	private final String title;
	private final String message;
	private final boolean allowCancel;

	public VisualSwingWorker(final Component father, final String title,
			final String message, final boolean allowCancel) {
		this.father = father;
		this.title = title;
		this.message = message;
		this.allowCancel = allowCancel;
	}

	@Override
	protected final T doInBackground() throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final WaitingDialog dialog = new WaitingDialog(father, title,
						message, allowCancel);
				addPropertyChangeListener(dialog);
				dialog.setVisible(true);
			}
		});
		return lockedDoInBackground();
	}

	protected abstract T lockedDoInBackground() throws Exception;

}
