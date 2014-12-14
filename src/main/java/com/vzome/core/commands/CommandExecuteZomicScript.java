

package com.vzome.core.commands;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.zomic.Interpreter;
import com.vzome.core.zomic.ZomicException;
import com.vzome.core.zomic.parser.ErrorHandler;
import com.vzome.core.zomic.parser.Parser;
import com.vzome.core.zomic.program.Anything;

/**
 * @author Scott Vorthmann
 */
public class CommandExecuteZomicScript extends AbstractCommand
{
    public void setFixedAttributes( Map attributes, XmlSaveFormat format )
    {
        super.setFixedAttributes( attributes, format );

        symmetry = (IcosahedralSymmetry) attributes .get( CommandTransform .SYMMETRY_GROUP_ATTR_NAME );
        if ( symmetry == null )
            symmetry = (IcosahedralSymmetry) format .parseSymmetry( "icosahedral" );
    }

    public CommandExecuteZomicScript( IcosahedralSymmetry symmetry )
    {
        super();
        this.symmetry = symmetry;
    }

    public CommandExecuteZomicScript()
    {
        super();
        this.symmetry = null;
    }

    private IcosahedralSymmetry symmetry;

    public static final String SCRIPT_ATTR = "script";

    private static final Object[][] PARAM_SIGNATURE = new Object[][]{ { "start", Point.class } };

    private static final Object[][] ATTR_SIGNATURE = new Object[][]{ { SCRIPT_ATTR, Anything.class } };

    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }
    

    public ConstructionList apply( ConstructionList parameters, Map attrs, ConstructionChanges effects )
    throws Command.Failure
    {
        String script = (String) attrs .get( SCRIPT_ATTR );
        Parser parser = new Parser( (IcosahedralSymmetry) symmetry );
        List errors = new ArrayList();
        Anything program = parser .parse(
            new ByteArrayInputStream( script .getBytes() ), new ErrorHandler.Default( errors ), "" );
        if ( errors.size() > 0 )
            throw new Failure( (String) errors .get(0) );
        
//        try {
//            PrintWriter out = new PrintWriter( System.out );
//            program .accept( new PrintVisitor( out ) );
//            out .close();
//        } catch ( ZomicException e1 ) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
        
        ConstructionList result = new ConstructionList();
        if ( parameters .size() != 1 )
            throw new Failure( "start parameter must be a single connector" );
        Construction c = (Construction) parameters .get( 0 );
        if ( ! ( c instanceof Point ) )
            throw new Failure( "start parameter must be a connector" );
        Point pt1 = (Point) c;

        ZomicVirtualMachine builder = new ZomicVirtualMachine( pt1, effects, symmetry );
        try {
            program .accept( new Interpreter( builder, symmetry ) );
        } catch ( ZomicException e ) {
            throw new Failure( e );
        }
        result .addConstruction( builder .getLastPoint() );
        return result;
    }
}
