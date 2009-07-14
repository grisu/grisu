package org.vpac.grisu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper methods for String handling.
 * 
 * @author markus
 *
 */
public class SeveralStringHelpers {

	/**
	 * Converts an inputstream to a string.
	 * 
	 * @param is the input stream
	 * @return the string
	 */
	public static String fromInputStream(InputStream is) {
		
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
        return sb.toString();
	}

}
