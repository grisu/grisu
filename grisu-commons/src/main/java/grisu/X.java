package grisu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class X {

	public static void main(String[] args) {

		Logger myLogger = LoggerFactory.getLogger(X.class);

		myLogger.debug("LOGGING FROM JAVA");

	}

	public static void p(String t) {
		System.out.println(t);
	}

}
