
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
        
        Set panels = new HashSet();
        Set struts = new HashSet();
        Set balls = new HashSet();
        for ( Iterator mans = selection .iterator(); mans .hasNext(); ) {
            Manifestation man = (Manifestation) mans .next();
            if ( man instanceof Strut )
                struts .add( man );
            else if ( man instanceof Connector )
                balls .add( man );
            else if ( withPanels && ( man instanceof Panel ) )
                panels .add( man );
        }
        for ( Iterator bs = balls .iterator(); bs .hasNext(); ) {
            Connector ball = (Connector) bs .next();
            int[] /*AlgebraicVector*/ loc = ball .getLocation();
            for ( Iterator ms = model .getAllManifestations(); ms .hasNext(); ) {
                Manifestation man = (Manifestation) ms .next();
                if ( man .getRenderedObject() == null )
                    continue;  // hidden!
                if ( man instanceof Strut && ! struts .contains( man ) ) {
                    Strut strut = (Strut) man;
                    if ( Arrays .equals( strut .getLocation(), loc ) 
                            || Arrays .equals( strut .getEnd(), loc ) )
                    	select( strut );
                }
                else if ( withPanels && ( man instanceof Panel ) && ! panels .contains( man ) ) {
                    Panel panel = (Panel) man;
                    for (Iterator iterator = panel .getVertices(); iterator.hasNext(); ) {
                        int[] vertex = (int[]) iterator.next();
                        if ( Arrays .equals( vertex, loc ) )
                            select( panel );
                    }
                }
            }
        }
        for ( Iterator ss = struts .iterator(); ss .hasNext(); ) {
            Strut strut = (Strut) ss .next();
            int[] /*AlgebraicVector*/ loc = strut .getLocation();
            int[] /*AlgebraicVector*/ end = strut .getEnd();
            for ( Iterator ms = model .getAllManifestations(); ms .hasNext(); ) {
                Manifestation man = (Manifestation) ms .next();
                if ( man .getRenderedObject() == null )
                    continue;  // hidden!
                if ( man instanceof Connector && ! balls .contains( man ) ) {
                    int[] /*AlgebraicVector*/ bloc = man .getLocation();
                    if ( Arrays .equals( loc, bloc ) || Arrays .equals( end, bloc ) )
                    	select( man );
                }
            }
        }
        if ( withPanels )
            for ( Iterator ss = panels .iterator(); ss .hasNext(); ) {
                Panel panel = (Panel) ss .next();
                for (Iterator iterator = panel .getVertices(); iterator.hasNext(); ) {
                    int[] loc = (int[]) iterator.next();
                    for ( Iterator ms = model .getAllManifestations(); ms .hasNext(); ) {
                        Manifestation man = (Manifestation) ms .next();
                        if ( man .getRenderedObject() == null )
                            continue;  // hidden!
                        if ( man instanceof Connector && ! balls .contains( man ) ) {
                            int[] /*AlgebraicVector*/ bloc = man .getLocation();
                            if ( Arrays .equals( loc, bloc ) )
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
