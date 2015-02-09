package net.asrex.skillful.ui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import net.minecraft.util.ResourceLocation;

/**
 * Specifies position information for a particular part of a texture image.
 */
@Data
public class TexturePosition {

	/**
	 * A unique identifier for this texture. Required for textures defined in
	 * {@code textures.yml}, but can be ignored if defined directly in
	 * {@code ui.yml}.
	 */
	public String name;
	
	/**
	 * An optional texture to set before attempting to draw. If null, the
	 * previously bound texture is used.
	 */
	public String texture;
	
	public int x;
	public int y;
	public int width;
	public int height;

	public int offsetX = 0; // drawing offset
	public int offsetY = 0; // drawing offset

	public float red = 1.0f;
	public float green = 1.0f;
	public float blue = 1.0f;
	public float alpha = 1.0f;
	
	@SideOnly(Side.CLIENT)
	private ResourceLocation resource;

	public TexturePosition() {
	}
	
	public TexturePosition(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public TexturePosition(
			String texture, int x, int y, int width, int height) {
		this.texture = texture;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public TexturePosition(
			int x, int y, int width, int height, int offsetX, int offsetY) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public TexturePosition(
			String texture, int x, int y, int width, int height,
			int offsetX, int offsetY) {
		this.texture = texture;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getResource() {
		if (texture == null) {
			return null;
		}
		
		if (resource == null) {
			resource = new ResourceLocation(texture);
		}
		
		return resource;
	}
	
}
