package net.asrex.skillful.exception;

/**
 * An exception thrown when a generic, high-level error occurs in some
 * part of Skillful core functionality.
 */
public class SkillfulException extends RuntimeException {

	public SkillfulException() {
	}

	public SkillfulException(String message) {
		super(message);
	}

	public SkillfulException(Throwable cause) {
		super(cause);
	}

	public SkillfulException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
