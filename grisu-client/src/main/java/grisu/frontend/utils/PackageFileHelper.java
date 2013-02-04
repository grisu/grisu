package grisu.frontend.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class PackageFileHelper {

	public static File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"), "grisu-temp-files");

	static File getFile(String fileName) {

		File file = new File(TEMP_DIR, fileName);
		file.deleteOnExit();

		if (!file.exists()) {
			TEMP_DIR.mkdirs();
			InputStream inS = PackageFileHelper.class.getResourceAsStream("/" + fileName);
			try {
				IOUtils.copy(inS, new FileOutputStream(file));
				inS.close();
			} catch (Exception e) {
				throw new RuntimeException(
						"Can't create temporary input files: "
								+ e.getLocalizedMessage());
			}
		}

		return file;
	}	

	static String getPath(String fileName) {

		return getFile(fileName).getAbsolutePath();
	}

}
