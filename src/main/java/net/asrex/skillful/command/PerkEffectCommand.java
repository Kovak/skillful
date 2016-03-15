package net.asrex.skillful.command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.asrex.skillful.PlayerNetworkHelper;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;
import net.asrex.skillful.perk.Perk;
import net.asrex.skillful.perk.PerkDefinition;
import net.asrex.skillful.perk.PerkRegistry;
import static net.asrex.skillful.util.TextUtil.slugify;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import static net.minecraft.util.EnumChatFormatting.*;

/**
 * A command to allow players to manage their active perk effects.
 */
public class PerkEffectCommand extends CommandBase {

	private final List<String> aliases;
	private final List<String> perkSlugs;
	
	public PerkEffectCommand() {
		aliases = Arrays.asList("peffect", "pe");
		perkSlugs = PerkRegistry.getPerkDefinitionSlugs();
	}
	
	@Override
	public String getCommandName() {
		return "perkeffect";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/peffect <perk> <effect>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (!(sender instanceof EntityPlayer)) {
			send(sender, "Cannot run /peffect command from console.");
			return;
		}
		
		EntityPlayer player = (EntityPlayer) sender;
		
		if (args.length == 0) {
			listPerks(player);
		} else if (args.length == 1) {
			togglePerk(player, args[0].toLowerCase());
		} else if (args.length == 2) {
			togglePerkEffect(
					player,
					args[0].toLowerCase(),
					args[1].toLowerCase());
		} else {
			send(player, "Invalid command. Usage: " + getCommandUsage(sender));
		}
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
	public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos bPos) {
		if (args.length == 1) {
			return filterStartsWith(perkSlugs, args[0]);
		} else if (args.length == 2) {
			PerkDefinition p = PerkRegistry.getPerkDefinitionFromSlug(args[0]);
			if (p == null) {
				return null;
			}
			
			return filterStartsWith(p.getEffectSlugs(), args[1]);
		}
		
		return null;
	}
	
	@Override
	public List getCommandAliases() {
		return aliases;
	}
	
	private void listPerks(EntityPlayer player) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		send(player, "You have the following perks and effects available:");
		for (Perk perk : info.getPerks()) {
			send(player, String.format(
					" - %s (%s)",
					perk.getName(),
					slugify(perk.getName())));
			
			for (EffectDefinition effect : perk.getDefinition().getEffects()) {
				boolean active = info
						.getActiveEffect(perk.getName(), effect.getName()) 
						!= null;
				
				send(player, String.format(
						"    -> %s (%s) - %s%s%s",
						effect.getName(),
						slugify(effect.getName()),
						BOLD + (active ? GREEN.toString() : RED.toString()),
						active ? "active" : "inactive",
						RESET));
			}
		}
	}
	
	private void togglePerk(EntityPlayer player, String perkSlug) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		PerkDefinition def = PerkRegistry.getPerkDefinitionFromSlug(perkSlug);
		if (def == null) {
			send(player, "Unknown perk: " + perkSlug);
			return;
		}
		
		if (!info.hasPerk(def.getName())) {
			send(player, "You do not have the perk: " + def.getName());
			return;
		}
		
		Perk perk = info.getPerk(def.getName());
		if (info.getActiveEffects(def.getName()).isEmpty()) {
			if (!perk.getDefinition().isActivatable()) {
				send(player, "You may not activate this perk manually: "
						+ def.getName());
				return;
			}
			
			if (!perk.canActivate(player.ticksExisted)) {
				send(player, String.format(
						"You cannot use perk \"%s\" until it has finished"
								+ " cooling down. %.1f seconds remain.",
						perk.getName(),
						perk.getCooldownTimeRemaining(player.ticksExisted)));
				return;
			}
			
			// no currently active effects, activate all
			PlayerNetworkHelper.togglePerk(
					player,
					perk,
					true);
			
			perk.setLastActivatedTick(player.ticksExisted);
			
			send(player, "All effects of perk activated: " + perk.getName());
		} else {
			if (!perk.getDefinition().isCancelable()) {
				send(player, "You may not deactivate this perk manually: "
						+ def.getName());
				return;
			}
			
			// at least one is active, disable all
			PlayerNetworkHelper.togglePerk(
					player,
					perk,
					false);
			
			send(player, "All effects of perk deactivated: " + perk.getName());
		}
	}
	
	private void togglePerkEffect(
			EntityPlayer player, String perkSlug, String effectSlug) {
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		PerkDefinition p = PerkRegistry.getPerkDefinitionFromSlug(perkSlug);
		if (p == null) {
			send(player, "Unknown perk: " + perkSlug);
			return;
		}
		
		Effect e = info.getActiveEffectFromSlugs(perkSlug, effectSlug);
		if (e == null) {
			// activate
			Perk perk = info.getPerk(p.getName());
			if (perk == null) {
				send(player, "You do not have the perk: " + p.getName());
				return;
			}
			
			if (!perk.canActivate(player.ticksExisted)) {
				send(player, String.format(
						"You cannot use effects from perk \"%s\" until it has"
								+ " finished cooling down. %.1f seconds"
								+ " remain.",
						p.getName(),
						perk.getCooldownTimeRemaining(player.ticksExisted)));
				return;
			}
			
			if (!p.isActivatable()) {
				send(player, "You may not activate this perk manually: "
						+ p.getName());
				return;
			}
			
			EffectDefinition effectDef = p.getEffectFromSlug(effectSlug);
			if (effectDef == null) {
				send(player, "Unknown effect: " + effectSlug);
				return;
			}
			
			perk.setLastActivatedTick(player.ticksExisted);
			
			e = effectDef.create(p.getName(), player);
			PlayerNetworkHelper.addAndActivateEffect(player, e);
			
			send(player, String.format(
					"Effect activated: %s",
					e.getEffectName()));
		} else {
			if (!p.isCancelable()) {
				send(player, "You may not deactivate this perk manually: "
						+ p.getName());
				return;
			}
			
			// deactivate
			PlayerNetworkHelper.removeAndDeactivateEffect(player, e);
			
			send(player, "Effect deactivated: " + e.getEffectName());
		}
	}
	
	private void send(ICommandSender target, String message) {
		target.addChatMessage(new ChatComponentText(message));
	}
	
}
