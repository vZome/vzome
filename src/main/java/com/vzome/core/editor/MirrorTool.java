
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Plane;
import com.vzome.core.construction.PlaneExtensionOfPolygon;
import com.vzome.core.construction.PlaneFromNormalSegment;
import com.vzome.core.construction.PlaneReflection;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.construction.Transformation;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class MirrorTool extends TransformationTool
{
    private final ModelRoot modelRoot;

    public MirrorTool( String name, ModelRoot modelRoot, Selection selection, RealizedModel realized, Tool.Registry tools, Point originPoint )
    {
        super( name, selection, realized, tools, originPoint );
        this .modelRoot = modelRoot;
    }

    public void perform() throws Command.Failure
    {
        Point center = null;
        Segment axis = null;
        Polygon mirrorPanel = null;
        if ( isAutomatic() )
        {
            center = originPoint;
            AlgebraicField field = originPoint .getField();
            AlgebraicVector xAxis = field .basisVector( 3, AlgebraicVector .X );
            Point p2 = new FreePoint( xAxis, modelRoot );
            axis = new SegmentJoiningPoints( center, p2 );
        }
        else
            for ( Iterator mans = mSelection .iterator(); mans .hasNext(); ) {
                Manifestation man = (Manifestation) mans .next();
                unselect( man );
                if ( man instanceof Connector )
                {
                    if ( center != null )
                    {
                        break;
                    }
                    center = (Point) ((Connector) man) .getConstructions() .next();
                }
                else if ( man instanceof Strut )
                {
                    if ( axis != null )
                    {
                        break;
                    }
                    axis = (Segment) ((Strut) man) .getConstructions() .next();
                }
                else if ( man instanceof Panel )
                {
                    if ( mirrorPanel != null )
                    {
                        break;
                    }
                    mirrorPanel = (Polygon) ((Panel) man) .getConstructions() .next();
                }
            }
        if ( center == null )
            center = this.originPoint;
        
        Plane mirrorPlane = null;
        if ( axis != null && center != null && mirrorPanel == null )
            mirrorPlane = new PlaneFromNormalSegment( center, axis );
        else if ( axis == null && mirrorPanel != null )
            mirrorPlane = new PlaneExtensionOfPolygon( mirrorPanel );
        else
            throw new Command.Failure( "mirror tool requires a single panel,\n"
                                        + "or a single strut and a single (optional) center ball" );
    
        this .transforms = new Transformation[ 1 ];
        transforms[ 0 ] = new PlaneReflection( mirrorPlane );
    
        defineTool();
    }

    protected String getXmlElementName()
    {
        return "MirrorTool";
    }

    public String getCategory()
    {
        return "mirror";
    }

    public String getDefaultName( String baseName )
    {
        return "reflect through X axis";
    }
}
