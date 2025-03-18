

package com.vzome.core.commands;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.Point;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
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
            symmetry = (IcosahedralSymmetry) ((XmlSymmetryFormat) format) .parseSymmetry( "icosahedral" );
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

        ConstructionList result = new ConstructionList();
        if ( parameters .size() != 1 )
            throw new Failure( "start parameter must be a single connector" );
        Construction c = parameters .get( 0 );
        if ( ! ( c instanceof Point ) )
            throw new Failure( "start parameter must be a connector" );
		
        Point pt1 = (Point) c;
        try {
            symmetry .interpretScript( script, "zomic", pt1, symmetry, effects );
        } catch (Exception e) {
            throw new Failure( e.getMessage(), e );
        }
        
        // TODO this probably breaks things unless I find a way to implement it.
        // The builder (ZomicVirtualMachine) is now an internal detail.
//        result .addConstruction( builder .getLastPoint() );
        return result;
    }
}
