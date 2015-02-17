package net.asrex.skillful.mod.tinkers.effect.modifier;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.asrex.skillful.effect.Effect;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.event.ToolCraftedEvent;

/**
 *
 */
public class TinkersModifierEffect extends Effect {

	private int bonus;

	public TinkersModifierEffect() {
	}

	public TinkersModifierEffect(int bonus) {
		this.bonus = bonus;
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
		int base = tag.getInteger("Modifiers");
		
		// set the new values
		tag.setInteger("Modifiers", base + bonus);
	}
	
	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		bonus = tag.getInteger("bonus");
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setInteger("bonus", bonus);
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
