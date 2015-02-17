package net.asrex.skillful.mod.tinkers.effect.repair;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.effect.Effect;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.event.ToolCraftedEvent;

/**
 * An effect that occasionally fails to increment the repair counter for a tool.
 * Intended for use with iguanaTweaks.
 */
@EqualsAndHashCode(callSuper = true)
public class TinkersFreeRepairEffect extends Effect {

	private double chance;

	public TinkersFreeRepairEffect() {
	}

	public TinkersFreeRepairEffect(double chance) {
		this.chance = chance;
	}
	
	@SubscribeEvent
	public void onToolCrafted(ToolCraftedEvent event) {
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
		
		// roll some dice
		if (Math.random() > chance) {
			return;
		}
		
		NBTTagCompound tag = event.tool
				.getTagCompound()
				.getCompoundTag("InfiTool");
		
		// skip if it's a new item
		if (!tag.hasKey("RepairCount")) {
			return;
		}
		
		int repairCount = tag.getInteger("RepairCount");
		if (repairCount <= 0) {
			// as above, skip if this is (somehow?) a new item
			return;
		}
		
		// subtract a repair (presumably the one causing this event)
		tag.setInteger("RepairCount", repairCount - 1);
	}
	
	@Override
	public void doEnable() {
		registerForge(this);
	}

	@Override
	public void doDisable() {
		unregisterForge(this);
	}

	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		chance = tag.getDouble("chance");
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setDouble("chance", chance);
	}
	
}
