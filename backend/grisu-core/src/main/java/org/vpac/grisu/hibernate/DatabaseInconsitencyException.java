package org.vpac.grisu.hibernate;

public class DatabaseInconsitencyException extends RuntimeException {

	public DatabaseInconsitencyException(String message) {
		super(message);
	}
}
