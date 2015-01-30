package net.asrex.skillful;

import net.asrex.skillful.effect.Ability;
import net.asrex.skillful.effect.AbilityDefinition;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;
import net.asrex.skillful.event.SkillfulEffectToggledEvent;
import net.asrex.skillful.message.EffectToggleMessage;
import net.asrex.skillful.message.SkillInfoMessage;
import net.asrex.skillful.perk.Perk;
import net.asrex.skillful.perk.PerkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

/**
 * General utilities for managing player data sync between the server and
 * clients, and safely dispatching events that need to take both sides into
 * account. These methods are the only recommended way to perform many tasks
 * and, in many cases, are the only methods which will dispatch necessary
 * events.
 * 
 * <p>In all cases, methods in this class are only intended for use <b>on the
 * server</b>. If called from a client context, they will throw an
 * exception.</p>
 */
public class PlayerNetworkHelper {
	
	private PlayerNetworkHelper() {
		
	}
	
	/**
	 * Sends the current {@link PlayerSkillInfo} to the given player's client
	 * via a {@link SkillInfoMessage}.
	 * <p>Note that this method may only be called from a server context.</p>
	 * @param player the player to update
	 */
	public static void updateSkillInfo(EntityPlayer player) {
		if (player.worldObj.isRemote) {
			throw new IllegalStateException("This method may not be called"
					+ " from a client context.");
		}
		
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		SkillInfoMessage m = new SkillInfoMessage();
		
		NBTTagCompound tag = new NBTTagCompound();
		info.writeNBT(tag);
		m.setInfoTag(tag);
		
		SkillfulMod.CHANNEL.sendTo(
				m,
				(EntityPlayerMP) player);
	}
	
	/**
	 * Toggles the given effect, either enabling or disabling it on both the
	 * client and server.
	 * <p>The server will immediately execute {@link Effect#enable()}, and an
	 * {@link EffectToggleMessage} will be sent to the client to enable the
	 * effect on the client.</p>
	 * <p>Note that this method may only be called from a server context.</p>
	 * @param player the player on which to toggle the effect
	 * @param perkName the name of the effect's parent perk (to uniquely
	 *     identify the effect in the {@link PerkRegistry})
	 * @param effectName the name of the effect
	 * @param state if true enable the effect; if false, disable it
	 */
	public static void toggleEffect(
			EntityPlayer player, String perkName, String effectName,
			boolean state) {
		if (player.worldObj.isRemote) {
			throw new IllegalStateException("This method may not be called"
					+ " from a client context.");
		}
		
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		EffectDefinition eDef = info.getEffectDefinition(perkName, effectName);
		if (eDef == null) {
			throw new IllegalArgumentException(String.format(
					"The effect [%s,%s] could not be found on the player: %s",
					perkName,
					effectName,
					player.toString()));
		}
		
		Effect e;
		if (eDef instanceof AbilityDefinition) {
			if (!state) {
				throw new IllegalArgumentException(String.format(
						"Ability [%s,%s] can only be enabled, not disabled",
						perkName,
						effectName));
			}
			
			// can only turn on abilities
			e = eDef.create(perkName, player);
		} else {
			e = info.getActiveEffect(perkName, effectName);
			if (e == null) {
				throw new IllegalArgumentException(String.format(
						"The effect [%s, %s] is not active on player: %s",
						perkName,
						effectName,
						player.toString()));
			}
		}
		
		System.out.println("=== toggling " + state + " for player: " + player);
		
		// toggle on the server
		e.toggle(state);
		
		// note: this will dispatch multiple events if called multiple times
		// for the same state, while toggle() would have no effect
		MinecraftForge.EVENT_BUS.post(SkillfulEffectToggledEvent.builder()
				.player(player)
				.info(info)
				.effect(e)
				.enabled(state)
				.build());
		
		// also toggle on the client - see EffectToggleHandler
		EffectToggleMessage m = EffectToggleMessage.builder()
				.activated(state)
				.effect(effectName)
				.perk(perkName)
				.build();
		
		SkillfulMod.CHANNEL.sendTo(m, (EntityPlayerMP) player);
	}
	
	/**
	 * Toggles the given effect, either enabling or disabling it on both the
	 * client and server.
	 * <p>The server will immediately execute {@link Effect#enable()}, and an
	 * {@link EffectToggleMessage} will be sent to the client to enable the
	 * effect on the client.</p>
	 * <p>Note that this method may only be called from a server context.</p>
	 * @param player the player on which to toggle the effect
	 * @param effect the effect instance to toggle
	 * @param state if true, enable the effect; if false, disable it
	 */
	public static void toggleEffect(
			EntityPlayer player, Effect effect, boolean state) {
		
		// given that we'd still need to call PlayerSkillInfo.getActiveEffect()
		// to verify the effect is on the player, it's not really more efficient
		// to take advantage of the Effect instance we already have
		// so, just make this a wrapper method
		
		toggleEffect(
				player,
				effect.getPerkName(), effect.getEffectName(),
				state);
	}
	
