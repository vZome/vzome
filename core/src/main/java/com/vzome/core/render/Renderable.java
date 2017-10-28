
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.render;

public interface Renderable
{
    void render( Object renderer );
    
    void setRenderedObject( Object renderedObject );
    
    Object getRenderedObject();
}
