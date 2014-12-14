

package com.vzome.core.construction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;


/**
 * @author Scott Vorthmann
 * 
 * The root in a graph of constructions.  All "free" constructions in the graph
 * should derive directly from this construction.
 */
public class ModelRoot extends Construction
{
    public ModelRoot( AlgebraicField field )
    {
        super( field );
    }

    public void attach() {}

    public void detach() {}

    public void accept( Visitor v )
    {
        v .visitModelRoot( this );
    }

    protected boolean mapParamsToState()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "modelRoot" );
        return result;
    }

}
