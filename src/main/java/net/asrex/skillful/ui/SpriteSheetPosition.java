package net.asrex.skillful.ui;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A TexturePosition that represents a tile on a sprite sheet. By default, it
 * will use Skillful's built-in 16x16 sprite sheet, but alternative textures
 * may be provided as well.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SpriteSheetPosition extends TexturePosition {

	public static final String DEFAULT_TEXTURE = "skillful:textures/icons.png";
	
	private int tileX;
	private int tileY;
	
	private int tileWidth = 1;
	private int tileHeight = 1;
	
	/**
	 * The size of each tile, in pixels.
	 */
	private int tileSize = 16;
	
	public SpriteSheetPosition() {
		texture = DEFAULT_TEXTURE;
		
		width = tileSize;
		height = tileSize;
	}

	public SpriteSheetPosition(int tileX, int tileY) {
		texture = DEFAULT_TEXTURE;
		
		width = tileSize;
		height = tileSize;
		
		setTileX(tileX);
		setTileY(tileY);
	}

	public SpriteSheetPosition(int tileX, int tileY, int offsetX, int offsetY) {
		texture = DEFAULT_TEXTURE;
		
		width = tileSize;
		height = tileSize;
		
		setTileX(tileX);
		setTileY(tileY);
		
		setOffsetX(offsetX);
		setOffsetY(offsetY);
	}
	
	public final void setTileX(int tileX) {
		this.tileX = tileX;
		
		x = tileSize * tileX;
	}
	
	public final void setTileY(int tileY) {
		this.tileY = tileY;
		
		y = tileSize * tileY;
	}
	
}
