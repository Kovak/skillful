package net.asrex.skillful.requirement;

import net.asrex.skillful.PlayerSkillInfo;
import net.minecraft.entity.player.EntityPlayer;

/**
 *
 */
public interface Requirement {
	
	public boolean satisfied(EntityPlayer player, PlayerSkillInfo info);
	
	/**
	 * Describes this perk requirement. This text will be shown to players when
	 * they are viewing perks available for purchase to describe this cost (and
	 * its associated perk).
	 * @return a human-readable description of this requirement
	 */
	public String describe();
	
}
