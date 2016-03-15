package net.asrex.skillful.message.server;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * A message sent from the client to attempt activate a named perk.
 */
@Data
public class PerkActivateMessage implements IMessage {

	private String perk;
	private boolean activated;

	public PerkActivateMessage() {
	}
	
	public PerkActivateMessage(String perk, boolean activated) {
		this.perk = perk;
		this.activated = activated;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		perk = ByteBufUtils.readUTF8String(buf);
		activated = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, perk);
		buf.writeBoolean(activated);
	}
	
}
