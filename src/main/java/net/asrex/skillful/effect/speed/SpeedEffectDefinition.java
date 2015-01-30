package net.asrex.skillful.effect.speed;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 * Constructs instances of {@link SpeedEffect}.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SpeedEffectDefinition extends EffectDefinition {
	
	private double modifier;
	
	@Override
	protected Effect doCreate() {
		return new SpeedEffect(modifier);
	}
	
}
