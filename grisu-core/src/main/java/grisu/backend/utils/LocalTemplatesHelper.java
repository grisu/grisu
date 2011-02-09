package grisu.backend.utils;

import grisu.settings.Environment;
import grith.jgrith.Init;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

public final class LocalTemplatesHelper {

	static final Logger myLogger = Logger.getLogger(LocalTemplatesHelper.class
			.getName());

	private static boolean globusFolderCopied = false;
	private static boolean templatesCopied = false;

	public static final File GLITE_DIRECTORY = new File(
			System.getProperty("user.home"), ".glite");

	public static void copyFile(final File in, final File out)
			throws IOException {
		final FileChannel inChannel = new FileInputStream(in).getChannel();
		final FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (final IOException e) {
			throw e;
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}

	public static void copyGlobusFolder() {

		if (!globusFolderCopied) {
			// copy globus floder if not already there
			try {
				if (!new File(Environment.getGlobusHome()).exists()) {
					unzipFileToDir("/globus.zip",
							Environment.getVarGrisuDirectory());
				}
			} catch (final Exception e) {
				myLogger.error(e);
			}
			globusFolderCopied = true;
		}
	}

	/**
	 * Extracts the files in the vomses.zip file in the directory
	 * $HOME/.glite/vomses These files are pointing Grix to the voms/vomrs
	 * server(s) the APACGrid is using.
	 * 
	 * @throws Exception
	 */
	public static void copyTemplatesAndMaybeGlobusFolder() throws Exception {

		if (!new File(Environment.getAvailableTemplatesDirectory()).exists()
				|| !Environment.getGrisuDirectory().exists()) {
			createGrisuDirectories();
		}

		if (!templatesCopied) {
			final File templatesDir = new File(
					Environment.getAvailableTemplatesDirectory());

			if (!templatesDir.exists() || (templatesDir.list().length == 0)) {

				if (!templatesDir.mkdirs()) {
					myLogger.warn("Could not create Templates directory...");
				}
				myLogger.debug("Filling templates_available folder with a set of base templates...");
				final int BUFFER_SIZE = 8192;
				int count;
				final byte[] data = new byte[BUFFER_SIZE];

				final InputStream in = Init.class
						.getResourceAsStream("/templates_available.zip");
				final ZipInputStream zipStream = new ZipInputStream(in);

				BufferedOutputStream dest = null;

				try {

					ZipEntry entry = null;

					while ((entry = zipStream.getNextEntry()) != null) {

						if (!entry.isDirectory()) {

							myLogger.debug("Template name: " + entry.getName());
							final File vomses_file = new File(
									Environment
											.getAvailableTemplatesDirectory(),
									entry.getName());

							// Write the file to the file system and overwrite
							// possible
							// old files with the same name
							final FileOutputStream fos = new FileOutputStream(
									vomses_file);
							dest = new BufferedOutputStream(fos, BUFFER_SIZE);
							while ((count = zipStream
									.read(data, 0, BUFFER_SIZE)) != -1) {
								dest.write(data, 0, count);
							}
							dest.flush();
							dest.close();
						}
					}
				} catch (final Exception e) {
					myLogger.error(e);
				}
			} else {
				myLogger.debug("Templates folder already contains files. Not copying any into it...");
			}
			templatesCopied = true;
		}

		copyGlobusFolder();

	}

	/**
	 * Creates the grisu directory if it doesn't exist yet.
	 * 
	 * @throws Exception
	 *             if something goes wrong
	 */
	public static void createGrisuDirectories() throws Exception {

		if (!Environment.getGrisuDirectory().exists()) {
			if (!Environment.getGrisuDirectory().mkdirs()) {
				myLogger.error("Could not create grisu directory.");
				throw new Exception(
						"Could not create grisu directory. Please set permissions for "
								+ Environment.getGrisuDirectory().toString()
								+ " to be created.");
			}
		}

		if (!new File(Environment.getAvailableTemplatesDirectory()).exists()) {
			if (!new File(Environment.getAvailableTemplatesDirectory())
					.mkdirs()) {
				myLogger.error("Could not create available_vomses directory.");
				throw new Exception(
						"Could not create templates_available directory. Please set permissions for "
								+ Environment.getAvailableTemplatesDirectory()
								+ " to be created.");
			}
		}

		if (!Environment.getVarGrisuDirectory().exists()) {
			if (!Environment.getVarGrisuDirectory().mkdirs()) {
				myLogger.error("Coud not create grisu var directory.");
				throw new Exception(
						"Could not create grisu var directory. Please set proper permission for "
								+ Environment.getVarGrisuDirectory()
								+ " to be created.");
			}
		}
	}

	private static void unzipFileToDir(final String zipFileResourcePath,
			final File targetDir) {

		final int BUFFER_SIZE = 8192;
		int count;
		final byte[] data = new byte[BUFFER_SIZE];

		final InputStream in = Init.class
				.getResourceAsStream(zipFileResourcePath);
		final ZipInputStream zipstream = new ZipInputStream(in);

		BufferedOutputStream dest = null;

		try {

			ZipEntry entry = null;

			while ((entry = zipstream.getNextEntry()) != null) {
				myLogger.debug("Entry: " + entry.getName());
				final String filePath = targetDir.getAbsolutePath()
						+ File.separator + entry.getName();

				if (!entry.isDirectory()) {

					// File vomses_file = new File(TEMPLATES_AVAILABLE_DIR,
					// entry.getName());
					final File vomses_file = new File(filePath);

					// Write the file to the file system and overwrite possible
					// old files with the same name
					final FileOutputStream fos = new FileOutputStream(
							vomses_file);
					dest = new BufferedOutputStream(fos, BUFFER_SIZE);
					while ((count = zipstream.read(data, 0, BUFFER_SIZE)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				} else {
					// new File(GRISU_DIRECTORY,entry.getName()).mkdirs();
					new File(filePath).mkdirs();
				}
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			myLogger.error(e);
		}

	}

	private LocalTemplatesHelper() {
	}

}
