
package com.vzome.core.tools;


import com.vzome.core.construction.ChangeOfBasis;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class LinearMapTool extends TransformationTool
{
	static final String CATEGORY = "linear map";
	static final String LABEL = "Create a linear map tool";
	static final String TOOLTIP = "<p>" +
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
