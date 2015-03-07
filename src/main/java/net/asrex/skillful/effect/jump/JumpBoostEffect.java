package net.asrex.skillful.effect.jump;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.asrex.skillful.effect.Effect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 *
 */
public class JumpBoostEffect extends Effect {

	private double boost;
	private double sprintVBoost;
	private double sprintHBoost;
	
	private boolean onSneak;

	public JumpBoostEffect() {
	}

	public JumpBoostEffect(
			double boost, double sprintVBoost, double sprintHBoost,
			boolean onSneak) {
		this.boost = boost;
		this.sprintVBoost = sprintVBoost;
		this.sprintHBoost = sprintHBoost;
		this.onSneak = onSneak;
	}
	
	@SubscribeEvent
	public void onLivingJump(LivingEvent.LivingJumpEvent event) {
		if (!(event.entityLiving instanceof EntityPlayer)) {
			return;
		}
		
		EntityPlayer p = (EntityPlayer) event.entityLiving;
		if (!player.equals(p)) {
			return;
		}
		
		// apply the sprint boost if sprinting
		if (p.isSprinting()) {
			// yaw is in degrees, convert it to radians
			double angleRads = Math.toRadians(player.rotationYaw);
			
			// find boost x,z components in angleRads direction
			double xBoost = Math.sin(angleRads) * sprintHBoost;
			double zBoost = Math.cos(angleRads) * sprintHBoost;
			
			// add boost to player
			p.motionX -= xBoost;
			p.motionZ += zBoost;
			
			p.motionY += sprintVBoost;
		} else if (!onSneak || player.isSneaking()) {
			p.motionY += boost;
		}
	}

	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		boost = tag.getDouble("boost");
		sprintVBoost = tag.getDouble("sprintVBoost");
		sprintHBoost = tag.getDouble("sprintHBoost");
		onSneak = tag.getBoolean("onSneak");
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setDouble("boost", boost);
		tag.setDouble("sprintVBoost", sprintVBoost);
		tag.setDouble("sprintHBoost", sprintHBoost);
		tag.setBoolean("onSneak", onSneak);
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
