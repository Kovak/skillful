package net.asrex.skillful.effect.potion;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Ability;
import net.asrex.skillful.effect.AbilityDefinition;
import net.minecraft.potion.Potion;

/**
 * Defines a {@link PotionAbility}.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PotionAbilityDefinition extends AbilityDefinition {

	private int id = -1;
	private String potionName;
	
	private int duration = 360;
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
	protected Ability doCreate() {
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
		
		return new PotionAbility(id, duration, amplifier);
	}
	
}
