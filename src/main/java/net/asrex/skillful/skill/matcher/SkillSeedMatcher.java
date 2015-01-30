package net.asrex.skillful.skill.matcher;

/**
 * Defines a seed matcher, which can help match defined skill seeds to
 * specially-tagged 
 */
public interface SkillSeedMatcher {
	
	/**
	 * Determines if the given input event token string matches the skill seed.
	 * Note that the tag for this matcher will be stripped, leaving only the raw
	 * event token.
	 * @param eventToken the input event token to attempt to match
	 * @param seedToken the seed token to attempt to match against
	 * @return true if the event should seed the skill associated with the
	 *     seed token, false if not
	 */
	public boolean matches(String eventToken, String seedToken);
	
}
