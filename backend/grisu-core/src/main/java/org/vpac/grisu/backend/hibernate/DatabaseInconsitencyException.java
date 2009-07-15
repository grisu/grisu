package org.vpac.grisu.backend.hibernate;

public class DatabaseInconsitencyException extends RuntimeException {

	public DatabaseInconsitencyException(String message) {
		super(message);
	}
}
