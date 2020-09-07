package com.vzome.core.edits;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Bivector3d;
import com.vzome.core.algebra.Vector3d;
import com.vzome.core.commands.Command;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class SelectByPlane extends SelectByBoundary
{
    public static final String NAME = "SelectByPlane";
    
    @Override
    protected String getXmlElementName() {
        return NAME;
    }

    private Bivector3d plane;

    private AlgebraicVector anchor;
    
    private int desiredOrientation;

    public SelectByPlane( EditorModel editor )
    {
        super( editor );
    }

    @Override
    public String usage()
    {
        return  "This command requires four selected connectors.\n\n" +
                "The first three connectors must not be collinear,\n" +
                "so that they define a plane.\n" +
                "The fourth connector must lie outside of that plane,\n" + 
                "and defines which half space you wish to select.\n\n" +
                "All parts that are completely within that half-space will be selected.\n";
    }

    @Override
    public void perform() throws Command.Failure
    {
        setOrderedSelection( true );
        super.perform();
    }

    @Override
    protected String setBoundary()
    {
        AlgebraicVector p1 = null, p2 = null, p3 = null, p4 = null;
        for (Manifestation man : mSelection) {
            if ( man instanceof Connector )
            {
                if ( p1 == null )
                {
                    p1 = man .getLocation();
                    continue;
                }
                if ( p2 == null )
                {
                    p2 = man .getLocation();
                    continue;
                }
                if ( p3 == null )
                {
                    p3 = man .getLocation();
                    continue;
                }
                if ( p4 == null )
                {
                    p4 = man .getLocation();
                    continue;
                }
                else
                {
                    return "You have selected more than four connectors.";
                }
            }
        }

        if ( p4 == null )
            return "You have selected fewer than four connectors.";

        Vector3d v1 = new Vector3d( p2 .minus( p1 ) );
        Vector3d v2 = new Vector3d( p3 .minus( p1 ) );
        this .plane = v1 .outer( v2 );
        this .anchor = p1;
        this .desiredOrientation = orient( p4 );

        if ( this .desiredOrientation == 0 ) {
            return "Your last selected connector lies in the plane of the other three.";
        }
        
        return null;
    }

    private int orient( AlgebraicVector point )
    {
        AlgebraicVector diff = point .minus( anchor );
        Vector3d v = new Vector3d( diff );
        AlgebraicNumber volume = plane .outer( v );
        if ( volume .isZero() )
            return 0;
        else
        {
            double volD = volume .evaluate();
            return (volD > 0d)? 1 : -1;
        }
    }

    @Override
    protected boolean boundaryContains( AlgebraicVector v )
    {
        int orientation = orient( v );
        return ( orientation == 0 ) || ( orientation == this .desiredOrientation );
    }
}
