package net.asrex.skillful.util;

/**
 * Generic text utilities
 */
public class TextUtil {

	public static String slugify(String text) {
		if (text == null) {
			return null;
		}
		
		return text
				.toLowerCase()
				.replaceAll("\\s+", "-")
				.replaceAll("[^a-z\\-]", "");
	}
	
}
