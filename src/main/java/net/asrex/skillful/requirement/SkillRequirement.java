package net.asrex.skillful.requirement;

import java.util.logging.Logger;
import lombok.Data;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.skill.Skill;
import net.asrex.skillful.util.Conditional;
import net.asrex.skillful.util.Log;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

/**
 * A perk requirement that checks for a certain skill level.
 */
@Data
public class SkillRequirement implements Requirement {

	private static final Logger log = Log.get(SkillRequirement.class);
	
	private String skill;
	private int level;
	private Conditional is = Conditional.at_least;
	
	@Override
	public boolean satisfied(EntityPlayer player, PlayerSkillInfo info) {
		if (skill == null) {
			log.warning("Skill not specified in skill requirement!");
			return true;
		}
		
		Skill s = info.getSkill(skill);
		if (s == null) {
			return false;
		}
		
		return is.compare(s.getLevel(), level);
	}
	
	@Override
	public String describe() {
		return String.format(
				"Skill: Min. %s%s%s level: %s%d%s",
				EnumChatFormatting.DARK_GREEN,
				skill,
				EnumChatFormatting.RESET,
				
				EnumChatFormatting.GOLD,
				level,
				EnumChatFormatting.RESET);
	}
	
}
