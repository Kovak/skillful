package net.asrex.skillful.message.server;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.asrex.skillful.PlayerNetworkHelper;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.exception.PerkException;
import net.asrex.skillful.message.client.PerkActivateErrorMessage;
import net.asrex.skillful.perk.Perk;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Handles {@link PerkActivateMessage} objects sent from the client.
 */
public class PerkActivateHandler
		implements IMessageHandler<PerkActivateMessage, IMessage> {

	@Override
	public IMessage onMessage(PerkActivateMessage message, MessageContext ctx) {
		if (ctx.side == Side.CLIENT) {
			// ignore
			return null;
		}
		
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		Perk perk = info.getPerk(message.getPerk());
		if (perk == null) {
			return new PerkActivateErrorMessage(
					message.getPerk(),
					"Cannot activate unknown perk: " + message.getPerk(),
					message.isActivated());
		}
		
		try {
			if (message.isActivated()) {
				PlayerNetworkHelper.activatePerk(player, perk);
			} else {
				PlayerNetworkHelper.deactivatePerk(player, perk);
			}
		} catch (PerkException ex) {
			return new PerkActivateErrorMessage(
					message.getPerk(),
					ex.getMessage(),
					message.isActivated());
		}
		
		return null;
	}
	
}
