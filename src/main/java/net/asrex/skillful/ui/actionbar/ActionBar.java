package net.asrex.skillful.ui.actionbar;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import net.asrex.skillful.ClientNetworkHelper;
import net.asrex.skillful.PlayerSkillInfo;
import net.asrex.skillful.perk.Perk;
import net.asrex.skillful.perk.PerkDefinition;
import net.asrex.skillful.ui.PerkUI;
import net.asrex.skillful.ui.TexturePosition;
import net.asrex.skillful.ui.TextureRegistry;
import net.asrex.skillful.ui.actionbar.ActionBarDefinition.Orientation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * An action bar is a simple perk UI that (like the vanilla action bar) allows
 * players to select a single "active" perk from a list of available perks.
 */
@SideOnly(Side.CLIENT)
public class ActionBar extends Gui implements PerkUI {
	
	private final ActionBarDefinition def;
	
	private final ResourceLocation barTextureResource;
	
	private final KeyBinding activateKey;
	private final KeyBinding modifierKey;
	private final KeyBinding hideKey;
	
	private final Map<Integer, Perk> perks;
	
	private final Minecraft mc;
	
	private boolean enabled = false;
	
	@Getter
	private boolean hidden = false;
	
	@Getter @Setter
	private int selected = -1;
	
	private int easeTicks = -1;
	
	/**
	 * The relative X position at which to draw bar buttons. Used for animating
	 * on/off the screen.
	 */
	private int relX = 0;
	
	/**
	 * The relative Y position at which to draw bar buttons. Used for animating
	 * on/off the screen.
	 */
	private int relY = 0;
	
	private int startX = 0;
	private int startY = 0;
	
	private int destX;
	private int destY;
	
	/**
	 * The number of accumulated ticks in the auto-hide timer. When it reaches
	 * {@link ActionBarDefinition#autoHideTicks}, the bar will be hidden again.
	 * <p>This is initialized to zero so an auto-hide bar will be shown for a
	 * short time on first login as a small reminder to the player that it
	 * exists.</p>
	 */
	private int autoHideTicks = 0;
	
	public ActionBar(ActionBarDefinition def) {
		this.def = def;
		
		perks = new HashMap<>();
		enabled = false;
		
		modifierKey = new KeyBinding(
				"Selection Modifier",
				def.getModifierKey(),
				"key.categories.misc");
		
		activateKey = new KeyBinding(
				"Activate Selected",
				def.getActivateKey(),
				"key.categories.misc");
		
		hideKey = new KeyBinding(
				"Hide Action Bar",
				def.getHideKey(),
				"key.categories.misc");
		
		barTextureResource = new ResourceLocation(def.getBarTexture());
		
		mc = Minecraft.getMinecraft();
	}
	
	@SubscribeEvent
	public void keyDown(InputEvent.KeyInputEvent event) {
		if (mc == null || mc.thePlayer == null || mc.theWorld == null) {
			return;
		}
		
		// only run on keydown
		if (!Keyboard.getEventKeyState()) {
			return;
		}
		
		int key = Keyboard.getEventKey();
		if (key == hideKey.getKeyCode()) {
			if (hidden) {
				unhide();
			} else {
				hide();
			}
		} else if (key == activateKey.getKeyCode()) {
			Perk perk = perks.get(selected);
			if (perk != null) {
				ClientNetworkHelper.togglePerk(perk.getName());
			}
		}
	}
	
