
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;


public class ShowNormals extends ChangeManifestations
{
    public static final String NAME = "ShowNormals";

    @Override
	public void perform() throws Failure
    {
        // scale down 3 powers, and halve
        final AlgebraicNumber SCALE_DOWN = this .mManifestations .getField()
                .createAlgebraicNumber( 1, 0, 2, -3 );
        
        unselectConnectors();
        unselectStruts();
        
        for ( Panel panel : Manifestations .getPanels( mSelection ) ) {
            unselect( panel );
            
            AlgebraicVector centroid = panel .getCentroid();
            AlgebraicVector tip = centroid .plus( panel.getNormal().scale( SCALE_DOWN ) );

            Point p1 = new FreePoint( centroid );
            Point p2 = new FreePoint( tip );
            Segment s = new SegmentJoiningPoints( p1, p2 );

            select( manifestConstruction( p1 ) );
            select( manifestConstruction( p2 ) );
            select( manifestConstruction( s ) );
        }
        redo();
    }
    
    public ShowNormals( Selection selection, RealizedModel realized )
    {
        super( selection, realized, false );
    }

    @Override
    protected String getXmlElementName()
    {
        return NAME;
    }

}
