package net.asrex.skillful;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.EffectDefinition;
import net.asrex.skillful.effect.PublicEffect;
import net.asrex.skillful.perk.Perk;
import net.asrex.skillful.perk.PerkDefinition;
import net.asrex.skillful.skill.Skill;
import net.asrex.skillful.skill.SkillDefinition;
import net.asrex.skillful.skill.SkillRegistry;
import net.asrex.skillful.ui.PerkUIData;
import net.asrex.skillful.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/**
 * Stores player skill and perk data, and handles reading and writing to the
 * player NBT.
 */
@ToString(of = {"player"})
@Log4j2
public class PlayerSkillInfo  {

	public static final String TAG_NAME = "skillful";
	
	private static Map<UUID, PlayerSkillInfo> playerMap;
	
	@SideOnly(Side.CLIENT)
	private static PlayerSkillInfo clientInfo;
	
	/**
	 * The UUID of the current player.
	 */
	@Getter
	private final UUID playerId;
	
	/**
	 * The current player instance. This may change after certain events, e.g.
	 * player death, that cause the creation of a new player entity.
	 */
	@Getter @Setter
	private EntityPlayer player;
	
	private final Map<String, Skill> skills;
	private final Map<String, Perk> perks;
	private List<Effect> activeEffects;
	
	private final Map<String, PerkUIData> uiData;
	
	/**
	 * Information other players should know about this player.
	 */
	@Getter
	private final PublicPlayerSkillInfo publicSkillInfo;
	
	/**
	 * Information this player knows about other players. This is not read from
	 * player NBT but will be updated dynamically to reflect the current
	 * environment, including other players currently connected and any active
	 * public effects.
	 */
	private final Map<UUID, PublicPlayerSkillInfo> otherPlayerInfo;
	
	public PlayerSkillInfo(EntityPlayer player) {
		this.player = player;
		this.playerId = player.getGameProfile().getId();
		
		skills = new LinkedHashMap<>();
		perks = new LinkedHashMap<>();
		
		activeEffects = new LinkedList<>();
		uiData = new LinkedHashMap<>();
		
		// currentPlayer == targetPlayer for player's own public info
		publicSkillInfo = new PublicPlayerSkillInfo(player, player);
		otherPlayerInfo = new LinkedHashMap<>();
	}
	
	public Collection<Skill> getSkills() {
		return skills.values();
	}
	
	public Skill getSkill(String skillType) {
		return skills.get(skillType);
	}
	
	public void removeSkill(String skillType) {
		skills.remove(skillType);
	}
	
	public void removeSkill(Skill skill) {
		skills.remove(skill.getName());
	}
	
	public void removeSkill(SkillDefinition def) {
		skills.remove(def.getName());
	}
	
	public boolean hasSkill(String skillType) {
		return skills.containsKey(skillType);
	}
	
	public boolean hasSkill(SkillDefinition def) {
		return skills.containsKey(def.getName());
	}
	
	/**
	 * Gets all skills for this player that should be seeded by the given event.
	 * @param event the event to look up
	 * @return a list of matching skills for the given event
	 */
	public List<Skill> getSkillsForEvent(String event) {
		List<Skill> ret = new LinkedList<>();
		
		for (Skill s : skills.values()) {
			if (s.getDefinition().matchesSeed(event)) {
				ret.add(s);
			}
		}
		
		return ret;
	}
	
	/**
	 * Updates skills for the player, adding any missing {@link Skill}
	 * definitions that may have been added since this {@code PlayerSkillInfo}
	 * instance was created.
	 * 
	 * <p>Note that old skills are currently <em>not</em> removed from the
	 * player if they are removed from the configuration file.</p>
	 * @return true if a skill was added or removed from the player, false if no
	 *     changes were made
	 */
	public boolean updateSkills() {
		boolean updated = false;
		
		// look for new skills to add, and find skills to prune
		Set<String> oldSkills = new HashSet<>(skills.keySet());
		for (SkillDefinition def : SkillRegistry.getSkillDefinitions()) {
			oldSkills.remove(def.getName());
			
			if (!hasSkill(def)) {
				skills.put(def.getName(), def.createSkill());
				updated = true;
				
				// TODO: consider checking for skill definition changes here
				// TODO: also implicit perks should be applied automatically (?)
				// (but only on the server)
			}
		}
		
		// prune old skills
		for (String skillName : oldSkills) {
			skills.remove(skillName);
		}
		
		return updated;
	}
	
