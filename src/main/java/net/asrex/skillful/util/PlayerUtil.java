package net.asrex.skillful.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

/**
 *
 */
public class PlayerUtil {
	
	public static EntityPlayerMP getPlayer(UUID id) {
		List<EntityPlayerMP> players = MinecraftServer.getServer()
				.getConfigurationManager()
				.playerEntityList;
		
		for (EntityPlayerMP player : players) {
			if (player.getGameProfile().getId().equals(id)) {
				return player;
			}
		}
		
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public static EntityPlayer getPlayerClient(UUID id) {
		List<EntityPlayer> players = Minecraft.getMinecraft()
				.theWorld
				.playerEntities;
		
		for (EntityPlayer player : players) {
			if (player.getGameProfile().getId().equals(id)) {
				return player;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the latest client player, but only creating an indirect reference.
	 * Code only executed on the client but loaded on the server can reference
	 * this safely without causing a server crash (e.g. messages)
	 * @return the current client player
	 */
	@SideOnly(Side.CLIENT)
	public static EntityPlayer getPlayerClient() {
		return Minecraft.getMinecraft().thePlayer;
	}
	
}
