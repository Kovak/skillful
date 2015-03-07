package net.asrex.skillful.effect.food;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FoodOverTimeEffectDefinition extends EffectDefinition {

	private int delay = 600;
	private int foodAmount = 1;
	private int saturationAmount = 1;
	
	@Override
	protected Effect doCreate() {
		return new FoodOverTimeEffect(delay, foodAmount, saturationAmount);
	}
	
}
