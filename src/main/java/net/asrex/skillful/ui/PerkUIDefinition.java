package net.asrex.skillful.ui;

import lombok.Data;

/**
 * Defines a basic UI factory. Definitions are essentially named UI templates
 * that construct a perk UI of a particular type.
 */
@Data
public abstract class PerkUIDefinition {
	
	/**
	 * A unique, human-readable identifier for this definition.
	 */
	private String name;
	
	/**
	 * A human-readable description of this UI template.
	 */
	private String description;
	
	/**
	 * Creates a new instance of this Perk UI, suitable for immediate use.
	 * The returned UI may be arbitrarily configured by subclasses.
	 * @return a new PerkUI instance
	 */
	public abstract PerkUI createUI();
	
}
