package net.asrex.skillful.seed;

import net.asrex.skillful.skill.SkillSeeder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 * A seed that fires an event on player death.
 */
public class DeathSeed {
	
	public void onLivingDeath(LivingDeathEvent event) {
		if (!(event.entityLiving instanceof EntityPlayer)) {
			return;
		}
		
		EntityPlayer player = (EntityPlayer) event.entityLiving;
		
		// server only
		if (player.worldObj.isRemote) {
			return;
		}
		
		SkillSeeder.seed(player, "death");
	}
	
}
