package java.util;

import def.js.Math;

public class UUID
{
    private final String value;
        
    private UUID( String s )
    {
        this.value = s;
    }

    public static UUID randomUUID()
    {
        return new UUID( Double.toString( Math.random() ) );
    }

    public String toString()
    {
        return this .value;
    }
    
    public static UUID fromString( String s )
    {
        return new UUID( s );
    }
}
