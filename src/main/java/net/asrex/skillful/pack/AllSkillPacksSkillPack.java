package net.asrex.skillful.pack;

import java.util.LinkedList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.skill.SkillDefinition;
import net.asrex.skillful.skill.SkillRegistry;

/**
 * A confusingly-named meta-skill-pack that includes all currently defined skill
 * packs, with default parameters.
 */
@Log4j2
public class AllSkillPacksSkillPack extends SkillPack {

	@Override
	public List<SkillDefinition> createSkillDefintions() {
		List<SkillDefinition> ret = new LinkedList<>();
		
		for (Class<? extends SkillPack> pack : SkillRegistry.getPacks()) {
			if (getClass().isAssignableFrom(pack)) {
				// don't recurse D:
				continue;
			}
			
			try {
				SkillPack instance = pack.newInstance();
				ret.addAll(instance.createSkillDefintions());
			} catch (ReflectiveOperationException ex) {
				log.warn("Unable to create skill pack: " + pack.getName(), ex);
			}
		}
		
		return ret;
	}
	
}
