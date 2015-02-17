package net.asrex.skillful.mod.tinkers.effect.modifier;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TinkersModifierDefinition extends EffectDefinition {

	private int bonus;
	
	@Override
	protected Effect doCreate() {
		return new TinkersModifierEffect(bonus);
	}
	
}
