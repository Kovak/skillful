package net.asrex.skillful.perk.cost;

import net.asrex.skillful.PlayerSkillInfo;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Base class for perk cost implementations
 */
public interface PerkCost {
	
	/**
	 * Determines if the given player can afford this cost.
	 * @param player the player to check
	 * @param info the skill info for the player
	 * @return true if the player can afford this cost, false if not
	 */
	public boolean canAfford(EntityPlayer player, PlayerSkillInfo info);
	
	/**
	 * Called to apply this cost to the player before the perk has been added.
	 * At this point it should already be known that the given player can afford
	 * this cost, as determined by
	 * {@link #canAfford(EntityPlayer, PlayerSkillInfo)}.
	 * @param player the player to affect
	 * @param info the skill info for the given player
	 */
	public void apply(EntityPlayer player, PlayerSkillInfo info);
	
	/**
	 * Refunds whatever this cost is to the given player.
	 * @param player the player to refund
	 * @param info the skill info for the player
	 */
	public void refund(EntityPlayer player, PlayerSkillInfo info);
	
	/**
	 * Describes this perk cost. This text will be shown to players when they
	 * are viewing perks available for purchase to describe this cost (and its
	 * associated perk).
	 * @return a human-readable cost description
	 */
	public String describe();
	
}