	/**
	 * Adds the given effect to the player's active effects and enables it.
	 * The player's client will first be updated {@link PlayerSkillInfo}, and
	 * then the effect is activated with a call to {@link Effect#enable()} on
	 * the server, while a {@link EffectToggleMessage} is sent to the player to
	 * activate on the client.
	 * <p>If no matching effect can be found (as determined by the unique
	 * {@code perkName} and {@code effectName} combination), an
	 * {@link IllegalArgumentException} will be thrown. Similarly, if an
	 * equivalent effect is already active on the player (enabled or not), an
	 * {@code IllegalArgumentException} will also be thrown.</p>
	 * <p>Note that this method may only be called from a server context.</p>
	 * @see #updateSkillInfo(EntityPlayer) 
	 * @see #toggleEffect(EntityPlayer, String, String, boolean) 
	 * @param player the player on which to apply the effect
	 * @param perkName the name of the effect's parent perk (to uniquely
	 *     identify the effect in the {@link PerkRegistry})
	 * @param effectName the name of the effect
	 * @throws IllegalArgumentException if a matching effect is already active
	 *     on the player, or if no matching effect can be found
	 */
	public static void addAndActivateEffect(
			EntityPlayer player, String perkName, String effectName) {
		
		if (player.worldObj.isRemote) {
			throw new IllegalStateException("This method may not be called"
					+ " from a client context.");
		}
		
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		if (info.getActiveEffect(perkName, effectName) != null) {
			throw new IllegalArgumentException(String.format(
					"The effect [%s, %s] is already active on player: %s",
					perkName,
					effectName,
					player.toString()));
		}
		
		Effect effect = PerkRegistry.createEffect(player, perkName, effectName);
		if (effect == null) {
			throw new IllegalArgumentException(String.format(
					"The effect [%s, %s] could not be found",
					perkName,
					effectName));
		}
		
		if (!(effect instanceof Ability)) {
			info.addActiveEffect(effect);
			updateSkillInfo(player);
		}
		
		toggleEffect(player, effect, true);
	}
	
	/**
	 * Adds the given effect to the player's active effects and enables it.
	 * The player's client will first be updated {@link PlayerSkillInfo}, and
	 * then the effect is activated with a call to {@link Effect#enable()} on
	 * the server, while a {@link EffectToggleMessage} is sent to the player to
	 * activate on the client.
	 * <p>Unlike {@link #addAndActivateEffect(EntityPlayer, String, String)},
	 * this method expects a fully initialized {@link Effect} instance (see
	 * {@link PerkRegistry#createEffect(EntityPlayer, String, String)}).</p>
	 * <p>Note that this method may only be called from a server context.</p>
	 * @see #updateSkillInfo(EntityPlayer) 
	 * @see #toggleEffect(EntityPlayer, Effect, boolean) 
	 * @param player the player on which to apply the effect
	 * @param effect the effect to apply
	 * @throws IllegalStateException if called from a client context
	 * @throws IllegalArgumentException if a matching effect is already active
	 *     on the player
	 */
	public static void addAndActivateEffect(EntityPlayer player, Effect effect) {
		if (player.worldObj.isRemote) {
			throw new IllegalStateException("This method may not be called"
					+ " from a client context.");
		}
		
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		Effect dup = info.getActiveEffect(
				effect.getPerkName(), effect.getEffectName());
		if (dup != null) {
			throw new IllegalArgumentException(String.format(
					"The effect %s is already active on player: %s",
					dup.toString(),
					player.toString()));
		}
		
		if (!(effect instanceof Ability)) {
			info.addActiveEffect(effect);
			updateSkillInfo(player);
		}
		
		toggleEffect(player, effect, true);
	}
	
