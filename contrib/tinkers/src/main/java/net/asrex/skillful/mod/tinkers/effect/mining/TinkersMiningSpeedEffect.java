package net.asrex.skillful.mod.tinkers.effect.mining;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.asrex.skillful.effect.Effect;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.event.ToolCraftedEvent;

/**
 *
 */
public class TinkersMiningSpeedEffect extends Effect {

	private int bonus = 0;
	private float multiplier = 0f;
	

	public TinkersMiningSpeedEffect() {
	}

	public TinkersMiningSpeedEffect(int bonus, float multiplier) {
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
		
		// get old values + add our modifiers
		int base = tag.getInteger("MiningSpeed");
		
		int total = (int) ((base + bonus) * (1f + multiplier));
		
		// set the new values
		tag.setInteger("MiningSpeed", total);
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
