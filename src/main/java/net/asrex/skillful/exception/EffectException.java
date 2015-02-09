package net.asrex.skillful.exception;

import net.asrex.skillful.effect.Effect;

/**
 * An exception thrown when some precondition of high-level functionality
 * relating to {@link Effect} instances is failed.
 */
public class EffectException extends SkillfulException {

	public EffectException() {
	}

	public EffectException(String message) {
		super(message);
	}

	public EffectException(Throwable cause) {
		super(cause);
	}

	public EffectException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
