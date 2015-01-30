package net.asrex.skillful.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Builder;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.skill.Skill;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * An event dispatched <i>before</i> progress has been applied to a player
 * skill.
 * 
 * <p>This event is posted to {@link MinecraftForge#EVENT_BUS} and may be
 * canceled to prevent the noted progress from being applied to the skill.</p>
 * 
 * <p>Note that this event is currently only dispatched <b>on the server</b>.
 * Client-side code may listen for the {@link SkillfulClientInfoUpdatedEvent}
 * for a similar notification.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Cancelable
public class SkillfulProgressEvent extends PlayerEvent {
	
	private final PlayerSkillInfo info;
	private final Skill skill;
	private final int progressAmount;

	@Builder
	public SkillfulProgressEvent(
			EntityPlayer player, PlayerSkillInfo info,
			Skill skill, int progressAmount) {
		super(player);
		
		this.info = info;
		this.skill = skill;
		this.progressAmount = progressAmount;
	}
	
}