	/**
	 * Disables the matched effect for the given player on both the server and
	 * client, and removes it from the player's list of active effects. The
	 * client will receive updated skill info with the effect removed
	 * immediately after it has been disabled.
	 * <p>Note that this method may only be called from a server context.</p>
	 * @param player the player from which to remove the effect
	 * @param perkName the name of the effect's parent perk (to uniquely
	 *     identify the effect in the {@link PerkRegistry})
	 * @param effectName the name of the effect
	 * @throws IllegalStateException if called from a client context
	 * @throws IllegalArgumentException if no matching active effect exists on
	 *     the player
	 */
	public static void removeAndDeactivateEffect(
			EntityPlayer player, String perkName, String effectName) {
		if (player.worldObj.isRemote) {
			throw new IllegalStateException("This method may not be called"
					+ " from a client context.");
		}
		
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		Effect effect = info.getActiveEffect(perkName, effectName);
		if (effect == null) {
			throw new IllegalArgumentException(String.format(
					"Cannot remove effect [%s,%s] not active on player: %s",
					perkName,
					effectName,
					player));
		}
		
		toggleEffect(player, effect, false);
		
		info.removeActiveEffect(effect);
		
		System.out.println("=== effect removed, new PSI: " + info);
		
		updateSkillInfo(player);
	}
	
	/**
	 * Disables the matched effect for the given player on both the server and
	 * client, and removes it from the player's list of active effects. The
	 * client will receive updated skill info with the effect removed
	 * immediately after it has been disabled.
	 * <p>Note that this method may only be called from a server context.</p>
	 * @param player the player from which to remove the effect
	 * @param effect the effect to remove from the player
	 * @throws IllegalStateException if called from a client context
	 * @throws IllegalArgumentException if no matching active effect exists on
	 *     the player
	 */
	public static void removeAndDeactivateEffect(
			EntityPlayer player, Effect effect) {
		
		removeAndDeactivateEffect(
				player,
				effect.getPerkName(),
				effect.getEffectName());
	}
	
	/**
	 * Toggles all effects of the given perk on both the client and the server.
	 * Effects that are currently in the given state will not be affected; that
	 * is, active effects will remain activate (and will not be re-activated)
	 * if {@code state} is true
	 * @param player the player to toggle the perk on
	 * @param perk the perk to toggle
	 * @param state the new state for all effects of the perk
	 * @throws IllegalStateException if called from a client context
	 * @throws IllegalArgumentException if the player doesn't have the given
	 *     perk
	 */
	public static void togglePerk(
			EntityPlayer player, Perk perk, boolean state) {
		if (player.worldObj.isRemote) {
			throw new IllegalStateException("This method may not be called"
					+ " from a client context.");
		}
		
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		if (!info.hasPerk(perk.getName())) {
			throw new IllegalArgumentException(
					"Player does not have perk to toggle: " + perk);
		}
		
		String perkName = perk.getName();
		for (EffectDefinition def : perk.getDefinition().getEffects()) {
			String effectName = def.getName();
			
			if (state && !info.hasActiveEffect(perkName, effectName)) {
				addAndActivateEffect(player, perkName, effectName);
			} else if (!state && info.hasActiveEffect(perkName, effectName)) {
				removeAndDeactivateEffect(player, perkName, effectName);
			}
		}
	}
	
	/**
	 * Adds and activates all effects on the given perk for the specified
	 * player, and sends the appropriate new information to the client.
	 * 
	 * <p>Note that requirements and costs are <b>not</b> checked or applied by
	 * this method: the caller will need to ensure the player is actually
	 * eligible for the perk.</p>
	 * @param player the player to activate the perk on
	 * @param perk the perk to add and activate
	 * @throws IllegalStateException if called from a client context
	 * @throws IllegalArgumentException if the player already has the given perk
	 */
	public static void addAndActivatePerk(EntityPlayer player, Perk perk) {
		if (player.worldObj.isRemote) {
			throw new IllegalStateException("This method may not be called"
					+ " from a client context.");
		}
		
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		if (info.hasPerk(perk.getName())) {
			throw new IllegalArgumentException(
					"Player already has perk: " + perk.getName());
		}
		
		info.addPerk(perk);
		
		// don't need to update skill info here - togglePerk will send an update
		//updateSkillInfo(player);
		
		togglePerk(player, perk, true);
	}
	
	/**
	 * Removes and deactivates all effects from the given perk for the specified
	 * player, and sends the appropriate changes to the client.
	 * 
	 * <p>Note that refunds are <b>not</b> checked or applied by this method:
	 * the caller will apply any relevant additional changes.</p>
	 * @param player the player to deactivate and remove the perk from
	 * @param perk the perk to deactivate and remove
	 */
	public static void removeAndDeactivatePerk(EntityPlayer player, Perk perk) {
		if (player.worldObj.isRemote) {
			throw new IllegalStateException("This method may not be called"
					+ " from a client context.");
		}
		
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		if (!info.hasPerk(perk.getName())) {
			throw new IllegalArgumentException(
					"Player does not have perk to remove: " + perk.getName());
		}
		
		togglePerk(player, perk, false);
		
		info.removePerk(perk);
		updateSkillInfo(player);
	}
	
}
