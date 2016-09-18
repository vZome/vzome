
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import org.vorthmann.ui.Configuration;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Color;
import com.vzome.core.render.RenderedModel.OrbitSource;
import com.vzome.core.render.Shapes;
import com.vzome.desktop.controller.CameraController;

public class SymmetryController extends DefaultController// implements RenderedModel.OrbitSource
{
    @Override
    public String getProperty( String string )
    {
        if ( "renderingStyle" .equals( string ) )
        {
            return this .symmetrySystem .getStyle() .getName();
        }
        else if ( string .startsWith( "orbitColor." ) )
        {
            String name = string .substring( "orbitColor." .length() );
            Direction dir = buildOrbits .getDirection( name );
            Color color = getColor( dir );
            return color .toString();
        }
        return super.getProperty( string );
    }
    
    private SymmetrySystem symmetrySystem;
    public OrbitSet availableOrbits;
    public OrbitSet snapOrbits;
    public OrbitSet buildOrbits;
    private CameraController.Snapper snapper;
    
    public OrbitSetController availableController;
    public OrbitSetController snapController;
    public OrbitSetController buildController;
    
    public Map<Direction, LengthController> orbitLengths = new HashMap<>();
        
    public Symmetry getSymmetry()
    {
        return this .symmetrySystem .getSymmetry();
    }
    
    public CameraController.Snapper getSnapper()
    {
        return snapper;
    }
    
    public SymmetryController( Controller parent, SymmetrySystem model )
    {
		this .setNextController( parent );
        this .symmetrySystem = model;
    }
    
    public Controller asController()
    {
    	if ( availableOrbits != null )
    		return this; // idempotency
        Symmetry symmetry = this.symmetrySystem .getSymmetry();
        availableOrbits = new OrbitSet( symmetry );
        snapOrbits = new OrbitSet( symmetry );
        buildOrbits = new OrbitSet( symmetry );
        snapper = new SymmetrySnapper( snapOrbits );
        for (Direction dir : symmetry .getOrbitSet()) {
            if ( dir .isStandard() )
            {
                availableOrbits .add( dir );
                snapOrbits .add( dir );
                Axis zone = dir .getAxis( 0, 0 );
                if ( zone .getRotationPermutation() != null )
                {
                    buildOrbits .add( dir );
                }
            }
            orbitLengths .put( dir, new LengthController( dir ) );
        }
        availableController = new OrbitSetController( availableOrbits, this .symmetrySystem .getOrbits(), this .symmetrySystem, false );
        availableController .setNextController( this );
        snapController = new OrbitSetController( snapOrbits, availableOrbits, this .symmetrySystem, false );
        snapController .setNextController( this );
        buildController = new OrbitSetController( buildOrbits, availableOrbits, this .symmetrySystem, true );
        buildController .setNextController( this );

        for (Direction dir : this .symmetrySystem .getOrbits()) {
            LengthController lengthModel = new LengthController( dir );
            lengthModel .setNextController( buildController );
            orbitLengths .put( dir, lengthModel );
        }
        if ( propertyIsTrue( "disable.known.directions" ) )
        	this .symmetrySystem .disableKnownDirection();
        return this;
    }
    

    @Override
    public Configuration getConfiguration( String name )
    {
        if ( name .equals( "availableOrbits" ) )
            return availableController;
        if ( name .equals( "snapOrbits" ) )
            return snapController;
        if ( name .equals( "buildOrbits" ) )
            return buildController;
        return null;
    }

    @Override
    public Controller getSubController( String name )
    {
        if ( name .equals( "availableOrbits" ) )
            return availableController;
        if ( name .equals( "snapOrbits" ) )
            return snapController;
        if ( name .equals( "buildOrbits" ) )
            return buildController;
        if ( name .startsWith( "length." ) )
        {
            String dirName = name .substring( "length." .length() );
            Direction dir = this .symmetrySystem .getOrbits() .getDirection( dirName );
            return getLengthController( dir );
        }
        return null;
    }
    
    @Override
    public String[] getCommandList( String listName )
    {
        if ( "styles" .equals( listName ) )
        {
            return this .symmetrySystem .getStyleNames();
        }
        if ( listName .equals( "orbits" ) )
        {
            String[] result = new String[ this .symmetrySystem .getOrbits() .size() ];
            int i = 0;
            for (Direction orbit : this .symmetrySystem .getOrbits()) {
                result[ i ] = orbit .getName();
                i++;
            }
            return result;
        }
        if ( "tool.templates" .equals( listName ) )
        {
            return new String[]{ "icosahedral", "octahedral", "tetrahedral", "rotation", "mirror" };
        }
        return new String[0];
    }

    @Override
    public void doAction( String action, ActionEvent e ) throws Exception
    {
        switch ( action ) {

        case "configureDirections":
            super .doAction( action, e );
            break;
           
        case "rZomeOrbits":
        case "predefinedOrbits":
        case "setAllDirections":
            availableController .doAction( action, e );
            break;
        
        default:
        	if ( action .startsWith( "setStyle." ) ) {
        		String styleName =  action .substring( "setStyle." .length() );
        		this .symmetrySystem .setStyle( styleName );
        	}
    		super .doAction( action, e ); // falling through so that rendering gets adjusted
        }
    }
    
    private LengthController getLengthController( Direction dir )
    {
        LengthController result = orbitLengths .get( dir );
        if ( result == null && dir != null )
        {
        	result = new LengthController( dir );
        	result .setNextController( buildController );
            orbitLengths .put( dir, result );
            availableOrbits .add( dir );
        }
        return result;
    }

    public OrbitSet getOrbits()
    {
        return this .symmetrySystem .getOrbits();
    }
    
    public OrbitSource getOrbitSource()
    {
        return this .symmetrySystem;
    }
    
    // TODO this should take over all functions of symmetry.getAxis()
    
    public Axis getZone( AlgebraicVector offset )
    {
    	return this .symmetrySystem .getAxis( offset );
    }
    
    public Color getColor( Direction orbit )
    {
        return this .symmetrySystem .getColor( orbit );
    }

	public OrbitSet getBuildOrbits()
	{
		return this .buildOrbits;
	}

    public Shapes getRenderingStyle()
    {
        return this .symmetrySystem .getRenderingStyle();
    }
}
