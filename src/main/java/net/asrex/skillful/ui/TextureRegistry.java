package net.asrex.skillful.ui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Handles loading of texture data from config files.
 */
@Log4j2
@SideOnly(Side.CLIENT)
public class TextureRegistry {
	
	public static final String TEXTURES_FILENAME = "textures.yml";
	
	private static TextureRegistry instance;
	
	private final Constructor constructor;
	
	private final Map<String, TexturePosition> textures;
	
	private TextureRegistry() {
		constructor = new Constructor();
		
		map(TexturePosition.class, "!texture");
		map(SpriteSheetPosition.class, "!sprite");
		
		textures = new LinkedHashMap<>();
	}

	public static TextureRegistry getInstance() {
		if (instance == null) {
			instance = new TextureRegistry();
		}
		
		return instance;
	}
	
	private void map(Class clazz, String tag) {
		TypeDescription old = 
				constructor.addTypeDescription(new TypeDescription(clazz, tag));
		if (old != null) {
			log.warn("Configuration tag \"{}\" for class {} has been "
					+ "overwritten by new class {}. The old value will not be "
					+ "available for use - be warned of unexpected errors or "
					+ "functionality differences!",
					tag,
					old.getType().getName(),
					clazz.getName());
		}
	}
	
	public static void init(File configDir) throws IOException {
		TextureRegistry r = getInstance();
		
		Yaml yaml = new Yaml(r.constructor);
		TextureConfig conf = yaml.loadAs(
				new FileReader(new File(configDir, TEXTURES_FILENAME)),
				TextureConfig.class);
		
		for (TexturePosition tex : conf.textures) {
			if (tex.getName() == null) {
				log.warn("Texture in {} does not have a defined name, "
						+ "it will be ignored.", TEXTURES_FILENAME);
				continue;
			}
			
			if (r.textures.containsKey(tex.getName())) {
				log.warn("A texture with name \"{}\" has been defined multiple "
						+ "times. Additional declarations will be discarded.",
						tex.getName());
				continue;
			}
			
			r.textures.put(tex.getName(), tex);
		}
		
	}
	
	public static TexturePosition getTexture(String name) {
		return getInstance().textures.get(name);
	}
	
	@Data
	public static class TextureConfig {
		
		public List<TexturePosition> textures = new LinkedList<>();
		
	}
	
}
