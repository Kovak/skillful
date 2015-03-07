package net.asrex.skillful.effect.jump;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JumpBoostEffectDefinition extends EffectDefinition {

	private double boost = 0.2;
	private double sprintVBoost = 0.0;
	private double sprintHBoost = 0.0;
	
	private boolean onSneak = true;
	
	@Override
	protected Effect doCreate() {
		return new JumpBoostEffect(boost, sprintVBoost, sprintHBoost, onSneak);
	}
	
}
