
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

public class PolytopesController extends DefaultController
{
	private final DocumentModel model;
    
    private String group = "H4";
    
    private final String[] groups;

    private boolean[] generateEdge = new boolean[]{ false, false, false, true };
    private boolean[] renderEdge = new boolean[]{ true, true, true, true };
    private AlgebraicNumber[] edgeScales = new AlgebraicNumber[4];
    private final VectorController rotationQuaternion;

    public PolytopesController( DocumentModel document )
    {
        this .model = document;
        AlgebraicField field = document .getField();
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
    		// TODO get current selected strut vector
    		Segment strut = model .getSelectedSegment();
    		if ( strut != null ) {
    			AlgebraicVector vector = strut .getOffset();
        		rotationQuaternion .setVector( vector .inflateTo4d() );
    		} else {
    			rotationQuaternion .setVector( model .getField() .basisVector( 4, 0 ) );
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
            AlgebraicVector quaternion = rotationQuaternion .getVector();
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
