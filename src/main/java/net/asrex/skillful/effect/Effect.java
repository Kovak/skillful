package net.asrex.skillful.effect;

import cpw.mods.fml.common.FMLCommonHandler;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

/**
 * An Effect is an optionally persistent modifier associated with a player.
 * Effects apply to both client- and server-side code and may modify
 * functionality that exists in either context. Effects can be uniquely
 * identified based on their {@code perkName} and {@code effectName}, and
 * multiple instances of the same effect could potentially be active on the same
 * player (with different parameters).
 * 
 * <p>Effects are generally created by some {@link EffectDefinition} after a
 * player gains or activates a perk. After creation, the {@link #perkName},
 * {@link #effectName}, {@link #definitionClass}, and {@link #player} properties
 * should be set by whatever caller originally requested the
 * {@code EffectDefintion} to construct the {@code Effect}. Finally,
 * {@link #doEnable()} should be called to begin applying the effect to the
 * player of interest.</p>
 * 
 * <p>Instantiated {@code Effect} objects may subscribe to the Forge event bus
 * to handle any needed events from within their {@link #doEnable()} method. If
 * so, they should also make sure to unregister themselves when
 * {@link #doDisable()} is called.</p>
 * 
 * <p>Note that this class will be instantiated automatically when read from
 * player NBT. As such, subclasses <b>must</b> make a no-args constructor
 * available, and implement {@link #readNBT(NBTTagCompound)} and
 * {@link #writeNBT(NBTTagCompound)} to ensure proper serialization.
 * {@link EffectDefinition} instances may pass properties to {@code Effects} in
 * additional constructors, but {@code Effects} must maintain consistent
 * functionality when instantiated via the no-args constructor and a subsequent
 * {@link #readNBT(NBTTagCompound)} call. Other properties, such as
 * {@code perkName}, {@code effectName}, {@code definitionClass}, and
 * {@code player} are also guaranteed to be set before {@code doEnable()} is
 * executed.</p>
 */
@Log4j2
@ToString(of = {"perkName", "effectName", "enabled"})
public abstract class Effect {
	
	@Getter @Setter private String perkName;
	@Getter @Setter private String effectName;
	
	@Getter @Setter private Class<? extends EffectDefinition> definitionClass;
	
	/**
	 * An NBT-transient property indicating if {@link #enable()} has been called
	 * for this effect on the current side (client, server).
	 * <p>Note that this property should not be written to player NBT with other
	 * effect data.</p>
	 */
	@Getter private boolean enabled;
	
	/**
	 * The player to apply this {@code Effect} to. Note that this player
	 * instance will likely change when an effect has been stored to NBT and
	 * reinstantiated, particularly when the effect is enabled on the client.
	 */
	@Setter protected EntityPlayer player;
	
	public Effect() {
		enabled = false;
	}
	
	public abstract void doEnable();
	public abstract void doDisable();
	
	public void enable() {
		doEnable();
		
		enabled = true;
	}
	
	public void disable() {
		doDisable();
		
		enabled = false;
	}
	
	/**
	 * Toggles the current effect, ensuring that the resulting state is
	 * {@code state}. If the current state as returned by {@link #isEnabled()}
	 * is already {@code state}, nothing will happen.. That is, if
	 * {@code toggle(true)} is called, either the effect is currently disabled
	 * and will be enabled, or nothing will happen.
	 * @param state the new state to ensure
	 */
	public void toggle(boolean state) {
		if (state && !enabled) {
			enable();
		} else if (!state && enabled) {
			disable();
		}
	}
	
	/**
	 * Toggles the current effect. If the effect is currently enabled, it will
	 * be disabled; if currently disabled, it will be enabled. The new state as
	 * returned by {@link #isEnabled()} will be reflected.
	 */
	public void toggle() {
		if (enabled) {
			disable();
		} else {
			enable();
		}
	}
	
