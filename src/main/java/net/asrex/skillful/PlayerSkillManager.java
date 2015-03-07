package net.asrex.skillful;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.event.SkillfulPerkPurchaseEvent;
import net.asrex.skillful.event.SkillfulProgressEvent;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

/**
 * Manages the lifecycle of player skill data, particularly the loading, saving,
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
		
		// purge old attribute modifiers
		// sometimes these won't get disabled / saved properly with an active
		// effect during a crash or server shutdown
		// normally they'll be disabled + removed when the player logs out
		// if a 'skillful.*' effect remains on the player, then it wasn't
		// disabled as expected and can be safely removed now
		Collection<IAttributeInstance> attrs = event.player
				.getAttributeMap()
				.getAllAttributes();
		
		for (IAttributeInstance attr : attrs) {
			Collection<AttributeModifier> mods = attr.func_111122_c();
			
			List<AttributeModifier> toRemove = new LinkedList<>();
			
			for (AttributeModifier mod : mods) {
				if (mod.getName().startsWith("skillful.")) {
					toRemove.add(mod);
				}
			}
			
			for (AttributeModifier mod : toRemove) {
				attr.removeModifier(mod);
			}
		}
		
		// load skills from NBT and update to the config file
		// an empty PlayerSkillInfo will be created if the player hasn't logged
		// in before
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(event.player);
		info.updateSkills();
		
		// send the updated info to the player
		PlayerNetworkHelper.updateSkillInfo(event.player);
		
		List<Effect> deadEffects = new LinkedList<>();
		
		// activate all suspended effects
		for (Effect e : info.getActiveEffects()) {
			log.debug("Re-enabling suspended effect: {}", e);
			
			try {
				PlayerNetworkHelper.toggleEffect(event.player, e, true);
			} catch (Exception ex) {
				log.warn("Could not re-enable effect " + e + ", it will be "
								+ "removed from the player", ex);
				deadEffects.add(e);
			}
		}
		
		for (Effect e : deadEffects) {
			info.removeActiveEffect(e);
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
	public void onPlayerSkillUpdate(SkillfulProgressEvent.Post event) {
		// do a quick save
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(event.entityPlayer);
		info.writeNBT(event.entityPlayer.getEntityData());
		
		// TODO: is this intensive? potentially more than 1x save per second
		// maybe rate-limit somehow, with a tick counter or some such?
	}
	
	@SubscribeEvent
	public void onPlayerPerkPurchase(SkillfulPerkPurchaseEvent.Post event) {
		// do a quick save
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(event.entityPlayer);
		info.writeNBT(event.entityPlayer.getEntityData());
	}
	
	@SubscribeEvent
	public void onClientConnected(
			FMLNetworkEvent.ClientConnectedToServerEvent event) {
		PlayerSkillInfo.clientReset();
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
