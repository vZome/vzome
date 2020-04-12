/*
 * Created on Jun 25, 2003
 */
package com.vzome.core.viewing;

import java.io.File;
import java.io.InputStream;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.parts.StrutGeometry;
import com.vzome.core.parts.ZomicPolyhedronModelInterpreter;
import com.vzome.core.parts.ZomicStrutGeometry;
import com.vzome.core.zomic.ZomicASTCompiler;
import com.vzome.core.zomic.program.ZomicStatement;


public class ScriptedShapes extends AbstractShapes
{
    private static final String NODE_SCRIPT = "connector.zomic";
    
    private final AbstractShapes fallback;

    public ScriptedShapes( File prefsFolder, String pkgName, String name, IcosahedralSymmetry symm )
    {
        this( prefsFolder, pkgName, name, symm, null );
    }

    public ScriptedShapes( File prefsFolder, String pkgName, String name, IcosahedralSymmetry symm, AbstractShapes fallback )
    {
        super( pkgName, name, symm );
        this.fallback = fallback;
    }
    
    @Override
    protected StrutGeometry createStrutGeometry( Direction dir )
    {
        ZomicStrutGeometry zsg = new ZomicStrutGeometry( mPkgName, dir, mSymmetry );
        if ( zsg .isDefined() )
            return zsg;
        if ( fallback != null )
            return fallback .createStrutGeometry( dir );
        return super .createStrutGeometry( dir );
    }
    
    @Override
    protected Polyhedron buildConnectorShape( String pkgName )
    {
        String prefix = ZomicStrutGeometry.SCRIPT_PREFIX + pkgName + "/";
        InputStream nodeScript = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(
                        prefix + NODE_SCRIPT );
        if ( nodeScript == null )
            throw new IllegalStateException( "missing script: " + prefix
                    + NODE_SCRIPT );
        //ZomicStatement connScript = Parser.parse( nodeScript, (IcosahedralSymmetry) mSymmetry );
        ZomicStatement connScript = ZomicASTCompiler.compile( nodeScript, (IcosahedralSymmetry) mSymmetry );
        AlgebraicNumber zero = mSymmetry .getField() .zero();
        ZomicPolyhedronModelInterpreter zpmi = new ZomicPolyhedronModelInterpreter( mSymmetry,
                connScript, zero, mSymmetry.getPermutation( 0 ),
                Symmetry.PLUS );
        return zpmi.getPolyhedron();
    }
}
