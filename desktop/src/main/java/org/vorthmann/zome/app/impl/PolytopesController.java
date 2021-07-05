
package org.vorthmann.zome.app.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Quaternion;
import com.vzome.core.algebra.VefVectorExporter;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetries4D;
import com.vzome.core.math.symmetry.WythoffConstruction;

public class PolytopesController extends DefaultController
{
    private final DocumentModel model;

    private String group = "H4";

    private final String[] groups;

    private boolean[] generateEdge = new boolean[]{ false, false, false, true };
    private boolean[] renderEdge = new boolean[]{ true, true, true, true };
    private AlgebraicNumber[] edgeScales = new AlgebraicNumber[4];
    private final AlgebraicField field;
    private final AlgebraicNumber defaultScaleFactor;
    private final Symmetries4D fieldApp;

    public PolytopesController( DocumentModel document )
    {
        this .model = document;
        this .field = document .getField();
        this .fieldApp = document .getFieldApplication();
        this .defaultScaleFactor = field .createPower( Direction .USER_SCALE + 2 );
        for (int i = 0; i < edgeScales.length; i++)
        {
            edgeScales[ i ] = field .one();
        }
        // TODO: get the list from the field itself
        if ( null == document .getFieldApplication() .getQuaternionSymmetry( "H_4" ) ) {
            groups = new String[]{ "A4", "B4/C4", "D4", "F4" };
            group = "F4";
        } else {
            groups = new String[]{ "A4", "B4/C4", "D4", "F4", "H4" };
            group = "H4";
        }
    }

    @Override
    public void doAction( String action ) throws Exception
    {
        switch ( action ) {

        case "setQuaternion":
            /*
             *   The old way:
             *     With no symmetry axis set, the 120-cell comes out with medium blue struts.
             *     With a short blue symmetry axis set, it comes out with double short blue struts.
             *   We don't need to scale that way now, but we do need to scale in a predictable way.
             *   The new way:
             *     A quaternion value of (1,0,0,0) produces a 120-cell with medium blue struts, as before.
             *     With no quaternion strut selected, the quaternion value defaults to (1,0,0,0).
             *     With a single short blue selected as the quaternion, the quaternion is (0,1,0,0) or similar.
             */
            Segment strut = model .getSelectedSegment();
            if ( strut != null ) {
                AlgebraicVector vector = strut .getOffset();
                OrbitSource symm = model .getSymmetrySystem();
                Axis zone = symm .getAxis( vector );
                AlgebraicNumber len = zone .getLength( vector );
                len = zone .getOrbit() .getLengthInUnits( len );
                vector = zone .normal() .scale( len );
                VectorController vc = (VectorController) super .getSubController( "quaternion" );
                vc .setVector( vector .inflateTo4d() );
            } else {
                // use whatever value the quaternion has from before
            }
            return;

        default:
            break;
        }
        if ( "generate".equals( action ) )
        {
            int index = encodeBits( this .generateEdge );
            int edgesToRender = encodeBits( this .renderEdge );
            VectorController vc = (VectorController) super .getSubController( "quaternion" );
            AlgebraicVector quaternion = vc .getVector() .scale( this .defaultScaleFactor );
            Map<String, Object> params = new HashMap<>();
            params .put( "groupName", group );
            params .put( "renderGroupName", group );
            params .put( "index", index );
            params .put( "edgesToRender", edgesToRender );
            params .put( "edgeScales", edgeScales );
            params .put( "quaternion", quaternion );
            model .doEdit( "Polytope4d", params );
        }
        else if ( action .startsWith( "setGroup." ) )
        {
            group = action .substring( "setGroup." .length() );
        }
        else if ( action .startsWith( "edge." ) )
        {
            String edgeName = action .substring( "edge." .length() );
            int edge = Integer .parseInt( edgeName );
            boolean state = generateEdge[ edge ];
            generateEdge[ edge ] = ! state;
        }
        else if ( action .startsWith( "render." ) )
        {
            String edgeName = action .substring( "render." .length() );
            int edge = Integer .parseInt( edgeName );
            boolean state = renderEdge[ edge ];
            renderEdge[ edge ] = ! state;
        }
        else
            super.doAction( action );
    }
    
    private static int encodeBits( boolean[] bits )
    {
        int result = 0;
        for ( int i = 0; i < 4; i++ )
        {
            if ( bits[ i ] )
                result += 1 << i;
        }
        return result;
    }

    @Override
    public void doFileAction( String command, File file )
    {
        try {
            Writer out = new FileWriter( file );
            try {
                int index = encodeBits( this .generateEdge );
                int edgesToRender = encodeBits( this .renderEdge );
                VectorController vc = (VectorController) super .getSubController( "quaternion" );
                AlgebraicVector quaternion = vc .getVector() .scale( this .defaultScaleFactor );
                quaternion = quaternion .scale( field .createPower( -5 ) );
                Quaternion rightQuat = new Quaternion( field, quaternion );
                VefVectorExporter exporter = new VefVectorExporter( out, this .field );
                this .fieldApp .constructPolytope( group, index, edgesToRender, this .edgeScales, new WythoffConstruction.Listener()
                {
                    @Override
                    public Object addVertex( AlgebraicVector v )
                    {
                        AlgebraicVector projected = rightQuat .leftMultiply( v );
                        exporter .exportPoint( projected );
                        return projected;
                    }
                    
                    @Override
                    public Object addEdge( Object p1, Object p2 )
                    {
                        exporter .exportSegment( (AlgebraicVector) p1, (AlgebraicVector) p2 );
                        return null;
                    }
                    
                    @Override
                    public Object addFace( Object[] vertices )
                    {
                        return null;
                    }
                });
                exporter .finishExport();
            } finally {
                out.close();
            }
        }
        catch ( IOException e ) {
            mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[] { e } );
        }
    }

    @Override
    public String[] getCommandList( String listName )
    {
        return this .groups;
    }

    @Override
    public String getProperty( String propName )
    {
        if ( "group" .equals( propName ) )
        {
            return this .group;
        }
        else if ( propName .startsWith( "edge." ) )
        {
            String edgeName = propName .substring( "edge." .length() );
            int edge = Integer .parseInt( edgeName );
            return Boolean .toString( generateEdge[ edge ] );
        }
        else if ( propName .startsWith( "render." ) )
        {
            String edgeName = propName .substring( "render." .length() );
            int edge = Integer .parseInt( edgeName );
            return Boolean .toString( renderEdge[ edge ] );
        }
        else
            return super .getProperty( propName );
    }

    @Override
    public Controller getSubController( String name )
    {
        switch ( name ) {

        default:
            return super.getSubController( name );
        }
    }
}
