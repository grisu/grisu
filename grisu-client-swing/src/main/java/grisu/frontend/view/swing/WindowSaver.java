package grisu.frontend.view.swing;

import grisu.settings.ClientPropertiesManager;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public class WindowSaver implements AWTEventListener {

	static final Logger myLogger = Logger
			.getLogger(WindowSaver.class.getName());

	static PropertiesConfiguration propertiesConfig;

	private static WindowSaver saver;

	public static WindowSaver getInstance() {
		if (saver == null) {
			saver = new WindowSaver();
		}
		return saver;
	}

	public static int getInt(String name, int value) {

		final String v = (String) (propertiesConfig.getProperty(name));
		if (v == null) {
			return value;
		}
		return Integer.parseInt(v);
	}

	public static void loadSettings(JFrame frame) throws IOException {

		final String name = frame.getName();
		final int x = getInt(name + ".x", 100);
		final int y = getInt(name + ".y", 100);
		final int w = getInt(name + ".w", 780);
		final int h = getInt(name + ".h", 680);
		frame.setLocation(x, y);
		frame.setSize(new Dimension(w, h));
		saver.frameMap.put(name, frame);
		frame.validate();
	}

	public static void saveSettings() {

		final Iterator it = saver.frameMap.keySet().iterator();
		while (it.hasNext()) {
			final String name = (String) it.next();
			final JFrame frame = (JFrame) saver.frameMap.get(name);
			propertiesConfig.setProperty(name + ".x", "" + frame.getX());
			propertiesConfig.setProperty(name + ".y", "" + frame.getY());
			propertiesConfig.setProperty(name + ".w", "" + frame.getWidth());
			propertiesConfig.setProperty(name + ".h", "" + frame.getHeight());
		}
		try {
			propertiesConfig.save();
		} catch (final ConfigurationException e) {
			myLogger.error("Couldn't save window properties: "
					+ e.getLocalizedMessage());
		}
	}

	private final Map frameMap;

	private WindowSaver() {
		frameMap = new HashMap();
		try {
			propertiesConfig = ClientPropertiesManager.getClientConfiguration();
		} catch (final ConfigurationException e) {
			myLogger.error("Could not init properties configuration to save window position...");
		}
	}

	public void eventDispatched(AWTEvent evt) {

		try {
			if (evt.getID() == WindowEvent.WINDOW_OPENED) {
				final ComponentEvent cev = (ComponentEvent) evt;
				if (cev.getComponent() instanceof JFrame) {
					final JFrame frame = (JFrame) cev.getComponent();
					loadSettings(frame);
				}
			}
		} catch (final Exception ex) {
			myLogger.error(ex.toString());
		}

	}

}
