package net.asrex.skillful.pack;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.asrex.skillful.perk.PerkDefinition;
import net.asrex.skillful.perk.PerkRegistry;

/**
 * Defines a perk pack, which allows for third-party mods to dynamically create
 * some number of custom {@link PerkDefinition} instances in bulk.
 * 
 * <p>Skill and perk packs are, like their associated individual definitions,
 * loaded at the request of the end-user from the configuration file by use of a
 * YAML {@code !tag}. Packs may accept an arbitrary number of configuration
 * parameters set by the configuration parser at load time using
 * JavaBean-compatible getter and setter methods.</p>
 * 
 * <p>{@link PerkPack} classes should be registered with the
 * {@link PerkRegistry} during FML initialization to be made available to
 * configuration files when they are parsed. Additionally, {@code PerkPack}
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
public abstract class PerkPack {
	
	/**
	 * An unused property used to work around an issue with the YAML parser.
	 * This property may be set in the configuration file to allow class
	 * instantiation without the use of any other properties.
	 */
	@Getter @Setter private boolean dummy;
	
	/**
	 * Called during perk initialization to request the list of perks added
	 * from this perk pack. These perks definitions will be appended to the
	 * global list of perk definitions within the {@link PerkRegistry} and made
	 * available for user purchase.
	 * 
	 * <p>Subclasses may return any desired list of (uniquely named) skills, and
	 * may optionally make use of user-specified parameters when constructing
	 * the definitions.</p>
	 * @return a list of new {@link PerkDefinition} instances
	 */
	public abstract List<PerkDefinition> createPerkDefintions();
	
}
