package net.asrex.skillful.effect;

import lombok.extern.log4j.Log4j2;

/**
 * An Ability is an {@link Effect} that is manually triggered and does not
 * persist after activation. Abilities may continue acting on the player for a
 * (generally) short period of time, but they should expire either immediately
 * or after a set duration; in addition, they are not stored with the player's
 * save data and will not be automatically enabled at next login if the ability
 * is still active when the player logs out.
 * 
 * <p>Note that, unlike normal {@link Effect} subclasses, {@code Abilities} are
 * not to be retained in the {@code activeEffects} of the current player's
 * {@link PlayerSkillInfo}. When enabled, {@link #execute()} is called to
 * perform a one-time action, and the effect should immediately stop affecting
 * the player. Additionally, {@link #isEnabled()} should always return
 * {@code false}.</p>
 * 
 * <p>As Abilities are not written to player NBT, subclasses should not need to
 * implement {@link #readNBT(NBTTagCompound)} and
 * {@link #writeNBT(NBTTagCompound)}.</p>
 * 
 * <p>Subclasses are responsible for unregistering themselves from any external
 * event busses or other facilities that may retain a reference to instances of
 * the class. The {@link #disable()} method normally present in {@link Effect}
 * implementations <b>will not be called!</b></p>
 */
@Log4j2
public abstract class Ability extends Effect {

	@Override
	public void enable() {
		// don't run doEnable
		execute();
	}
	
	@Override
	public final void doEnable() {
		// do nothing
		
	}

	@Override
	public final void disable() {
		// do nothing
		// disable will -not- be called for ability subclasses
		log.warn("Incorrectly attempted to disable Ability instance: {}",
				getPerkName(),
				getEffectName());
	}
	
	@Override
	public final void doDisable() {
		// do nothing, don't allow overrides
		log.warn("Incorrectly attempted to disable Ability instance: {}",
				getPerkName(),
				getEffectName());
	}
	
	/**
	 * Executes the ability. The {@link #player} variable may be used to
	 * interact directly with the player of interest.
	 */
	public abstract void execute();
	
}
