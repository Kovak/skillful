package net.asrex.skillful.perk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.effect.EffectDefinition;
import net.asrex.skillful.perk.cost.PerkCost;
import net.asrex.skillful.requirement.Requirement;
import net.asrex.skillful.skill.SkillSeed;
import net.asrex.skillful.skill.matcher.SkillSeedMatcher;
import net.asrex.skillful.util.TextUtil;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.StringUtils;

/**
 * A perk template as read from the configuration file.
 */
@Data
@EqualsAndHashCode(of = {"name"})
public class PerkDefinition {
	
	private String name;
	private String description;
	
	private List<EffectDefinition> effects;
	
	private List<Requirement> requirements;
	private List<PerkCost> costs;
	
	/**
	 * A list seed event strings that will cause this perk's effects to be
	 * enabled.
	 */
	private List<String> triggers;
	
	/**
	 * A list of seed event strings that will cause this perk's effects to be
	 * disabled.
	 */
	private List<String> interrupts;
	
	/**
	 * If true, allows this perk to be activated manually by the player. If
	 * false, the perk must be triggered by a matching event as defined in
	 * {@link #triggers}.
	 */
	private boolean activatable = true;
	
	/**
	 * If true, allows this perk to be canceled manually by the player. If
	 * false, this perk must be interrupted by a matching event as defined in
	 * {@link #interrupts}.
	 */
	private boolean cancelable = true;
	
	/**
	 * If true, this perk will be automatically activated when acquired by the
	 * player. If false, it either must activated manually or triggered by an
	 * event.
	 */
	private boolean activatedOnPurchase = true;
	
	/**
	 * If true, this perk will be automatically purchased when all of its
	 * requirements have been met after a seed event has taken place.
	 */
	private boolean autoPurchase = false;
	
	/**
	 * If nonzero, the number of ticks that must pass before the perk can be
	 * reactivated. A negative value means that this perk has no cooldown.
	 */
	private int cooldownTicks = -1;
	
	public PerkDefinition() {
		effects = new LinkedList<>();
		
		requirements = new LinkedList<>();
		costs = new LinkedList<>();
		
		triggers = new LinkedList<>();
		interrupts = new LinkedList<>();
	}
	
	public boolean satisfiesRequirements(
			EntityPlayer player, PlayerSkillInfo info) {
		for (Requirement req : requirements) {
			if (!req.satisfied(player, info)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean canAfford(EntityPlayer player, PlayerSkillInfo info) {
		for (PerkCost cost : costs) {
			if (!cost.canAfford(player, info)) {
				return false;
			}
		}
		
		return true;
	}
	
	public Perk createPerk() {
		return new Perk(this);
	}
	
	public List<String> getEffectNames() {
		List<String> ret = new ArrayList<>();
		
		for (EffectDefinition def : effects) {
			ret.add(def.getName());
		}
		
		return ret;
	}
	
	public List<String> getEffectSlugs() {
		List<String> ret = new LinkedList<>();
		
		for (EffectDefinition def : effects) {
			ret.add(TextUtil.slugify(def.getName()));
		}
		
		return ret;
	}
	
	public EffectDefinition getEffect(String name) {
		for (EffectDefinition def : effects) {
			if (def.getName().equals(name)) {
				return def;
			}
		}
		
		return null;
	}
	
	public EffectDefinition getEffectFromSlug(String slug) {
		for (EffectDefinition def : effects) {
			if (TextUtil.slugify(def.getName()).equals(slug)) {
				return def;
			}
		}
		
		return null;
	}
	
	private boolean eventMatchesAny(List<String> list, String event) {
		String[] eventTokens = StringUtils.split(event);
		
		for (String seed : list) {
			String[] seedTokens = StringUtils.split(seed);
			
			if (seedTokens.length > eventTokens.length) {
				continue;
			}
			
			// check each seed token against each event token
			// if any match, return true
			boolean match = true;
			for (int i = 0; i < seedTokens.length; i++) {
				if (!SkillSeed.matchToken(eventTokens[i], seedTokens[i])) {
					match = false;
					break;
				}
			}
			
			if (match) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determines if the given event string matches any trigger for this perk.
	 * As trigger events make use of seed events, they are compatible with any
	 * defined {@link SkillSeedMatcher}.
	 * @param eventName the name of the event to match
	 * @return true if a matching trigger exists, false if not
	 */
	public boolean matchesTrigger(String eventName) {
		return eventMatchesAny(triggers, eventName);
	}
	
	/**
	 * Determines if the given event string matches any interrupt for this perk.
	 * As interrupt events make use of seed events, they are compatible with any
	 * defined {@link SkillSeedMatcher}.
	 * @param eventName the name of the event to match
	 * @return true if a matching interrupt exists, false if not
	 */
	public boolean matchesInterrupt(String eventName) {
		return eventMatchesAny(interrupts, eventName);
	}
	
}
