
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.tools;

import java.util.Arrays;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.ChangeManifestations;
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
            Construction result = transform .transform( c );
            if ( result == null )
                continue;
            result .setColor( c .getColor() ); // just for consistency
            Manifestation m = applyTool .manifestConstruction( result );
            if ( m != null )  // not sure why, but this happens
                applyTool .colorManifestation( m, c .getColor() );
        }
        applyTool .redo();
    }

    @Override
    public void performSelect( Manifestation man, ChangeManifestations applyTool ) {};

    @Override
    public void unselect( Manifestation man, boolean ignoreGroups )
    {
        Construction c = man .getFirstConstruction();
        this .addParameter( c );

        super .unselect( man, ignoreGroups );
    }

    protected boolean isAutomatic() {
        return this .getId() .contains( ".auto/" );
    }
}
