package net.asrex.skillful.skill.scaling;

/**
 * An abstract scaling strategy, used to determine the number of experience
 * points for a particular skill level.
 */
public interface ScalingStrategy {

	public int getProgressForLevel(int level);
	
}
