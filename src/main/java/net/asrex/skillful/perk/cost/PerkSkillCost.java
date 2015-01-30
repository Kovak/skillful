package net.asrex.skillful.perk.cost;

import lombok.Data;
import net.asrex.skillful.PlayerSkillInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

/**
 * A perk cost that deducts skill levels from players.
 */
@Data
public class PerkSkillCost implements PerkCost {
	
	private String skill;
	private int levels;
	
	@Override
	public boolean canAfford(EntityPlayer player, PlayerSkillInfo info) {
		if (!info.hasSkill(skill)) {
			return false;
		}
		
		return info.getSkill(skill).getLevel() >= levels;
	}
	
	@Override
	public void apply(EntityPlayer player, PlayerSkillInfo info) {
		if (!info.hasSkill(skill)) {
			return;
		}
		
		// TODO: this will reset the progress to zero, players could lose
		// partial levels
		info.getSkill(skill).addLevels(-levels);
	}
	
	@Override
	public void refund(EntityPlayer player, PlayerSkillInfo info) {
		if (!info.hasSkill(skill)) {
			return;
		}
		
		// TODO: this will reset progress to zero, players could lose
		// partial levels
		info.getSkill(skill).addLevels(levels);
	}
	
	@Override
	public String describe() {
		return String.format(
				"Skill: %s%d%s level%s of: %s%s%s",
				EnumChatFormatting.GOLD,
				levels,
				EnumChatFormatting.RESET,
				
				(levels == 1 ? "" : "s"),
				
				EnumChatFormatting.DARK_GREEN,
				skill,
				EnumChatFormatting.RESET);
	}
	
}
