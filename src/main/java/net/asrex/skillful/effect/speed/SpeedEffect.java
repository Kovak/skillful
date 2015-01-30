package net.asrex.skillful.effect.speed;

import java.util.UUID;
import net.asrex.skillful.effect.Effect;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;

/**
 * An effect that modifies a player's speed without using a potion effect (to
 * avoid particle effects).
 */
public class SpeedEffect extends Effect {

	private double modifier;
	private UUID uuid;
	
	public SpeedEffect() {
		
	}

	public SpeedEffect(double modifier) {
		this.modifier = modifier;
		
		uuid = UUID.randomUUID();
	}

	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		modifier = tag.getDouble("modifier");
		uuid = UUID.fromString(tag.getString("uuid"));
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setDouble("modifier", modifier);
		tag.setString("uuid", uuid.toString());
	}
	
	@Override
	public void doEnable() {
		IAttributeInstance attr = player
				.getAttributeMap()
				.getAttributeInstance(SharedMonsterAttributes.movementSpeed);
		
		// don't apply the modifier twice
		AttributeModifier old = attr.getModifier(uuid);
		if (old != null) {
			return;
		}
		
		attr.applyModifier(new AttributeModifier(
				uuid,
				"skillful.speed",
				modifier,
				2)); // magic number? :/
	}

	@Override
	public void doDisable() {
		IAttributeInstance attr = player
				.getAttributeMap()
				.getAttributeInstance(SharedMonsterAttributes.movementSpeed);
		
		attr.removeModifier(new AttributeModifier(
				uuid,
				"skillful.speed",
				modifier,
				2));
	}
	
}
