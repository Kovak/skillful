package net.asrex.skillful.message.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sent from the server to a client when a particular perk could not be
 * activated.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerkActivateErrorMessage implements IMessage {

	private String perk;
	private String message;
	private boolean activated;
	
	@Override
	public void fromBytes(ByteBuf buf) {
		perk = ByteBufUtils.readUTF8String(buf);
		message = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, perk);
		ByteBufUtils.writeUTF8String(buf, message);
	}
	
}
