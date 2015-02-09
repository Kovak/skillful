package net.asrex.skillful.effect.potion;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;
import net.minecraft.potion.Potion;

/**
 * Defines a potion effect. Optionally takes either a potion id or a name
 * (e.g. 'potion.jump') which will be resolved to an id at runtime.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PotionEffectDefinition extends EffectDefinition {
	
	private int id = -1;
	private String potionName;
	
	private int duration = 180;
	private int amplifier = 0;
	
	private int idForName(String potionName) {
		for (Potion p : Potion.potionTypes) {
			if (p == null || p.getName() == null) {
				continue;
			}
			
			if (p.getName().equalsIgnoreCase(potionName)) {
				return p.getId();
			}
		}
		
		return -1;
	}
	
	@Override
	protected Effect doCreate() {
		if (id == -1 && (potionName == null || potionName.isEmpty())) {
			throw new IllegalArgumentException(
					"Potion effect must have a defined 'name' or 'id'");
		} else if (id == -1) {
			id = idForName(potionName);
			if (id == -1) {
				throw new IllegalArgumentException(
						"Potion effect not found with id: " + potionName);
			}
		}
		
		return new PotionEffect(id, duration, amplifier);
	}
	
}
