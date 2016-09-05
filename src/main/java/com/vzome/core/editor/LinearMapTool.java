
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.construction.ChangeOfBasis;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class LinearMapTool extends TransformationTool
{
	private static final String CATEGORY = "linear map";
	
	public static class Factory extends AbstractToolFactory implements ToolFactory
	{
		public Factory( EditorModel model, UndoableEdit.Context context )
		{
			super( model, context );
		}

		@Override
		protected boolean countsAreValid( int total, int balls, int struts, int panels )
		{
			return ( total == 7 && balls == 1 && struts == 6 )
				|| ( total == 4 && balls == 1 && struts == 3 );
		}

		@Override
		public Tool createToolInternal( int index )
		{
			return new LinearMapTool( CATEGORY + "." + index, getSelection(), getModel(), null, false );
		}

		@Override
		protected boolean bindParameters( Selection selection, SymmetrySystem symmetry )
		{
			// TODO check for linear independence
			return true;
		}
	}

	private final boolean originalScaling;

    public LinearMapTool( String name, Selection selection, RealizedModel realized, Point originPoint )
    {
        this( name, selection, realized, originPoint, false );
    }

    public LinearMapTool(
        String name, Selection selection, RealizedModel realized, Point originPoint, boolean originalScaling )
    {
        super( name, selection, realized, null, originPoint );
        this.originalScaling = originalScaling;
    }

	// Not quite the same as overriding equals since the tool name is not compared
    // We're basically just checking if the tool's input parameters match
	public boolean hasEquivalentParameters( Object that )
	{
		if (this == that) {
			return true;
		}
		if (!super.equals(that)) {
			return false;
		}
		if (getClass() != that.getClass()) {
			return false;
		}
		LinearMapTool other = (LinearMapTool) that;
		if (originalScaling != other.originalScaling) {
			return false;
		}
		return true;
	}

    protected String checkSelection( boolean prepareTool )
    {
        Segment[] oldBasis = new Segment[3];
        Segment[] newBasis = new Segment[3];
        int index = 0;
        boolean correct = true;
        Point center = null;
        for (Manifestation man : mSelection) {
        	if ( prepareTool )
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
                if ( index >= 6 ) {
                    correct = false;
                    break;
                }
                if ( index / 3 == 0 )
                {
                    oldBasis[ index % 3 ] = (Segment) man .getConstructions() .next();
                }
                else
                {
                    newBasis[ index % 3 ] = (Segment) man .getConstructions() .next();
                }
                ++index;
            }
        }
                
        correct = correct && ( ( index == 3) || ( index == 6 ) );
        if ( !correct )
            return "linear map tool requires three adjacent, non-parallel struts (or two sets of three) and a single (optional) center ball";

        if ( prepareTool ) {
        	if ( center == null )
        		center = this.originPoint;
        	this .transforms = new Transformation[ 1 ];
        	if ( index == 6 )
                // TODO validate linear independence of oldBasis
        		transforms[ 0 ] = new ChangeOfBasis( oldBasis, newBasis, center );
        	else
        		transforms[ 0 ] = new ChangeOfBasis( oldBasis[ 0 ], oldBasis[ 1 ], oldBasis[ 2 ], center, originalScaling );
        }
        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "LinearTransformTool";
    }

    @Override
    public String getCategory()
    {
        return CATEGORY;
    }

    @Override
    public String getDefaultName( String baseName )
    {
        return "SHOULD NEVER HAPPEN";
    }
}
