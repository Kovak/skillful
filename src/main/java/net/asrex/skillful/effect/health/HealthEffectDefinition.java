package net.asrex.skillful.effect.health;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HealthEffectDefinition extends EffectDefinition {

	public static final double DEFAULT_MULTIPLIER = 1.0;
	
	private int amount;
	private double multiplier = 1.0;
	
	@Override
	protected Effect doCreate() {
		return new HealthEffect(amount, multiplier);
	}
	
}
