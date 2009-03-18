

package org.vpac.grisu.control;

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

import org.apache.log4j.Logger;

/**
 * Several methods to ease the handling of files.
 * 
 * @author Markus Binsteiner
 * 
 */
public class FileHelpers {

	static final Logger myLogger = Logger
			.getLogger(FileHelpers.class.getName());

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
	public static void saveToDisk(DataSource source, File file)
			throws IOException {

		FileOutputStream outputStream = new FileOutputStream(file);
		InputStream inputStream = source.getInputStream();

		try {
			byte[] buffer = new byte[4096];
			for (int n; (n = inputStream.read(buffer)) != -1;)
				outputStream.write(buffer, 0, n);
		} finally {
			outputStream.close();
			inputStream.close();
		}

	}

	/**
	 * Reads the content of a file into a String. Only use this when you know
	 * the file is a text file.
	 * 
	 * @param file
	 *            the file
	 * @return the content of the file as String.
	 */
	public static String readFromFile(File file) {

		StringBuffer sb = new StringBuffer(1024);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			myLogger.error("Could not read from file: "+file.toString()+". "+e1.getLocalizedMessage());
			return null;
		}

		try {
			// char[] chars = new char[1024];
			String line = null;
			int numRead = 0;
			while ((line = reader.readLine()) != null) {
				sb.append(String.valueOf(line) + "\n");
			}

			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			myLogger.error("Could not read lines from file: "+file.toString()+". "+e.getLocalizedMessage());
			return null;
		}

		return sb.toString();
	}

	public static void copyFileIntoDirectory(File[] sources, File dest) {

		for (File source : sources) {

			if (source.isDirectory()) {
				File newDest = new File(dest, source.getName());
				copyFileIntoDirectory(source.listFiles(), newDest);
			} else {

				FileChannel in = null, out = null;
				try {
					in = new FileInputStream(source).getChannel();
					out = new FileOutputStream(dest).getChannel();

					long size = in.size();
					MappedByteBuffer buf = in.map(
							FileChannel.MapMode.READ_ONLY, 0, size);

					out.write(buf);

				} catch (Exception e) {

				} finally {
					try {
						if (in != null)
							in.close();
						if (out != null)
							out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
	public static boolean deleteDirectory(File path) {
		if ( ! path.isDirectory() ) {
			path.delete();
		}
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

}
