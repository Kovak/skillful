package net.asrex.skillful.message.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.effect.AbilityDefinition;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;
import net.asrex.skillful.event.SkillfulEffectToggledEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Handler class for {@link EffectToggleMessage}.
 */
@Log4j2
public class EffectToggleHandler
		implements IMessageHandler<EffectToggleMessage, IMessage> {
	
	@Override
	public IMessage onMessage(EffectToggleMessage message, MessageContext ctx) {
		if (ctx.side == Side.SERVER) {
			// ignore (presumably) malicious messages
			return null;
		}
		
		// attempt to toggle the effect
		PlayerSkillInfo info = PlayerSkillInfo.getClientInfo();
		
		EffectDefinition eDef = info.getEffectDefinition(
				message.getPerk(), message.getEffect());
		if (eDef == null) {
			log.warn("Server attempted to toggle effect on client that is "
					+ "missing from the player: {}", info);
			return null;
		}
		
		Effect e;
		if (eDef instanceof AbilityDefinition) {
			// ability classes should be instantiated on-demand
			e = eDef.create(message.getPerk(), info.getPlayer());
		} else {
			e = info.getActiveEffect(message.getPerk(), message.getEffect());
			if (e == null) {
				log.warn("Server attempted to toggle effect on client that is "
						+ "not active on the player: {}", info);
				return null;
			}
		}
		
		e.toggle(message.isActivated());
		
		MinecraftForge.EVENT_BUS.post(SkillfulEffectToggledEvent.builder()
				.player(info.getPlayer())
				.info(info)
				.effect(e)
				.enabled(true)
				.build());
		
		log.info("Toggled effect [{}] on client: {}", message.isActivated(), e);
		
		return null;
	}
	
}
