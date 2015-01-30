package net.asrex.skillful.skill;

import lombok.Data;
import net.asrex.skillful.skill.matcher.SkillSeedMatcherRegistry;
import org.apache.commons.lang3.StringUtils;

/**
 * A skill seed tuple, consisting of an event name and a point value. Seeds may
 * match 
 */
@Data
public class SkillSeed {
	
	private String event;
	private int value;
	
	public boolean matchesEvent(String[] eventTokens) {
		String[] seedTokens = StringUtils.split(event);

		if (seedTokens.length > eventTokens.length) {
			return false;
		}

		for (int i = 0; i < seedTokens.length; i++) {
			if (!matchToken(eventTokens[i], seedTokens[i])) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean matchToken(String eventToken, String seedToken) {
		if (eventToken.startsWith("!") && eventToken.contains(":")) {
			String tag = eventToken.substring(1, eventToken.indexOf(":"));
			
			String token = eventToken.substring(
					eventToken.indexOf(":") + 1,
					eventToken.length());
			
			return SkillSeedMatcherRegistry.matches(tag, token, seedToken);
		}
		
		return seedToken.equalsIgnoreCase(eventToken);
	}
	
}
