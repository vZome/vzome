

package com.vzome.core.construction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Marker extends Construction
{
    private Construction mTarget;

    public Marker( Construction target )
    {
        super( target .field );
        // the usual pattern is to call mapParamsToState()
        mTarget = target;
    }
    
    public Construction getTarget()
    {
        return mTarget;
    }

    public void attach()
    {
        mTarget .addDerivative( this );
        }

    public void detach()
    {
        mTarget .addDerivative( this );
    }

    public void accept( Visitor v )
    {}

    protected boolean mapParamsToState()
    {
        return false;
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "marker" );
        return result;
    }

}
