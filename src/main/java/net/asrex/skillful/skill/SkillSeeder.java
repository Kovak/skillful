package net.asrex.skillful.skill;

import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.PlayerNetworkHelper;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.event.SkillfulLevelUpEvent;
import net.asrex.skillful.event.SkillfulPerkPurchaseEvent;
import net.asrex.skillful.event.SkillfulProgressEvent;
import net.asrex.skillful.perk.Perk;
import net.asrex.skillful.perk.PerkDefinition;
import net.asrex.skillful.perk.PerkRegistry;
import net.asrex.skillful.skill.matcher.SkillSeedMatcherRegistry;
import net.asrex.skillful.util.TextUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

/**
 * A utility to input event seeds to player skills. Third-party mods are
 * encouraged to use this method to
 */
@Log4j2
public class SkillSeeder {
	
	/**
	 * Updates player skills matching the given event name string. Additionally,
	 * events seeded here can both trigger and interrupt {@link Perk} effects
	 * on a player, if configured to do so in the associated
	 * {@link PerkDefinition}.
	 * 
	 * <p>Event strings are a space-separated list of event categories that
	 * should gain granularity for each token, as read left-to-right. As an
	 * example, the event {@code "block break minecraft:dirt"} will seed skills
	 * matching both {@code "block break"} and {@code "block"}, as well as an
	 * exact match to all three tokens. Keep in mind that individual tokens must
	 * not contain spaces - it is recommended to use valid slug names when
	 * possible. See {@link TextUtil#slugify(String)} for a utility that can aid
	 * in this. Note that all string comparisons are
	 * <b>case insensitive</b>.</p>
	 * 
	 * <p>Event names passed to this method may optionally include special
	 * <i>seed matcher strings</i>. These strings allow more advanced matching
	 * to take place for specially-notated event strings. For example, an event
	 * token {@code !class:java.lang.String} will match against both
	 * {@code java.lang.String} and its parent classes, such as
	 * {@code java.lang.CharSequence}. Additional matcher tags may be defined
	 * in the {@link SkillSeedMatcherRegistry}, if desired.</p>
	 * 
	 * <p>Third-party mods are welcome to use this method to add additional seed
	 * events to the Skillful mod. To do so, simply pass an event string to this
	 * method along with the relevant player. It is recommended that you use a
	 * consistent event hierarchy and prefix it with a logical namespace. For
	 * example, a custom event from your mod might be:
	 * {@code "my-mod-id some-event-type some-parameter"}. See the above
	 * paragraphs for more information on event string requirements.</p>
	 * 
	 * <p>Note that the actual amount that the skill increases by is configured
	 * by end-users - actual event seeds should not have an implicit value.</p>
	 * 
	 * <p>This method will dispatch cancelable {@link SkillfulProgressEvent}
	 * instances as well as {@link SkillfulLevelUpEvent} instances to the Forge
	 * event bus, {@link MinecraftForge#EVENT_BUS}, when relevant.</p>
	 * 
	 * <p>Note that this method <b>may only be called from a server context</b>.
	 * If called with a client-side {@code player}, an
	 * {@link IllegalStateException} will be thrown.</p>
	 * 
	 * @param player the player whose skills should be updated
	 * @param eventName the name of the event to seed
	 * @throws IllegalStateException when called from a client context
	 */
	public static void seed(EntityPlayer player, String eventName) {
		if (player.worldObj.isRemote) {
			throw new IllegalStateException("This method may not be called"
					+ " from a client context.");
		}
		
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		log.debug("Seeding event [{}] for player: {}", eventName, player);
		
		// seed skills
		for (Skill s : info.getSkillsForEvent(eventName)) {
			// only seed when all requirements have been met
			if (!s.getDefinition().satisfiesRequirements(player, info)) {
				continue;
			}
			
			int amount = s.getDefinition().getEventValue(eventName);
			
			boolean canceled = MinecraftForge.EVENT_BUS.post(
					SkillfulProgressEvent.Pre.builder()
							.player(player)
							.info(info)
							.skill(s)
							.progressAmount(amount)
							.build());
			
			log.trace(
					"Trying to seed {} by {}, canPost: {}",
					s, amount, canceled);
			
			if (!canceled) {
				int startLevel = s.getLevel();
				
				if (s.seed(eventName)) {
					MinecraftForge.EVENT_BUS.post(SkillfulLevelUpEvent.builder()
							.player(player)
							.info(info)
							.skill(s)
							.progressAmount(amount)
							.startLevel(startLevel)
							.build());
				}
				
				MinecraftForge.EVENT_BUS.post(
						SkillfulProgressEvent.Post.builder()
								.player(player)
								.info(info)
								.skill(s)
								.progressAmount(amount)
								.build());
			}
		}
		
		// auto-add any autoPurchase perks the player doesn't already have whose
		// requirements are now met
		for (PerkDefinition def : PerkRegistry.getPerkDefinitions()) {
			if (info.hasPerk(def)) {
				continue;
			}
			
			if (!def.isAutoPurchase()) {
				continue;
			}
			
			if (!def.satisfiesRequirements(player, info)) {
				continue;
			}
			
			Perk perk = def.createPerk();
			
			boolean canceled = MinecraftForge.EVENT_BUS.post(
					SkillfulPerkPurchaseEvent.Pre.builder()
							.player(player)
							.info(info)
							.perk(perk)
							.automatic(true)
							.build());
			
			if (canceled) {
				continue;
			}
			
			info.addPerk(perk);
			PlayerNetworkHelper.updateSkillInfo(player);
			
			if (def.isActivatedOnPurchase()) {
				PlayerNetworkHelper.togglePerk(player, perk, true);
			}
			
			MinecraftForge.EVENT_BUS.post(
					SkillfulPerkPurchaseEvent.Post.builder()
							.player(player)
							.info(info)
							.perk(perk)
							.automatic(true)
							.build());
		}
		
		// seed perk interrupts
		for (Perk perk : info.getInterruptedPerksForEvent(eventName)) {
			log.debug("Dectiving perk due to seed '{}': {}", eventName, perk);
			PlayerNetworkHelper.togglePerk(player, perk, false);
		}
		
		// seed perk triggers
		for (Perk perk : info.getTriggeredPerksForEvent(eventName)) {
			log.debug("Activing perk due to seed '{}': {}", eventName, perk);
			PlayerNetworkHelper.togglePerk(player, perk, true);
		}
	}
	
}
