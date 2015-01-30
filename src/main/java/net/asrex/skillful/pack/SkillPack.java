package net.asrex.skillful.pack;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.asrex.skillful.skill.SkillDefinition;
import net.asrex.skillful.skill.SkillRegistry;

/**
 * Defines a skill pack, which allows for third-party mods to dynamically create
 * some number of custom {@link SkillDefinition} instances in bulk.
 * 
 * <p>Skill and perk packs are, like their associated individual definitions,
 * loaded at the request of the end-user from the configuration file by use of a
 * YAML {@code !tag}. Packs may accept an arbitrary number of configuration
 * parameters set by the configuration parser at load time using
 * JavaBean-compatible getter and setter methods.</p>
 * 
 * <p>{@link SkillPack} classes should be registered with the
 * {@link SkillRegistry} during FML initialization to be made available to
 * configuration files when they are parsed. Additionally, {@code SkillPack}
 * classes <b>must</b> have a no-args constructor to allow automatic
 * construction to occur.</p>
 * 
 * <p>Note that due to a requirement of the YAML parser (SnakeYAML), classes
 * loaded from {@code !tag} declarations must have at least one set property to
 * avoid an error. For packs that do not require any additional configuration,
 * users may specify {@code dummy: true} after the {@code !tag} declaration to
 * correctly instantiate the skill pack without any other configuration
 * parameters.</p>
 */
public abstract class SkillPack {
	
	/**
	 * An unused property used to work around an issue with the YAML parser.
	 * This property may be set in the configuration file to allow class
	 * instantiation without the use of any other properties.
	 */
	@Getter @Setter private boolean dummy;
	
	/**
	 * Called during skill initialization to request the list of skills added
	 * from this skill pack. Subclasses may return any desired list of (uniquely
	 * named) skills, and may optionally make use of user-specified parameters.
	 * @return a list of new {@link SkillDefinition} instances
	 */
	public abstract List<SkillDefinition> createSkillDefintions();
	
}
