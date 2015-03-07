package net.asrex.skillful.ui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.event.SkillfulClientInfoUpdatedEvent;
import net.asrex.skillful.perk.Perk;

/**
 * Manages the lifecycle of perk UIs.
 */
@Log4j2
@SideOnly(Side.CLIENT)
public class PerkUIManager {
	
	/**
	 * A map of PerkUI instances to their names.
	 */
	private final Map<String, PerkUI> uis;

	public PerkUIManager() {
		uis = new LinkedHashMap<>();
	}
	
	@SubscribeEvent
	public void onSkillInfoUpdated(SkillfulClientInfoUpdatedEvent event) {
		PlayerSkillInfo info = event.getInfo();
		
		Set<String> removed = new LinkedHashSet<>(uis.keySet());
		
		// process added and modified PerkUIData instances
		for (PerkUIData data : info.getPerkUIData()) {
			// this PerkUIData was not removed
			removed.remove(data.getName());
			
			if (!uis.containsKey(data.getName())) {
				// add and activate
				PerkUI ui = data.getDefinition().createUI();
				ui.enable();
				uis.put(data.getName(), ui);
				
				log.info("Added new UI: {} (type: {})",
						data.getName(),
						data.getDefinition().getName());
			}
		}
		
		// process removals
		for (String removedName : removed) {
			PerkUI ui = uis.get(removedName);
			ui.disable();
			
			uis.remove(removedName);
		}
		
		// update remaining (sets active perks)
		for (Entry<String, PerkUI> entry : uis.entrySet()) {
			PerkUI ui = entry.getValue();
			PerkUIData data = info.getPerkUIData(entry.getKey());
			
			// clear old perks
			ui.clearPerks();
			
			for (Entry<Integer, String> perkData : data.getPerks().entrySet()) {
				Perk perk = info.getPerk(perkData.getValue());
				if (perk == null) {
					log.warn("Could not add missing perk to UI: {}",
							perkData.getValue());
					continue;
				}
				
				ui.setPerk(perkData.getKey(), perk);
			}
			
		}
	}
	
}
