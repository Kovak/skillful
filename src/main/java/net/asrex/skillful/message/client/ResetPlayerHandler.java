package net.asrex.skillful.message.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.util.PlayerUtil;

/**
 *
 */
public class ResetPlayerHandler
		implements IMessageHandler<ResetPlayerMessage, IMessage> {

	@Override
	public IMessage onMessage(ResetPlayerMessage req, MessageContext mc) {
		if (mc.side != Side.CLIENT) {
			return null;
		}
		
		PlayerSkillInfo.getClientInfo().resetPlayer(
				PlayerUtil.getPlayerClient());
		
		return null;
	}
	
}
