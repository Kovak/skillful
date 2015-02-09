package net.asrex.skillful.ui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.ui.actionbar.ActionBarDefinition;
import net.asrex.skillful.util.TextUtil;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * A central registry for all {@link PerkUIDefinition} classes and associated
 * configuration data. Other mods may register their own UI implementations
 * here using 
 * 
 * <p>Note that </p>
 */
@Log4j2
@SideOnly(Side.CLIENT)
public class PerkUIRegistry {
	
	public static final String UI_FILENAME = "ui.yml";
	
	private static PerkUIRegistry instance;
	
	private final Constructor constructor;
	private final Map<String, PerkUIDefinition> uiDefinitions;
	
	private final List<String> defaultDefinitions;
	
	private PerkUIRegistry() {
		constructor = new Constructor();
		
		map(ActionBarDefinition.class, "!action_bar");
		
		uiDefinitions = new LinkedHashMap<>();
		defaultDefinitions = new LinkedList<>();
	}

	private static PerkUIRegistry getInstance() {
		if (instance == null) {
			instance = new PerkUIRegistry();
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
	
	public static void init(File configDir) throws IOException {
		PerkUIRegistry r = getInstance();
		
		Yaml yaml = new Yaml(r.constructor);
		PerkUIConfig conf = yaml.loadAs(
				new FileReader(new File(configDir, UI_FILENAME)),
				PerkUIConfig.class);
		
		for (PerkUIDefinition def : conf.templates) {
			if (r.uiDefinitions.containsKey(def.getName())) {
				log.warn("A UI template with name \"{}\" has been defined "
						+ "multiple times. Additional declarations will be "
						+ "discarded.",
						def.getName());
				continue;
			}
			
			r.uiDefinitions.put(def.getName(), def);
		}
		
		r.defaultDefinitions.addAll(conf.defaults);
	}
	
	public static PerkUIDefinition getDefinition(String name) {
		return getInstance().uiDefinitions.get(name);
	}
	
	public static PerkUIDefinition getDefinitionFromSlug(String slug) {
		for (PerkUIDefinition def : getPerkUIDefinitions()) {
			if (TextUtil.slugify(def.getName()).equals(slug)) {
				return def;
			}
		}
		
		return null;
	}
	
	public static Collection<PerkUIDefinition> getPerkUIDefinitions() {
		return getInstance().uiDefinitions.values();
	}
	
	public static List<String> getPerkUIDefinitionSlugs() {
		List<String> ret = new LinkedList<>();
		
		for (String name : getInstance().uiDefinitions.keySet()) {
			ret.add(TextUtil.slugify(name));
		}
		
		return ret;
	}
	
	/**
	 * Registers a perk UI implementation with the given name. Names should be
	 * all lower-case and consist only of letters a-z and underscores ('_').
	 * 
	 * <p>Third-party UI classes should be registered at startup during the
	 * {@link FMLInitializationEvent}. Skill configuration will be read from
	 * disk during the {@link FMLPostInitializationEvent}, and any missing
	 * definitions at this time will result in an error.</p>
	 * @param name the name of the UI to register
	 * @param clazz the class of the implementation to add
	 */
	public static void registerUi(
			String name, Class<? extends PerkUIDefinition> clazz) {
		getInstance().map(clazz, "!" + name);
	}
	
	public static class PerkUIConfig {
		
		public List<PerkUIDefinition> templates = new LinkedList<>();
		public List<String> defaults = new LinkedList<>();
		
	}
	
}
