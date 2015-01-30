package net.asrex.skillful.requirement;

import lombok.Data;
import net.asrex.skillful.PlayerSkillInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

/**
 * A perk requirement that requires another perk.
 */
@Data
public class PerkRequirement implements Requirement {

	private String perk;
	
	@Override
	public boolean satisfied(EntityPlayer player, PlayerSkillInfo info) {
		return info.hasPerk(perk);
	}
	
	@Override
	public String describe() {
		return String.format("Perk: %s%s%s",
				EnumChatFormatting.AQUA,
				perk,
				EnumChatFormatting.RESET);
	}

}
