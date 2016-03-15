package net.asrex.skillful.perk;

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
import net.asrex.skillful.PlayerNetworkHelper;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;
import net.asrex.skillful.effect.food.FoodOnKillEffectDefinition;
import net.asrex.skillful.effect.food.FoodOverTimeEffectDefinition;
import net.asrex.skillful.effect.glide.GlideEffectDefinition;
import net.asrex.skillful.effect.health.HealthEffectDefinition;
import net.asrex.skillful.effect.invisibility.InvisbilityEffectDefinition;
import net.asrex.skillful.effect.jump.JumpBoostEffectDefinition;
import net.asrex.skillful.effect.potion.PotionAbilityDefinition;
import net.asrex.skillful.effect.potion.PotionEffectDefinition;
import net.asrex.skillful.effect.resist.DamageResistEffectDefinition;
import net.asrex.skillful.effect.speed.SpeedEffectDefinition;
import net.asrex.skillful.effect.step.StepHeightEffectDefinition;
import net.asrex.skillful.pack.AllPerkPacksPerkPack;
import net.asrex.skillful.pack.PerkPack;
import net.asrex.skillful.perk.cost.PerkCost;
import net.asrex.skillful.perk.cost.PerkSkillCost;
import net.asrex.skillful.requirement.Requirement;
import net.asrex.skillful.skill.SkillRegistry;
import net.asrex.skillful.util.TextUtil;
import net.minecraft.entity.player.EntityPlayer;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * A central, static registry for all {@link PerkDefinition} instances.
 * 
 * <p>Note that this must be initialized via the {@link #init(java.io.File)}
 * method <em>after</em> the {@link SkillRegistry} has been initialized.</p>
 */
@Log4j2
public class PerkRegistry {
	
	public static final String PERKS_FILENAME = "perks.yml";
	
	private static PerkRegistry instance;
	
	private final Constructor constructor;
	
	private final Map<String, PerkDefinition> perks;
	
	private final Map<String, Class<? extends EffectDefinition>> effects;
	private final Map<String, Class<? extends PerkPack>> packs;
	
	private PerkRegistry() {
		constructor = new Constructor();
		
		// init predefined costs/packs types
		// (requirements defined in RequirementRegistry)
		map(PerkSkillCost.class, "!skill_cost");
		map(AllPerkPacksPerkPack.class, "!all");
		
		perks = new LinkedHashMap<>();
		effects = new LinkedHashMap<>();
		packs = new LinkedHashMap<>();
		
		initDefaultEffects();
	}
	
	private static PerkRegistry getInstance() {
		if (instance == null) {
			instance = new PerkRegistry();
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
	
	private void initDefaultEffects() {
		_register("glide", GlideEffectDefinition.class);
		_register("potion", PotionEffectDefinition.class);
		_register("potion_ability", PotionAbilityDefinition.class);
		_register("speed", SpeedEffectDefinition.class);
		_register("health", HealthEffectDefinition.class);
		_register("step_height", StepHeightEffectDefinition.class);
		_register("food_over_time", FoodOverTimeEffectDefinition.class);
		_register("food_on_kill", FoodOnKillEffectDefinition.class);
		_register("damage_resist", DamageResistEffectDefinition.class);
		_register("jump_boost", JumpBoostEffectDefinition.class);
		_register("invisibility", InvisbilityEffectDefinition.class);
	}
	
	private static void processConfigFile(File configDir, String path)
			throws IOException {
		PerkRegistry r = getInstance();
		
		Yaml yaml = new Yaml(r.constructor);
		PerkConfig conf = yaml.loadAs(
				new FileReader(new File(configDir, path)),
				PerkConfig.class);
		
		if (conf == null) {
			log.warn("Skill config '{}' was empty, ignoring...", path);
			return;
		}
		
		for (PerkDefinition def : conf.perks) {
			if (r.perks.containsKey(def.getName())) {
				log.warn("A perk with name \"{}\" has been defined multiple "
						+ "times. Additional declarations will be discarded.",
						def.getName());
				continue;
			}
			
			r.perks.put(def.getName(), def);
		}
		
		for (PerkPack pack : conf.packs) {
			for (PerkDefinition def : pack.createPerkDefintions()) {
				if (r.perks.containsKey(def.getName())) {
					log.warn("A perk with conflicting name \"{}\" is being "
							+ "added by the perk pack {}. It will be "
							+ "discarded.",
							def.getName(),
							pack.getClass().getName());
					continue;
				}
				
				r.perks.put(def.getName(), def);
			}
		}
		
		// process includes
		for (String include : conf.includes) {
			processConfigFile(configDir, include);
		}
	}
	
	public static void init(File configDir) throws IOException {
		processConfigFile(configDir, PERKS_FILENAME);
	}
	
	private void _register(
			String name, Class<? extends EffectDefinition> clazz) {
		effects.put(name, clazz);
		map(clazz, "!" + name);
	}
	
	private void _registerPack(String name, Class<? extends PerkPack> clazz) {
		packs.put(name, clazz);
		map(clazz, "!" + name);
	}
	
	/**
	 * Registers an effect with the given name. Names should be all lower-case
	 * and consist only of letters a-z and underscores ('_').
	 * 
	 * <p>Effect names are used in the perks configuration to instantiate
	 * instances of their associated {@link EffectDefinition} object. Note that
	 * they will implicitly be prefixed with '!' in configuration file.</p>
	 * 
	 * <p>Third-party effects, requirements, and costs should be registered
	 * at startup during the {@link FMLInitializationEvent}. Perk configuration
	 * will be read from disk during the {@link FMLPostInitializationEvent}, and
	 * any missing definitions will result in an error.</p>
	 * @param name the name of the effect
	 * @param clazz the EffectDefinition class to register
	 */
	public static void register(
			String name, Class<? extends EffectDefinition> clazz) {
		getInstance()._register(name, clazz);
	}
	
	/**
	 * Registers a perk requirement provider. Names should be all lower-case
	 * and consist only of letters a-z and underscores ('_').
	 * 
	 * <p>Requirements are used in the perks configuration to create instances
	 * of their associated {@link Requirement} object. Note that they will
	 * implicitly be prefixed with '!' in configuration file.</p>
	 * 
	 * <p>Third-party effects, requirements, and costs should be registered
	 * at startup during the {@link FMLInitializationEvent}. Perk configuration
	 * will be read from disk during the {@link FMLPostInitializationEvent}, and
	 * any missing definitions will result in an error.</p>
	 * @param name the name for the configuration tag to associate with the
	 *     given class
	 * @param requirementType the class to associate with the given tag name
	 */
	public static void registerRequirement(
			String name, Class<? extends Requirement> requirementType) {
		getInstance().map(requirementType, "!" + name);
	}
	
	/**
	 * Registers a perk cost provider. Names should be all lower-case and
	 * consist only of letters a-z and underscores ('_').
	 * 
	 * <p>Costs are used in the perks configuration to create instances of their
	 * associated {@link PerkCost} object. Note that names will implicitly be
	 * prefixed with a '!' character in configuration files.</p>
	 * 
	 * <p>Third-party effects, requirements, and costs should be registered
	 * at startup during the {@link FMLInitializationEvent}. Perk configuration
	 * will be read from disk during the {@link FMLPostInitializationEvent}, and
	 * any missing definitions will result in an error.</p>
	 * @param name the name for the configuration tag to associate with the
	 *     given class
	 * @param costType the class to associate with the given tag name
	 */
	public static void registerCost(
			String name, Class<? extends PerkCost> costType) {
		getInstance().map(costType, "!" + name);
	}
	
	/**
	 * Registers a perk pack provider with the given name. Names should be all
	 * lower-case and consist only of letters a-z and underscores ('_').
	 * 
	 * <p>Perk packs are used in the perk configuration to allow users to add
	 * preconfigured collections of perks in bulk. For additional information
	 * and requirements for perk pack implementations, see {@link PerkPack}.
	 * Note that names will implicitly be prefixed with '!' character in
	 * configuration file.</p>
	 * 
	 * <p>Third-party perk packs should be registered at startup during the
	 * {@link FMLInitializationEvent}. Perk configuration will be read from disk
	 * during the {@link FMLPostInitializationEvent}, and any missing
	 * definitions at this time will result in an error.</p>
	 * @param name the name for the configuration tag to associate with the
	 *     given class
	 * @param clazz the class of the perk pack to register
	 */
	public static void registerPack(
			String name, Class<? extends PerkPack> clazz) {
		getInstance()._registerPack(name, clazz);
	}
	
	public static Collection<Class<? extends PerkPack>> getPacks() {
		return getInstance().packs.values();
	}
	
	public static Collection<PerkDefinition> getPerkDefinitions() {
		return getInstance().perks.values();
	}
	
	public static PerkDefinition getPerkDefinition(String name) {
		return getInstance().perks.get(name);
	}
	
	public static PerkDefinition getPerkDefinitionFromSlug(String slug) {
		for (PerkDefinition def : getPerkDefinitions()) {
			if (TextUtil.slugify(def.getName()).equals(slug)) {
				return def;
			}
		}
		
		return null;
	}
	
	public static List<String> getPerkDefinitionSlugs() {
		List<String> ret = new LinkedList<>();
		
		for (PerkDefinition def : getPerkDefinitions()) {
			ret.add(TextUtil.slugify(def.getName()));
		}
		
		return ret;
	}
	
	/**
	 * Locates all perks definitions that the given player does not have but
	 * whose requirements are (optionally) fully met and costs can be
	 * (optionally) afforded.
	 * <p>Note that this method is currently usable on the server.</p>
	 * @param player the player for which to retrieve matching perks
	 * @param eligibleOnly if true, only include results whose requirements are
	 *     fully met
	 * @param affordableOnly if true, only include results the player can afford
	 * @return a list of matching perk definitions, if any exist
	 */
	public static List<PerkDefinition> getAvailablePerks(
			EntityPlayer player, boolean eligibleOnly, boolean affordableOnly) {
		// TODO: client-side support (needed for UI)
		PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
		
		List<PerkDefinition> ret = new LinkedList<>();
		
		for (PerkDefinition def : getPerkDefinitions()) {
			if (info.hasPerk(def.getName())) {
				continue;
			}
			
			if (eligibleOnly && !def.satisfiesRequirements(player, info)) {
				continue;
			}
			
			if (affordableOnly && !def.canAfford(player, info)) {
				continue;
			}
			
			ret.add(def);
		}
		
		return ret;
	}
	
	/**
	 * Attempts to create and initialize a new {@link Effect} instance for the
	 * given player using the given uniquely-identifying perk and effect name.
	 * <p>This method will perform the initialization steps as described in
	 * the class documentation for {@link Effect}, but will <b>not</b> enable
	 * the effect. It should first be added to the player, and then activated
	 * on both the client and server concurrently (see
	 * {@link PlayerNetworkHelper#addAndActivateEffect(EntityPlayer, String, String)}
	 * for an alternative method that will perform these remaining steps for
	 * you.</p>
	 * @param player the player to set as the parent for the effect
	 * @param perkName the name of the effect's parent perk, used to uniquely
	 *     identify the effect's {@link EffectDefinition}.
	 * @param effectName the name of the effect
	 * @return the effect if a matching effect definition could be found, or
	 *     null
	 */
	public static Effect createEffect(
			EntityPlayer player, String perkName, String effectName) {
		
		PerkDefinition def = getPerkDefinition(perkName);
		if (def == null) {
			return null;
		}

		for (EffectDefinition effectDef : def.getEffects()) {
			if (!effectDef.getName().equals(effectName)) {
				continue;
			}

			return effectDef.create(def.getName(), player);
		}
		
		return null;
	}
	
	/**
	 * An alternative implementation of
	 * {@link #createEffect(EntityPlayer, String, String)} that uses slugs for
	 * querying, suitable for usage with text commands. The {@code perkSlug}
	 * and {@code effectSlug} parameters are assumed already be in "slug" form;
	 * in particular, case sensitivity is enforced and the input strings should
	 * already be all-lowercase.
	 * @see TextUtil#slugify(java.lang.String) 
	 * @see #createEffect(EntityPlayer, String, String) 
	 * @param player the player to set as the parent for the effect
	 * @param perkSlug the perk name slug to query
	 * @param effectSlug the effect name slug to query
	 * @return the effect if a matching effect definition could be found, or
	 *     null
	 */
	public static Effect createEffectFromSlugs(
			EntityPlayer player, String perkSlug, String effectSlug) {
		
		for (PerkDefinition def : getPerkDefinitions()) {
			if (!TextUtil.slugify(def.getName()).equals(perkSlug)) {
				continue;
			}

			for (EffectDefinition effectDef : def.getEffects()) {
				if (!TextUtil.slugify(effectDef.getName()).equals(effectSlug)) {
					continue;
				}
				
				return effectDef.create(def.getName(), player);
			}
		}
		
		return null;
	}
	
	@Data
	public static class PerkConfig {
		
		public List<PerkDefinition> perks = new LinkedList<>();
		public List<PerkPack> packs = new LinkedList<>();
		public List<String> includes = new LinkedList<>();
		
	}
	
	public static void main(String[] args) throws IOException {
		init(new File("."));
		
		for (PerkDefinition p : instance.perks.values()) {
			System.out.println(p);
		}
	}
	
}
