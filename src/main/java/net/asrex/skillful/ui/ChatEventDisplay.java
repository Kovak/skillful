package net.asrex.skillful.ui;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.asrex.skillful.event.SkillfulClientPerkActivateErrorEvent;
import net.asrex.skillful.event.SkillfulLevelUpEvent;
import net.asrex.skillful.event.SkillfulPerkPurchaseEvent;
import net.minecraft.client.Minecraft;
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
	public void onPerkPurchased(SkillfulPerkPurchaseEvent.Post event) {
		// server only
		if (event.entityPlayer.worldObj.isRemote) {
			return;
		}
		
		// TODO: event on manual purchase
		event.entityPlayer.addChatMessage(new ChatComponentText(String.format(
				"Perk added: %s%s%s!",
				EnumChatFormatting.AQUA,
				event.getPerk().getName(),
				EnumChatFormatting.RESET)));
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPerkActivateError(SkillfulClientPerkActivateErrorEvent e) {
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
				String.format(
						"Could not activate perk [%s%s%s]: %s",
						EnumChatFormatting.AQUA,
						e.getPerk(),
						EnumChatFormatting.RESET,
						e.getMessage())));
	}
	
}
