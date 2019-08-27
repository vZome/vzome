
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;

import java.util.HashSet;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.ChangeSelection;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class SelectNeighbors extends ChangeSelection
{
    private final EditorModel editor;
    private boolean withPanels = false;

    public SelectNeighbors( EditorModel editor )
    {
        super( editor .getSelection() );
        this.editor = editor;
    }
    
    // TODO: add configure() to support "SelectNeighbors/withPanels"
    
    public void perform() throws Failure
    {
        RealizedModel model = this.editor .getRealizedModel();
        Set<Panel> panels = new HashSet<>();
        Set<Strut> struts = new HashSet<>();
        Set<Connector> balls = new HashSet<>();
        for (Manifestation man : mSelection) {
            if ( man instanceof Strut )
                struts .add( (Strut) man );
            else if ( man instanceof Connector )
                balls .add( (Connector) man );
            else if ( withPanels && ( man instanceof Panel ) )
                panels .add( (Panel) man );
        }
        for (Connector ball : balls) {
            AlgebraicVector loc = ball .getLocation();
            for (Manifestation man : model) {
                if ( ! man .isRendered() )
                    continue;  // hidden!
                if ( man instanceof Strut && ! struts .contains( man ) ) {
                    Strut strut = (Strut) man;
                    if ( loc .equals( strut .getLocation() ) 
                            || loc .equals( strut .getEnd() ) )
                        select( strut );
                }
                else if ( withPanels && ( man instanceof Panel ) && ! panels .contains( man ) ) {
                    Panel panel = (Panel) man;
                    for (AlgebraicVector vertex : panel) {
                        if ( loc .equals( vertex ) ) {
                            select( panel );
                            // no need to continue the loop since we have already selected this panel.
                            break;
                        }
                    }
                }
            }
        }
        for (Strut strut : struts) {
            AlgebraicVector loc = strut .getLocation();
            AlgebraicVector end = strut .getEnd();
            for (Manifestation man : model) {
                if ( ! man .isRendered() )
                    continue;  // hidden!
                if ( man instanceof Connector && ! balls .contains( man ) ) {
                    AlgebraicVector bloc = man .getLocation();
                    if ( bloc .equals( loc ) || bloc .equals( end ) )
                        select( man );
                }
            }
        }
        if ( withPanels ) {
                for (Panel panel : panels) {
                for (AlgebraicVector loc : panel) {
                    for (Manifestation man : model) {
                        if ( man .isRendered() ) {// if not hidden!
                            if ( man instanceof Connector && ! balls .contains( man ) ) {
                                AlgebraicVector bloc = man .getLocation();
                                if ( bloc .equals( loc ) ) {
                                    select( man );
                                }
                            }
                        }
                    }
                }
            }
        }
        super .perform();
    }

    @Override
    protected String getXmlElementName()
    {
        return "SelectNeighbors";
    }

}
