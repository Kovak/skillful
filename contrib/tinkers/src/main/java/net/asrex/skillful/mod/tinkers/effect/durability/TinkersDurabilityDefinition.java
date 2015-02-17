package net.asrex.skillful.mod.tinkers.effect.durability;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TinkersDurabilityDefinition extends EffectDefinition {

	private int bonus;
	
	private int multiplier;
	
	@Override
	protected Effect doCreate() {
		return new TinkersDurabilityEffect(bonus, multiplier);
	}
	
}
