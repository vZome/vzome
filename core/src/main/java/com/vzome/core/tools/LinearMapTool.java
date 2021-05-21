
package com.vzome.core.tools;


import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.ChangeOfBasis;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class LinearMapTool extends TransformationTool
{
	private static final String CATEGORY = "linear map";
	private static final String LABEL = "Create a linear map tool";
	private static final String TOOLTIP = "<p>" +
    		"<b>For experts and Linear Algebra students...</b><br>" +
    		"<br>" +
    		"Each tool applies a linear transformation to the selected<br>" +
    		"objects, possibly rotating, stretching, and compressing.  To<br>" +
    		"create a tool, select a ball as the center of the mapping,<br>" +
    		"three struts (in order) to define the input basis, and three<br>" +
    		"more struts to define the output basis.<br>" +
    		"<br>" +
    		"You can omit the input basis if it would consist of three<br>" +
    		"identical blue struts at right angles; the three struts you<br>" +
    		"select will be interpreted as the output basis.<br>" +
    		"<br>" +
    		"By default, the input selection will be removed, and replaced<br>" +
    		"with the transformed equivalent.  If you want to keep the inputs,<br>" +
    		"you can right-click after creating the tool, to configure it.<br>" +
		"</p>";

	public static class Factory extends AbstractToolFactory
	{
		private final boolean originalScaling;
		
		public Factory( ToolsModel tools, Symmetry symmetry, boolean originalScaling )
		{
			super( tools, symmetry, CATEGORY, LABEL, TOOLTIP );
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

	private final boolean originalScaling;

    public LinearMapTool( String name, ToolsModel tools, boolean originalScaling )
    {
        super( name, tools );
        this.originalScaling = originalScaling;
        this .setInputBehaviors( false, true );
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
                center = (Point) ((Connector) man) .getFirstConstruction();
            }
            else if ( man instanceof Strut )
            {
                if ( index >= 6 ) {
                    correct = false;
                    break;
                }
                if ( index / 3 == 0 )
                {
                    oldBasis[ index % 3 ] = (Segment) man .getFirstConstruction();
                }
                else
                {
                    newBasis[ index % 3 ] = (Segment) man .getFirstConstruction();
                }
                ++index;
            }
        }

        // Note, we are not checking coincidence of the segments in each basis, which means that a failure may
        //  come later during ChangeOfBasis.mapParamsToState().  However, we are now checking that during
        //  bindParameters, so it will be impossible for the tool to be created with disjoint bases.
        //  We cannot check that here, since existing saved edits are more permissive, requiring only the
        //  first two segments of each basis to be coincident.  (See ChangeOfBasis.mapParamsToState().)
        
        // TODO: We also aren't checking for linear independence of the input basis, and we need to, either
        //   above in bindParameters or here!
        
        correct = correct && ( ( index == 3) || ( index == 6 ) );
        if ( !correct )
            return "linear map tool requires three adjacent, non-parallel struts (or two sets of three) and a single (optional) center ball";

        if ( prepareTool ) {
            if ( center == null )
                center = this.originPoint;
            this .transforms = new Transformation[ 1 ];
            if ( index == 6 )
                transforms[ 0 ] = new ChangeOfBasis( oldBasis, newBasis, center );
            else
                transforms[ 0 ] = new ChangeOfBasis( oldBasis[ 0 ], oldBasis[ 1 ], oldBasis[ 2 ], center, originalScaling );
        }
        return null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "LinearTransformTool"; // confusing, but this is the new default serialization for LinearMapTool
    }

    @Override
    public String getCategory()
    {
        return CATEGORY;
    }
}
