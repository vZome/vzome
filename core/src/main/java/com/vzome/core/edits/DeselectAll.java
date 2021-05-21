
package com.vzome.core.edits;


import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.api.ChangeSelection;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;

public class DeselectAll extends ChangeSelection
{
    @Override
    public void perform() throws Failure
    {        
        for ( Manifestation man : this .mSelection ) {
            unselect( man, true );
        }
        super.perform();
    }

    public DeselectAll( EditorModel editor )
    {
        super( editor .getSelection() );
    }
    
    @Override
    protected boolean groupingAware()
    {
        return true;
    }

    @Override
    protected String getXmlElementName()
    {
        return "DeselectAll";
    }

}
