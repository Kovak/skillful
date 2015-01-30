package net.asrex.skillful.skill.scaling;

import lombok.Data;

/**
 * A scaling strategy with a
 */
@Data
public class FixedScaling implements ScalingStrategy {
	
	private int value;
	
	public FixedScaling() {
		
	}
	
	@Override
	public int getProgressForLevel(int level) {
		return value;
	}
	
}
