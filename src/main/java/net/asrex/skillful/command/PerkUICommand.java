package net.asrex.skillful.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import net.asrex.skillful.PlayerNetworkHelper;
import net.asrex.skillful.PlayerSkillInfo;
import static net.asrex.skillful.command.ChatComponentHelper.*;
import net.asrex.skillful.perk.Perk;
import net.asrex.skillful.perk.PerkDefinition;
import net.asrex.skillful.perk.PerkRegistry;
import net.asrex.skillful.ui.PerkUIData;
import net.asrex.skillful.ui.PerkUIDefinition;
import net.asrex.skillful.ui.PerkUIRegistry;
import net.asrex.skillful.util.TextUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import static net.minecraft.util.EnumChatFormatting.*;

/**
 * A command to manage perk UI configuration.
 */
public class PerkUICommand extends CommandBase {

	private final List<String> aliases;
	
	private final List<String> actions;

	public PerkUICommand() {
		aliases = Arrays.asList("ui", "pui");
		
		actions = Arrays.asList("add", "remove", "set", "help");
	}
	
	@Override
	public String getCommandName() {
		return "perkui";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/perkui add <template-slug> <ui-slug>\n"
				+ "/perkui remove <ui-slug>\n"
				+ "/perkui set <ui-slug> <perk-slug>\n"
				+ "/perkui set <ui-slug> <perk-slug> <index>\n"
				+ "/perkui show <ui-slug>\n"
				+ "/perkui clear <ui-slug>";
	}

	@Override
	public List getCommandAliases() {
		return aliases;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (!(sender instanceof EntityPlayer)) {
			send(sender, "Cannot run /perkui command from console.");
			return;
		}
		
		EntityPlayer player = (EntityPlayer) sender;
		
		send(sender, generateLine());
		
		if (args.length < 1) {
			send(sender, getCommandUsage(sender));
			send(sender, generateLine());
			return;
		}
		
		String action = args[0].toLowerCase();
		
		if (args.length == 1) {
			switch (action) {
				case "add": showTemplateList(player); break;
				case "remove": showUIList(player); break;
				case "help": showHelp(player); break;
				default: send(sender, getCommandUsage(sender)); break;
			}
		} else if (args.length == 2) {
			String uiSlug = args[1];
			
			switch (action) {
				case "remove": doRemoveUI(player, uiSlug); break;
				case "show": showUIInfo(player, uiSlug); break;
				case "clear": doClearUI(player, uiSlug); break;
				default: send(sender, getCommandUsage(sender)); break;
			}
		} else if (args.length == 3) {
			switch (action) {
				case "add": doAddUI(player, args[1], args[2]); break;
				default: send(sender, getCommandUsage(sender)); break;
			}
		} else if (args.length == 4) {
			switch (action) {
				case "set": doSetUI(player, args[1], args[2], args[3]); break;
				default: send(sender, getCommandUsage(sender)); break;
			}
		}
		
		send(sender, generateLine());
	}
	
	private void showTemplateList(EntityPlayer player) {
		Collection<PerkUIDefinition> defs
				= PerkUIRegistry.getPerkUIDefinitions();
		
		if (defs.isEmpty()) {
			send(player, "No UI templates are available for use.");
			return;
		}
		
		send(player, String.format(
				"The following %d template%s are available:",
				defs.size(),
				defs.size() == 1 ? "" : "s"));
		
		for (PerkUIDefinition def : defs) {
			send(player, String.format(
					"%s-%s %s (%s)",
					DARK_GRAY,
					RESET,
					def.getName(),
					TextUtil.slugify(def.getName())));
			
			if (def.getDescription() != null) {
				send(player, "    " + def.getDescription());
			}
		}
	}
	
	private void showUIList(EntityPlayer player) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		if (info.getPerkUIData().isEmpty()) {
			send(player, "You currently have no active UIs.");
			return;
		}
		
