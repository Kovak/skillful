package net.asrex.skillful.effect.food;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FoodOnKillEffectDefinition extends EffectDefinition {

	private List<String> entityClasses = new LinkedList<>();
	private int foodAmount = 1;
	private int saturationAmount = 1;
	
	@Override
	protected Effect doCreate() {
		return new FoodOnKillEffect(entityClasses, foodAmount, saturationAmount);
	}
	
}
