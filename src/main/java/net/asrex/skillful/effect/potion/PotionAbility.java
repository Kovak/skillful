package net.asrex.skillful.effect.potion;

import net.asrex.skillful.effect.Ability;

/**
 * An alternative to the {@link PotionEffect} that applies a one-time potion
 * effect with a set duration rather than a persistent effect.
 */
public class PotionAbility extends Ability {

	private int effectId;
	private int duration;
	private int amplifier;

	public PotionAbility() {
	}

	public PotionAbility(int effectId, int duration, int amplifier) {
		this.effectId = effectId;
		this.duration = duration;
		this.amplifier = amplifier;
	}
	
	@Override
	public void execute() {
		// do not run on client
		if (player.worldObj.isRemote) {
			return;
		}
		
		player.addPotionEffect(new net.minecraft.potion.PotionEffect(
				effectId, duration, amplifier, true));
	}
	
}
