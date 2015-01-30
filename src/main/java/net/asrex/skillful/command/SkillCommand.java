package net.asrex.skillful.command;

import java.util.Arrays;
import java.util.List;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.skill.Skill;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 *
 */
public class SkillCommand extends CommandBase {

	private final List<String> aliases;
	
	public SkillCommand() {
		aliases = Arrays.asList("skills");
	}
	
	@Override
	public String getCommandName() {
		return "skill";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/skill <args>"; // TODO
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (!(sender instanceof EntityPlayer)) {
			send(sender, "Cannot run /skill command from console.");
			return;
		}
		
		EntityPlayer player = (EntityPlayer) sender;
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		if (info.getSkills().isEmpty()) {
			send(sender, "You have no current skills.");
		} else {
			send(sender, "Your current skills:");
			for (Skill skill : info.getSkills()) {
				send(sender, String.format(" -> %s: %d/%d (%d)",
						skill.getDefinition().getName(),
						skill.getProgress(),
						skill.getMaxProgress(),
						skill.getLevel()));
			}
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return (sender instanceof EntityPlayer);
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return null; // TODO
	}

	@Override
	public List getCommandAliases() {
		return aliases;
	}
	
	private void send(ICommandSender target, String message) {
		target.addChatMessage(new ChatComponentText(message));
	}
	
}
