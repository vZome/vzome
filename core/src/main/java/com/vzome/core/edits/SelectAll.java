
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.api.ChangeSelection;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class SelectAll extends ChangeSelection
{
    private final RealizedModel realizedModel;

    @Override
    public void perform() throws Failure
    {
        for ( Manifestation m : this.realizedModel ) {
            if ( m .isRendered() )
            {
                if ( ! this .mSelection .manifestationSelected( m ) )
                    select( m, true );
            }
        }
        super.perform();
    }

    public SelectAll( EditorModel editor )
    {
        super( editor .getSelection() );
        this .realizedModel = editor .getRealizedModel();
    }
    
    @Override
    protected boolean groupingAware()
    {
        return true;
    }

    @Override
    protected String getXmlElementName()
    {
        return "SelectAll";
    }

}
