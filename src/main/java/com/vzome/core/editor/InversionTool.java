
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import com.vzome.core.commands.Command;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PointReflection;
import com.vzome.core.construction.Transformation;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class InversionTool extends TransformationTool
{
    public InversionTool( String name, Selection selection, RealizedModel realized, Point originPoint )
    {
        super( name, selection, realized, null, originPoint );
    }
	
	public static class Factory extends AbstractToolFactory implements ToolFactory
	{
		private transient Connector center;  // TODO use these in createToolInternal()
		private transient Symmetry symmetry;
		
		public Factory( EditorModel model, UndoableEdit.Context context )
		{
			super( model, context );
		}

		@Override
		protected boolean countsAreValid( int total, int balls, int struts, int panels )
		{
			return ( total == 1 && balls == 1 );
		}

		@Override
		public Tool createToolInternal( int index )
		{
			return new InversionTool( "point reflection." + index, getSelection(), getModel(), null );
		}

		@Override
		protected boolean bindParameters( Selection selection, SymmetrySystem symmetry )
		{
			this .symmetry = symmetry .getSymmetry();
			assert selection .size() == 1;
        	for ( Manifestation man : selection )
        		center = (Connector) man;
			return true;
		}
	}
	
    @Override
    public void perform() throws Command.Failure
    {
    	String error = checkSelection( true );
    	if ( error != null )
    		// the old way of creating tools, validating the selection after the user action
    		throw new Command.Failure( error );
    	else
    		defineTool();
	}

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        Point center = null;
        if ( ! isAutomatic() )
        	for (Manifestation man : mSelection) {
        		if ( prepareTool )
        			unselect( man );
        		if ( man instanceof Connector )
        		{
        			if ( center != null )
        				return "more than one center selected";
        			center = (Point) ((Connector) man) .getConstructions() .next();
        		}
        		else if ( ! prepareTool )
        			return "panel or strut selected";
        	}
        
        if ( center == null ) {
        	if ( prepareTool ) {
        		center = originPoint;
        		this .addParameter( center );
        	}
        	else
        		return "No symmetry center selected";
        }
    
    	if ( prepareTool ) {
    		this .transforms = new Transformation[ 1 ];
    		transforms[ 0 ] = new PointReflection( center );
    	}
        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "InversionTool";
    }

    @Override
    public String getCategory()
    {
        return "point reflection";
    }

    @Override
    public String getDefaultName( String baseName )
    {
        return "point reflect through origin";
    }
}
