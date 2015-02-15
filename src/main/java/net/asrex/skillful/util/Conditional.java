package net.asrex.skillful.util;

/**
 * 
 */
public enum Conditional {
	
	above("above"),
	below("below"),
	at_least("at least"),
	at_most("at most"),
	equal_to("equal to"),
	not_equal_to("not equal to");

	public final String text;

	private Conditional(String text) {
		this.text = text;
	}

	public boolean compare(int a, int b) {
		switch (this) {
			case above:        return a > b;
			case below:        return a < b;
			case at_least:     return a >= b;
			case at_most:      return a <= b;
			case equal_to:     return a == b;
			case not_equal_to: return a != b;
		}
		
		return true;
	}
	
	public boolean compare(float a, float b) {
		switch (this) {
			case above:        return a > b;
			case below:        return a < b;
			case at_least:     return a >= b;
			case at_most:      return a <= b;
			case equal_to:     return a == b;
			case not_equal_to: return a != b;
		}
		
		return true;
	}
	
	public boolean compare(double a, double b) {
		switch (this) {
			case above:        return a > b;
			case below:        return a < b;
			case at_least:     return a >= b;
			case at_most:      return a <= b;
			case equal_to:     return a == b;
			case not_equal_to: return a != b;
		}
		
		return true;
	}
	
}
