package net.asrex.skillful.requirement;

import lombok.Data;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.util.Conditional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

/**
 *
 */
@Data
public class LightLevelRequirement implements Requirement {

	private int level;
	private Conditional is = Conditional.equal_to;
	
	@Override
	public boolean satisfied(EntityPlayer player, PlayerSkillInfo info) {
		if (is == null) {
			return false;
		}
		
		int light = player.worldObj.getLight(new BlockPos(
				(int) player.posX,
				(int) player.posY,
				(int) player.posZ));
		
		return is.compare(light, level);
	}

	@Override
	public String describe() {
		return String.format("Light level %s %d",
				is.text,
				level);
	}
	
}
