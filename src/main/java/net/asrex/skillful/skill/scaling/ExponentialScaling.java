package net.asrex.skillful.skill.scaling;

import lombok.Data;

/**
 * An exponential scaling strategy.
 */
@Data
public class ExponentialScaling implements ScalingStrategy {

	private float base;
	private float baseCoef;
	private float expCoef;
	
	public ExponentialScaling() {
	}

	public ExponentialScaling(float base, float baseCoef, float expCoef) {
		this.base = base;
		this.baseCoef = baseCoef;
		this.expCoef = expCoef;
	}
	
	@Override
	public int getProgressForLevel(int level) {
		// a * b^(c * level)
		return (int) (baseCoef * Math.pow(base, expCoef * (double) level));
	}
	
}
