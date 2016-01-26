
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Scaling;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class ScalingTool extends SymmetryTool
{
    public String getCategory()
    {
        return "scaling";
    }

    public ScalingTool( String name, Symmetry symmetry, Selection selection, RealizedModel realized, Tool.Registry tools, Point originPoint )
    {
        super( name, symmetry, selection, realized, tools, originPoint );
    }

    public void perform() throws Command.Failure
    {
        Segment s1 = null, s2 = null;
        Point center = null;
        boolean correct = true;
        for ( Iterator<Manifestation> mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = mans .next();
            unselect( man );
            if ( man instanceof Connector )
            {
                if ( center != null )
                {
                    correct = false;
                    break;
                }
                center = (Point) ((Connector) man) .getConstructions() .next();
            }
            else if ( man instanceof Strut )
            {
                if ( s2 != null )
                {
                    correct = false;
                    break;
                }
                if ( s1 == null )
                    s1 = (Segment) ((Strut) man) .getConstructions() .next();
                else
                    s2 = (Segment) ((Strut) man) .getConstructions() .next();
            }
        }
        
        if ( center == null )
            center = this.originPoint;
        
        correct = correct && s2 != null;
        if ( !correct )
            throw new Command.Failure( "scaling tool requires before and after struts, and a single optional center" );

        Axis zone1 = symmetry .getAxis( s1 .getOffset() );
        Axis zone2 = symmetry .getAxis( s2 .getOffset() );
        Direction orbit = zone1 .getDirection();
        if ( orbit != zone2 .getDirection() )
            throw new Command.Failure( "before and after struts must be from the same orbit" );

        this .transforms = new Transformation[ 1 ];
        transforms[ 0 ] = new Scaling( s1, s2, center, symmetry );

        defineTool();
    }

    protected String getXmlElementName()
    {
        return "ScalingTool";
    }

    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        String symmName = element .getAttribute( "symmetry" );

        if ( symmName == null || symmName .isEmpty() )
        {
            element .setAttribute( "symmetry", "icosahedral" );
            logBugAccommodation( "scaling tool serialized with no symmetry; assuming icosahedral" );
        }
        super .setXmlAttributes( element, format );
    }
}
