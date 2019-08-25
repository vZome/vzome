
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.ChangeSelection;
import com.vzome.core.editor.EditorModel;


public class ValidateSelection extends ChangeSelection
{
    @Override
    public void perform() throws Failure
    {
        if ( mSelection .isEmpty() )
            throw new Failure( "selection is empty" );
    }

    public ValidateSelection( EditorModel editor )
    {
        super( editor .getSelection() ); 
    }

    @Override
    protected String getXmlElementName()
    {
        return "ValidateSelection";
    }

}
