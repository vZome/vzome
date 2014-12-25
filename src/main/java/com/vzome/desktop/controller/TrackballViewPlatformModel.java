
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.desktop.controller;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.vzome.core.viewing.ViewModel;

public class TrackballViewPlatformModel extends ViewPlatformModel
{
    private ViewPlatformModel mDelegate;

    public TrackballViewPlatformModel( ViewPlatformModel delegate )
    {
        mDelegate = delegate;
    }

    public void addViewpointRotation( Quat4d rotation )
    {
        super.addViewpointRotation( rotation );
        mDelegate .addViewpointRotation( rotation );
    }

    public void setViewDirection( Vector3f lookDir, Vector3f upDir )
    {
        super.setViewDirection( lookDir, upDir );
        mDelegate .setViewDirection( lookDir, upDir );
    }
    
    public ViewModel restoreView( ViewModel view )
    {
    	ViewModel vm = super .restoreView( view );
    	
    	Vector3d lookdir = new Vector3d(), updir = new Vector3d();
    	getViewOrientation( lookdir, updir );
    	mDelegate .setViewDirection( new Vector3f( lookdir ), new Vector3f( updir ) );
        
        return vm;
    }


}
