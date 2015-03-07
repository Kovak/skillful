package net.asrex.skillful.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Builder;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.perk.Perk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * An event dispatched before a perk has been purchased by a player.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SkillfulPerkPurchaseEvent extends PlayerEvent {

	private final PlayerSkillInfo info;
	private final Perk perk;
	private final boolean automatic;

	private SkillfulPerkPurchaseEvent(
			EntityPlayer player, PlayerSkillInfo info,
			Perk perk, boolean automatic) {
		super(player);
		this.info = info;
		this.perk = perk;
		this.automatic = automatic;
	}
	
	@Cancelable
	public static class Pre extends SkillfulPerkPurchaseEvent {

		@Builder
		public Pre(
				EntityPlayer player, PlayerSkillInfo info,
				Perk perk, boolean automatic) {
			super(player, info, perk, automatic);
		}
		
	}
	
	public static class Post extends SkillfulPerkPurchaseEvent {

		@Builder
		public Post(
				EntityPlayer player, PlayerSkillInfo info,
				Perk perk, boolean automatic) {
			super(player, info, perk, automatic);
		}
		
	}
	
}
