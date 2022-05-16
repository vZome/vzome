package com.vzome.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.StringTokenizer;

import com.vzome.desktop.api.Controller;

public class ControllerTesting
{
    public static void assertStateAfter( Controller controller, String[] actions, String[] propertyValues )
    {
        if ( actions != null )
            doActions( controller, actions );
        String[] properties = new String[]{ "half", "scale", "unitText", "unitIsCustom", "lengthText" };
        for ( int i = 0; i < properties.length; i++ ) {
            String actual = controller .getProperty( properties[ i ] );
            String expected = propertyValues[ i ];
            assertEquals( expected, actual );
        }
    }

    public static Controller getSubController( Controller controller, String... names )
    {
        for ( String name : names )
        {
            controller = controller .getSubController( name );
            if ( controller == null ) {
                return null;
            }
        }
        return controller;
    }
    
    public static void doActions( Controller controller, String... actions )
    {
        try {
            for ( String actionOrChange : actions ) {
                if ( actionOrChange .contains( "=" ) ) {
                    StringTokenizer tokens = new StringTokenizer( actionOrChange, "=" );
                    String name = tokens .nextToken();
                    String value = tokens .nextToken();
                    controller .setProperty( name, value );
                }
                else
                    controller .actionPerformed( controller, actionOrChange );
            }
        } catch (Exception e) {
            fail( e .getMessage() );
        }
    }
    
    public static void assertPropertyValue( Controller controller, String name, String value )
    {
        String actual = controller .getProperty( name );
        assertEquals( value, actual );
    }
}
