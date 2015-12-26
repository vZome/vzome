
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.ArrayList;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.model.RealizedModel;

public abstract class ChangeConstructions extends ChangeManifestations
{
    public ChangeConstructions( Selection selection, RealizedModel realized, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );
    }
    
    public void addConstruction( Construction c ) {}
    
    protected void removeConstruction( Construction c ) {}
    
    // TODO use this in CommandEdit as well
    //
    protected class NewConstructions extends ArrayList implements ConstructionChanges
    {
        public NewConstructions() {}

        public void constructionAdded( Construction c )
        {
            addConstruction( c );
            manifestConstruction( c );
            redo();
        }
    
        public void constructionHidden( Construction c )
        {
            throw new UnsupportedOperationException();
        }
    
        public void constructionRemoved( Construction c )
        {
            throw new UnsupportedOperationException();
        }
    
        public void constructionRevealed( Construction c )
        {
            throw new UnsupportedOperationException();
        }
    }
}
