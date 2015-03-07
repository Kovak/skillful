package net.asrex.skillful.effect.step;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.asrex.skillful.effect.Effect;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 */
public class StepHeightEffect extends Effect {

	public static final float DEFAULT_STEP_HEIGHT = 0.5f;
	
	private float height;

	public StepHeightEffect() {
	}

	public StepHeightEffect(float height) {
		this.height = height;
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player.worldObj.isRemote != player.worldObj.isRemote) {
			// wrong side
			return;
		}
		
		if (!event.player.equals(player)) {
			return;
		}
		
		event.player.stepHeight = height;
	}

	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		height = tag.getFloat("height");
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setFloat("height", height);
	}
	
	@Override
	public void doEnable() {
		registerFML(this);
		
		player.stepHeight = height;
	}

	@Override
	public void doDisable() {
		unregisterFML(this);
		
		player.stepHeight = DEFAULT_STEP_HEIGHT;
	}
	
}
