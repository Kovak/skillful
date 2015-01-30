package net.asrex.skillful.seed;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.asrex.skillful.skill.SkillSeeder;
import net.minecraft.item.Item;

/**
 * A seed that fires an event when a player crafts an item.
 */
public class CraftingSeed {

	@SubscribeEvent
	public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
		if (event.player == null) {
			return;
		}
		
		// server only
		if (event.player.worldObj.isRemote) {
			return;
		}
		
		Item item = event.crafting.getItem();
		SkillSeeder.seed(
				event.player,
				"craft " + Item.itemRegistry.getNameForObject(item));
	}
	
}
