
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


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
    
    @Override
    public String getDefaultName( String baseName )
    {
        return "translation along X axis";
    }

	@Override
	public boolean isValidForSelection()
	{
		return null == checkSelection( false );
	}

    @Override
    public void perform() throws Command.Failure
    {
    	String error = checkSelection( true );
    	if ( error != null )
    		throw new Command.Failure( error );
    	else
    		defineTool();
	}

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        Point p1 = null, p2 = null;
        boolean correct = true;
        if ( ! isAutomatic() )
        	for (Manifestation man : mSelection) {
        		if ( prepareTool )
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
        		} else if ( !prepareTool ) {
        			// allowed in legacy documents
        			return "Only balls can be selected for this tool.";
        		}
        	}
        
        if ( p1 == null )
        {
            if ( isAutomatic() || this .getName() .equals( "translation.builtin/move right" ) )
            {
                p1 = originPoint;
        		this .addParameter( p1 );
                AlgebraicField field = originPoint .getField();
                AlgebraicVector xAxis = field .basisVector( 3, AlgebraicVector .X );
                AlgebraicNumber scale = field .createPower( 3 );
                scale = scale .times( field .createRational( 2 ) );
                xAxis = xAxis .scale( scale );
                p2 = new FreePoint( xAxis );
        		this .addParameter( p2 );
            }
            else
            {
                correct = false;
            }
        }
        else if ( p2 == null )
        	if ( prepareTool )
        	{
        		p2 = p1;
        		p1 = this.originPoint;
        	}
        	else // doing isValidForSelection
        		correct = false;

        if ( ! correct )
            return "translation tool requires start and end points, or just an end point";

        if ( prepareTool ) {
            this .transforms = new Transformation[ 1 ];
            transforms[ 0 ] = new PointToPointTranslation( p1, p2 );
        }
        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "TranslationTool";
    }

    @Override
    public String getCategory()
    {
        return "translation";
    }
}
