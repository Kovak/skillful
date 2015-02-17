package net.asrex.skillful.mod.tinkers;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.mod.tinkers.effect.attack.TinkersAttackDefinition;
import net.asrex.skillful.mod.tinkers.effect.durability.TinkersDurabilityDefinition;
import net.asrex.skillful.mod.tinkers.effect.mining.TinkersMiningSpeedDefinition;
import net.asrex.skillful.mod.tinkers.effect.modifier.TinkersModifierDefinition;
import net.asrex.skillful.mod.tinkers.effect.repair.TinkersFreeRepairDefinition;
import static net.asrex.skillful.perk.PerkRegistry.register;

/**
 * 
 */
@Mod(modid = "skillful-tinkers",
		name = "skillful-tinkers",
		version = SkillfulTinkersMod.VERSION)
@Log4j2
public class SkillfulTinkersMod {
	
	public static final String VERSION = "0.1";
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		log.info("Initializing Skillful mod...");
		
		// register Skillful effect definitions
		register("tinkers_attack",       TinkersAttackDefinition.class);
		register("tinkers_durability",   TinkersDurabilityDefinition.class);
		register("tinkers_modifier",     TinkersModifierDefinition.class);
		register("tinkers_mining_speed", TinkersMiningSpeedDefinition.class);
		register("tinkers_free_repair",  TinkersFreeRepairDefinition.class);
	}
	
}
