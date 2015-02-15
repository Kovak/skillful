package net.asrex.skillful.requirement;

import net.asrex.skillful.PlayerSkillInfo;
import net.minecraft.entity.player.EntityPlayer;

/**
 *
 */
public class SneakRequirement implements Requirement {

	@Override
	public boolean satisfied(EntityPlayer player, PlayerSkillInfo info) {
		return player.isSneaking();
	}

	@Override
	public String describe() {
		return "Sneaking";
	}
	
}
