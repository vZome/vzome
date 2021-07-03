
package com.vzome.core.editor;

import com.vzome.core.commands.AbstractCommand;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;

/**
 * This class is part of a possible solution to a problem in CommandQuaternionSymmetry that arises as part of
 * the removal of static Symmetry and AlgebraicField INSTANCEs.
 * 
 * I don't recall the details, but it has to do with overriding a particular method to have an opportunity to inject the right
 * H4 symmetry.
 * 
 * @author Scott Vorthmann
 *
 */
public class QuaternionCommandEdit extends CommandEdit
{
    private final QuaternionicSymmetry left, right;

    public QuaternionCommandEdit( AbstractCommand cmd, EditorModel editor, QuaternionicSymmetry left, QuaternionicSymmetry right )
    {
        super( cmd, editor );
        this.left = left;
        this.right = right;
    }

    // TODO construct this at the right time, and set attributes for apply
}
