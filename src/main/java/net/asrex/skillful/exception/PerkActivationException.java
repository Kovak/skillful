package net.asrex.skillful.exception;

/**
 * An exception thrown when perk activation fails.
 */
public class PerkActivationException extends PerkException {

	public PerkActivationException() {
	}

	public PerkActivationException(String message) {
		super(message);
	}

	public PerkActivationException(Throwable cause) {
		super(cause);
	}

	public PerkActivationException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
