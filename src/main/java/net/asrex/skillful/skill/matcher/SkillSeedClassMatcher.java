package net.asrex.skillful.skill.matcher;

import lombok.extern.log4j.Log4j2;

/**
 * A matcher that matches class names, and treats seeds as a possible matching
 * superclass of the event class.
 */
@Log4j2
public class SkillSeedClassMatcher implements SkillSeedMatcher {

	@Override
	public boolean matches(String eventToken, String seedToken) {
		try {
			Class eventClass = Class.forName(eventToken);
			Class seedClass = Class.forName(seedToken);
			
			return seedClass.isAssignableFrom(eventClass);
		} catch (ClassNotFoundException ex) {
			// ignore silently
			return false;
		}
	}
	
}
