package com.vzome.core.editor;

import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class OctahedralToolFactory extends AbstractToolFactory implements ToolFactory
{
	private transient Symmetry symmetry;

	public OctahedralToolFactory( EditorModel model, UndoableEdit.Context context )
	{
		super( model, context );
	}

	@Override
	protected boolean countsAreValid( int total, int balls, int struts, int panels )
	{
		return ( total == 1 && balls == 1 ) || ( total == 2 && balls == 1 && struts == 1 );
	}

	@Override
	public Tool createToolInternal( int index )
	{
		return new SymmetryTool( "octahedral." + index, symmetry, getSelection(), getModel(), null );
	}

	protected Symmetry getSymmetry()
	{
		return this .symmetry;
	}

	@Override
	protected boolean bindParameters( Selection selection, SymmetrySystem symmetrySystem )
	{
		this .symmetry = symmetrySystem .getSymmetry();
		int total = getSelection() .size();
		if ( symmetry instanceof IcosahedralSymmetry ) {
			if ( total != 2 )
				return false;
			for ( Manifestation man : selection )
        		if ( man instanceof Strut )
        		{
        			Axis zone = symmetry .getAxis( ((Strut) man) .getOffset() );
        			if ( zone == null )
        				return false;
        			switch ( zone .getDirection() .getName() ) {
					case "blue":
					case "green":
						return true;

					default:
						return false;
					}
        		}
			return false; // should never get here
		}
		else {
			return total == 1;
		}
	}
}