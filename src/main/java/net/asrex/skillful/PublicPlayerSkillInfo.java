package net.asrex.skillful;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.effect.PublicEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/**
 * Skill, perk, and effect information about a particular player synchronized
 * with all clients. This contains information about <i>other</i> players than
 * the current client that wouldn't be otherwise accessible via
 * {@link PlayerSkillInfo} unless on the server.
 * 
 * <p>As this class is mostly redundant on the server, many of the
 * record-keeping global lists are only used on the client. </p>
 */
@Log4j2
public class PublicPlayerSkillInfo {
	
	public static final String TAG_NAME = "skillful_public";
	
	@Getter
	private final UUID currentPlayerId;
	
	/**
	 * The current (local) player
	 */
	@Getter @Setter
	private EntityPlayer currentPlayer;
	
	@Getter
	private final UUID targetPlayerId;
	
	@Getter @Setter
	private EntityPlayer targetPlayer;
	
	@Getter
	private List<PublicEffect> publicEffects;

	public PublicPlayerSkillInfo(
			EntityPlayer currentPlayer, EntityPlayer targetPlayer) {
		this.currentPlayerId = currentPlayer.getGameProfile().getId();
		this.currentPlayer = currentPlayer;
		this.targetPlayerId = targetPlayer.getGameProfile().getId();
		this.targetPlayer = targetPlayer;
		
		publicEffects = new LinkedList<>();
	}
	
	public PublicEffect getPublicEffect(String perkName, String effectName) {
		for (PublicEffect e : publicEffects) {
			if (e.getPerkName().equals(perkName)
					&& e.getEffectName().equals(effectName)) {
				return e;
			}
		}
		
		return null;
	}
	
	public void removePublicEffect(String perkName, String effectName) {
		for (PublicEffect e : publicEffects) {
			if (e.getPerkName().equals(perkName)
					&& e.getEffectName().equals(effectName)) {
				e.disable();
				publicEffects.remove(e);
				return;
			}
		}
	}
	
	public void addPublicEffect(PublicEffect e) {
		publicEffects.add(e);
		
		e.setPlayer(currentPlayer);
		e.setTargetPlayer(targetPlayer);
		
		e.enable();
	}
	
	public void disableAllPublicEffects() {
		for (PublicEffect e : publicEffects) {
			e.disable();
		}
	}
	
	private PublicEffect updatePublicEffect(NBTTagCompound tag) {
		PublicEffect pubE = getPublicEffect(
				tag.getString("perkName"),
				tag.getString("effectName"));
		
		if (pubE == null) {
			Effect e = Effect.fromNBT(tag);
			if (e == null) {
				log.warn("Could not update public effect from NBT: {}", tag);
			} else if (!(e instanceof PublicEffect)) {
				log.warn("Effect is not a public effect: {}", tag);
			} else {
				pubE = (PublicEffect) e;
				pubE.setPlayer(currentPlayer);
				pubE.setTargetPlayer(targetPlayer);
				
				pubE.enable();
			}
		}
		
		return pubE;
	}
	
	public void readNBT(NBTTagCompound playerData) {
		if (!playerData.hasKey(TAG_NAME)) {
			return;
		}
		
		NBTTagCompound tag = playerData.getCompoundTag(TAG_NAME);
		
		// roughly follow PlayerSkillInfo.readNBT() here
		List<PublicEffect> newEffects = new LinkedList<>();
		NBTTagList publicEffectsList = tag.getTagList(
				"publicEffects", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < publicEffectsList.tagCount(); i++) {
			PublicEffect e = updatePublicEffect(
					publicEffectsList.getCompoundTagAt(i));
			if (e != null) {
				newEffects.add(e);
			}
		}
		
		// find leftover (i.e. removed) effects and disable them
		// unlike activeEffects in PlayerSkillInfo, public effects are *always*
		// enabled if activate, so all 
		publicEffects.removeAll(newEffects);
		for (PublicEffect e : publicEffects) {
			e.disable();
		}
		
		// swap the lists
		publicEffects = newEffects;
	}
	
	public void writeNBT(NBTTagCompound playerData) {
		NBTTagCompound tag = new NBTTagCompound();
		
		NBTTagList publicEffectsList = new NBTTagList();
		for (PublicEffect e : publicEffects) {
			NBTTagCompound effectTag = new NBTTagCompound();
			e.writeNBT(effectTag);
			publicEffectsList.appendTag(effectTag);
		}
		tag.setTag("publicEffects", publicEffectsList);
		
		playerData.setTag(TAG_NAME, tag);
	}
	
}
