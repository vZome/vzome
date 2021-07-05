
package com.vzome.core.model;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Group extends ArrayList<GroupElement> implements GroupElement
{
    private Group mContainer;
    
    public Group getContainer()
    {
        return mContainer;
    }
    
    @Override
    public void setContainer( Group container )
    {
        mContainer = container;
    }
}
