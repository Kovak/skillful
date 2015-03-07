package net.asrex.skillful.seed;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.asrex.skillful.skill.SkillSeeder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * Event seeds for player combat events (player attacks other, player takes
 * damage...).
 */
public class CombatSeed {
	
	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
		// server only
		if (event.entity.worldObj.isRemote) {
			return;
		}
		
		if (!(event.entityLiving instanceof EntityPlayer)) {
			return;
		}
		
		Entity source = event.source.getEntity();
		if (source == null) {
			return;
		}
		
		//System.out.println("=== "
		//		+ "hurt: " + event.source + "; "
		//		+ "amount: " + event.ammount);
	}
	
	@SubscribeEvent
	public void onLivingAttacked(LivingAttackEvent event) {
		// server only
		if (event.entity.worldObj.isRemote) {
			return;
		}
		
		Entity e = event.source.getEntity();
		if (e != null && e instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) e;
			
			SkillSeeder.seed(entityPlayer, "combat attack !class:"
					+ event.entityLiving.getClass().getName());
		}
	}
	
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		// server only
		if (event.entity.worldObj.isRemote) {
			return;
		}
		
		Entity e = event.source.getEntity();
		if (e != null && e instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) e;
			
			SkillSeeder.seed(entityPlayer, "combat killed !class:"
					+ event.entityLiving.getClass().getName());
		}
	}
	
	@SubscribeEvent
	public void onLivingJump(LivingJumpEvent event) {
		// server only
		if (event.entity.worldObj.isRemote) {
			return;
		}
		
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) event.entityLiving;
			
			SkillSeeder.seed(entityPlayer, "jump");
		}
	}
	
}
