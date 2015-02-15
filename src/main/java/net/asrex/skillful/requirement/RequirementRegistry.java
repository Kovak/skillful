package net.asrex.skillful.requirement;

import net.asrex.skillful.perk.PerkRegistry;
import net.asrex.skillful.skill.SkillRegistry;

/**
 * A utility to register implementations of {@link Requirement}.
 */
public class RequirementRegistry {
	
	private RequirementRegistry() {
		
	}
	
	/**
	 * Registers a requirement provider. Names should be all lower-case
	 * and consist only of letters a-z and underscores ('_').
	 * 
	 * <p>Requirements are used in the skills and perks configuration to create
	 * instances of their associated {@link Requirement} object. Note that they
	 * will implicitly be prefixed with '!' in configuration file.</p>
	 * 
	 * <p>This method is a wrapper for requirement registration methods found
	 * in the {@link SkillRegistry} and the {@link PerkRegistry}.</p>
	 * 
	 * @see SkillRegistry#registerRequirement(String, Class) 
	 * @see PerkRegistry#registerRequirement(String, Class) 
	 * @param name the name for the configuration tag to associate with the
	 *     given class
	 * @param requirementType the class to associate with the given tag name
	 */
	public static void register(
			String name, Class<? extends Requirement> requirementType) {
		SkillRegistry.registerRequirement(name, requirementType);
		PerkRegistry.registerRequirement(name, requirementType);
	}
	
	public static void registerDefaults() {
		register("block_level", BlockLevelRequirement.class);
		register("light_level", LightLevelRequirement.class);
		register("sneak", SneakRequirement.class);
		register("motion", MotionRequirement.class);
		
		register("perk", PerkRequirement.class);
		register("skill", SkillRequirement.class);
	}
	
}
