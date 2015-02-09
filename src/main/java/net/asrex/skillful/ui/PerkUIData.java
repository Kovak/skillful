package net.asrex.skillful.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/**
 * Player-stored data for PerkUI classes. Mainly a list of configured perk names
 * configured.
 */
public class PerkUIData {
	
	/**
	 * The name of this perk UI
	 */
	@Getter
	private String name;
	
	/**
	 * The template type for this perk UI instance. 
	 */
	@Getter
	private PerkUIDefinition definition;
	
	private final Map<Integer, String> perks;

	public PerkUIData() {
		perks = new LinkedHashMap<>();
	}

	public PerkUIData(String name, PerkUIDefinition definition) {
		this.name = name;
		this.definition = definition;
		
		perks = new LinkedHashMap<>();
	}
	
	public void readNBT(NBTTagCompound tag) {
		name = tag.getString("name");
		definition = PerkUIRegistry.getDefinition(tag.getString("definition"));
		
		NBTTagList list = tag.getTagList("perks", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound entry = list.getCompoundTagAt(i);
			
			perks.put(entry.getInteger("i"), entry.getString("perk"));
		}
	}
	
	public void writeNBT(NBTTagCompound tag) {
		tag.setString("name", name);
		tag.setString("definition", definition.getName());
		
		NBTTagList list = new NBTTagList();
		for (Entry<Integer, String> e : perks.entrySet()) {
			NBTTagCompound entry = new NBTTagCompound();
			entry.setInteger("i", e.getKey());
			entry.setString("perk", e.getValue());
			list.appendTag(entry);
		}
		
		tag.setTag("perks", list);
	}
	
	public String getPerk(int index) {
		return perks.get(index);
	}
	
	public void setPerk(int index, String perk) {
		perks.put(index, perk);
	}

	public Map<Integer, String> getPerks() {
		return perks;
	}
	
	public boolean removePerk(String name) {
		int toRemove = -1;
		for (Entry<Integer, String> entry : perks.entrySet()) {
			if (entry.getValue().equals(name)) {
				toRemove = entry.getKey();
				break;
			}
		}
		
		if (toRemove >= 0) {
			perks.remove(toRemove);
			return true;
		} else {
			return false;
		}
	}
	
	public void clearPerks() {
		perks.clear();
	}
	
	public static PerkUIData fromNBT(NBTTagCompound tag) {
		PerkUIData data = new PerkUIData();
		data.readNBT(tag);
		
		return data;
	}
	
}