	/**
	 * Reads this effect from NBT. Subclasses may wish to store saved player
	 * data (such as a duration) that persists and updates as long as the effect
	 * remains active.
	 * <p>To ensure proper deserialization, subclasses <b>must</b> call
	 * {@code super.readNBT()} to save additional effect metadata.</p>
	 * @param tag the tag to read data from
	 */
	public void readNBT(NBTTagCompound tag) {
		// definitionClass set in fromNBT() to help catch possible reflection
		// errors more gracefully
		
		perkName = tag.getString("perkName");
		effectName = tag.getString("effectName");
	}
	
	public void writeNBT(NBTTagCompound tag) {
		tag.setString("class", getClass().getName());
		tag.setString("definitionClass", definitionClass.getName());
		
		tag.setString("perkName", perkName);
		tag.setString("effectName", effectName);
	}
	
	/**
	 * Deserializes an {@code Effect} from the given NBT tag. An {@code Effect}
	 * instance of the appropriate type will be returned. Note that the
	 * {@code player} field of the returned effect must be set before executing
	 * {@link Effect#enable()}.
	 * @param tag the tag to read from
	 * @return a deserialized Effect, or null if an error occurred and was
	 *     logged
	 */
	public static Effect fromNBT(NBTTagCompound tag) {
		String clazz = tag.getString("class");
		String defClazz = tag.getString("definitionClass");
		
		try {
			Class<? extends Effect> c = Class
					.forName(clazz)
					.asSubclass(Effect.class);
			
			Class<? extends EffectDefinition> dc = Class
					.forName(defClazz)
					.asSubclass(EffectDefinition.class);
			
			Effect effect = c.newInstance();
			effect.definitionClass = dc;
			
			effect.readNBT(tag);
			
			return effect;
		} catch (ClassNotFoundException ex) {
			log.error(
					"Could not load one or more effect classes: "
							+ clazz + " " + defClazz,
					ex);
			return null;
		} catch (ReflectiveOperationException ex) {
			log.error("Could not instantiate Effect class: " + clazz, ex);
			return null;
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 17 * hash + Objects.hashCode(this.perkName);
		hash = 17 * hash + Objects.hashCode(this.effectName);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Effect other = (Effect) obj;
		if (!Objects.equals(this.perkName, other.perkName)) {
			return false;
		}
		if (!Objects.equals(this.effectName, other.effectName)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Wrapper for registering on the MinecraftForge event bus
	 * ({@link MinecraftForge#EVENT_BUS}).
	 * @param o the object to register, such as {@code this}.
	 */
	protected void registerForge(Object o) {
		MinecraftForge.EVENT_BUS.register(o);
	}
	
	/**
	 * Wrapper for unregistering on the MinecraftForge event bus
	 * ({@link MinecraftForge#EVENT_BUS}). This catches any possible
	 * {@link NullPointerException} that may be caused by attempting to
	 * deregister an object that isn't already registered.
	 * @param o the object to deregister, such as {@code this}.
	 */
	protected void unregisterForge(Object o) {
		try {
			MinecraftForge.EVENT_BUS.unregister(o);
		} catch (NullPointerException e) {
			// :(
		}
	}
	
	/**
	 * Wrapper for registering on the FML event bus
	 * ({@link FMLCommonHandler#bus()}).
	 * @param o the object to register, such as {@code this}.
	 */
	protected void registerFML(Object o) {
		FMLCommonHandler.instance().bus().register(o);
	}
	
	/**
	 * Wrapper for unregistering on the FML event bus
	 * ({@link FMLCommonHandler#bus()}). This catches any possible
	 * {@link NullPointerException} that may be caused by attempting to
	 * deregister an object that isn't already registered.
	 * @param o the object to deregister, such as {@code this}.
	 */
	protected void unregisterFML(Object o) {
		try {
			FMLCommonHandler.instance().bus().unregister(o);
		} catch (NullPointerException e) {
			// :(
		}
	}
	
}
