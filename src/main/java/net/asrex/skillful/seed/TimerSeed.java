package net.asrex.skillful.seed;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
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
