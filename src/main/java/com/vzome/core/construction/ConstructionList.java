/*
 * Created on Dec 13, 2003
 *
 */
package com.vzome.core.construction;

import java.util.ArrayList;




/**
 * A selection in the model.
 */
public class ConstructionList extends ArrayList
{
    private static final Construction[] PROTOTYPE = new Construction[0];
    
    public ConstructionList addConstruction( Construction ball )
    {
        add( ball );
        return this;
    }
    
    public ConstructionList removeConstruction( Construction ball )
    {
        remove( ball );
        return this;
    }
    
    public Construction[] getConstructions()
    {
        return (Construction[]) toArray( PROTOTYPE );
    }
    
}
