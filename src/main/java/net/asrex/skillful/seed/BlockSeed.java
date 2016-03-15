package net.asrex.skillful.seed;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.asrex.skillful.skill.SkillSeeder;
import net.minecraft.block.Block;
import net.minecraftforge.event.world.BlockEvent;

/**
 * A seed that sends an event when a player performs some block action (break,
 * place).
 */
public class BlockSeed {
	
	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (event.getPlayer() == null) {
			return;
		}
		
		// server only
		if (event.world.isRemote) {
			return;
		}
		
		SkillSeeder.seed(
				event.getPlayer(),
				"block break "
						+ Block.blockRegistry.getNameForObject(event.state.getBlock()));
	}
	
	@SubscribeEvent
	public void onBlockPlace(BlockEvent.PlaceEvent event) {
		if (event.player == null) {
			return;
		}
		
		// server only
		if (event.world.isRemote) {
			return;
		}
		
		SkillSeeder.seed(
				event.player,
				"block place "
						+ Block.blockRegistry.getNameForObject(event.state.getBlock()));
	}
	
}
