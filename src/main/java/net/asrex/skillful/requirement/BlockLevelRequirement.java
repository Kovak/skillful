package net.asrex.skillful.requirement;

import lombok.Data;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.util.Conditional;
import net.minecraft.entity.player.EntityPlayer;

/**
 *
 */
@Data
public class BlockLevelRequirement implements Requirement {

	private int level;
	private Conditional is = Conditional.below;
	
	@Override
	public boolean satisfied(EntityPlayer player, PlayerSkillInfo info) {
		return is.compare((int) player.posY, level);
	}

	@Override
	public String describe() {
		return String.format("Player block level %s %d",
				is.text,
				level);
	}
	
}
