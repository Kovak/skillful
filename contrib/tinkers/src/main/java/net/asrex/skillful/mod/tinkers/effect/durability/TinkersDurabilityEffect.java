package net.asrex.skillful.mod.tinkers.effect.durability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.event.ToolCraftedEvent;

/**
 * An effect that modifies the durability of crafted Tinkers' items by a
 * specified amount.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TinkersDurabilityEffect extends Effect {

	private int bonus = 0;
	private float multiplier = 0f;

	public TinkersDurabilityEffect() {
	}

	public TinkersDurabilityEffect(int bonus, float multiplier) {
		this.bonus = bonus;
		this.multiplier = multiplier;
	}
	
	@SubscribeEvent
	public void onItemCrafted(ToolCraftedEvent event) {
		// player must be set
		if (event.player == null) {
			return;
		}
		
		// server only
		if (event.player.worldObj.isRemote) {
			return;
		}
		
		// player must be our player of interest
		if (!event.player.equals(player)) {
			return;
		}
		
		NBTTagCompound tag = event.tool
				.getTagCompound()
				.getCompoundTag("InfiTool");
		
		//
		// largely grabbed from tinkers' ModDurability.java
		//
		
		// get old values + add our modifiers
		int base = tag.getInteger("BaseDurability");
		int bonusDurability = tag.getInteger("BonusDurability") + bonus;
		float modDurability = tag.getFloat("ModDurability") + multiplier;
		
		int total = (int) ((base + bonusDurability) * (modDurability + 1f));
		
		// set the new values
		tag.setInteger("BonusDurability", bonusDurability);
		tag.setFloat("ModDurability", modDurability);
		tag.setInteger("TotalDurability", total);
	}
	
	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		bonus = tag.getInteger("bonus");
		multiplier = tag.getFloat("multiplier");
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setInteger("bonus", bonus);
		tag.setFloat("multiplier", multiplier);
	}
	
	@Override
	public void doEnable() {
		registerForge(this);
	}

	@Override
	public void doDisable() {
		unregisterForge(this);
	}
	
}
