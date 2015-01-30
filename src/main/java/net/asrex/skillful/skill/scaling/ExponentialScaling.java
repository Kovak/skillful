package net.asrex.skillful.skill.scaling;

import lombok.Data;

/**
 * An exponential scaling strategy.
 */
@Data
public class ExponentialScaling implements ScalingStrategy {

	private float base;
	private float value;
	
	public ExponentialScaling() {
	}
	
	public ExponentialScaling(float base, float value) {
		this.base = base;
		this.value = value;
	}
	
	@Override
	public int getProgressForLevel(int level) {
		return (int) (base + level * Math.pow(value, level));
	}
	
}
