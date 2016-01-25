package com.vzome.core.model;

/**
 * @author David Hall
 */
public interface GroupElement {
    // Elements of a Group can be a mix of Manifestations and Groups
    // This is mainly a marker interface to indicate what classes can be added to a Group
    // so the collection can be made type safe rather than just using Object.
    // but since both classes have a setContainer() method with the same signature,
    // I figured I'd add it to this interface to simplify the code.
    void setContainer( Group container );
}
