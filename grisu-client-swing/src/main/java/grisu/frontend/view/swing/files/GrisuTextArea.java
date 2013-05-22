package grisu.frontend.view.swing.files;

import java.awt.TextArea;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JPopupMenu;

import org.gjt.sp.jedit.IPropertyManager;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.syntax.ModeProvider;
import org.gjt.sp.jedit.textarea.StandaloneTextArea;
import org.gjt.sp.util.IOUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jidesoft.swing.OverlayableUtils;

public class GrisuTextArea extends StandaloneTextArea {
	

	public static final Logger myLogger = LoggerFactory
			.getLogger(GrisuTextArea.class);

	static final Properties props = new Properties();
	static IPropertyManager propertyManager;

	static {
		final Properties props = new Properties();
		props.putAll(loadProperties("/keymaps/jEdit_keys.props"));
		props.putAll(loadProperties("/org/gjt/sp/jedit/jedit.props"));
		propertyManager = new IPropertyManager() {
			public String getProperty(String name) {

				return props.getProperty(name);

			}
		};
	}

	public static Properties loadProperties(String fileName) {
		Properties props = new Properties();
		File file;
		if (fileName.charAt(0) == '/')
			file = new File(fileName.substring(1));
		else
			file = new File(fileName);

		InputStream in = null;
		try {
			if (file.isFile()) {
				in = new FileInputStream(file);
			} else {
				in = TextArea.class.getResourceAsStream(fileName);
			}
			props.load(in);
		} catch (IOException e) {
			myLogger.error("Can't load grisutextarea properties.", e);
		} finally {
			IOUtilities.closeQuietly(in);
		}
		return props;
	}

	public GrisuTextArea() {
		
		super(propertyManager);
		
		final Mode mode = new Mode("text");
		mode.setProperty("file", "text.xml");
		ModeProvider.instance.addMode(mode);
		getBuffer().setMode(mode);

		//textArea.setRightClickPopup(po);
		JPopupMenu p = new JPopupMenu();
		setRightClickPopup(p);
		
		addMenuItem("undo", "Undo");
		addMenuItem("redo", "Redo");
		p.addSeparator();
		addMenuItem("cut", "Cut");
		addMenuItem("copy", "Copy");
		addMenuItem("paste", "Paste");
		
		setRightClickPopupEnabled(true);		

	}
	
	@Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);
        OverlayableUtils.repaintOverlayable(this);
    }

}
