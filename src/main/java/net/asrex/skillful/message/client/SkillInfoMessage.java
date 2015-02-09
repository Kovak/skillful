package net.asrex.skillful.message.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.asrex.skillful.PlayerSkillInfo;
import net.minecraft.nbt.NBTTagCompound;

/**
 * A message sent to synchronize server-side {@link PlayerSkillInfo} instances
 * with the client.
 */
@Data
public class SkillInfoMessage implements IMessage {

	private NBTTagCompound infoTag;
	
	@Override
	public void fromBytes(ByteBuf buf) {
		infoTag = ByteBufUtils.readTag(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, infoTag);
	}
	
}
