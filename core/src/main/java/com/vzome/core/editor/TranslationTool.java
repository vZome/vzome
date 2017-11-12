
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import java.util.EnumSet;

import com.vzome.api.Tool.InputBehaviors;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PointToPointTranslation;
import com.vzome.core.construction.Transformation;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class TranslationTool extends TransformationTool
{
    public TranslationTool( String id, ToolsModel tools )
    {
        super( id, tools );
    }
    
	private static final String ID = "translation";
	private static final String LABEL = "Create a translation tool";
	private static final String TOOLTIP = "<p>" +
    		"Each tool moves the selected objects to a new location.<br>" +
    		"To create a tool, select two balls that are separated by<br>" +
    		"your desired translation offset.  Order of selection<br>" +
    		"matters: the first ball selected is the \"from\" location,<br>" +
    		"and the second is the \"to\" location.<br>" +
    		"<br>" +
    		"By default, the input selection will be moved to the new<br>" +
    		"location.  If you want to copy rather than move, you can<br>" +
    		"right-click after creating the tool, to configure it.<br>" +
		"</p>";
	
	public static class Factory extends AbstractToolFactory
	{
		public Factory( ToolsModel tools )
		{
			super( tools, null, ID, LABEL, TOOLTIP );
		}

		@Override
		protected boolean countsAreValid( int total, int balls, int struts, int panels )
		{
			return ( total == 2 && balls == 2 );
		}

		@Override
		public Tool createToolInternal( String id )
		{
			return new TranslationTool( id, getToolsModel() );
		}

		@Override
		protected boolean bindParameters( Selection selection )
		{
			return true;
		}
	}

	@Override
	public EnumSet<InputBehaviors> defaultInputBehaviors()
	{
		return EnumSet.of( InputBehaviors.DELETE );
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
            if ( isAutomatic() || isPredefined() )
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
        return ID;
    }
}
