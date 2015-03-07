package net.asrex.skillful;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.command.PerkCommand;
import net.asrex.skillful.command.PerkEffectCommand;
import net.asrex.skillful.command.PerkUICommand;
import net.asrex.skillful.command.SkillCommand;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.message.client.*;
import net.asrex.skillful.message.server.*;
import net.asrex.skillful.perk.PerkRegistry;
import net.asrex.skillful.requirement.RequirementRegistry;
import net.asrex.skillful.seed.*;
import net.asrex.skillful.skill.SkillRegistry;
import net.asrex.skillful.ui.ChatEventDisplay;
import net.asrex.skillful.ui.PerkUIManager;
import net.asrex.skillful.ui.PerkUIRegistry;
import net.asrex.skillful.ui.TextureRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "skillful",
		name = "skillful",
		version = SkillfulMod.VERSION)
@Log4j2
public class SkillfulMod {
	
	public static final String CONFIG_FOLDER_NAME = "skillful";
	public static final String VERSION = "0.1";
	
	public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry
			.INSTANCE
			.newSimpleChannel("skillful");
	
	private File configDir;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		configDir = new File(
				event.getModConfigurationDirectory(),
				CONFIG_FOLDER_NAME);
		
		if (!configDir.exists()) {
			configDir.mkdir();
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		log.info("Initializing Skillful mod...");
		
		// forge events
		MinecraftForge.EVENT_BUS.register(new ChatEventDisplay());
		MinecraftForge.EVENT_BUS.register(new BlockSeed());
		MinecraftForge.EVENT_BUS.register(new DeathSeed());
		MinecraftForge.EVENT_BUS.register(new CombatSeed());
		
		// FML events
		FMLCommonHandler.instance().bus().register(new PlayerSkillManager());
		FMLCommonHandler.instance().bus().register(new CraftingSeed());
		FMLCommonHandler.instance().bus().register(new TimerSeed());
		
		InfoLifecycleManager ilm = new InfoLifecycleManager();
		MinecraftForge.EVENT_BUS.register(ilm);
		FMLCommonHandler.instance().bus().register(ilm);
		
		PublicInfoLifecycleManager pilm = new PublicInfoLifecycleManager();
		MinecraftForge.EVENT_BUS.register(pilm);
		FMLCommonHandler.instance().bus().register(pilm);
		
		// messages: server -> client
		CHANNEL.registerMessage(
				SkillInfoHandler.class,
				SkillInfoMessage.class,
				0, Side.CLIENT);
		CHANNEL.registerMessage(
				EffectToggleHandler.class,
				EffectToggleMessage.class,
				1, Side.CLIENT);
		CHANNEL.registerMessage(
				PerkActivateErrorHandler.class,
				PerkActivateErrorMessage.class,
				2, Side.CLIENT);
		CHANNEL.registerMessage(
				PublicSkillInfoHandler.class,
				PublicSkillInfoMessage.class,
				4, Side.CLIENT);
		CHANNEL.registerMessage(
				ResetPlayerHandler.class,
				ResetPlayerMessage.class,
				5, Side.CLIENT);
		
		// messages: client -> server
		CHANNEL.registerMessage(
				PerkActivateHandler.class,
				PerkActivateMessage.class,
				3, Side.SERVER);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		RequirementRegistry.registerDefaults();
		
		try {
			SkillRegistry.init(configDir);
		} catch (IOException ex) {
			log.error("Could not read skills.yml", ex);
		}
		
		try {
			PerkRegistry.init(configDir);
		} catch (IOException ex) {
			log.error("Could not read perks.yml", ex);
		}
		
		try {
			PerkUIRegistry.init(configDir);
		} catch (IOException ex) {
			log.error("Could not read ui.yml", ex);
		}
	}
	
	@EventHandler
	@SideOnly(Side.CLIENT)
	public void initClient(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PerkUIManager());
	}
	
	@EventHandler
	@SideOnly(Side.CLIENT)
	public void postInitClient(FMLPostInitializationEvent event) {
		try {
			TextureRegistry.init(configDir);
		} catch (IOException ex) {
			log.error("Could not read textures.yml", ex);
		}
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		log.info("Initializing server-side events...");
		
		event.registerServerCommand(new SkillCommand());
		event.registerServerCommand(new PerkCommand());
		event.registerServerCommand(new PerkEffectCommand());
		event.registerServerCommand(new PerkUICommand());
	}
	
	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event) {
		// integrated server saving mechanics are weird
		// the dedicated server will fire a PlayerLoggedOutEvent when a player
		// logs out, but the integrated server will not
		//
		// this is the only way to get at the player object from the integated
		// server *before* the server is shutdown so we can store our player
		// data :(
		//
		// on the dedicated server, player data is stored in the
		// PlayerSkillManager in onPlayerLogout
		
		MinecraftServer server = MinecraftServer.getServer();
		
		// don't run on the dedicated server - it handles saving correctly
		//if (server.isDedicatedServer()) {
		//	return;
		//}
		
		for (String user : server.getAllUsernames()) {
			// func_152612_a() -> getPlayerForUsername
			EntityPlayerMP player = server
					.getConfigurationManager()
					.func_152612_a(user);
			
			PlayerSkillInfo info = PlayerSkillInfo.getInfo(player);
			
			// disable all active effects - they'll resume at next login
			for (Effect e : info.getActiveEffects()) {
				e.disable();
			}
			
			info.writeNBT(player.getEntityData());
			
			log.info("Wrote player NBT (integrated server): {}", user);
		}
		
		// reset for good measure
		PlayerSkillInfo.reset();
	}
	
}
