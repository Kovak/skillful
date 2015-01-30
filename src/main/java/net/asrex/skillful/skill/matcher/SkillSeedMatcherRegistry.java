package net.asrex.skillful.skill.matcher;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

/**
 *
 */
@Log4j2
public class SkillSeedMatcherRegistry {
	
	private static SkillSeedMatcherRegistry instance;
	
	private final Map<String, SkillSeedMatcher> matchers;
	
	private SkillSeedMatcherRegistry() {
		matchers = new HashMap<>();
		
		initDefaultMatchers();
	}
	
	public static SkillSeedMatcherRegistry getInstance() {
		if (instance == null) {
			instance = new SkillSeedMatcherRegistry();
		}
		
		return instance;
	}

	
	private void initDefaultMatchers() {
		matchers.put("class", new SkillSeedClassMatcher());
	}
	
	public static void registerMatcher(
			String tagName, SkillSeedMatcher matcher) {
		getInstance().matchers.put(tagName, matcher);
	}
	
	public static boolean matches(
			String tag, String eventToken, String seedToken) {
		
		SkillSeedMatcher m = getInstance().matchers.get(tag);
		if (m == null) {
			log.warn(
					"Unknown matcher tag \"{}\", cannot match: {} <-> {}",
					tag, eventToken, seedToken);
			return false;
		}
		
		return m.matches(eventToken, seedToken);
	}
	
}
