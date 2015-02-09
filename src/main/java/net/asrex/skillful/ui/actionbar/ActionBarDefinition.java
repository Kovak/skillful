package net.asrex.skillful.ui.actionbar;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;
import net.asrex.skillful.ui.PerkUI;
import net.asrex.skillful.ui.PerkUIDefinition;
import net.asrex.skillful.ui.SpriteSheetPosition;
import net.asrex.skillful.ui.TexturePosition;
import net.asrex.skillful.ui.TextureRegistry;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SideOnly(Side.CLIENT)
public class ActionBarDefinition extends PerkUIDefinition {
	
	public static final String ICONS_TEXTURE = "skillful:textures/icons.png";
	
	public static final TexturePosition DEFAULT_FIRST_TEXTURE;
	public static final TexturePosition DEFAULT_MIDDLE_TEXTURE;
	public static final TexturePosition DEFAULT_LAST_TEXTURE;
	public static final TexturePosition DEFAULT_SELECTED_TEXTURE;
	public static final TexturePosition DEFAULT_ACTIVE_TEXTURE;
	
	public static final AnchorLocation DEFAULT_ANCHOR;
	
	static {
		// widgets.png
		DEFAULT_FIRST_TEXTURE    = new TexturePosition(0, 0, 21, 22);
		DEFAULT_MIDDLE_TEXTURE   = new TexturePosition(21, 0, 20, 22);
		DEFAULT_LAST_TEXTURE     = new TexturePosition(161, 0, 21, 22);
		DEFAULT_SELECTED_TEXTURE = new TexturePosition(0, 22, 24, 24, -2, -1);
		
		// icons.png
		DEFAULT_ACTIVE_TEXTURE   = new SpriteSheetPosition(0, 15, 4, 4);
		
		DEFAULT_ANCHOR = AnchorLocation.builder()
				.horizontal(AnchorHorizontal.center)
				.vertical(AnchorVertical.top)
				.build();
	}
			
	/**
	 * The texture path. By default, this is {@code textures/gui/widgets.png} to
	 * match the vanilla action bar.
	 */
	private String barTexture = "textures/gui/widgets.png";
	
	/**
	 * The size of this action bar. By default, the bar is 9 spaces long to
	 * match the vanilla action bar.
	 */
	private int size = 9;
	
	/**
	 * If true, automatically hide this bar shortly after the selection has
	 * changed. The bar will be shown again 
	 */
	private boolean autoHide = false;
	
	/**
	 * The delay, in ticks, before the bar is hidden when {@link #autoHide} is
	 * enabled after user interaction occurs.
	 */
	private int autoHideTicks = 40;
	
	/**
	 * The position of the image within {@link #texture} to use for the first
	 * tile on the bar.
	 */
	private TexturePosition firstTexture = DEFAULT_FIRST_TEXTURE;
	
	/**
	 * The position of the image within {@link #texture} to use for the
	 * {@code (size - 2)} center tiles on the bar.
	 */
	private TexturePosition middleTexture = DEFAULT_MIDDLE_TEXTURE;
	
	/**
	 * The position of the image within {@link #texture} to use for the last
	 * tile on the bar.
	 */
	private TexturePosition lastTexture = DEFAULT_LAST_TEXTURE;
	
	/**
	 * The position of the image within {@link #texture} to overlay on top of
	 * the currently selected tile, if any exists.
	 */
	private TexturePosition selectedTexture = DEFAULT_SELECTED_TEXTURE;
	
	/**
	 * The texture to overlay on top of any bar tile with a perk that is
	 * currently active.
	 */
	private TexturePosition activeTexture = DEFAULT_ACTIVE_TEXTURE;
	
	/**
	 * The size of the icons. 16 by default.
	 */
	private int iconSize = 16;
	
	private int iconOffsetX = 2;
	private int iconOffsetY = 3;
	
	/**
	 * The orientation, either horizontal or vertical, of this action bar. Note
	 * that the default texture positions (mirroring the vanilla action bar) do
	 * not adapt without modification to a vertical layout.
	 */
	private Orientation orientation = Orientation.horizontal;
	
	/**
	 * The screen anchor location at which to position the bar. By default, the
	 * bar will be positioned at the top-center of the screen.
	 */
	private AnchorLocation anchor = DEFAULT_ANCHOR;
	
	/**
	 * The animation duration in ticks (1/20 seconds each).
	 */
	private int easeTime = 15; // ~750ms

	private int hideKey = -1;
	private String hideKeyName = "MINUS";
	
	private int activateKey = -1;
	private String activateKeyName = "F";
	
	private int modifierKey = -1;
	private String modifierKeyName = "LSHIFT";
	
	@Override
	public PerkUI createUI() {
		return new ActionBar(this);
	}
	
	// TODO: these don't work
	// we may need some custom constructors defined
	
	public void setFirstTexture(String texName) {
		firstTexture = TextureRegistry.getTexture(texName);
	}
	
	public void setMiddleTexture(String texName) {
		middleTexture = TextureRegistry.getTexture(texName);
	}
	
	public void setLastTexture(String texName) {
		lastTexture = TextureRegistry.getTexture(texName);
	}
	
	public void setSelectedTexture(String texName) {
		selectedTexture = TextureRegistry.getTexture(texName);
	}
	
	public void setActiveTexture(String texName) {
		activeTexture = TextureRegistry.getTexture(texName);
	}
	
	public int getHideKey() {
		if (hideKey != -1) {
			return hideKey;
		} else if (hideKeyName != null) {
			return Keyboard.getKeyIndex(hideKeyName);
		} else {
			return 0;
		}
	}
	
	public int getActivateKey() {
		if (activateKey != -1) {
			return activateKey;
		} else if (activateKeyName != null) {
			return Keyboard.getKeyIndex(activateKeyName);
		} else {
			return 0;
		}
	}
	
	public int getModifierKey() {
		if (modifierKey != -1) {
			return modifierKey;
		} else if (modifierKeyName != null) {
			return Keyboard.getKeyIndex(modifierKeyName);
		} else {
			return 0;
		}
	}
	
	public static enum AnchorHorizontal {
		left,
		center,
		right;
	}
	
	public static enum AnchorVertical {
		top,
		center,
		bottom;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class AnchorLocation {
		
		public AnchorHorizontal horizontal;
		public AnchorVertical vertical;
		
		public int offsetX = 0;
		public int offsetY = 0;
		
		public AnchorLocation(
				AnchorHorizontal horizontal, AnchorVertical vertical) {
			this.horizontal = horizontal;
			this.vertical = vertical;
		}
		
		public int getStartX(ScaledResolution res, ActionBar bar) {
			int ret;
			if (horizontal == AnchorHorizontal.left) {
				ret = 0;
			} else if (horizontal == AnchorHorizontal.right) {
				ret = res.getScaledWidth() - bar.getTotalWidth();
			} else {
				ret = (res.getScaledWidth() / 2) - (bar.getTotalWidth() / 2);
			}
			
			return ret + offsetX;
		}

		public int getStartY(ScaledResolution res, ActionBar bar) {
			int ret;
			if (vertical == AnchorVertical.top) {
				ret = 0;
			} else if (vertical == AnchorVertical.bottom) {
				ret = res.getScaledHeight() - bar.getHeight();
			} else {
				ret = (res.getScaledHeight() / 2) - (bar.getHeight() / 2);
			}
			
			return ret + offsetY;
		}
		
	}
	
	public static enum Orientation {
		horizontal,
		vertical;
	}
	
}