	@SubscribeEvent
	public void mouseEvent(MouseEvent event) {
		if (Keyboard.isKeyDown(modifierKey.getKeyCode())) {
			if (event.dwheel > 0) {
				moveSelected(-1);
				event.setCanceled(true);
			} else if (event.dwheel < 0) {
				moveSelected(1);
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			// progress the animation
			if (easeTicks >= 0) {
				easeTicks++;
			}
			
			if (autoHideTicks >= 0) {
				autoHideTicks++;
			}
		} else { // end phase
			if (easeTicks >= def.getEaseTime()) {
				// animation has finished, end it
				easeTicks = -1;
			}
			
			if (autoHideTicks >= def.getAutoHideTicks()) {
				// auto-hide delay expired, hide the bar
				autoHideTicks = -1;
				
				if (def.isAutoHide()) {
					hide();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void draw(RenderGameOverlayEvent.Post event) {
		if (!enabled) {
			return;
		}
		
		if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR
				|| event.isCanceled()) {
			return;
		}
		
		updateAnimations(event);
		
		GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		zLevel = -90F;
		
		// first pass: render actual bar
		this.mc.renderEngine.bindTexture(barTextureResource);
		
		int x = def.getAnchor().getStartX(event.resolution, this);
		int y = def.getAnchor().getStartY(event.resolution, this);
		
		// cached x/y positions of texture corners
		// (used to position icons in 2nd pass without recalculating)
		int[] posX = new int[def.getSize()];
		int[] posY = new int[def.getSize()];
		
		TexturePosition currentTexture;
		for (int i = 0; i < def.getSize(); i++) {
			if (i == 0) {
				currentTexture = def.getFirstTexture();
			} else if (i == def.getSize() - 1) {
				currentTexture = def.getLastTexture();
			} else {
				currentTexture = def.getMiddleTexture();
			}
			
			posX[i] = x;
			posY[i] = y;
			
			drawTexture(x, y, currentTexture);
			
			// move the cursor to the next position (depending on orientation)
			if (def.getOrientation() == Orientation.horizontal) {
				x += currentTexture.width;
			} else if (def.getOrientation() == Orientation.vertical) {
				y += currentTexture.height;
			}
		}
		
		// second pass: render textures for perks
		for (Entry<Integer, Perk> entry : perks.entrySet()) {
			PerkDefinition perk = entry.getValue().getDefinition();
			if (perk.getTexture() == null) {
				continue;
			}
			
			TexturePosition tex = TextureRegistry.getTexture(perk.getTexture());
			if (tex == null) {
				continue;
			}
			
			if (tex.getTexture() != null) {
				mc.renderEngine.bindTexture(tex.getResource());
			}
			
			int px = posX[entry.getKey()];
			int py = posY[entry.getKey()];
			
			drawTexture(
					px + def.getIconOffsetX(),
					py + def.getIconOffsetY(),
					tex);
		}
		
		// draw selected, if applicable
		this.mc.renderEngine.bindTexture(barTextureResource);
		if (selected >= 0 && selected < def.getSize()) {
			drawTexture(
					posX[selected], posY[selected],
					def.getSelectedTexture());
		}
		
		// third pass: draw status overlays
		PlayerSkillInfo info = PlayerSkillInfo.getClientInfo();
		if (info == null) {
			// player info not yet initialized?
			GL11.glDisable(GL11.GL_BLEND);
			return;
		}
		
		for (Entry<Integer, Perk> entry : perks.entrySet()) {
			Perk perk = entry.getValue();
			PerkDefinition perkDef = perk.getDefinition();
			
			int px = posX[entry.getKey()];
			int py = posY[entry.getKey()];
			
			// draw active effect?
			if (info.hasActiveEffects(perkDef.getName())) {
				mc.renderEngine.bindTexture(
						def.getActiveTexture().getResource());
				
				drawTexture(
						px + def.getIconOffsetX(),
						py + def.getIconOffsetY(),
						def.getActiveTexture());
			}
			
			// draw cooldown?
			int currentTick = info.getPlayer().ticksExisted;
			float ticksRemaining = perk.getCooldownTicksRemaining(currentTick)
					- event.partialTicks;
			if (ticksRemaining > 0) {
				float ratio = ticksRemaining / perkDef.getCooldownTicks();
				int height = (int) (def.getIconSize() * ratio);
				
				drawTexture(
						px + def.getIconOffsetX(),
						py + def.getIconOffsetY(),
						height,
						def.getCooldownTexture());
			}
		}
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private void updateAnimations(RenderGameOverlayEvent.Post event) {
		if (easeTicks >= 0) {
			// animation in progress
			double realTime = easeTicks + event.partialTicks;

			// finish 1 tick early (animation won't finish due to timing
			// otherwise)
			double progress = realTime / (double) (def.getEaseTime() - 1);
			double easeValue = 1 - ease(1 - progress); // outward easing

			int distX = destX - startX;
			int distY = destY - startY;

			relX = startX + (int) (easeValue * (double) distX);
			relY = startY + (int) (easeValue * (double) distY);
		}
	}
	
	private void drawTexture(int x, int y, TexturePosition tex) {
		if (tex.getTexture() != null) {
			mc.renderEngine.bindTexture(tex.getResource());
		}
		
		GL11.glColor4f(tex.red, tex.green, tex.blue, tex.alpha);
		drawTexturedModalRect(
				x + tex.offsetX + relX, y + tex.offsetY + relY,
				tex.x, tex.y,
				tex.width, tex.height);
	}
	
	private void drawTexture(int x, int y, int height, TexturePosition tex) {
		if (tex.getTexture() != null) {
			mc.renderEngine.bindTexture(tex.getResource());
		}
		
		GL11.glColor4f(tex.red, tex.green, tex.blue, tex.alpha);
		drawTexturedModalRect(
				x + tex.offsetX + relX, y + tex.offsetY + relY,
				tex.x, tex.y,
				tex.width, height);
	}
	
	/**
	 * Gets the total width of this bar along its primary axis. This may not
	 * necessary match the bar's width along the actual screen X axis.
	 * @return the total width of this bar
	 */
	public int getTotalWidth() {
		int size = def.getSize();
		if (size <= 0) {
			return 0;
		}
		
		if (size == 1) {
			return def.getFirstTexture().width;
		}
		
		if (size == 2) {
			return def.getFirstTexture().width + def.getLastTexture().width;
		}
		
		return def.getFirstTexture().width
				+ def.getLastTexture().width
				+ ((size - 2) * def.getMiddleTexture().width);
	}
	
	public int getHeight() {
		return def.getFirstTexture().height;
	}
	
	@Override
	public void enable() {
		enabled = true;
		
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	private double ease(double ratio) {
		return Math.pow(ratio, 3); // quick cubic easing
	}
	
	public void moveSelected(int amount) {
		// extra weirdness to get python-style modulo behavior
		int size = def.getSize();
		selected = (((selected + amount) % size) + size) % size;
		
		// show the bar if not already shown
		unhide();
		
		if (def.isAutoHide()) {
			autoHideTicks = 0;
		}
	}
	
	public void hide() {
		if (hidden) {
			return;
		}
		
		hidden = true;
		
		if (def.getOrientation() == Orientation.horizontal) {
			// move along y axis to off-screen
			
			// movement along center axes is a little derpy with opposite
			// orientations
			// e.g. top-center vertical
			
			switch (def.getAnchor().vertical) {
				case top:
				case center:
					// move up and out
					destY = -getHeight();
					break;
				default:
					// move down
					destY = getHeight();
			}
		} else if (def.getOrientation() == Orientation.vertical) {
			// move along x axis to off-screen
			
			switch (def.getAnchor().horizontal) {
				case left:
				case center:
					// move left
					destX = -getHeight();
					break;
				default:
					// move right
					destX = getHeight();
			}
		}
		
		startX = relX;
		startY = relY;
		easeTicks = 0;
	}
	
	public void unhide() {
		if (!hidden) {
			return;
		}
		
		hidden = false;
		
		// start animating back to starting position
		startX = relX;
		startY = relY;
		
		destX = 0;
		destY = 0;
		easeTicks = 0;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void disable() {
		enabled = false;
		
		try {
			MinecraftForge.EVENT_BUS.unregister(this);
			FMLCommonHandler.instance().bus().unregister(this);
		} catch (NullPointerException ex) {
			// :(
		}
	}
	
	@Override
	public boolean addPerk(Perk perk) {
		for (int i = 0; i < def.getSize(); i++) {
			if (!perks.containsKey(i)) {
				perks.put(i, perk);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean removePerk(Perk perk) {
		for (int i = 0; i < def.getSize(); i++) {
			if (perks.containsKey(i) && perks.get(i).equals(perk)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean setPerk(int position, Perk perk) {
		if (position < 0 || position >= def.getSize()) {
			return false;
		}
		
		perks.put(position, perk);
		
		return true;
	}

	@Override
	public void clearPerks() {
		perks.clear();
	}
	
}
