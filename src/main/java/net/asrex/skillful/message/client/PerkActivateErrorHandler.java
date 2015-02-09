package net.asrex.skillful.message.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.asrex.skillful.event.SkillfulClientPerkActivateErrorEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Handles {@link PerkActivateErrorMessage} objects coming from the server.
 */
public class PerkActivateErrorHandler
		implements IMessageHandler<PerkActivateErrorMessage, IMessage> {

	@Override
	public IMessage onMessage(
			PerkActivateErrorMessage message, MessageContext ctx) {
		if (ctx.side != Side.CLIENT) {
			return null;
		}
		
		MinecraftForge.EVENT_BUS.post(new SkillfulClientPerkActivateErrorEvent(
				message.getPerk(),
				message.getMessage(),
				message.isActivated()));
		
		return null;
	}
	
}
