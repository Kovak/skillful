package net.asrex.skillful.skill.scaling;

import lombok.Data;

/**
 * A linear scaling strategy.
 */
@Data
public class LinearScaling implements ScalingStrategy {
	private int value;
	private float base;
	private float multiplier;
	
	public LinearScaling() {
	}
	
	public LinearScaling(float base, float multiplier) {
		this.base = base;
		this.multiplier = multiplier;
	}
	
	@Override
	public int getProgressForLevel(int level) {
		return (int) (base + multiplier * level);
	}
	
}
