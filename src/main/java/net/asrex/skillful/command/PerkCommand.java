package net.asrex.skillful.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.asrex.skillful.PlayerNetworkHelper;
import net.asrex.skillful.PlayerSkillInfo;
import static net.asrex.skillful.command.ChatComponentHelper.*;
import net.asrex.skillful.perk.Perk;
import net.asrex.skillful.perk.PerkDefinition;
import net.asrex.skillful.perk.PerkRegistry;
import net.asrex.skillful.perk.cost.PerkCost;
import net.asrex.skillful.requirement.Requirement;
import static net.asrex.skillful.util.TextUtil.slugify;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import static net.minecraft.util.EnumChatFormatting.*;
import org.apache.commons.lang3.StringUtils;

/**
 * Player perk management command.
 */
public class PerkCommand extends CommandBase {

	private final List<String> aliases;
	
	private final List<String> actions;
	private final List<String> perkParamActions;
	private final List<String> perkSlugs;

	public PerkCommand() {
		aliases = Arrays.asList("perks");
		
		actions = Arrays.asList("show", "show-all", "buy", "refund", "help");
		perkParamActions = Arrays.asList("show","buy", "refund");
		perkSlugs = PerkRegistry.getPerkDefinitionSlugs();
	}
	
	@Override
	public String getCommandName() {
		return "perk";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return String.format(
				"/perk <show|show-all|buy|refund|help> [%sperk-slug%s]",
				LIGHT_PURPLE,
				RESET);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (!(sender instanceof EntityPlayer)) {
			send(sender, "Cannot run /perk command from console.");
			return;
		}
		
		EntityPlayer player = (EntityPlayer) sender;
		
		send(sender, generateLine());
		
		if (args.length == 1) {
			String action = args[0].toLowerCase();
			
			switch (action) {
				case "show":     showPerks(player);       break;
				case "show-all": showAllPerks(player);    break;
				case "buy":      showPurchasable(player); break;
				case "refund":   showRefundable(player);  break;
				case "help":     showHelp(player);        break;
				default:       send(sender, getCommandUsage(sender)); break;
			}
		} else if (args.length == 2) {
			String action = args[0].toLowerCase();
			String perkSlug = args[1].toLowerCase();
			
			switch (action) {
				case "show":   showPerk(player, perkSlug);   break;
				case "buy":    doPurchase(player, perkSlug); break;
				case "refund": doRefund(player, perkSlug);   break;
				default:       send(sender, getCommandUsage(sender)); break;
			}
		} else {
			send(sender, getCommandUsage(sender));
		}
		
		send(sender, generateLine());
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return (sender instanceof EntityPlayer);
	}
	
	private List<String> filterStartsWith(List<String> pool, String partial) {
		if (partial == null || partial.trim().isEmpty()) {
			return pool;
		}
		
		List<String> ret = new LinkedList<>();
		
		for (String str : pool) {
			if (str.startsWith(partial.toLowerCase())) {
				ret.add(str);
			}
		}
		
		return ret;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return filterStartsWith(actions, args[0]);
		} else if (args.length == 2) {
			// make sure the command takes a perk param
			if (perkParamActions.contains(args[0])) {
				return filterStartsWith(perkSlugs, args[1]);
			}
		}
		
