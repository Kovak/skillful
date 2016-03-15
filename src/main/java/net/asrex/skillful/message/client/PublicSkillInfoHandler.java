package net.asrex.skillful.message.client;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import java.util.Map.Entry;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.PublicPlayerSkillInfo;
import net.asrex.skillful.util.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 */
@Log4j2
public class PublicSkillInfoHandler
		implements IMessageHandler<PublicSkillInfoMessage, IMessage> {

	@Override
	public IMessage onMessage(PublicSkillInfoMessage req, MessageContext ctx) {
		if (ctx.side != Side.CLIENT) {
			return null;
		}
		
		PlayerSkillInfo playerInfo = PlayerSkillInfo.getClientInfo();
		if (req.isResync()) {
			log.info("Resyncing public player info...");
			playerInfo.clearOtherInfo();
		}
		
		// update each of the players included in the message
		for (Entry<UUID, NBTTagCompound> e : req.getInfoTags().entrySet()) {
			PublicPlayerSkillInfo other = playerInfo.getOtherInfo(e.getKey());
			if (other == null) {
				EntityPlayer player = PlayerUtil.getPlayerClient(e.getKey());
				if (player == null) {
					log.warn("Player with UUID could not be found in current "
							+ "dimension, ignoring: {}",
							e.getKey());
					continue;
				}
				
				// this will create the previously nonexistent info for us
				other = playerInfo.getOtherInfo(player);
			}
			
			// update the info with the new data
			// this will potentially cause effect activation/deactivation
			other.readNBT(e.getValue());
			
			log.debug("Updated public info for player {}",
					other.getTargetPlayer());
		}
		
		// remove each noted player
		for (UUID id : req.getRemovedIds()) {
			playerInfo.removeOtherInfo(id);
			
			log.debug("Forgot player {}", id);
		}
		
		return null;
	}
	
}
