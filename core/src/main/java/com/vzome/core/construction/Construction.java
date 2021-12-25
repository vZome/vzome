

package com.vzome.core.construction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;

  


/*
 * I have made the following tentative determinations:
 * 
 * 1. There is such a thing as a free point, even if there's no way currently
 *     to manipulate it in all three dimensions at once.  Dragging will always
 *     use a "current grid" - a plane plus a grid scale, not stored in the model.
 * 
 * 2. Basic, from-where-you-stopped strut building is modeled with an "anchored segment",
 *     construction, which has only a "start" point parent, and a "segment end" point
 *     construction that can be (auto) derived from it.  This is simpler, I think, than the
 *     "offset point" with a 2-ended segment derived... less redundancy.
 * 
 * 3. Dragging a derivative construction drags the "nearest", most constrained ancestor
 *     from those ancestors that are not fully constrained.  If all candidate ancestors
 *     are free, the current grid is used as a constraint.
 * 
 * 4. Transformation is derived from its parameters, but not from its inputs.  It shows up
 *     as a derivative of its parameters.  It has derivatives of its own, one for each input.
 *     Each transformed derivative has its prototype as a parent, but not its Transformation;
 *     the transformation is treated as an attribute.  This implies a collection of
 *     "Transformed<construction>" constructions, probably one each for point, segment,
 *     polygon, and polyhedron.
 * 
 * 5. Any partially or fully constrained construction may have attributes (not parents)
 *     that can be edited, usually by constrained dragging.  This is very distinct from
 *     dragging of constructions as described in #3.  Attributes may be axis, color, scale,
 *     length (etc?), and combinations thereof.  The "anchored segment" construction described
 *     in #2 has attributes axis, scale, and length.
 * 
 * @author Scott Vorthmann
 */



/**
 * @author Scott Vorthmann
 */
public abstract class Construction
{
    protected final AlgebraicField field;
    
    /**
     * true for "impossible" constructions
     */
    private boolean mImpossible = false;
        
    protected Construction( AlgebraicField field )
    {
        this.field = field;
    }
    
    public AlgebraicField getField()
    {
        return field;
    }
    
    private int mIndex = -1;
    
    public void setIndex( int index )
    {
        mIndex = index;
    }
    
    public int getIndex()
    {
        return mIndex;
    }

    public boolean isImpossible()
    {
        return mImpossible;
    }
    
    public void setImpossible( boolean value )
    {
        mImpossible = value;
    }

    public abstract boolean is3d();

    /**
     * Update the state variables (like location) of this construction
     * according to the current parameters and attributes.
     * 
     * This function does NOT propagate updates to derivatives,
     * nor does it notify listeners or otherwise drive rendering.
     * 
     * @return true if the state changed.
     */
    protected abstract boolean mapParamsToState();


    public abstract Element getXml( Document doc );

    
    // here we accommodate loading vZome files that recorded command failures in their history
    
    private boolean failed = false;

    private Color color;
    
    public void setFailed()
    {
        failed = true;
    }

    public boolean failed()
    {
        return failed;
    }

    public void setColor( Color color )
    {
        this .color = color;
    }

    public Color getColor()
    {
        return this .color;
    }
    
    public String getSignature()
    {
        return "";
    }
}
