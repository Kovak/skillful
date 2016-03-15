package net.asrex.skillful.skill;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.pack.AllSkillPacksSkillPack;
import net.asrex.skillful.pack.SkillPack;
import net.asrex.skillful.requirement.Requirement;
import net.asrex.skillful.skill.scaling.ExponentialScaling;
import net.asrex.skillful.skill.scaling.FixedScaling;
import net.asrex.skillful.skill.scaling.LinearScaling;
import net.asrex.skillful.skill.scaling.ScalingStrategy;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * A central registry for all {@link Skill} classes. Other mods may register
 * skill packs here with {@link #registerPack(String, Class)}.
 */
@Log4j2
public class SkillRegistry {
	
	public static final String SKILLS_FILENAME = "skills.yml";
	
	private static SkillRegistry instance;
	
	private final Constructor constructor;
	
	private final Map<String, SkillDefinition> skillDefinitions;
	private final Map<String, Class<? extends SkillPack>> packs;
	
	private SkillRegistry() {
		constructor = new Constructor();
		
		map(FixedScaling.class, "!fixed");
		map(LinearScaling.class, "!linear");
		map(ExponentialScaling.class, "!exponential");
		map(AllSkillPacksSkillPack.class, "!all");
		
		skillDefinitions = new LinkedHashMap<>();
		packs = new LinkedHashMap<>();
	}
	
	private static SkillRegistry getInstance() {
		if (instance == null) {
			instance = new SkillRegistry();
		}
		
		return instance;
	}
	
	private void map(Class clazz, String tag) {
		TypeDescription old = 
				constructor.addTypeDescription(new TypeDescription(clazz, tag));
		if (old != null) {
			log.warn("Configuration tag \"{}\" for class {} has been "
					+ "overwritten by new class {}. The old value will not be "
					+ "available for use - be warned of unexpected errors or "
					+ "functionality differences!",
					tag,
					old.getType().getName(),
					clazz.getName());
		}
	}
	
	private void _registerPack(String name, Class<? extends SkillPack> clazz) {
		packs.put(name, clazz);
		map(clazz, "!" + name);
	}
	
	private static void processConfigFile(File configDir, String path)
			throws IOException {
		SkillRegistry r = getInstance();
		
		Yaml yaml = new Yaml(r.constructor);
		SkillConfig conf = yaml.loadAs(
				new FileReader(new File(configDir, path)),
				SkillConfig.class);
		
		if (conf == null) {
			log.warn("Skill config '{}' was empty, ignoring...", path);
			return;
		}
		
		for (SkillDefinition def : conf.skills) {
			if (r.skillDefinitions.containsKey(def.getName())) {
				log.warn("A skill with name \"{}\" has been defined multiple "
						+ "times. Additional declarations will be discarded.",
						def.getName());
				continue;
			}
			
			r.skillDefinitions.put(def.getName(), def);
		}
		
		for (SkillPack pack : conf.packs) {
			for (SkillDefinition def : pack.createSkillDefintions()) {
				if (r.skillDefinitions.containsKey(def.getName())) {
					log.warn("A perk with conflicting name \"{}\" is being "
							+ "added by the perk pack {}. It will be "
							+ "discarded.",
							def.getName(),
							pack.getClass().getName());
					continue;
				}
				
				r.skillDefinitions.put(def.getName(), def);
			}
		}
		
		// TODO: can recurse, should probably prevent that...
		for (String include : conf.includes) {
			processConfigFile(configDir, include);
		}
	}
	
	public static void init(File configDir) throws IOException {
		processConfigFile(configDir, SKILLS_FILENAME);
	}
	
	/**
	 * Registers the given {@link SkillDefinition}. A new instance of the skill
	 * will be added to player data (generally) on next player login, or when
	 * the skill is first seeded.
	 * <p>Note that except in special cases, mods should generally not define
	 * their own skills. Ideally, they should allow players to configure skills
	 * as they see fit, and can optionally create {@link SkillPack} classes to
	 * aid in this process.</p>
	 * @param def the skill definition to register
	 */
	public static void registerSkillDefinition(SkillDefinition def) {
		getInstance().skillDefinitions.put(def.getName(), def);
	}
	
	/**
	 * Gets a list of all currently registered skill classes.
	 * @return the list of registered skill classes
	 */
	public static Collection<SkillDefinition> getSkillDefinitions() {
		return getInstance().skillDefinitions.values();
	}
	
	public static SkillDefinition getDefinition(String name) {
		return getInstance().skillDefinitions.get(name);
	}
	
	/**
	 * Registers a skill pack provider with the given name. Names should be all
	 * lower-case and consist only of letters a-z and underscores ('_').
	 * 
	 * <p>Skill packs are used in the skill configuration to allow users to add
	 * preconfigured collections of skills in bulk. For additional information
	 * and requirements for skill pack implementations, see {@link SkillPack}.
	 * Note that names will implicitly be prefixed with '!' character in
	 * configuration file.</p>
	 * 
	 * <p>Third-party skill packs should be registered at startup during the
	 * {@link FMLInitializationEvent}. Skill configuration will be read from
	 * disk during the {@link FMLPostInitializationEvent}, and any missing
	 * definitions at this time will result in an error.</p>
	 * @param name the name for the configuration tag to associate with the
	 *     given class
	 * @param packClass the class of the skill pack to register
	 */
	public static void registerPack(
			String name, Class<? extends SkillPack> packClass) {
		getInstance()._registerPack(name, packClass);
	}
	
	public static Collection<Class<? extends SkillPack>> getPacks() {
		return getInstance().packs.values();
	}
	
	/**
	 * Registers a level scaling strategy implementation with the given name.
	 * Names should be all lower-case and consist only of letters a-z and
	 * underscores ('_').
	 * 
	 * <p>Scaling strategies are used to determine the amount of experience
	 * points for skills needed to progress in a given level. They are specified
	 * by players as part of the configured {@link SkillDefinition}.</p>
	 * 
	 * <p>Third-party scaling strategies should be registered at startup during
	 * the {@link FMLInitializationEvent}. Skill configuration will be read from
	 * disk during the {@link FMLPostInitializationEvent}, and any missing
	 * definitions at this time will result in an error.</p>
	 * @param name the name of the scaling strategy to add, for use in the
	 *     configuration file
	 * @param clazz the class of the implementation to add
	 */
	public static void registerScalingStrategy(
			String name, Class<? extends ScalingStrategy> clazz) {
		getInstance().map(clazz, "!" + name);
	}
	
	/**
	 * Registers a skill requirement provider. Names should be all lower-case
	 * and consist only of letters a-z and underscores ('_').
	 * 
	 * <p>Requirements are used in the skills configuration to create instances
	 * of their associated {@link Requirement} object. Note that they will
	 * implicitly be prefixed with '!' in configuration file.</p>
	 * 
	 * <p>Third-party requirements should be registered at startup during the
	 * {@link FMLInitializationEvent}. Skill configuration will be read from
	 * disk during the {@link FMLPostInitializationEvent}, and any missing
	 * definitions will result in an error.</p>
	 * @param name the name for the configuration tag to associate with the
	 *     given class
	 * @param requirementType the class to associate with the given tag name
	 */
	public static void registerRequirement(
			String name, Class<? extends Requirement> requirementType) {
		getInstance().map(requirementType, "!" + name);
	}
	
	@Data
	public static class SkillConfig {
		
		public List<SkillDefinition> skills = new LinkedList<>();
		public List<SkillPack> packs = new LinkedList<>();
		public List<String> includes = new LinkedList<>();
		
	}
	
}
