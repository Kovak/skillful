package net.asrex.skillful.exception;

/**
 * An exception thrown when an effect cannot be activated.
 */
public class EffectActivationException extends SkillfulException {

	public EffectActivationException() {
	}

	public EffectActivationException(String message) {
		super(message);
	}

	public EffectActivationException(Throwable cause) {
		super(cause);
	}

	public EffectActivationException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
