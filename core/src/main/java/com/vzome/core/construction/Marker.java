

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

    @Override
    protected boolean mapParamsToState()
    {
        return false;
    }

    @Override
    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "marker" );
        return result;
    }

    @Override
    public boolean is3d()
    {
        return mTarget .is3d();
    }

}
