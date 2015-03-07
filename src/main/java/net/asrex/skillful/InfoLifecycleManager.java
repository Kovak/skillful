package net.asrex.skillful;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.asrex.skillful.effect.Effect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 * Manages the lifecycle of player skill info instances. In particular, this
 * handles the updating of player instances after player death and dimension
 * changes.
 * <p>Similarly, {@link PublicInfoLifecycleManager} manages the lifecycle of
 * public info and effects after events have occurred for other players.</p>
 */
public class InfoLifecycleManager {
	
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
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		for (Effect effect : info.getActiveEffects()) {
			effect.disable();
			
			PlayerNetworkHelper.toggleEffect(player, effect, false);
		}
	}
	
	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
		if (e.player.worldObj.isRemote) {
			return;
		}
		
		// update player instance from UUID
		PlayerNetworkHelper.resetClientPlayer(e.player);
		
		// re-enable all effects
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(e.player);
		for (Effect effect : info.getActiveEffects()) {
			effect.enable();
			
			PlayerNetworkHelper.toggleEffect(e.player, effect, true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerChangedDimension(
			PlayerEvent.PlayerChangedDimensionEvent e) {
		if (e.player.worldObj.isRemote) {
			return;
		}
		
		PlayerNetworkHelper.resetClientPlayer(e.player);
	}
	
}
