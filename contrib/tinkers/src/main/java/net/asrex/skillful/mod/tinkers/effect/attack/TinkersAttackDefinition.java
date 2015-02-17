package net.asrex.skillful.mod.tinkers.effect.attack;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TinkersAttackDefinition extends EffectDefinition {

	private int bonus = 0;
	private float multiplier = 0f;
	
	@Override
	protected Effect doCreate() {
		return new TinkersAttackEffect(bonus, multiplier);
	}
	
}
