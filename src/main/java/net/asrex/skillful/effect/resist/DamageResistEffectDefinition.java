package net.asrex.skillful.effect.resist;

import java.util.Arrays;
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
public class DamageResistEffectDefinition extends EffectDefinition {
	
	private float amount = 1;
	
	private List<String> sources = new LinkedList<>();

	@Override
	protected Effect doCreate() {
		return new DamageResistEffect(amount, sources);
	}
	
}
