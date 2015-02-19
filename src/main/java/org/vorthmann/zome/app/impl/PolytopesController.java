
//(c) Copyright 2013, Scott Vorthmann.

package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.Polytope4d;

public class PolytopesController extends DefaultController
{
    private final DocumentModel model;
    
    private String group = "H4";

    private boolean[] generateEdge = new boolean[]{ false, false, false, true };
    private boolean[] renderEdge = new boolean[]{ true, true, true, true };
    private AlgebraicNumber[] edgeScales = new AlgebraicNumber[4];

    public PolytopesController( DocumentModel document )
    {
        this .model = document;
        AlgebraicField field = document .getField();
        for (int i = 0; i < edgeScales.length; i++)
        {
            edgeScales[ i ] = field .createPower( 0 );
        }
    }

    public void doAction( String action, ActionEvent e ) throws Exception
    {
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
            model .generatePolytope( group, group, index, edgesToRender, edgeScales );
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

    public String[] getCommandList( String listName )
    {
        return Polytope4d .getSupportedGroups();
    }

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
}
