package net.asrex.skillful.effect.food;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.asrex.skillful.effect.Effect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;

/**
 *
 */
public class FoodOverTimeEffect extends Effect {

	private int delay;
	private int foodAmount;
	private int saturationAmount;
	
	// refresh timer - not stored in NBT
	private int lastRefreshTick = 0;
	
	public FoodOverTimeEffect() {
	}

	public FoodOverTimeEffect(int delay, int foodAmount, int saturationAmount) {
		this.delay = delay;
		this.foodAmount = foodAmount;
		this.saturationAmount = saturationAmount;
	}

	@SubscribeEvent
	public void tick(TickEvent.PlayerTickEvent event) {
		// currently this event is server-only, but check to be a bit more
		// update-proof
		if (event.player.worldObj.isRemote) {
			return;
		}
		
		if (!event.player.equals(player)) {
			return;
		}
		
		if (player.ticksExisted - lastRefreshTick > delay) {
			FoodStats fs = player.getFoodStats();
			
			if (foodAmount == 0) {
				fs.foodSaturationLevel = Math.min(
						(float) fs.getFoodLevel(),
						(float) fs.getSaturationLevel() + saturationAmount);
			} else {
				fs.addStats(foodAmount, saturationAmount);
			}
			
			lastRefreshTick = player.ticksExisted;
		}
	}
	
	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		delay = tag.getInteger("delay");
		foodAmount = tag.getInteger("foodAmount");
		saturationAmount = tag.getInteger("saturationAmount");
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setInteger("delay", delay);
		tag.setInteger("foodAmount", foodAmount);
		tag.setInteger("saturationAmount", saturationAmount);
	}
	
	@Override
	public void doEnable() {
		if (player.worldObj.isRemote) {
			return;
		}
		
		registerFML(this);
	}

	@Override
	public void doDisable() {
		if (player.worldObj.isRemote) {
			return;
		}
		
		unregisterFML(this);
	}
	
}
