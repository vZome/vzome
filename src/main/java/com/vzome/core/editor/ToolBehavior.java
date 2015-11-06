package com.vzome.core.editor;

/**
 * @author David Hall
 */
public class ToolBehavior {
	// These masks are copied from ActionEvent, so ActionEvent.getModifiers() can be directly passed to the c'tor.
    // This allows a compile-time dependency on ActionEvent to be avoided.

    public static final int SHIFT_MASK = 1;
    public static final int CTRL_MASK = 2;
    public static final int META_MASK = 4;
    public static final int ALT_MASK = 8;

	// Assign standard generic meanings to given masks
    // Input behaviors
    public static final int SELECT_INPUTS_MASK = SHIFT_MASK;
    public static final int HIDE_INPUTS_MASK = CTRL_MASK;
    // Output behaviors
    public static final int DESELECT_OUTPUTS_MASK = ALT_MASK;
    public static final int JUST_SELECT_EXISTING_MASK = META_MASK;

	// Derived classes could add new behavior suggestions based on additional bitwise masks.
    // The masks could potentially even be unrelated to ActionEvent. 
    // Maybe use checkboxes or other UI features to control additional mask bits.
    private final int modifierFlags;

	// This c'tor is just to used as an initializer for a ToolBehavior variable so it won't be null.
    // There is intentionally not a method to change the modifiers after the c'tor. 
    // If this is needed, then replace the whole ToolBehavior object. (they're cheap)
    public ToolBehavior() {
        this(0);
    }

    public ToolBehavior(int modifiers) {
        modifierFlags = modifiers;
    }

    protected final boolean hasAllMaskBitsSet(int mask) {
        // be sure this works on multi-bit flage
        return (modifierFlags & mask) == mask;
    }

    protected final boolean hasAnyMaskBitsSet(int mask) {
        return (modifierFlags & mask) != 0;
    }

	// These methods provide a common generic meaning to each of the associated keyboard modifier keys.
    // They are based on the meanings of each mask as implemented in ApplyTool before this class was implemented.
    // The meanings are only suggestions. Actual behavior is implementation dependent.
    public boolean selectInputs() {
        return hasAllMaskBitsSet(SELECT_INPUTS_MASK);
    }

    public boolean hideInputs() {
        return hasAllMaskBitsSet(HIDE_INPUTS_MASK);
    }

    public boolean deselectOutputs() {
        return hasAllMaskBitsSet(DESELECT_OUTPUTS_MASK);
    }

    public boolean justSelectExisting() {
        return hasAllMaskBitsSet(JUST_SELECT_EXISTING_MASK);
    }

    // convenience methods
    public boolean hasAnyModifiers() {
        return (modifierFlags != 0);
    }

    public boolean hasAnyInputModifiers() {
        return hasAnyMaskBitsSet(SELECT_INPUTS_MASK | HIDE_INPUTS_MASK);
    }

    public boolean hasAnyOutputModifiers() {
        return hasAnyMaskBitsSet(DESELECT_OUTPUTS_MASK | JUST_SELECT_EXISTING_MASK);
    }

    // allows serialization
    public Integer getModifiers() {
        return modifierFlags;
    }

}
