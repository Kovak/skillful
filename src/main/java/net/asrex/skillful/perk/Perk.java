package net.asrex.skillful.perk;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.exception.SkillfulException;
import net.minecraft.nbt.NBTTagCompound;

/**
 * A Perk is an earned or purchased group of {@link Effect} instances associated
 * with a particular player.
 * 
 * <p>Note that subclasses <b>must</b> have a no-args constructor!</p>
 */
@Log4j2
@ToString
public class Perk {
	
	@Getter @Setter
	private PerkDefinition definition;
	
	/**
	 * The {@code ticksExisted} value at the last activation of this perk or any
	 * of its effects.
	 * <p>TODO: this should be stored to player NBT at some point to prevent
	 * login/logout cooldown bypass abuse.</p>
	 */
	@Getter @Setter
	private int lastActivatedTick = -1;
	
	/**
	 * Constructs a new perk.
	 */
	public Perk() {
		
	}
	
	public Perk(PerkDefinition definition) {
		this.definition = definition;
	}
	
	public String getName() {
		return definition.getName();
	}
	
	public void readNBT(NBTTagCompound tag) {
		definition = PerkRegistry.getPerkDefinition(tag.getString("name"));
		if (definition == null) {
			throw new SkillfulException("Missing perk definition: "
					+ tag.getString("name"));
		}
		
		// TODO: figure out cooldown/lastActivatedTick
	}
	
	/**
	 * Serializes this {@code Perk} to the given NBT tag. Subclasses should
	 * override this method if additional persistent data needs to be stored;
	 * however, {@code super.writeNBT()} must be called to ensure deserializtion
	 * can occur.
	 * @param tag the tag to write data into
	 */
	public void writeNBT(NBTTagCompound tag) {
		tag.setString("class", getClass().getName());
		tag.setString("name", definition.getName());
		
		// TODO: figure out cooldown/lastActivatedTick
	}
	
	/**
	 * Determines if this perk has a cooldown, and if so, whether or not it has
	 * passed. If no cooldown exists, or if the perk has not been previously
	 * activated, {@code true} is also returned.
	 * @param currentTick the current player tick (see
	 *     {@code EntityPlayer.ticksExisted})
	 * @return true if the perk can be activated, false if not
	 */
	public boolean canActivate(int currentTick) {
		if (definition.getCooldownTicks() < 0) {
			return true;
		}
		
		// has it been activated yet?
		if (lastActivatedTick == -1) {
			return true;
		}
		
		int diff = currentTick - lastActivatedTick;
		return diff > definition.getCooldownTicks();
	}
	
	/**
	 * Determines the number of cooldown ticks remaining. If this perk has no
	 * cooldown, or if the cooldown has already passed, 0 is returned.
	 * @param currentTick the current player tick
	 * @return the number of ticks remaining in the cooldown, or 0
	 */
	public int getCooldownTicksRemaining(int currentTick) {
		if (definition.getCooldownTicks() < 0) {
			return 0;
		}
		
		// has it been activated yet?
		if (lastActivatedTick == -1) {
			return 0;
		}
		
		return definition.getCooldownTicks()
				- (currentTick - lastActivatedTick);
	}
	
	/**
	 * Gets the remaining cooldown time in seconds.
	 * @param currentTick the current player tick
	 * @return the number of seconds remaining in the cooldown
	 */
	public double getCooldownTimeRemaining(int currentTick) {
		return (double) getCooldownTicksRemaining(currentTick) / 20d;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(this.definition);
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
		final Perk other = (Perk) obj;
		if (!Objects.equals(this.definition, other.definition)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Deserializes a Perk from the given NBT tag. A {@code Perk} instance of
	 * the appropriate type will be returned.
	 * @param tag the tag to read perk data from
	 * @return a Perk instance, or null if no perk could be loaded
	 */
	public static Perk fromNBT(NBTTagCompound tag) {
		String clazz = tag.getString("class");
		try {
			Class<? extends Perk> c = Class.forName(clazz).asSubclass(Perk.class);
			
			Perk perk = c.newInstance();
			perk.readNBT(tag);
			
			return perk;
		} catch (ClassNotFoundException ex) {
			log.error("Could not load perk class: " + clazz, ex);
			return null;
		} catch (ClassCastException ex) {
			log.error("Invalid perk class: " + clazz, ex);
			return null;
		} catch (ReflectiveOperationException ex) {
			log.error("Could not instantiate perk: " + clazz, ex);
			return null;
		} catch (SkillfulException ex) {
			log.error("Could not read perk from NBT: " + clazz, ex);
			return null;
		}
	}
	
}
