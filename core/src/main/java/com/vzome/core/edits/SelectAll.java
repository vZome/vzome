
package com.vzome.core.edits;


import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.api.ChangeSelection;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class SelectAll extends ChangeSelection
{
    private final RealizedModel realizedModel;
    
    private boolean originLast = true; // default is the new behavior

    @Override
    public void perform() throws Failure
    {
        if ( this .originLast ) {
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
        } else
        {
            // The legacy behavior.  This is important for edit sequences like (SelectAll,JoinPoints).
            for ( Manifestation m : this.realizedModel ) {
                if ( m .isRendered() )
                {
                    if ( ! this .mSelection .manifestationSelected( m ) )
                        select( m, true );
                }
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
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
        String mode = xml .getAttribute( "originLast" );
        this .originLast = "true" .equals( mode );
    }
    
    @Override
    protected void getXmlAttributes( Element element )
    {
        if ( this .originLast )
            element .setAttribute( "originLast", "true" );
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
