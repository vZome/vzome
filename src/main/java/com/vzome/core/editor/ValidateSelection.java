
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import com.vzome.core.commands.Command.Failure;


public class ValidateSelection extends ChangeSelection
{
    public void perform() throws Failure
    {
        if ( ! mSelection .iterator() .hasNext() )
            throw new Failure( "selection is empty" );
    }

    public ValidateSelection( Selection selection )
    {
        super( selection, false ); 
    }

    protected String getXmlElementName()
    {
        return "ValidateSelection";
    }

}
