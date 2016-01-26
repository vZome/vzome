
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PointToPointTranslation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class TranslationTool extends TransformationTool
{
    public TranslationTool( String name, Selection selection, RealizedModel realized, Tool.Registry tools, Point originPoint )
    {
        super( name, selection, realized, tools, originPoint );
    }
    
    public String getDefaultName( String baseName )
    {
        return "translation along X axis";
    }

    public void perform() throws Command.Failure
    {
        Point p1 = null, p2 = null;
        boolean correct = true;
        if ( ! isAutomatic() )
            for ( Iterator<Manifestation> mans = mSelection .iterator(); mans .hasNext(); ) {
                Manifestation man = mans .next();
                unselect( man );
                if ( man instanceof Connector )
                {
                    if ( p2 != null )
                    {
                        correct = false;
                        break;
                    }
                    if ( p1 == null )
                        p1 = (Point) ((Connector) man) .getConstructions() .next();
                    else
                        p2 = (Point) ((Connector) man) .getConstructions() .next();
                }
            }
        
        if ( p1 == null )
        {
            if ( isAutomatic() )
            {
                p1 = originPoint;
                AlgebraicField field = originPoint .getField();
                AlgebraicVector xAxis = field .basisVector( 3, AlgebraicVector .X );
                AlgebraicNumber scale = field .createPower( 3 );
                scale = scale .times( field .createRational( new int[]{ 2, 1 } ) );
                xAxis = xAxis .scale( scale );
                p2 = new FreePoint( xAxis );
            }
            else
            {
                correct = false;
            }
        }
        else if ( p2 == null )
        {
            p2 = p1;
            p1 = this.originPoint;
        }

        if ( ! correct )
            throw new Command.Failure( "translation tool requires start and end points, or just an end point" );

        this .transforms = new Transformation[ 1 ];
        transforms[ 0 ] = new PointToPointTranslation( p1, p2 );

        defineTool();
    }

    protected String getXmlElementName()
    {
        return "TranslationTool";
    }

    public String getCategory()
    {
        return "translation";
    }
}
