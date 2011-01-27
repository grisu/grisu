package org.vpac.grisu.backend.hibernate;

public class DatabaseInconsitencyException extends RuntimeException {

	public DatabaseInconsitencyException(final String message) {
		super(message);
	}
}
