package net.asrex.skillful;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.effect.Effect;

/**
 * Manages the lifecycle of player skill data, particular the loading, saving,
 * and updating of player skills and perks.
 */
@Log4j2
public class PlayerSkillManager {
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		// server only
		if (event.player.worldObj.isRemote) {
			return;
		}
		
		// load skills from NBT and update to the config file
		// an empty PlayerSkillInfo will be created if the player hasn't logged
		// in before
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(event.player);
		info.updateSkills();
		
		// send the updated info to the player
		PlayerNetworkHelper.updateSkillInfo(event.player);
		
		// activate all suspended effects
		for (Effect e : info.getActiveEffects()) {
			log.info("Re-enabling suspended effect: {}", e);
			PlayerNetworkHelper.toggleEffect(event.player, e, true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		// due to some forge weirdness we don't get notified of exits from the
		// clientside integrated server - that's handled in
		// SkillfulMod.serverStopping()
		
		// server only
		if (event.player.worldObj.isRemote) {
			return;
		}
		
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(event.player);
		
		// disable all active effects - they'll resume at next login
		for (Effect e : info.getActiveEffects()) {
			e.disable();
		}
		
		info.writeNBT(event.player.getEntityData());
		
		log.info("Wrote player NBT (dedicated server): {}", event.player);
	}
	
	@SubscribeEvent
	public void onClientConnected(
			FMLNetworkEvent.ClientConnectedToServerEvent event) {
		System.out.println("=== connected");
		//PlayerSkillInfo.reset();
	}
	
	@SubscribeEvent
	public void onClientDisconnected(
			FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		
		// disable any active client-side effects
		for (Effect e : PlayerSkillInfo.getClientInfo().getActiveEffects()) {
			e.disable();
		}
		
		//PlayerSkillInfo.reset();
	}
	
}
