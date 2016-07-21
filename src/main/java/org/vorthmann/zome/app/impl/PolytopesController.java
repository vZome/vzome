
//(c) Copyright 2013, Scott Vorthmann.

package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;

import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.math.symmetry.Direction;

public class PolytopesController extends DefaultController
{
	private final DocumentModel model;
    
    private String group = "H4";
    
    private final String[] groups;

    private boolean[] generateEdge = new boolean[]{ false, false, false, true };
    private boolean[] renderEdge = new boolean[]{ true, true, true, true };
    private AlgebraicNumber[] edgeScales = new AlgebraicNumber[4];
    private final VectorController rotationQuaternion;
    private final AlgebraicField field;
    private final AlgebraicNumber defaultScaleFactor, strutScaleFactor;

    public PolytopesController( DocumentModel document )
    {
        this .model = document;
        this .field = document .getField();
        this .defaultScaleFactor = field .createPower( Direction .USER_SCALE + 2 );
        this .strutScaleFactor = field .createAlgebraicNumber( 1, 0, 2, - Direction .USER_SCALE );
        for (int i = 0; i < edgeScales.length; i++)
        {
            edgeScales[ i ] = field .createPower( 0 );
        }
        // TODO: get the list from the field itself
        if ( null == field .getQuaternionSymmetry( "H_4" ) ) {
        	groups = new String[]{ "A4", "B4/C4", "D4", "F4" };
        	group = "F4";
        } else {
        	groups = new String[]{ "A4", "B4/C4", "D4", "F4", "H4" };
        	group = "H4";
        }
        rotationQuaternion = new VectorController( field .basisVector( 4, 0 ) );
    }

    @Override
    public void doAction( String action, ActionEvent e ) throws Exception
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
    			vector = vector .scale( this .strutScaleFactor );
        		rotationQuaternion .setVector( vector .inflateTo4d() );
    		} else {
    			AlgebraicVector vector =  model .getField() .basisVector( 4, 0 );
    			rotationQuaternion .setVector( vector .inflateTo4d() );
    		}
			return;

		default:
			break;
		}
        if ( "generate".equals( action ) )
        {
            int index = 0;
            int edgesToRender = 0;
            for ( int i = 0; i < 4; i++ )
            {
                if ( generateEdge [ i ] )
                    index += 1 << i;
                if ( renderEdge[ i ] )
                    edgesToRender += 1 << i;
            }
            AlgebraicVector quaternion = rotationQuaternion .getVector() .scale( this .defaultScaleFactor );
            model .generatePolytope( group, group, index, edgesToRender, quaternion, edgeScales );
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
            super.doAction( action, e );
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

    	case "rotation":
			return this .rotationQuaternion;

		default:
			return super.getSubController( name );
		}
	}
}
