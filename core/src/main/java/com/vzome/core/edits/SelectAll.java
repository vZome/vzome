
package com.vzome.core.edits;


import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.api.ChangeSelection;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class SelectAll extends ChangeSelection
{
    private final RealizedModel realizedModel;

    @Override
    public void perform() throws Failure
    {
        Connector originBall = null;
        final boolean ignoreGroups = true;
        for (Manifestation m : this.realizedModel) {
            if (m.isRendered()) {
                if (originBall == null && m instanceof Connector && m.getLocation().isOrigin()) {
                    originBall = (Connector) m;
                } else if (!this.mSelection.manifestationSelected(m)) {
                    select(m, ignoreGroups);
                }
            }
        }
        if (originBall != null) {
            if (this.mSelection.manifestationSelected(originBall)) {
                unselect(originBall, ignoreGroups);
                redo(); // commit the current selection state of all manifestations 
            }
            // originBall is the last manifestation to be selected
            select(originBall, ignoreGroups);
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
