
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class SelectNeighbors extends ChangeSelection
{
    public SelectNeighbors( Selection selection, RealizedModel model, boolean groupInSelection )
    {
        this( selection, model, groupInSelection, false );
    }

    public SelectNeighbors( Selection selection, RealizedModel model, boolean groupInSelection, boolean withPanels )
    {
        super( selection, groupInSelection ); 
        
        Set<Panel> panels = new HashSet<>();
        Set<Strut> struts = new HashSet<>();
        Set<Connector> balls = new HashSet<>();
        for ( Iterator<Manifestation> mans = selection .iterator(); mans .hasNext(); ) {
            Manifestation man = mans .next();
            if ( man instanceof Strut )
                struts .add( (Strut) man );
            else if ( man instanceof Connector )
                balls .add( (Connector) man );
            else if ( withPanels && ( man instanceof Panel ) )
                panels .add( (Panel) man );
        }
        for ( Iterator<Connector> bs = balls .iterator(); bs .hasNext(); ) {
            Connector ball = bs .next();
            AlgebraicVector loc = ball .getLocation();
            for ( Iterator<Manifestation> ms = model .iterator(); ms .hasNext(); ) {
                Manifestation man = ms .next();
                if ( man .getRenderedObject() == null )
                    continue;  // hidden!
                if ( man instanceof Strut && ! struts .contains( man ) ) {
                    Strut strut = (Strut) man;
                    if ( loc .equals( strut .getLocation() ) 
                            || loc .equals( strut .getEnd() ) )
                    	select( strut );
                }
                else if ( withPanels && ( man instanceof Panel ) && ! panels .contains( man ) ) {
                    Panel panel = (Panel) man;
                    for (Iterator<AlgebraicVector> iterator = panel .iterator(); iterator.hasNext(); ) {
                        AlgebraicVector vertex = iterator.next();
                        if ( loc .equals( vertex ) ) {
                            select( panel );
                            // no need to continue the loop since we have already selected this panel.
                            break;
                        }
                    }
                }
            }
        }
        for ( Iterator<Strut> ss = struts .iterator(); ss .hasNext(); ) {
            Strut strut = ss .next();
            AlgebraicVector loc = strut .getLocation();
            AlgebraicVector end = strut .getEnd();
            for ( Iterator<Manifestation> ms = model .iterator(); ms .hasNext(); ) {
                Manifestation man = ms .next();
                if ( man .getRenderedObject() == null )
                    continue;  // hidden!
                if ( man instanceof Connector && ! balls .contains( man ) ) {
                    AlgebraicVector bloc = man .getLocation();
                    if ( bloc .equals( loc ) || bloc .equals( end ) )
                    	select( man );
                }
            }
        }
        if ( withPanels )
            for ( Iterator<Panel> ss = panels .iterator(); ss .hasNext(); ) {
                Panel panel = ss .next();
                for (Iterator<AlgebraicVector> iterator = panel .iterator(); iterator.hasNext(); ) {
                    AlgebraicVector loc = iterator.next();
                    for ( Iterator<Manifestation> ms = model .iterator(); ms .hasNext(); ) {
                        Manifestation man = ms .next();
                        if ( man .getRenderedObject() == null )
                            continue;  // hidden!
                        if ( man instanceof Connector && ! balls .contains( man ) ) {
                            AlgebraicVector bloc = man .getLocation();
                            if ( bloc .equals( loc ) )
                                select( man );
                        }
                    }
                }
            }
    }

    protected String getXmlElementName()
    {
        return "SelectNeighbors";
    }

}
