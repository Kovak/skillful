package net.asrex.skillful.effect.food;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.LinkedList;
import java.util.List;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.skill.SkillSeeder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

/**
 *
 */
public class FoodOnKillEffect extends Effect {
	
	private List<String> entityClasses;
	
	private int foodAmount;
	private int saturationAmount;

	public FoodOnKillEffect() {
	}

	public FoodOnKillEffect(
			List<String> entityClasses, int foodAmount, int saturationAmount) {
		this.entityClasses = entityClasses;
		this.foodAmount = foodAmount;
		this.saturationAmount = saturationAmount;
	}
	
	@SubscribeEvent
	public void onLivingAttacked(LivingAttackEvent event) {
		// server only
		if (event.entity.worldObj.isRemote) {
			return;
		}
		
		Entity e = event.source.getEntity();
		if (e != null && e instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) e;
			
			if (!entityPlayer.equals(player)) {
				return;
			}
			
			for (String cstr : entityClasses) {
				try {
					Class c = Class.forName(cstr);
					if (event.entityLiving.getClass().isAssignableFrom(c)) {
						player.getFoodStats().addStats(foodAmount, saturationAmount);
						return;
					}
				} catch (ClassNotFoundException ex) {
					// ignore and continue
				}
			}
		}
	}

	@Override
	public void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		
		foodAmount = tag.getInteger("foodAmount");
		saturationAmount = tag.getInteger("saturationAmount");
		
		entityClasses = new LinkedList<>();
		NBTTagList clist = tag.getTagList(
				"entityClasses",
				Constants.NBT.TAG_STRING);
		
		for (int i = 0; i < clist.tagCount(); i++) {
			entityClasses.add(clist.getStringTagAt(i));
		}
	}

	@Override
	public void writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		
		tag.setInteger("foodAmount", foodAmount);
		tag.setInteger("saturationAmount", saturationAmount);
		
		NBTTagList clist = new NBTTagList();
		for (String clazz : entityClasses) {
			clist.appendTag(new NBTTagString(clazz));
		}
		tag.setTag("entitiyClasses", clist);
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
