package net.asrex.skillful.ui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.asrex.skillful.event.SkillfulLevelUpEvent;
import net.asrex.skillful.event.SkillfulPerkPurchaseEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

/**
 * A class that displays certain Skillful player events in chat.
 */
public class ChatEventDisplay {
	
	@SubscribeEvent
	public void onLevelUp(SkillfulLevelUpEvent event) {
		// server only
		if (event.entityPlayer.worldObj.isRemote) {
			return;
		}
		
		event.entityPlayer.addChatMessage(new ChatComponentText(String.format(
				"%sYour skill [%s%s%s] has reached level %d!",
				EnumChatFormatting.GOLD,
				EnumChatFormatting.GREEN,
				event.getSkill().getName(),
				EnumChatFormatting.GOLD,
				event.getSkill().getLevel())));
	}
	
	@SubscribeEvent
	public void onPerkPurchased(SkillfulPerkPurchaseEvent event) {
		
	}
	
}
