package net.asrex.skillful.event;

import cpw.mods.fml.common.eventhandler.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.message.server.PerkActivateMessage;

/**
 * An event dispatched when the client sends a {@link PerkActivateMessage} and
 * receives an error response from the server.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SkillfulClientPerkActivateErrorEvent extends Event {
	
	private final String perk;
	private final String message;
	private final boolean activated;
	
}
