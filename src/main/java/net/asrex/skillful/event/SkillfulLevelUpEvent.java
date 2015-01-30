package net.asrex.skillful.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Builder;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.skill.Skill;
import net.asrex.skillful.skill.SkillSeeder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * An event dispatched after progress that has been added to a skill causes the
 * skill's level to increase. Note that the skill's level may increase multiple
 * times if a large amount of progress is added; see {@link #getStartLevel()}.
 * 
 * <p>This event will be dispatched to the Forge event bus
 * ({@link MinecraftForge#EVENT_BUS}) by the{@link SkillSeeder} when a levelup
 * occurs. This event cannot be canceled; however, {@link SkillfulProgressEvent}
 * may be caught to cancel the event beforehand.</p>
 * 
 * <p>Note that this event is currently only dispatched <b>on the server</b>.
 * Client-side code may listen for the {@link SkillfulClientInfoUpdatedEvent}
 * for a similar notification.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SkillfulLevelUpEvent extends PlayerEvent {

	private PlayerSkillInfo info;
	private Skill skill;
	private int progressAmount;
	private int startLevel;
	
	@Builder
	public SkillfulLevelUpEvent(
			EntityPlayer player, PlayerSkillInfo info,
			Skill skill, int progressAmount, int startLevel) {
		super(player);
		
		this.info = info;
		this.skill = skill;
		this.progressAmount = progressAmount;
		this.startLevel = startLevel;
	}

}
