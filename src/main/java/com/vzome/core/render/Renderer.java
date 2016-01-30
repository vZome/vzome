
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.render;

import java.lang.reflect.Method;

import com.vzome.core.viewing.ViewModel;

public abstract class Renderer
{
    protected abstract void render( ViewModel viewModel );
    
    public void render( Renderable renderable )
    {
        try {
            Method method = this .getClass() .getDeclaredMethod( "render", renderable .getClass() );
            method .invoke( this, renderable );
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