		return null;
	}
	
	@Override
	public List getCommandAliases() {
		return aliases;
	}
	
	private void showPerks(EntityPlayer player) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		List<PerkDefinition> eligible = PerkRegistry.getAvailablePerks(
				player, true, false);
		
		if (eligible.isEmpty()) {
			send(
					player,
					"You do not currently meet the requirements for any "
							+ "perks.");
			return;
		}
		
		send(player, String.format(
				"You have %s%d%s %sperk%s%s are available for purchase:",
				GOLD,
				eligible.size(),
				RESET,
				
				AQUA,
				(eligible.size() == 1 ? "" : "s"),
				RESET));
		
		for (PerkDefinition def : eligible) {
			ChatComponentText text = new ChatComponentText(String.format(
					"%s-%s ", DARK_GRAY, RESET));
			
			text.appendSibling(generatePerkComponent(player, info, def));
			text.appendText(" - ");
			text.appendSibling(generatePerkBuyComponent(player, info, def));
			
			player.addChatComponentMessage(text);
		}
	}
	
	private void showAllPerks(EntityPlayer player) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		List<PerkDefinition> available = PerkRegistry.getAvailablePerks(
				player, false, false);
		
		if (available.isEmpty()) {
			send(player, "No unowned perks are available for purchase.");
			return;
		}
		
		send(player, String.format(
				"You have %s%d%s unowned %sperk%s%s available:",
				GOLD,
				available.size(),
				RESET,
				
				AQUA,
				(available.size() == 1 ? "" : "s"),
				RESET));
		
		for (PerkDefinition def : available) {
			ChatComponentText text = new ChatComponentText(String.format(
					"%s-%s ", DARK_GRAY, RESET));
			
			text.appendSibling(generatePerkComponent(player, info, def));
			text.appendText(" - ");
			text.appendSibling(generatePerkBuyComponent(player, info, def));
			
			player.addChatComponentMessage(text);
		}
	}
	
	private void showPerk(EntityPlayer player, String perkSlug) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		PerkDefinition def = PerkRegistry.getPerkDefinitionFromSlug(perkSlug);
		if (def == null) {
			send(player, "Perk not found: " + perkSlug);
			return;
		}
		
		send(player, String.format(
				"%s (%s): %s",
				def.getName(),
				slugify(def.getName()),
				def.getDescription()));
		
		// effects
		send(player, String.format(
				" - effects: %s",
				StringUtils.join(def.getEffectNames(), " , ")));
		
		// requirements
		send(player, String.format(
				" - requirements: %s%smet%s",
				BOLD,
				(def.satisfiesRequirements(player, info) ? "" : "not "),
				RESET));
		for (Requirement req : def.getRequirements()) {
			boolean satisfied = req.satisfied(player, info);
				
			send(player, String.format(
					"    - %s (%s%s%s met)",
					req.describe(),
					satisfied ? GREEN : RED,
					satisfied ? "is" : "is not",
					RESET));
		}
		
		// costs
		send(player, String.format(
				" - costs: %s%saffordable%s",
				BOLD,
				(def.canAfford(player, info) ? "" : "not "),
				RESET));
		for (PerkCost cost : def.getCosts()) {
			boolean canAfford = cost.canAfford(player, info);
				
			send(player, String.format(
					"    - %s (%scan%s afford%s)",
					cost.describe(),
					canAfford ? GREEN : RED,
					canAfford ? "" : " not",
					RESET));
		}
	}
	
	private void showPurchasable(EntityPlayer player) {
		List<PerkDefinition> purchasable = PerkRegistry.getAvailablePerks(
				player, true, true);
		
		if (purchasable.isEmpty()) {
			send(player,
					"You do not currently have any perks available for"
							+ " purchase.");
			return;
		}
		
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		send(player, String.format(
				"You can afford the following %s%d%s new %sperk%s%s:",
				GOLD,
				purchasable.size(),
				RESET,
				
				AQUA,
				(purchasable.size() == 1 ? "" : "s"),
				RESET));
		
		for (PerkDefinition def : purchasable) {
			ChatComponentText text = new ChatComponentText(String.format(
					"%s-%s ", DARK_GRAY, RESET));
			
			text.appendSibling(generatePerkComponent(player, info, def));
			text.appendText(" - ");
			text.appendSibling(generatePerkBuyComponent(player, info, def));
			
			player.addChatComponentMessage(text);
		}
	}
	
	private void showRefundable(EntityPlayer player) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		Collection<Perk> current = info.getPerks();
		
		if (current.isEmpty()) {
			send(player, "You do not currently have any perks to refund.");
			return;
		}
		
		send(player, String.format(
				"You can remove and refund the following %d perk%s:",
				current.size(),
				(current.size() == 1 ? "" : "s")));
		
		for (Perk perk : current) {
			ChatComponentText text = new ChatComponentText(String.format(
					"%s-%s ", DARK_GRAY, RESET));
			
			text.appendSibling(generatePerkComponent(
					player, info, perk.getDefinition()));
			//text.appendText(" - ");
			//text.appendSibling(generatePerkRefundComponent(player, info, def));
			
			player.addChatComponentMessage(text);
		}
	}
	
	private void doPurchase(EntityPlayer player, String perkSlug) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		PerkDefinition def = PerkRegistry.getPerkDefinitionFromSlug(perkSlug);
		if (def == null) {
			send(player, "Perk not found: " + perkSlug);
			return;
		}
		
		if (info.hasPerk(def)) {
			send(player, "You already have the perk: " + def.getName());
			send(player, "The /perkeffect command may be used to enable or"
					+ " disable any perk effects.");
			return;
		}
		
		if (!def.satisfiesRequirements(player, info)) {
			send(player, "You do not meet the requirements for the perk:");
			for (Requirement req : def.getRequirements()) {
				boolean satisfied = req.satisfied(player, info);
				
				send(player, String.format(
						"    - %s (%s%s%s met)",
						req.describe(),
						satisfied ? GREEN : RED,
						satisfied ? "is" : "is not",
						RESET));
			}
			
			return;
		}
		
		if (!def.canAfford(player, info)) {
			send(player, "You cannot afford to purchase this perk:");
			for (PerkCost cost : def.getCosts()) {
				boolean canAfford = cost.canAfford(player, info);
				
				send(player, String.format(
						"    - %s (%scan%s afford%s)",
						cost.describe(),
						canAfford ? GREEN : RED,
						canAfford ? "" : " not",
						RESET));
			}
			
			return;
		}
		
		for (PerkCost cost : def.getCosts()) {
			cost.apply(player, info);
		}
		
		if (def.isActivatedOnPurchase()) {
			PlayerNetworkHelper.addAndActivatePerk(player, def.createPerk());
		
			send(player, "You have purchased and activated the perk: "
					+ def.getName());
		} else {
			info.addPerk(def.createPerk());
			
			// TODO: update client? probably not necessary...
			
			send(player, "You have purchased the perk: " + def.getName());
		}
	}
	
	private void doRefund(EntityPlayer player, String perkSlug) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		PerkDefinition def = PerkRegistry.getPerkDefinitionFromSlug(perkSlug);
		if (def == null) {
			send(player, "Perk not found: " + perkSlug);
			return;
		}
		
		Perk perk = info.getPerk(def.getName());
		if (perk == null) {
			send(player, "You do not currently have the perk: "
					+ def.getName());
			return;
		}
		
		// no need to check requirements or costs again, just apply the refund
		// potential issue: changing the costs in the config file after a player
		// has purchased a perk will cause refunds to be the wrong amount
		// could potentially store actual costs in player perk NBT?
		for (PerkCost cost : def.getCosts()) {
			cost.refund(player, info);
		}
		
		PlayerNetworkHelper.removeAndDeactivatePerk(player, perk);
		
		send(player, "You have been refunded for the perk: " + def.getName());
	}
	
	private void showHelp(EntityPlayer player) {
		send(player, "Usage: " + getCommandUsage(player));
		send(player, "/perk show: show perks for purchase (requirements met)");
		send(player, "/perk show <perk-slug>: show detailed info about named "
				+ "perk");
		send(player, "/perk show-all: show all perks, even those with unmet "
				+ "requirements");
		send(player, "/perk buy: show perks for purchase (affordable, "
				+ "requirements met)");
		send(player, "/perk buy <perk-slug>: purchase the given perk");
		send(player, "/perk refund: show perks available to refund");
		send(player, "/perk refund <perk-slug>: refund the given perk");
	}
	
	private void send(ICommandSender target, String message) {
		target.addChatMessage(new ChatComponentText(message));
	}
	
}
