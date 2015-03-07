package net.asrex.skillful.message.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import lombok.Data;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 */
@Data
public class PublicSkillInfoMessage implements IMessage {

	/**
	 * If true, perform a complete resync. This will remove all client-side
	 * public skill info and replace it with the contents of this message.
	 */
	private boolean resync = false;
	
	/**
	 * A map of player UUIDs and their associated info tags.
	 */
	private Map<UUID, NBTTagCompound> infoTags;
	
	/**
	 * A list of player UUIDs that should be forgotten.
	 */
	private List<UUID> removedIds;

	public PublicSkillInfoMessage() {
		infoTags = new LinkedHashMap<>();
		removedIds = new LinkedList<>();
	}
	
	public void setInfo(UUID playerId, NBTTagCompound infoTag) {
		infoTags.put(playerId, infoTag);
	}
	
	public void addRemovedId(UUID removedId) {
		removedIds.add(removedId);
	}
	
	@Override
	public void fromBytes(ByteBuf bb) {
		resync = bb.readBoolean();
		
		int count = bb.readInt();
		for (int i = 0; i < count; i++) {
			UUID id = UUID.fromString(ByteBufUtils.readUTF8String(bb));
			NBTTagCompound tag = ByteBufUtils.readTag(bb);
			
			infoTags.put(id, tag);
		}
		
		int removedCount = bb.readInt();
		for (int i = 0; i < removedCount; i++) {
			UUID id = UUID.fromString(ByteBufUtils.readUTF8String(bb));
			removedIds.add(id);
		}
	}

	@Override
	public void toBytes(ByteBuf bb) {
		bb.writeBoolean(resync);
		
		bb.writeInt(infoTags.size());
		for (Entry<UUID, NBTTagCompound> e : infoTags.entrySet()) {
			ByteBufUtils.writeUTF8String(bb, e.getKey().toString());
			ByteBufUtils.writeTag(bb, e.getValue());
		}
		
		bb.writeInt(removedIds.size());
		for (UUID removedId : removedIds) {
			ByteBufUtils.writeUTF8String(bb, removedId.toString());
		}
	}
	
}
