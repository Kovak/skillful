package net.asrex.skillful.command;

import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.perk.PerkDefinition;
import net.asrex.skillful.perk.cost.PerkCost;
import net.asrex.skillful.requirement.Requirement;
import net.asrex.skillful.util.TextUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import static net.minecraft.util.EnumChatFormatting.*;

/**
 * Utilities for creating specialized ChatComponents.
 */
public class ChatComponentHelper {
	
	public static ChatComponentText generateRequirementsHoverText(
			EntityPlayer player, PlayerSkillInfo info, PerkDefinition def) {
		boolean satisfied = def.satisfiesRequirements(player, info);
		
		ChatComponentText ret = new ChatComponentText(String.format(
				"\n%s%sRequirements:%s %s%s%s",
				GRAY,
				ITALIC,
				RESET,
				satisfied ? GREEN : RED,
				satisfied ? "met" : "not met",
				RESET));
		
		for (Requirement req : def.getRequirements()) {
			ret.appendText(String.format(
					"\n    %s%s%s",
					req.satisfied(player, info) ? "" : RED,
					req.describe(),
					RESET));
		}
		
		return ret;
	}
	
	public static ChatComponentText generateCostHoverText(
			EntityPlayer player, PlayerSkillInfo info, PerkDefinition def) {
		boolean canAfford = def.canAfford(player, info);
		
		ChatComponentText ret = new ChatComponentText(String.format(
				"\n%s%sCosts:%s %s%s%s",
				GRAY,
				ITALIC,
				RESET,
				canAfford ? GREEN : RED,
				canAfford ? "met" : "not met",
				RESET));
		
		for (PerkCost cost : def.getCosts()) {
			ret.appendText(String.format(
					"\n    %s%s%s",
					cost.canAfford(player, info) ? "" : RED,
					cost.describe(),
					RESET));
		}
		
		return ret;
	}
	
	public static ChatComponentText generatePerkHoverText(
			EntityPlayer player, PlayerSkillInfo info, PerkDefinition def) {
		
		ChatComponentText text = new ChatComponentText(String.format(
				"%s%s%s\n",
				AQUA,
				def.getName(),
				RESET));
		
		// type? user string?
		// with automatic for common configs
		
		text.appendSibling(generateRequirementsHoverText(player, info, def));
		text.appendSibling(generateCostHoverText(player, info, def));
		
		if (def.getDescription() != null) {
			text.appendText(String.format(
					"\n\n%s%s%s",
					ITALIC,
					def.getDescription(), // wrap?
					RESET));
		}
		
		return text;
	}
	
	public static ChatComponentText generatePerkComponent(
			EntityPlayer player, PlayerSkillInfo info, PerkDefinition def) {
		ChatComponentText ret = new ChatComponentText(String.format(
				"[%s]", def.getName()));
		
		ChatStyle style = new ChatStyle();
		style.setColor(AQUA);
		style.setChatHoverEvent(new HoverEvent(
				HoverEvent.Action.SHOW_TEXT,
				generatePerkHoverText(player, info, def)));
		ret.setChatStyle(style);
		
		return ret;
	}
	
	public static ChatComponentText generatePerkBuyHoverText(
			EntityPlayer player, PlayerSkillInfo info, PerkDefinition def) {
		
		boolean owned = info.hasPerk(def);
		boolean satisfied = def.satisfiesRequirements(player, info);
		boolean canAfford = def.canAfford(player, info);
		
		EnumChatFormatting color;
		if (owned) {
			color = BLUE;
		} else if (satisfied && canAfford) {
			color = GREEN;
		} else if (satisfied && !canAfford) {
			color = RED;
		} else {
			color = DARK_GRAY;
		}
		
		ChatComponentText text = new ChatComponentText(String.format(
				"%s%s%s\n",
				color,
				def.getName(),
				RESET));
		
		text.appendSibling(generateRequirementsHoverText(player, info, def));
		text.appendSibling(generateCostHoverText(player, info, def));
		
		return text;
	}
	
	public static ChatComponentText generatePerkBuyComponent(
			EntityPlayer player, PlayerSkillInfo info, PerkDefinition def) {
		
		ChatComponentText ret = new ChatComponentText("[Purchase]");
		
		boolean owned = info.hasPerk(def);
		boolean satisfied = def.satisfiesRequirements(player, info);
		boolean canAfford = def.canAfford(player, info);
		
		ChatStyle style = new ChatStyle();
		if (owned) {
			style.setColor(BLUE);
		} else if (satisfied && canAfford) {
			style.setColor(GREEN);
		} else if (satisfied && !canAfford) {
			style.setColor(RED);
		} else {
			style.setColor(DARK_GRAY);
		}
		ret.setChatStyle(style);
		
		style.setChatHoverEvent(new HoverEvent(
				HoverEvent.Action.SHOW_TEXT,
				generatePerkBuyHoverText(player, info, def)));
		
		if (!owned && (satisfied && canAfford)) {
			style.setChatClickEvent(new ClickEvent(
					ClickEvent.Action.SUGGEST_COMMAND,
					"/perk buy " + TextUtil.slugify(def.getName())));
		}
		
		return ret;
	}

	public static String generateLine(int length, EnumChatFormatting color) {
		StringBuilder s = new StringBuilder();
		
		s.append(color.toString());
		s.append(STRIKETHROUGH);
		for (int i = 0; i < length; i++) {
			s.append("-");
		}
		s.append(RESET);
		
		return s.toString();
	}
	
	public static String generateLine() {
		return generateLine(40, DARK_GRAY);
	}
	
}
