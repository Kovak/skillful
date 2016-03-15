package net.asrex.skillful.message.client;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Builder;

/**
 * A message sent to clients when a perk should be enabled
 */
@Data
@AllArgsConstructor
@Builder
public class EffectToggleMessage implements IMessage {
	
	/**
	 * If true, enable the given effect on the client for the given player.
	 * Otherwise, disable it.
	 */
	private boolean activated;
	
	/**
	 * The name of the perk to enable.
	 */
	private String perk;
	
	/**
	 * The name of the effect within the perk to enable.
	 */
	private String effect;
	
	public EffectToggleMessage() {
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		activated = buf.readBoolean();
		perk = ByteBufUtils.readUTF8String(buf);
		effect = ByteBufUtils.readUTF8String(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(activated);
		ByteBufUtils.writeUTF8String(buf, perk);
		ByteBufUtils.writeUTF8String(buf, effect);
	}
	
}
