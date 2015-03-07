package net.asrex.skillful.effect;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.asrex.skillful.PlayerSkillInfo;
import net.minecraft.entity.player.EntityPlayer;

/**
 * An Effect subclass targeted at a particular player, but executed on all
 * players. Functionally, the only difference is that when enabled, the in-scope
 * {@code player} protected variable is the current player, and a new
 * {@code targetPlayer} field holds the "player of interest", for whatever
 * purpose that may represent for the particular effect implementation.
 * 
 * <p>More specifically, the {@code targetPlayer} is the player that caused the
 * this effect to be enabled on the current {@code player} instance. The target
 * player either directly or indirectly controls the lifecycle of the effect
 * via some perk.</p>
 * 
 * <p>Public effects are synchronized with the NBT of the {@code targetPlayer}
 * more or less in real-time to ensure newly spawned players can see and
 * subsequently activate the effect, and a {@link PublicEffectToggleMessage}
 * sent to all currently connected players to toggle the effect as it is enabled
 * or disabled. Note that the effect will also be enabled for the
 * {@link targetPlayer}, meaning that the {@code player} and
 * {@code targetPlayer} fields will in some cases be equal.</p>
 * 
 * <p>Like normal Effects, Public Effects activated by (not on) the current
 * player are stored in {@link PlayerSkillInfo#activeEffects} and will be
 * persisted in the player's NBT to be reactivated between sessions. Public
 * Effects activated on the current player by another are maintained in
 * {@link PlayerSkillInfo#publicEffects}</p>
 * 
 * <p>Note that public effects will apply to all players regardless of dimension
 * or distance from the target player. Effects that wish to account for
 * additional criteria are responsible for implementing it on their own.</p>
 */
public abstract class PublicEffect extends Effect {
	
	/**
	 * The player that caused this effect to apply to the current
	 * {@link #player}. This {@code targetPlayer} controls the lifecycle of the
	 * effect.
	 */
	@Setter protected EntityPlayer targetPlayer;
	
}
