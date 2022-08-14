package com.vzome.core.tools;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.ChangeOfBasis;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class LinearMapToolFactory extends AbstractToolFactory
{
	private final boolean originalScaling;
	
	public LinearMapToolFactory( ToolsModel tools, Symmetry symmetry, boolean originalScaling )
	{
		super( tools, symmetry, LinearMapTool.CATEGORY, LinearMapTool.LABEL, LinearMapTool.TOOLTIP );
		this .originalScaling = originalScaling;
	}

	@Override
	protected boolean countsAreValid( int total, int balls, int struts, int panels )
	{
		return ( total == 7 && balls == 1 && struts == 6 )
			|| ( total == 4 && balls == 1 && struts == 3 );
	}

	@Override
	public Tool createToolInternal( String id )
	{
		return new LinearMapTool( id, getToolsModel(), this .originalScaling );
	}

    @Override
    public Tool createTool()
    {
        Tool result = super.createTool();
        result .setCopyColors( false ); // Overriding true default, only for newly created tools
        return result;
    }

	@Override
	protected boolean bindParameters( Selection selection )
	{
	    // We can be strict here, requiring two sets of coincident struts, since this is called from the UI,
	    //   only when the countsAreValid() with one basis or two.
	    // For legacy edits, we don't want to be so strict, since we have always allowed the 3rd strut
	    //   of each basis to be detached.
        int index = 0;
	    Segment[] segments = new Segment[ 6 ];
	    for( Manifestation man: selection ) {
            if ( man instanceof Strut )
                segments[ index++ ] = (Segment) man .getFirstConstruction();
	    }
        AlgebraicVector c1 = ChangeOfBasis.findCommonVertex( segments[ 0 ], segments[ 1 ] );
        AlgebraicVector c2 = ChangeOfBasis.findCommonVertex( segments[ 2 ], segments[ 1 ] );
        if ( c1 == null || c2 == null || ! c1 .equals( c2 ) )
            return false;
        if ( index == 3 )
            // Only one basis was selected
            return true;
        c1 = ChangeOfBasis.findCommonVertex( segments[ 3 ], segments[ 4 ] );
        c2 = ChangeOfBasis.findCommonVertex( segments[ 5 ], segments[ 4 ] );
        if ( c1 == null || c2 == null || ! c1 .equals( c2 ) )
            return false;
        // TODO: verify linear independence of first 3 segments
		return true;
	}
}