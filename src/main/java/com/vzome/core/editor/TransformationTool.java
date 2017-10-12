
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Arrays;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.construction.TransformedPoint;
import com.vzome.core.construction.TransformedPolygon;
import com.vzome.core.construction.TransformedSegment;
import com.vzome.core.model.Manifestation;

public abstract class TransformationTool extends Tool
{
	@Override
    public void prepare( ChangeManifestations applyTool ) {}

    @Override
	public void complete( ChangeManifestations applyTool ) {}
    
    @Override
    public boolean needsInput()
    {
    	return true;
    }

    protected Transformation[] transforms;
        
    protected Point originPoint;

    public TransformationTool( String id, ToolsModel tools )
    {
        super( id, tools );
        
        this.originPoint = tools .getOriginPoint();
    }

	@Override
	public boolean equals( Object that )
	{
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (! that .getClass() .equals( this .getClass() ) ) {
			return false;
		}
		TransformationTool other = (TransformationTool) that;
		if (originPoint == null) {
			if (other.originPoint != null) {
				return false;
			}
		} else if (!originPoint.equals(other.originPoint)) {
			return false;
		}
		if (!Arrays.equals(transforms, other.transforms)) {
			return false;
		}
		return true;
	}
    
    @Override
    public void performEdit( Construction c, ChangeManifestations applyTool )
    {
        for (Transformation transform : transforms) {
            Construction result = null;
            if (c instanceof Point) {
                result = new TransformedPoint(transform, (Point) c);
            } else if (c instanceof Segment) {
                result = new TransformedSegment(transform, (Segment) c);
            } else if (c instanceof Polygon) {
                result = new TransformedPolygon(transform, (Polygon) c);
            } else {
                // TODO handle other constructions 
            }
            if ( result == null )
                continue;
            applyTool .manifestConstruction( result );
        }
        applyTool .redo();
    }
    
    @Override
	public void performSelect( Manifestation man, ChangeManifestations applyTool ) {};

    @Override
	public void unselect( Manifestation man, boolean ignoreGroups )
    {
    	Construction c = man .getConstructions() .next();
    	this .addParameter( c );
    	
		super .unselect( man, ignoreGroups );
	}

	protected boolean isAutomatic() {
		return this .getId() .contains( ".auto/" );
	}
}
