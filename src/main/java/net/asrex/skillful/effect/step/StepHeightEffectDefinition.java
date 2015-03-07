package net.asrex.skillful.effect.step;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StepHeightEffectDefinition extends EffectDefinition {

	private float height = 1.0f;
	
	@Override
	protected Effect doCreate() {
		return new StepHeightEffect(height);
	}
	
}
