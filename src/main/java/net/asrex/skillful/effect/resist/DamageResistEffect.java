package net.asrex.skillful.effect.resist;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.LinkedList;
import java.util.List;
import net.asrex.skillful.effect.Effect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 *
 */
public class DamageResistEffect extends Effect {
	
	public static final DamageSource[] EXCLUDED_SOURCES = {
		DamageSource.outOfWorld,
		DamageSource.drown,
		DamageSource.inWall,
		DamageSource.onFire
	};
	
	private float amount;
	
	private List<String> sources;

	public DamageResistEffect() {
		
	}

	public DamageResistEffect(float amount, List<String> sources) {
		this.amount = amount;
		this.sources = sources;
	}

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent e) {
		if (e.isCanceled() || e.entityLiving.worldObj.isRemote) {
			return;
		}
		
		if (!e.entityLiving.equals(player)) {
			return;
		}
		
		for (DamageSource excludedSource : EXCLUDED_SOURCES) {
			if (e.source == excludedSource) {
				return;
			}
		}
		
		if (!sources.isEmpty()) {
			boolean found = false;
			for (String source : sources) {
				if (e.source.damageType.equalsIgnoreCase(source)) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				return;
			}
		}
		
		e.ammount -= amount;
		if (e.ammount < 0) {
			e.ammount = 0;
		}
		
		if (e.ammount <= 0) {
			e.setCanceled(true);
		}
	}

	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		amount = tag.getFloat("amount");
		
		sources = new LinkedList<>();
		NBTTagList sourcesList = tag.getTagList(
				"sources", Constants.NBT.TAG_STRING);
		for (int i = 0; i < sourcesList.tagCount(); i++) {
			sources.add(sourcesList.getStringTagAt(i));
		}
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setFloat("amount", amount);
		
		NBTTagList sourcesList = new NBTTagList();
		for (String source : sources) {
			sourcesList.appendTag(new NBTTagString(source));
		}
		tag.setTag("sources", sourcesList);
	}
	
	@Override
	public void doEnable() {
		registerForge(this);
	}

	@Override
	public void doDisable() {
		unregisterForge(this);
	}
	
}
