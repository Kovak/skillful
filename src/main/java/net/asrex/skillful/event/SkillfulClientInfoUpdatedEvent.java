package net.asrex.skillful.event;

import cpw.mods.fml.common.eventhandler.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.PlayerSkillInfo;

/**
 * An event dispatched when the client skill info is synchronized with the
 * server.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SkillfulClientInfoUpdatedEvent extends Event {
	
	/**
	 * The updated {@code PlayerSkillInfo}.
	 */
	private final PlayerSkillInfo info;
	
}
