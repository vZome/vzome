

package com.vzome.core.commands;

import java.util.Map;

import org.python.util.PythonInterpreter;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.math.symmetry.Symmetry;

/**
 * @author Scott Vorthmann
 */
public class CommandExecutePythonScript extends AbstractCommand
{
    public static final String SCRIPT_ATTR = "script";

    private static final Object[][] PARAM_SIGNATURE = new Object[][]{ { "start", Point.class } };

    private static final Object[][] ATTR_SIGNATURE = new Object[][]{ { SCRIPT_ATTR, String.class } };

    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }
    

    public ConstructionList apply( ConstructionList parameters, AttributeMap attrs, ConstructionChanges effects )
    throws Command.Failure
    {
        ConstructionList result = new ConstructionList();
        if ( parameters .size() != 1 )
            throw new Failure( "start parameter must be a single connector" );
        Construction c = parameters .get( 0 );
        if ( ! ( c instanceof Point ) )
            throw new Failure( "start parameter must be a connector" );
        Point pt1 = (Point) c;
        String script = (String) attrs .get( SCRIPT_ATTR );
        final Symmetry symm = (Symmetry) attrs .get( CommandTransform.SYMMETRY_GROUP_ATTR_NAME );

        ZomicVirtualMachine builder = new ZomicVirtualMachine( pt1, effects, symm );

        PythonInterpreter interp = new PythonInterpreter();
        interp .set( "zomicVM", builder );
        interp .exec( script );

        result .addConstruction( builder .getLastPoint() );
        return result;
    }
}
