package java.util;

import def.js.Math;

public class UUID
{
    private final String value;
    
    public UUID()
    {
        this .value = Double.toString( Math.random() );
    }

    public static UUID randomUUID()
    {
        return new UUID();
    }

    public String toString()
    {
        return this .value;
    }
}
