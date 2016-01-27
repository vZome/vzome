

package com.vzome.core.commands;

import java.util.ArrayList;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.zomic.Interpreter;
import com.vzome.core.zomic.ZomicASTCompiler;
import com.vzome.core.zomic.ZomicException;
import com.vzome.core.zomic.parser.ErrorHandler;
import com.vzome.core.zomic.program.Walk;
import com.vzome.core.zomic.program.ZomicStatement;

/**
 * @author Scott Vorthmann
 */
public class CommandExecuteZomicScript extends AbstractCommand
{
	@Override
    public void setFixedAttributes( AttributeMap attributes, XmlSaveFormat format )
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

    private static final Object[][] ATTR_SIGNATURE = new Object[][]{ { SCRIPT_ATTR, ZomicStatement.class } };

	@Override
    public Object[][] getParameterSignature()
    {
        return PARAM_SIGNATURE;
    }

	@Override
    public Object[][] getAttributeSignature()
    {
        return ATTR_SIGNATURE;
    }

	@Override
    public ConstructionList apply( ConstructionList parameters, AttributeMap attrs, ConstructionChanges effects )
    throws Command.Failure
    {
        String script = (String) attrs .get( SCRIPT_ATTR );
        ArrayList<String> errors = new ArrayList<>();
		ErrorHandler errorHandler = new ErrorHandler.Default( errors );
//        Parser parser = new Parser( (IcosahedralSymmetry) symmetry );
//		ZomicStatement program = parser .parse(
//            new ByteArrayInputStream( script .getBytes() ), errorHandler, "" );
        ZomicASTCompiler compiler = new ZomicASTCompiler(symmetry);
        Walk program = compiler.compile( script, errorHandler );
        if ( !errors.isEmpty() ) {
            throw new Failure( errors.get(0) );
		}

        ConstructionList result = new ConstructionList();
        if ( parameters .size() != 1 )
            throw new Failure( "start parameter must be a single connector" );
        Construction c = parameters .get( 0 );
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
