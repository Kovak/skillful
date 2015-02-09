package net.asrex.skillful.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Builder;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.effect.Effect;
import net.asrex.skillful.message.client.EffectToggleHandler;
import net.asrex.skillful.message.client.EffectToggleMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * An effect dispatched when an effect has been toggled for a particular player.
 * 
 * <p>Note that this event will fire <em>after</em> the effect has been added
 * to the player's list of active effects and {@link Effect#enable()} has been
 * executed (when {@link #enabled} is {@code true}), but <em>before</em> the
 * effect is removed from the active effects after {@link Effect#disable()} has
 * been executed (if {@link #enabled} is {@code false}.</p>
 * 
 * <p>This event is posted to {@link MinecraftForge#EVENT_BUS} on both the
 * client and server. On the server, the event is posted by the
 * {@link PlayerNetworkHelper} in the relevant methods; as such, effect should
 * not be toggled manually unless they explicitly dispatch this event
 * appropriately. On the client, this event will be dispatched within the
 * {@link EffectToggleHandler} upon receipt of a
 * {@link EffectToggleMessage}.</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SkillfulEffectToggledEvent extends PlayerEvent {
	
	private final PlayerSkillInfo info;
	
	/**
	 * The instance of the effect that has been toggled.
	 */
	private final Effect effect;
	
	/**
	 * If true, the effect has been enabled; if false, the effect has been
	 * disabled.
	 */
	private final boolean enabled;
	
	@Builder
	public SkillfulEffectToggledEvent(
			EntityPlayer player, PlayerSkillInfo info,
			Effect effect, boolean enabled) {
		super(player);
		this.info = info;
		this.effect = effect;
		this.enabled = enabled;
	}
	
}
