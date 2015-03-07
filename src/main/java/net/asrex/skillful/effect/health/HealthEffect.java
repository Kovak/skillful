package net.asrex.skillful.effect.health;

import java.util.UUID;
import net.asrex.skillful.effect.Effect;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 */
public class HealthEffect extends Effect {

	private int amount;
	private double multiplier;

	private UUID uuid;
	
	public HealthEffect() {
	}

	public HealthEffect(int amount, double multiplier) {
		this.amount = amount;
		this.multiplier = multiplier;
		
		uuid = UUID.randomUUID();
	}

	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		amount = tag.getInteger("amount");
		if (tag.hasKey("multiplier")) {
			multiplier = tag.getDouble("multiplier");
		}
		
		uuid = UUID.fromString(tag.getString("uuid"));
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setInteger("amount", amount);
		tag.setDouble("multiplier", multiplier);
		tag.setString("uuid", uuid.toString());
	}
	
	@Override
	public void doEnable() {
		IAttributeInstance attr = player
				.getAttributeMap()
				.getAttributeInstance(SharedMonsterAttributes.maxHealth);
		
		// don't apply the modifier twice
		AttributeModifier old = attr.getModifier(uuid);
		if (old != null) {
			return;
		}
		
		// operations:
		//   - 0: add raw value to attribute
		//   - 2: multiply raw value to attribute
		//   - 3: multiply (1.0 + raw) by base value and add
		if (multiplier == HealthEffectDefinition.DEFAULT_MULTIPLIER) {
			// add as raw amount
			attr.applyModifier(new AttributeModifier(
					uuid,
					"skillful.health",
					amount,
					0)); // 0 == add
		} else {
			// add as multiplier
			attr.applyModifier(new AttributeModifier(
					uuid,
					"skillful.health",
					multiplier,
					1)); // 1 == multiply
		}
	}

	@Override
	public void doDisable() {
		IAttributeInstance attr = player
				.getAttributeMap()
				.getAttributeInstance(SharedMonsterAttributes.maxHealth);
		
		attr.removeModifier(new AttributeModifier(
				uuid,
				"skillful.health",
				amount,
				0));
	}
	
}
