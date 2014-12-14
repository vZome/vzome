
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;

import com.vzome.core.construction.ChangeOfBasis;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;

public class DefineTransformation extends ChangeSelection
{
    public DefineTransformation( Selection selection, EditorModel editor, boolean groupInSelection )
    {
        super( selection, groupInSelection );

        Segment[] oldBasis = new Segment[3];
        Segment[] newBasis = new Segment[3];
        int index = 0;
        for ( Iterator mans = selection .iterator(); mans .hasNext(); ) {
            Manifestation man = (Manifestation) mans .next();
            unselect( man );
            if ( man instanceof Strut )
            {
                if ( index >= 6 )
                    continue;
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
        if ( index < 6 )
            return;
               
        // now, construct the affine transformation
        Transformation transform = new ChangeOfBasis( oldBasis, newBasis, editor .getCenterPoint() );

        editor .setTransformation( transform );
    }
    
    protected String getXmlElementName()
    {
        return "DefineTransformation";
    }
}
