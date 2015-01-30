package net.asrex.skillful.effect;

import lombok.Data;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Event definitions provide a factory for {@link Effect} instances. They must
 * have a no-args constructor, and implement the {@link #doCreate(EntityPlayer)}
 * method.
 * 
 * <p>Event definitions can be configured at construction time in the config
 * file via Bean properties.</p>
 */
@Data
public abstract class EffectDefinition {
	
	private String name;
	
	/**
	 * Creates a new instance of the effect based on this definition. Any
	 * additional configured parameters may be set to fully configure the
	 * returned event.
	 * <p>Note that certain properties (such as {@code effectName} and
	 * {@code player} will be set by the wrapper method
	 * {@link #create(EntityPlayer)} and do not need to be set here.</p>
	 * @return a configured Effect instance
	 */
	protected abstract Effect doCreate();
	
	/**
	 * Creates a new {@code Effect} from this definition with a set player and
	 * effect name.
	 * @param perkName the name of the associated perk
	 * @param player the player to apply the effect to
	 * @return a new effect based on this definition
	 */
	public Effect create(String perkName, EntityPlayer player) {
		Effect ret = doCreate();
		ret.setDefinitionClass(this.getClass());
		ret.setPerkName(perkName);
		ret.setEffectName(name);
		ret.setPlayer(player);
		
		return ret;
	}
	
}
