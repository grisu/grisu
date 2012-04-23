package grisu.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.activation.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Several methods to ease the handling of files.
 * 
 * @author Markus Binsteiner
 * 
 */
public final class FileHelpers {

	static final Logger myLogger = LoggerFactory.getLogger(FileHelpers.class);

	public static final int BUFFER_SIZE = 1024;

	/**
	 * Copies a bunch of files into a target directory.
	 * 
	 * @param sources
	 *            the source files
	 * @param dest
	 *            the target directory
	 */
	public static void copyFileIntoDirectory(final File[] sources,
			final File dest) {

		for (final File source : sources) {

			if (source.isDirectory()) {
				final File newDest = new File(dest, source.getName());
				copyFileIntoDirectory(source.listFiles(), newDest);
			} else {

				FileChannel in = null, out = null;
				try {
					in = new FileInputStream(source).getChannel();
					out = new FileOutputStream(dest).getChannel();

					final long size = in.size();
					final MappedByteBuffer buf = in.map(
							FileChannel.MapMode.READ_ONLY, 0, size);

					out.write(buf);

				} catch (final Exception e) {
					myLogger.debug(e.getLocalizedMessage(), e);
				} finally {
					try {
						if (in != null) {
							in.close();
						}
						if (out != null) {
							out.close();
						}
					} catch (final IOException e) {
						myLogger.error(e.getLocalizedMessage(), e);
					}
				}
			}
		}
	}

	/**
	 * Recursively deletes a directory on the local filesystem.
	 * 
	 * @param path
	 *            the directory to delete
	 * @return whether the directory could be deleted entirely or not
	 */
	public static boolean deleteDirectory(final File path) {
		if (!path.isDirectory()) {
			path.delete();
		}
		if (path.exists()) {
			final File[] files = path.listFiles();
			for (final File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		return (path.delete());
	}

	public static String getFilename(String url) {

		int index = url.lastIndexOf("\\");
		if (index > 0) {
			// means windows
			return url.substring(index + 1);
		}
		index = url.lastIndexOf("/");

		return url.substring(index + 1);

	}

	/**
	 * Reads the content of a file into a String. Only use this when you know
	 * the file is a text file.
	 * 
	 * @param file
	 *            the file
	 * @return the content of the file as String.
	 */
	public static String readFromFile(final File file) {

		final StringBuffer sb = new StringBuffer(BUFFER_SIZE);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (final FileNotFoundException e1) {
			// TODO Auto-generated catch block
			// e1.printStackTrace();
			myLogger.error("Could not read from file: " + file.toString()
					+ ". " + e1.getLocalizedMessage());
			return null;
		}

		try {
			// char[] chars = new char[1024];
			String line = null;
			final int numRead = 0;
			while ((line = reader.readLine()) != null) {
				sb.append(String.valueOf(line) + "\n");
			}

			reader.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			myLogger.error("Could not read lines from file: " + file.toString()
					+ ". " + e.getLocalizedMessage());
			return null;
		}

		return sb.toString();
	}

	/**
	 * Tries to read a file. If the file isn't a text file, an exception is
	 * thrown.
	 * 
	 * @param file
	 *            the file
	 * @return the content of the (text-)file
	 * @throws Exception
	 *             if the file could not be read
	 */
	public static String readFromFileWithException(final File file)
			throws Exception {

		final StringBuffer sb = new StringBuffer(BUFFER_SIZE);
		BufferedReader reader = null;

		reader = new BufferedReader(new FileReader(file));

		// char[] chars = new char[1024];
		String line = null;
		final int numRead = 0;
		while ((line = reader.readLine()) != null) {
			sb.append(String.valueOf(line) + "\n");
		}
		reader.close();

		return sb.toString();
	}

	/**
	 * Saves a {@link DataSource} to a file on disk.
	 * 
	 * @param source
	 *            the source
	 * @param file
	 *            the file
	 * @throws IOException
	 *             if the file can't be written for some reason
	 */
	public static void saveToDisk(final DataSource source, final File file)
			throws Exception {

		final FileOutputStream outputStream = new FileOutputStream(file);
		final InputStream inputStream = source
				.getInputStream();

		try {
			final byte[] buffer = new byte[BUFFER_SIZE * 4];
			for (int n; (n = inputStream.read(buffer)) != -1;) {
				outputStream.write(buffer, 0, n);
			}
		} catch (final Exception e) {
			throw e;
		} finally {
			outputStream.close();
			inputStream.close();
			String tempfile = "n/a";
			try {
				tempfile = source.getName();
				myLogger.debug("Deleting temp file: {}", tempfile);

				if (!new File(tempfile).delete()) {
					myLogger.error("Could not delete tempfile: {}", tempfile);
				}
			} catch (Exception e) {
				myLogger.error("Could not delete temp file {}: {}", tempfile,
						e.getLocalizedMessage());
			}
		}

	}

	public static void saveToDisk(final InputStream source, final File file)
			throws Exception {

		final FileOutputStream outputStream = new FileOutputStream(file);
		try {
			final byte[] buffer = new byte[BUFFER_SIZE * 4];
			for (int n; (n = source.read(buffer)) != -1;) {
				outputStream.write(buffer, 0, n);
			}
		} catch (final Exception e) {
			throw e;
		} finally {
			outputStream.close();
			source.close();
		}

	}

	private FileHelpers() {
	}

}
