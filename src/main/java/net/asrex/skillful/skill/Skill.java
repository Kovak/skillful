package net.asrex.skillful.skill;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.asrex.skillful.perk.Perk;
import net.asrex.skillful.util.Log;
import net.minecraft.nbt.NBTTagCompound;

/**
 * A Skill is some named measure of progress in some area. Skills have levels
 * that increment after a certain amount of progress has been made in the skill.
 * These levels can then be used to enable or purchase a {@link Perk}.
 * 
 * <p>Skills may be <em>seeded</em> by various world events. Seeding increments
 * the progress by a certain amount (as specified by whatever causes the seeding
 * event).</p>
 * 
 * <p>The {@code Skill} class itself represents the player-stored data for an
 * associated {@link SkillDefinition}. {@code SkillDefinitions} are
 * user-configurable objects that control data such as events that seed the
 * skill and the level scaling method.</p>
 */
@ToString
public class Skill {

	private static final Logger log = Log.get(Skill.class);
	
	@Getter private int level;
	
	/**
	 * The amount of progress to the next level of this skill.
	 */
	@Getter private int progress;
	
	/**
	 * The amount of progress needed for this skill to progress to the next
	 * level. This may vary depending on skill level or other factors; see
	 * {@link #progressForLevel(int)} and {@link #addLevels(int)}.
	 */
	@Getter private int maxProgress;
	
	@Getter @Setter private SkillDefinition definition;
	
	/**
	 * Initializes an empty {@code Skill}.
	 */
	public Skill() {
		level = 0;

		progress = 0;
		maxProgress = 1;
	}
	
	public Skill(SkillDefinition definition) {
		this.definition = definition;
		
		level = 0;

		progress = 0;
		maxProgress = definition.getProgressForLevel(level);
	}
	
	public String getName() {
		return definition.getName();
	}
	
	/**
	 * Adds the given number of levels to this skill. Progress within the
	 * current level will be reset to zero.
	 * @see #progressForLevel(int) 
	 * @param levels the number of levels to add
	 */
	public void addLevels(int levels) {
		level += levels;

		progress = 0;
		maxProgress = definition.getProgressForLevel(level);
	}
	
	/**
	 * Adds a single level to this skill, reseting progress.
	 * @see #addLevels(int)
	 */
	public void addLevel() {
		addLevels(1);
	}
	
	/**
	 * Gets the current progress within the level as a floating point number
	 * such that {@code 0 <= x <= 1}.
	 * @see #getProgress() 
	 * @return the progress in the level as a float
	 */
	public float getProgressPercent() {
		return (float) progress / (float) maxProgress;
	}
	
	/**
	 * Adds the given amount of progress to this skill. If the amount added is
	 * enough to cause a rollover, {@link #addLevel()} will be called.
	 * <p>Note that for a large enough value of {@code diff}, multiple level
	 * rollovers may occur.
	 * @param diff the amount of progress to add
	 * @return true if the added progress caused this skill's level to increase,
	 *     false if not
	 */
	public boolean addProgress(int diff) {
		progress += diff;
		
		boolean levelUp = false;
		
		int overflow = progress - maxProgress;
		if (overflow >= 0) {
			addLevel();
			
			levelUp = true;
			
			if (overflow > 0) {
				addProgress(overflow);
			}
		}
		
		return levelUp;
	}
	
	/**
	 * Seeds this skill, causing the progress in the level to increase by the
	 * amount defined for the seed event.
	 * @param event the seed event
	 * @return true if the added progress caused this skill's level to increase,
	 *     false if not
	 */
	public boolean seed(String event) {
		return addProgress(definition.getEventValue(event));
	}
	
	/**
	 * Deserializes this {@code Skill} from NBT. Subclasses should override this
	 * method if additional persistent data needs to be stored; however,
	 * {@code super.writeNBT()} must be called to ensure deserializtion can
	 * occur.
	 * @param tag the tag to read data from
	 */
	public void readNBT(NBTTagCompound tag) {
		definition = SkillRegistry.getDefinition(tag.getString("name"));
		level = tag.getInteger("level");
		progress = tag.getInteger("progress");
		maxProgress = tag.getInteger("maxProgress");
	}
	
	/**
	 * Serializes this {@code Skill} to the given NBT tag. Subclasses should
	 * override this method if additional persistent data needs to be stored;
	 * however, {@code super.writeNBT()} must be called to ensure deserializtion
	 * can occur.
	 * @param tag the tag to write data into
	 */
	public void writeNBT(NBTTagCompound tag) {
		tag.setString("class", getClass().getName());
		
		tag.setString("name", definition.getName());
		tag.setInteger("level", level);
		tag.setInteger("progress", progress);
		tag.setInteger("maxProgress", maxProgress);
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + Objects.hashCode(this.definition);
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
		final Skill other = (Skill) obj;
		if (!Objects.equals(this.definition, other.definition)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Deserializes a Perk from the given NBT tag. A {@code Skill} instance of
	 * the appropriate type will be returned.
	 * @param tag the tag to read skill data from
	 * @return a {@code Skill} instance, or null if no skill could be loaded
	 *     (with a logged error)
	 */
	public static Skill fromNBT(NBTTagCompound tag) {
		String clazz = tag.getString("class");
		try {
			Class<? extends Skill> c =
					Class.forName(clazz).asSubclass(Skill.class);
			
			Skill skill = c.newInstance();
			skill.readNBT(tag);
			
			return skill;
		} catch (ClassNotFoundException ex) {
			log.log(Level.SEVERE, "Could not load skill class: " + clazz, ex);
			return null;
		} catch (ClassCastException ex) {
			log.log(Level.SEVERE, "Invalid skill class: " + clazz, ex);
			return null;
		} catch (ReflectiveOperationException ex) {
			log.log(Level.SEVERE, "Could not instantiate skill: " + clazz, ex);
			return null;
		}
	}
	
}
