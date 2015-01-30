package net.asrex.skillful.effect.glide;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.asrex.skillful.effect.Effect;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author tim
 */
public class GlideEffect extends Effect {
	
	private double terminalVelocity;
	private boolean negateFallDistance;

	public GlideEffect() {
	}
	
	public GlideEffect(double terminalVelocity, boolean negateFallDistance) {
		this.terminalVelocity = terminalVelocity;
		this.negateFallDistance = negateFallDistance;
	}
	
	@SubscribeEvent
	public void tick(TickEvent.PlayerTickEvent event) {
		if (event.player != player) {
			return;
		}
		
		// currently this event is server-only, but check to be a bit more
		// update-proof
		if (event.player.worldObj.isRemote) {
			return;
		}
		
		apply(event.player);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event) {
		// PlayerTickEvent doesn't tick on the client, so run that here
		apply(Minecraft.getMinecraft().thePlayer);
	}
	
	private void apply(EntityPlayer player) {
		if (player == null) {
			return;
		}
		
		if (player.capabilities.isFlying) {
			return;
		}
		
		if (player.motionY < terminalVelocity) {
			player.motionY = terminalVelocity;
			
			if (negateFallDistance) {
				player.fallDistance = 0;
			}
		}
	}
	
	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		terminalVelocity = tag.getDouble("terminalVelocity");
		negateFallDistance = tag.getBoolean("negateFallDistance");
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setDouble("terminalVelocity", terminalVelocity);
		tag.setBoolean("negateFallDistance", negateFallDistance);
	}
	
	@Override
	public void doEnable() {
		registerFML(this);
	}

	@Override
	public void doDisable() {
		unregisterFML(this);
	}
	
}
