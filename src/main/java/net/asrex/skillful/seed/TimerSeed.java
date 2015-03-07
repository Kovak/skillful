package net.asrex.skillful.seed;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import net.asrex.skillful.skill.SkillSeeder;

/**
 *
 */
public class TimerSeed {

	public static final int TICKS_SECOND = 20;
	public static final int TICKS_MINUTE = TICKS_SECOND * 60;
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.side != Side.SERVER) {
			return;
		}
		
		if (event.phase != Phase.END) {
			return;
		}
		
		if (event.player.ticksExisted % TICKS_SECOND == 0) {
			SkillSeeder.seed(event.player, "timer second");
		}
		
		if (event.player.ticksExisted % TICKS_MINUTE == 0) {
			SkillSeeder.seed(event.player, "timer minute");
		}
	}
	
}
