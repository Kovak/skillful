package net.asrex.skillful.effect;

/**
 * A definition wrapper for {@link Ability} implementations. At present, it does
 * not add any additional features to the standard {@link EffectDefinition} base
 * class, though this may change in the future.
 */
public abstract class AbilityDefinition extends EffectDefinition {
	
	// override to prevent accidental Effect returns - difficult to remove from
	// player NBT
	@Override
	protected abstract Ability doCreate();
	
}
