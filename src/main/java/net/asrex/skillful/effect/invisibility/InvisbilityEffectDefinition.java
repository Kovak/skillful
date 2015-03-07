package net.asrex.skillful.effect.invisibility;

import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 *
 */
@EqualsAndHashCode(callSuper = true)
public class InvisbilityEffectDefinition extends EffectDefinition {

	@Override
	protected Effect doCreate() {
		return new InvisibilityEffect();
	}
	
}
