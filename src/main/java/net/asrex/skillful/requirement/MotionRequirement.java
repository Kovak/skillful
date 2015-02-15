package net.asrex.skillful.requirement;

import lombok.Data;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.util.Conditional;
import net.minecraft.entity.player.EntityPlayer;

/**
 * A requirement satisfied when the player is in motion.
 * 
 * This is currently broken 
 */
@Data
public class MotionRequirement implements Requirement {

	private Parameter x;
	private Parameter y;
	private Parameter z;
	private Parameter magnitude;
	
	@Override
	public boolean satisfied(EntityPlayer player, PlayerSkillInfo info) {
		if (magnitude == null) {
			if (x != null && !x.compare(player.motionX)) {
				return false;
			}
			
			if (y != null && !y.compare(player.motionY)) {
				return false;
			}
			
			if (z != null && !z.compare(player.motionZ)) {
				return false;
			}
			
			return true;
		} else {
			double m = Math.sqrt(
					player.motionX * player.motionX +
					player.motionY * player.motionY +
					player.motionZ * player.motionZ);
			
			System.out.println("x: " + player.motionX);
			System.out.println("y: " + player.motionY);
			System.out.println("z: " + player.motionZ);
			
			System.out.println("=== magnitude: " + m + " " + magnitude.is
					+ " " + magnitude.value + "? " + magnitude.compare(m));
			
			return magnitude.compare(m);
		}
	}

	@Override
	public String describe() {
		if (magnitude == null) {
			return String.format("Velocity: %s%s%s",
					x != null ? String.format("%s %s %.1f",
							x.text(),
							x.is.text,
							x.value) : "",
					y != null ? String.format("%s %s %.1f",
							y.text(),
							y.is.text,
							y.value) : "",
					z != null ? String.format("%s %s %.1f",
							z.text(),
							z.is.text,
							z.value) : "");
		} else {
			return String.format("%s %s %.1f",
					magnitude.text(),
					magnitude.is.text,
					magnitude.value);
		}
	}
	
	public static class Parameter {
		
		public double value;
		public boolean absolute = true;
		public Conditional is = Conditional.equal_to;
		
		public boolean compare(double actual) {
			double v = value;
			if (absolute) {
				if (value < 0) {
					v = -v;
				}
				
				if (actual < 0) {
					actual = -actual;
				}
			}
			return is.compare(actual, v);
		}
		
		public String text() {
			return absolute ? "Speed" : "Velocity";
		}
		
	}
	
}
