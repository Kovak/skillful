package net.asrex.skillful;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import lombok.extern.log4j.Log4j2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 *
 */
@Log4j2
public class PublicInfoLifecycleManager {
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent e) {
		// server only
		if (e.entityLiving.worldObj.isRemote) {
			return;
		}
		
		if (!(e.entityLiving instanceof EntityPlayer)) {
			return;
		}
		
		// turn all effects off - they'll be turned back on when the player
		// respawns (the player object will have changed)
		
		EntityPlayer player = (EntityPlayer) e.entityLiving;
		PlayerNetworkHelper.removePublicSkillInfo(player, player.dimension);
	}
	
	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
		if (e.player.worldObj.isRemote) {
			return;
		}
		
		PlayerNetworkHelper.updatePublicSkillInfo(e.player);
	}
	
	@SubscribeEvent
	public void onPlayerChangedDimension(
			PlayerEvent.PlayerChangedDimensionEvent e) {
		if (e.player.worldObj.isRemote) {
			return;
		}
		
		// inform the old dimension that the player left
		PlayerNetworkHelper.removePublicSkillInfo(e.player, e.fromDim);
		
		// tell the player about others in the new dimension
		PlayerNetworkHelper.synchronizePublicInfo(e.player);
		
		// tell others in the new dimension about the player
		PlayerNetworkHelper.updatePublicSkillInfo(e.player);
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
		if (e.player.worldObj.isRemote) {
			return;
		}
		
		PlayerNetworkHelper.updatePublicSkillInfo(e.player);
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e) {
		if (e.player.worldObj.isRemote) {
			return;
		}
		
		PlayerSkillInfo.removeInfo(e.player);
		
		PlayerNetworkHelper.removePublicSkillInfo(e.player, e.player.dimension);
	}
	
}
