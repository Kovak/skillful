package net.asrex.skillful.effect.glide;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 * A glide effect that limits a player's terminal velocity, inspired by Morph's
 * AbilityFloat.
 */
@ToString
public class GlideEffectDefinition extends EffectDefinition {
	
	@Getter @Setter
	private double terminalVelocity = -1000;
	
	@Getter @Setter
	private boolean negateFallDistance = false;
	
	@Override
	protected Effect doCreate() {
		return new GlideEffect(terminalVelocity, negateFallDistance);
	}
	
}
