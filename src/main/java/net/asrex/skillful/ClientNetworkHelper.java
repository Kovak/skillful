package net.asrex.skillful;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.asrex.skillful.message.server.PerkActivateMessage;

/**
 * General utilities for communication with the server from the client.
 */
@SideOnly(Side.CLIENT)
public class ClientNetworkHelper {
	
	/**
	 * Sends a {@link PerkActivateMessage} to the server, requesting that the
	 * @param perkName the name of the perk to attempt to activate
	 */
	public static void togglePerk(String perkName) {
		PlayerSkillInfo info = PlayerSkillInfo.getClientInfo();
		
		if (info.hasActiveEffects(perkName)) {
			SkillfulMod.CHANNEL.sendToServer(
				new PerkActivateMessage(perkName, false));
		} else {
			SkillfulMod.CHANNEL.sendToServer(
				new PerkActivateMessage(perkName, true));
		}
		
	}
	
}
