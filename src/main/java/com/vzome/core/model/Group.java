
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.model;

import java.util.ArrayList;

public class Group extends ArrayList
{
    private Group mContainer;
    
    public Group getContainer()
    {
        return mContainer;
    }
    
    public void setContainer( Group container )
    {
        mContainer = container;
    }
}
