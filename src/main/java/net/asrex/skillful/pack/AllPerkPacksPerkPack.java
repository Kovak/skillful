package net.asrex.skillful.pack;

import java.util.LinkedList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.perk.PerkDefinition;
import net.asrex.skillful.perk.PerkRegistry;

/**
 * A confusingly-named meta-perk-pack that includes all currently-defined perk
 * packs, using default parameters.
 */
@Log4j2
public class AllPerkPacksPerkPack extends PerkPack {

	@Override
	public List<PerkDefinition> createPerkDefintions() {
		List<PerkDefinition> ret = new LinkedList<>();
		
		for (Class<? extends PerkPack> pack : PerkRegistry.getPacks()) {
			if (getClass().isAssignableFrom(pack)) {
				// don't recurse D:
				continue;
			}
			
			try {
				PerkPack instance = pack.newInstance();
				ret.addAll(instance.createPerkDefintions());
			} catch (ReflectiveOperationException ex) {
				log.warn("Unable to create perk pack: " + pack.getName(), ex);
			}
		}
		
		return ret;
	}
	
}
