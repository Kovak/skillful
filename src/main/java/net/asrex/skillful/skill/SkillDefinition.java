package net.asrex.skillful.skill;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.requirement.Requirement;
import net.asrex.skillful.skill.scaling.ScalingStrategy;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.StringUtils;

/**
 * A skill definition as read from a the configuration file.
 */
@Data
public class SkillDefinition {
	
	private String name;
	private String description;
	
	private ScalingStrategy scaling;
	
	private List<Requirement> requirements;
	private List<SkillSeed> seeds;

	public SkillDefinition() {
		requirements = new LinkedList<>();
		seeds = new LinkedList<>();
	}
	
	public boolean satisfiesRequirements(
			EntityPlayer player, PlayerSkillInfo info) {
		for (Requirement req : requirements) {
			if (!req.satisfied(player, info)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Determines if the given token string matches any seed string associated
	 * with this skill definition.
	 * @param event the event string to search for
	 * @return true if any seeds for this skill match the event string, false
	 *     otherwise
	 */
	public boolean matchesSeed(String event) {
		String[] eventTokens = StringUtils.split(event);
		
		for (SkillSeed seed : seeds) {
			if (seed.matchesEvent(eventTokens)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets the best seed value for the given event, or 0 if no seeds match the
	 * given event string. If multiple seeds match, the seed with the highest
	 * value is selected.
	 * @param event the event string to search for
	 * @return the best event value, or 0
	 */
	public int getEventValue(String event) {
		String[] eventTokens = StringUtils.split(event);
		int max = 0;
		
		for (SkillSeed seed : seeds) {
			if (seed.matchesEvent(eventTokens)) {
				if (seed.getValue() > max) {
					max = seed.getValue();
				}
			}
		}
		
		return max;
	}
	
	/**
	 * Gets the maximum progress for the given level, based on the current
	 * {@link ScalingStrategy}.
	 * @see ScalingStrategy#getProgressForLevel(int) 
	 * @param level the level to get the max progress for
	 * @return the maximum progress for the given level
	 */
	public int getProgressForLevel(int level) {
		return scaling.getProgressForLevel(level);
	}
	
	/**
	 * A basic factory for {@link Skill} objects. Subclasses could potentially
	 * override this method should they wish to create their own {@code Skill}
	 * derivatives.
	 * @return a new {@code Skill} object for this {@code SkillDefinition}.
	 */
	public Skill createSkill() {
		return new Skill(this);
	}
	
}
