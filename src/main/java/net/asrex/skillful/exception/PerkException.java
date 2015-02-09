package net.asrex.skillful.exception;

import net.asrex.skillful.perk.Perk;

/**
 * An exception thrown when some aspect of high-level {@link Perk} functionality
 * fails to meet a precondition.
 */
public class PerkException extends SkillfulException {

	public PerkException() {
	}

	public PerkException(String message) {
		super(message);
	}

	public PerkException(Throwable cause) {
		super(cause);
	}

	public PerkException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
