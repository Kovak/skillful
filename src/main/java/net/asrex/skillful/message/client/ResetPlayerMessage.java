package net.asrex.skillful.message.client;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/**
 *
 */
public class ResetPlayerMessage implements IMessage {

	@Override
	public void fromBytes(ByteBuf bb) {
		// no content
	}

	@Override
	public void toBytes(ByteBuf bb) {
		// no content
	}
	
}