		send(player, "You have the following active UIs:");
		for (PerkUIData data : info.getPerkUIData()) {
			send(player, String.format(
					"%s-%s %s (%d perk%s)",
					DARK_GRAY,
					RESET,
					data.getName(),
					data.getPerks().size(),
					data.getPerks().size()  == 1 ? "" : "s"));
		}
	}
	
	private void showHelp(EntityPlayer player) {
		send(player, "Usage: /perkui [arg]...");
		send(player, "/perkui add <template-slug> <ui-slug>: "
				+ "add a new UI with the given template and name");
		
		send(player, "/perkui remove <ui-slug>: "
				+ "remove the UI with the given name");
		
		send(player, "/perkui set <ui-slug> <perk-slug> <index>: "
				+ "add the perk to the UI in the given slot");
		
		send(player, "/perkui show <ui-slug>: "
				+ "list the perks in the given UI");
		
		send(player, "/perkui clear <ui-slug>: "
				+ "clear the named UI");
	}
	
	private void doRemoveUI(EntityPlayer player, String uiSlug) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		PerkUIData data = info.getPerkUIData(uiSlug);
		if (data == null) {
			send(player, "No UI with the given name found.");
			return;
		}
		
		// TODO: UI names might not always be slugs, we may need to convert
		// slug -> real name at some point
		
		info.removePerkUIData(uiSlug);
		
		PlayerNetworkHelper.updateSkillInfo(player);
		
		send(player, "UI has been removed: " + uiSlug);
	}

	private void showUIInfo(EntityPlayer player, String uiSlug) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		PerkUIData data = info.getPerkUIData(uiSlug);
		if (data == null) {
			send(player, "No UI with the given name found.");
			return;
		}
		
		send(player, "The UI %s has the following perks:");
		for (Entry<Integer, String> entry : data.getPerks().entrySet()) {
			ChatComponentText text = new ChatComponentText(String.format(
					"%s-%s %d: ", DARK_GRAY, RESET, entry.getKey()));
			
			text.appendSibling(generatePerkComponent(
					player,
					info,
					info.getPerk(entry.getValue()).getDefinition()));
			
			player.addChatComponentMessage(text);
		}
	}

	private void doClearUI(EntityPlayer player, String uiSlug) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		PerkUIData data = info.getPerkUIData(uiSlug);
		if (data == null) {
			send(player, "No UI with the given name found.");
			return;
		}
		
		data.clearPerks();
		
		PlayerNetworkHelper.updateSkillInfo(player);
		
		send(player, "The UI %s has been cleared.");
	}

	private void doAddUI(
			EntityPlayer player, String templateSlug, String uiSlug) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		PerkUIDefinition def
				= PerkUIRegistry.getDefinitionFromSlug(templateSlug);
		if (def == null) {
			send(player, "No template exists with slug: " + templateSlug);
			return;
		}
		
		PerkUIData data = info.getPerkUIData(uiSlug);
		if (data != null) {
			send(player, "A UI with the given name already exists.");
			return;
		}
		
		info.addPerkUIData(new PerkUIData(uiSlug, def));
		
		PlayerNetworkHelper.updateSkillInfo(player);
		
		send(player, "UI has been added.");
	}

	private void doSetUI(
			EntityPlayer player, String uiSlug,
			String perkSlug, String indexString) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		PerkDefinition perkDef
				= PerkRegistry.getPerkDefinitionFromSlug(perkSlug);
		if (perkDef == null) {
			send(player, "No perk with the given slug could be found.");
			return;
		}
		
		if (!info.hasPerk(perkDef)) {
			send(player, "You do not have the perk: " + perkDef.getName());
			return;
		}
		
		PerkUIData data = info.getPerkUIData(uiSlug);
		if (data == null) {
			send(player, "No UI with the given name could be found.");
			return;
		}
		
		try {
			int index = Integer.parseInt(indexString);
			data.setPerk(index, perkDef.getName());
			
			PlayerNetworkHelper.updateSkillInfo(player);
			
			ChatComponentText text = new ChatComponentText(String.format(
					"Perk set to slot #%d in UI [%s]: ",
					index,
					data.getName()));
			text.appendSibling(generatePerkComponent(player, info, perkDef));
			
			player.addChatComponentMessage(text);
		} catch (NumberFormatException ex) {
			send(player, "Invalid number: " + indexString);
		}
	}
	
	private void send(ICommandSender target, String message) {
		for (String line : message.split("\n")) {
			target.addChatMessage(new ChatComponentText(line));
		}
	}
	
}
