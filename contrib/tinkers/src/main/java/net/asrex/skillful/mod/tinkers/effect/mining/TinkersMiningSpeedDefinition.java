package net.asrex.skillful.mod.tinkers.effect.mining;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TinkersMiningSpeedDefinition extends EffectDefinition {

	private int bonus = 0;
	private float multiplier = 0f;
	
	@Override
	protected Effect doCreate() {
		return new TinkersMiningSpeedEffect(bonus, multiplier);
	}
	
}
