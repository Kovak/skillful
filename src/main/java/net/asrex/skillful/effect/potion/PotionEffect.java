package net.asrex.skillful.effect.potion;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.asrex.skillful.effect.Effect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * An effect that applies a persistent potion effect to a player.
 */
public class PotionEffect extends Effect {

	public static final int REFRESH_TICKS = 80;
	
	private int effectId;
	private int duration;
	private int amplifier;
	
	// refresh timer - not stored in NBT
	private int lastRefreshTick = 0;

	public PotionEffect() {
		
	}
	
	public PotionEffect(int effectId, int duration, int amplifier) {
		this.effectId = effectId;
		this.duration = duration;
		this.amplifier = amplifier;
	}
	
	@SubscribeEvent
	public void tick(TickEvent.PlayerTickEvent event) {
		// currently this event is server-only, but check to be a bit more
		// update-proof
		if (event.player.worldObj.isRemote) {
			return;
		}
		
		if (event.player != player) {
			return;
		}
		
		apply(event.player);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event) {
		// PlayerTickEvent doesn't tick on the client, so run that here
		// TODO: necessary for potion effects?
		//apply(Minecraft.getMinecraft().thePlayer);
	}
	
	private void apply(EntityPlayer player) {
		if (player == null) {
			return;
		}
		
		if (player.ticksExisted - lastRefreshTick > REFRESH_TICKS) {
			player.addPotionEffect(new net.minecraft.potion.PotionEffect(
					effectId, duration, amplifier, true));
			
			lastRefreshTick = player.ticksExisted;
		}
	}

	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		effectId = tag.getInteger("effectId");
		duration = tag.getInteger("duration");
		amplifier = tag.getInteger("amplifier");
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setInteger("effectId", effectId);
		tag.setInteger("duration", duration);
		tag.setInteger("amplifier", amplifier);
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
