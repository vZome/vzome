
package com.vzome.core.edits;


import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Polygon;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;


/**
 * Work in progress, to help someone create correctly oriented surfaces for vZome part export,
 * or for StL 3D printing export.
 * 
 * @author Scott Vorthmann
 *
 */
public class ReversePanel extends ChangeManifestations
{
    @Override
    public void perform() throws Failure
    {
        if ( panel != null )
        {
            if ( this .mSelection .manifestationSelected( panel ) )
                unselect( panel );
            Polygon polygon = (Polygon) panel .getFirstConstruction();
            unmanifestConstruction( polygon );
            // TODO complete this
        }
        redo();
    }

    private final Panel panel;
        
    public ReversePanel( Manifestation singlePanel, EditorModel editor )
    {
        super( editor );
        if ( singlePanel != null )
            this .panel = (Panel) singlePanel;
        else
            this .panel = null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "ReversePanel";
    }

}
