package net.asrex.skillful.ui;

import net.asrex.skillful.perk.Perk;

/**
 * Defines necessary functionality for a base perk UI. Perk UIs should have some
 * method for displaying a list of perks to the user, and can (with user input)
 * enable or disable player perks as needed. 
 */
public interface PerkUI {
	
	public void enable();
	public boolean isEnabled();
	public void disable();
	
	public boolean addPerk(Perk perk);
	public boolean removePerk(Perk perk);
	public void clearPerks();
	public boolean setPerk(int position, Perk perk);
	
}
