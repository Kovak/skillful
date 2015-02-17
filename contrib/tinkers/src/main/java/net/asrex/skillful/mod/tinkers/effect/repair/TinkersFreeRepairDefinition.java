package net.asrex.skillful.mod.tinkers.effect.repair;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TinkersFreeRepairDefinition extends EffectDefinition {

	private double chance;
	
	@Override
	protected Effect doCreate() {
		return new TinkersFreeRepairEffect(chance);
	}
	
}
