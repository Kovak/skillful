package net.asrex.skillful.effect.invisibility;

import net.asrex.skillful.effect.Effect;

/**
 *
 */
public class InvisibilityEffect extends Effect {

	@Override
	public void doEnable() {
		if (player.worldObj.isRemote) {
			return;
		}
		
		player.setInvisible(true);
	}

	@Override
	public void doDisable() {
		if (player.worldObj.isRemote) {
			return;
		}
		
		player.setInvisible(false);
	}
	
}