	/**
	 * Updates the given skill in this {@code PlayerSkillInfo} if a matching
	 * skill already exists. If not, a new skill is created and added based on
	 * the tag.
	 * @param tag the tag to update or create a skill from
	 */
	private Skill updateSkill(NBTTagCompound tag) {
		Skill skill = skills.get(tag.getString("name"));
		if (skill == null) {
			skill = Skill.fromNBT(tag);
			if (skill != null) {
				skills.put(skill.getName(), skill); // TODO: notify?
			} else {
				log.warn("Skill could not be read from NBT, "
						+ "removing from player: {}",
						tag);
			}
		} else {
			skill.readNBT(tag); // TODO: notify? update auto perks?
		}
		
		return skill;
	}
	
	public Collection<Perk> getPerks() {
		return perks.values();
	}
	
	public Perk getPerk(String perkType) {
		return perks.get(perkType);
	}
	
	public void removePerk(String perkType) {
		perks.remove(perkType);
	}
	
	public void removePerk(Perk perk) {
		removePerk(perk.getName());
	}
	
	public boolean hasPerk(String perkType) {
		return perks.containsKey(perkType);
	}
	
	public boolean hasPerk(PerkDefinition def) {
		return perks.containsKey(def.getName());
	}
	
	public void addPerk(Perk perk) {
		perks.put(perk.getName(), perk);
	}
	
	/**
	 * Locates all perks owned by this player that would be triggered by the
	 * given event.
	 * @see PerkDefinition#matchesTrigger(java.lang.String) 
	 * @param eventName the event string to match against
	 * @return a list of matching perks, if any
	 */
	public List<Perk> getTriggeredPerksForEvent(String eventName) {
		List<Perk> ret = new LinkedList<>();
		
		for (Perk perk : perks.values()) {
			if (perk.getDefinition().matchesTrigger(eventName)) {
				ret.add(perk);
			}
		}
		
		return ret;
	}
	
	/**
	 * Locates all perks owned by this player that would be interrupted by the
	 * given event.
	 * @see PerkDefinition#matchesInterrupt(java.lang.String) 
	 * @param eventName the event string to match against
	 * @return a list of matching perks, if any
	 */
	public List<Perk> getInterruptedPerksForEvent(String eventName) {
		List<Perk> ret = new LinkedList<>();
		
		for (Perk perk : perks.values()) {
			if (perk.getDefinition().matchesInterrupt(eventName)) {
				ret.add(perk);
			}
		}
		
		return ret;
	}
	
	/**
	 * Updates the given perk in this {@code PlayerSkillInfo} if a matching perk
	 * already exists. If not, a new perk is created and added based on the tag.
	 * @param tag the tag to update or create a perk from
	 * @param the perk updated, or null if error
	 */
	private Perk updatePerk(NBTTagCompound tag) {
		Perk p = perks.get(tag.getString("name"));
		if (p == null) {
			p = Perk.fromNBT(tag);
			if (p != null) {
				perks.put(p.getName(), p);
			} else {
				log.warn("Perk could not be updated from NBT, "
						+ "removing from player: {}",
						tag);
			}
		} else {
			p.readNBT(tag);
		}
		
		return p;
	}

	public Collection<PerkUIData> getPerkUIData() {
		return uiData.values();
	}
	
	public PerkUIData getPerkUIData(String name) {
		return uiData.get(name);
	}
	
	public void addPerkUIData(PerkUIData data) {
		uiData.put(data.getName(), data);
	}
	
	public void removePerkUIData(String name) {
		uiData.remove(name);
	}
	
	private PerkUIData updatePerkUIData(NBTTagCompound tag) {
		PerkUIData data = uiData.get(tag.getString("name"));
		if (data == null) {
			data = PerkUIData.fromNBT(tag);
			if (data != null) {
				uiData.put(data.getName(), data);
			} else {
				log.warn("UI data could not be loaded from NBT: {}", tag);
			}
		} else {
			data.readNBT(tag);
		}
		
		return data;
	}
	
	public List<Effect> getActiveEffects() {
		return activeEffects;
	}
	
	/**
	 * Generates a list of all active effects of the given type. Note that this
	 * only compares effect class, and will not account for different instances
	 * of the same effect class with different configured parameters.
	 * @param <T> the effect type
	 * @param effectClass the effect class to search for
	 * @return a list of effects of the given type
	 */
	public <T extends Effect> List<T> getActiveEffects(Class<T> effectClass) {
		List<T> ret = new LinkedList<>();
		
		for (Effect e : activeEffects) {
			if (effectClass.isInstance(e)) {
				ret.add((T) e);
			}
		}
		
		return ret;
	}
	
	/**
	 * Determines if the player has any active effects for the given perk.
	 * @param perkName the perk to search for
	 * @return true if at least one effect of the given perk is active, false
	 *     otherwise
	 */
	public boolean hasActiveEffects(String perkName) {
		for (Effect e : activeEffects) {
			if (e.getPerkName().equals(perkName)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets all active effects provided by the given perk.
	 * @param perkName the parent perk for which to query on active effects
	 * @return a list of matching active effects
	 */
	public List<Effect> getActiveEffects(String perkName) {
		List<Effect> ret = new LinkedList<>();
		
		for (Effect e : activeEffects) {
			if (e.getPerkName().equals(perkName)) {
				ret.add(e);
			}
		}
		
		return ret;
	}
	
	public List<Effect> getActiveEffectsOfType(
			Class<? extends EffectDefinition> definitionClass) {
		List<Effect> ret = new LinkedList<>();
		
		for (Effect e : activeEffects) {
			if (definitionClass.isAssignableFrom(e.getDefinitionClass())) {
				ret.add(e);
			}
		}
		
		return ret;
	}
	
	/**
	 * Determines if this player currently has an active effect of the same
	 * class as the given effect definition. Note that this only compares
	 * definition class, and will not account for different instances of the
	 * same effect class with different configured parameters.
	 * @param definitionClass the definition to search for
	 * @return true if an active effect of the given type exists, false if not
	 */
	public boolean hasActiveEffect(
			Class<? extends EffectDefinition> definitionClass) {
		for (Effect e : activeEffects) {
			if (definitionClass.isAssignableFrom(e.getDefinitionClass())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets the unique effect with the given parameters, if any matching effect
	 * is currently active on this player.
	 * @param perkName the perk name
	 * @param effectName the effect name
	 * @return the matching effect if a match is found, otherwise null
	 */
	public Effect getActiveEffect(String perkName, String effectName) {
		for (Effect e : activeEffects) {
			if (e.getPerkName().equals(perkName)
					&& e.getEffectName().equals(effectName)) {
				return e;
			}
		}
		
		return null;
	}
	
	public boolean hasActiveEffect(String perkName, String effectName) {
		return getActiveEffect(perkName, effectName) != null;
	}
	
	/**
	 * An alternative implementation of {@link #getActiveEffect(String, String)}
	 * that uses slugs for querying, suitable for usage with text commands. The
	 * {@code perkSlug} and {@code effectSlug} parameters are assumed already be
	 * in "slug" form; in particular, case sensitivity is enforced and the input
	 * strings should already be all-lowercase.
	 * @see TextUtil#slugify(java.lang.String) 
	 * @see #getActiveEffect(java.lang.String, java.lang.String) 
	 * @param perkSlug the perk name slug to query for
	 * @param effectSlug the effect name slug to query for
	 * @return a matching effect if any exists, or null
	 */
	public Effect getActiveEffectFromSlugs(String perkSlug, String effectSlug) {
		for (Effect e : activeEffects) {
			if (TextUtil.slugify(e.getPerkName()).equals(perkSlug)
					&& TextUtil.slugify(e.getEffectName()).equals(effectSlug)) {
				return e;
			}
		}
		
		return null;
	}
	
	public void removeActiveEffect(Effect effect) {
		activeEffects.remove(effect);
	}
	
	/**
	 * Adds the given effect to the player's list of active effects. Note that
	 * this will not enable the effect or propagate the changes to the client;
	 * see
	 * {@link PlayerNetworkHelper#addAndActivateEffect(EntityPlayer, Effect)}
	 * for an alternative that apply these changes.
	 * @param effect the effect to add
	 */
	public void addActiveEffect(Effect effect) {
		activeEffects.add(effect);
	}
	
	/**
	 * Updates the active effect represented by the given tag if it currently
	 * exists in this {@code PlayerSkillInfo}. If not, a new effect is added and
	 * marked as active.
	 * @param tag the tag to update or add
	 * @return the effect updated, or null if error
	 */
	private Effect updateActiveEffect(NBTTagCompound tag) {
		Effect e = getActiveEffect(
				tag.getString("perkName"),
				tag.getString("effectName"));
		if (e == null) {
			e = Effect.fromNBT(tag);
			if (e != null) {
				e.setPlayer(player);
				
				activeEffects.add(e);
			} else {
				log.warn("Could update effect from NBT: {}", tag);
			}
		} else {
			e.readNBT(tag);
		}
		
		return e;
	}
	
	/**
	 * Attempts to retrieve the effect definition with the given name, returning
	 * {@code null} if a matching perk does not exist, or if no matching effect
	 * exists within the perk.
	 * 
	 * <p>As Abilities are ephemeral and not stored in {@link #activeEffects},
	 * they can be checked using this method to ensure the player can execute
	 * them.</p>
	 * @param perkName the perk containing the effect
	 * @param effectName the name of the effect definition to retrieve
	 * @return the matching effect definition, or null
	 */
	public EffectDefinition getEffectDefinition(
			String perkName, String effectName) {
		Perk perk = perks.get(perkName);
		if (perk == null) {
			return null;
		}
		
		return perk.getDefinition().getEffect(effectName);
	}
	
	/**
	 * Determines if the player has access to the given effect via the given
	 * perk. 
	 * 
	 * <p>As Abilities are ephemeral and not stored in {@link #activeEffects},
	 * they can be checked using this method to ensure the player can execute
	 * them.</p>
	 * @param perkName the perk containing the effect
	 * @param effectName the name of the effect definition to retrieve
	 * @return true if the player has access to the given effect, false if not
	 */
	public boolean hasEffect(String perkName, String effectName) {
		return getEffectDefinition(perkName, effectName) != null;
	}
	
	public PublicPlayerSkillInfo getOtherInfo(UUID otherPlayerId) {
		if (otherPlayerId.equals(playerId)) {
			return publicSkillInfo;
		}
		
		return otherPlayerInfo.get(otherPlayerId);
	}
	
	public PublicPlayerSkillInfo getOtherInfo(EntityPlayer otherPlayer) {
		PublicPlayerSkillInfo info = getOtherInfo(
				otherPlayer.getGameProfile().getId());
		if (info == null) {
			info = new PublicPlayerSkillInfo(player, otherPlayer);
			otherPlayerInfo.put(otherPlayer.getGameProfile().getId(), info);
		}
		
		return info;
	}
	
	public void removeOtherInfo(UUID id) {
		PublicPlayerSkillInfo info = otherPlayerInfo.get(id);
		if (info != null) {
			info.disableAllPublicEffects();
			otherPlayerInfo.remove(id);
		}
	}
	
	/**
	 * Disables and removes all {@link PublicPlayerSkillInfo} instances
	 * maintained for other players in the {@link #otherPlayerInfo} map.
	 * 
	 * <p>This is called on client-side dimension change to prepare for a new
	 * public skill and effect data to resync for the new dimension.
	 */
	public void clearOtherInfo() {
		// disable all effects
		for (PublicPlayerSkillInfo i : otherPlayerInfo.values()) {
			i.disableAllPublicEffects();
		}
		
		// clear the list
		otherPlayerInfo.clear();
	}
	
	public void resetPlayer(EntityPlayer player) {
		setPlayer(player);
		
		// fix all effects
		for (Effect e : activeEffects) {
			e.setPlayer(player);
		}
	}
	
	public void readNBT(NBTTagCompound playerData) {
		if (!playerData.hasKey(TAG_NAME)) {
			// no current player data, use defaults
			return;
		}
		
		NBTTagCompound tag = playerData.getCompoundTag(TAG_NAME);
		
		// update skills and find skill names to prune
		Set<String> oldSkills = new HashSet<>(skills.keySet());
		NBTTagList skillsList = tag.getTagList(
				"skills", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < skillsList.tagCount(); i++) {
			Skill s = updateSkill(skillsList.getCompoundTagAt(i));
			if (s != null) {
				oldSkills.remove(s.getName());
			}
		}
		
		// prune old skills
		for (String skill : oldSkills) {
			skills.remove(skill);
		}
		
		// update perks and find perk names to prune
		Set<String> oldPerks = new HashSet<>(perks.keySet());
		NBTTagList perksList = tag.getTagList(
				"perks", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < perksList.tagCount(); i++) {
			Perk p = updatePerk(perksList.getCompoundTagAt(i));
			if (p != null) {
				oldPerks.remove(p.getName());
			}
		}
		
		// prune old perks
		for (String perk : oldPerks) {
			perks.remove(perk);
		}
		
		// update ui bars and find old entries to prune
		Set<String> oldData = new HashSet<>(uiData.keySet());
		NBTTagList dataList = tag.getTagList(
				"uiData", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < dataList.tagCount(); i++) {
			PerkUIData data = updatePerkUIData(dataList.getCompoundTagAt(i));
			if (data != null) {
				oldData.remove(data.getName());
			}
		}
		
		// prune
		for (String dataName : oldData) {
			uiData.remove(dataName);
		}
		
		// unlike skills and perks, effect pruning works a bit in reverse
		// (and given that it isn't a set, we don't want to do n^2 operations
		// for removals)
		
		// update effects and remove any not included in the update data
		List<Effect> newEffects = new LinkedList<>();
		NBTTagList activeEffectsList = tag.getTagList(
				"activeEffects", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < activeEffectsList.tagCount(); i++) {
			Effect e = updateActiveEffect(activeEffectsList.getCompoundTagAt(i));
			if (e != null) {
				newEffects.add(e);
			}
		}
		
		// disable the leftover effects before swapping the lists
		// they ideally should be disabled via a toggle message first
		activeEffects.removeAll(newEffects);
		for (Effect e : activeEffects) {
			// don't ever try to disable public effects here
			// they only exist in the PlayerSkillInfo to be restarted at login
			// disabling a PublicEffect instantiated here and not in PPSI will
			// leave the targetPlayer unset and will throw an exception
			
			if (!(e instanceof PublicEffect)) {
				e.disable();
			}
		}
		
		// do the swap
		activeEffects = newEffects;
		
		// read public info
		if (tag.hasKey("publicSkillInfo")) {
			publicSkillInfo.readNBT(tag.getCompoundTag("publicSkillInfo"));
		}
	}
	
	public void writeNBT(NBTTagCompound playerData) {
		NBTTagCompound tag = new NBTTagCompound();
		
		NBTTagList skillsList = new NBTTagList();
		for (Skill s : skills.values()) {
			NBTTagCompound skillTag = new NBTTagCompound();
			s.writeNBT(skillTag);
			skillsList.appendTag(skillTag);
		}
		tag.setTag("skills", skillsList);
		
		NBTTagList perksList = new NBTTagList();
		for (Perk p : perks.values()) {
			NBTTagCompound perkTag = new NBTTagCompound();
			p.writeNBT(perkTag);
			perksList.appendTag(perkTag);
		}
		tag.setTag("perks", perksList);
		
		NBTTagList dataList = new NBTTagList();
		for (PerkUIData data : uiData.values()) {
			NBTTagCompound dataTag = new NBTTagCompound();
			data.writeNBT(dataTag);
			dataList.appendTag(dataTag);
		}
		tag.setTag("uiData", dataList);
		
		NBTTagList activeEffectsList = new NBTTagList();
		for (Effect e : activeEffects) {
			NBTTagCompound effectTag = new NBTTagCompound();
			e.writeNBT(effectTag);
			activeEffectsList.appendTag(effectTag);
		}
		tag.setTag("activeEffects", activeEffectsList);
		
		NBTTagCompound publicInfoTag = new NBTTagCompound();
		publicSkillInfo.writeNBT(publicInfoTag);
		tag.setTag("publicSkillInfo", publicInfoTag);
		
		playerData.setTag(TAG_NAME, tag);
	}
	
	public static PlayerSkillInfo getInfo(UUID playerId) {
		if (playerMap == null) {
			playerMap = new LinkedHashMap<>();
		}
		
		return playerMap.get(playerId);
	}
	
	public static PlayerSkillInfo getInfo(EntityPlayer player) {
		if (player.worldObj.isRemote) {
			throw new IllegalStateException(
					"PlayerSkillInfo.getInfo() may not be executed on the"
							+ " client!");
		}
		
		PlayerSkillInfo info = getInfo(player.getGameProfile().getId());
		if (info == null) {
			info = new PlayerSkillInfo(player);
			info.readNBT(player.getEntityData());

			playerMap.put(player.getGameProfile().getId(), info);
		}
		
		return info;
	}
	
	public static void removeInfo(EntityPlayer player) {
		if (player.worldObj.isRemote) {
			throw new IllegalStateException(
					"PlayerSkillInfo.removeInfo() may not be executed on the"
							+ " client!");
		}
		
		if (playerMap == null) {
			return;
		}
		
		playerMap.remove(player.getGameProfile().getId());
		
		for (PlayerSkillInfo info : PlayerSkillInfo.getAllInfo()) {
			info.removeOtherInfo(player.getGameProfile().getId());
		}
	}
	
	public static Collection<PlayerSkillInfo> getAllInfo() {
		if (playerMap == null) {
			playerMap = new LinkedHashMap<>();
		}
		
		return playerMap.values();
	}
	
	/**
	 * Gets the {@link PlayerSkillInfo} for the current client player. Note that
	 * this method should <b>only</b> be used from the client: server-side code
	 * should make use of {@link #getInfo(EntityPlayer)} instead.
	 * 
	 * <p>The returned {@code PlayerSkillInfo} object will be synchronized
	 * periodically with the server whenever it is updated. Note that an empty
	 * skill info object (with no set skills when they are otherwise expected)
	 * may be returned if the skill info has not yet been synchronized with the
	 * client.</p>
	 * @return the client info, or null if no player exists yet
	 */
	@SideOnly(Side.CLIENT)
	public static PlayerSkillInfo getClientInfo() {
		if (Minecraft.getMinecraft().thePlayer == null) {
			return null;
		}
		
		if (clientInfo == null) {
			clientInfo = new PlayerSkillInfo(
					Minecraft.getMinecraft().thePlayer);
		}
		
		return clientInfo;
	}
	
	public static void reset() {
		if (playerMap != null) {
			playerMap.clear();
		}
	}
	
	public static void clientReset() {
		clientInfo = null;
	}
	
	//
	// TODO: reset client player on death (and propagate change to active
	// effects)
	// it seems like the player entity changes after death
	// (possibly on server too?)
	//
	
}
