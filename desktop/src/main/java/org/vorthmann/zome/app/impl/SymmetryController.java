
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Color;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.Shapes;
import com.vzome.desktop.controller.CameraController;

public class SymmetryController extends DefaultController
{
    @Override
    public String getProperty( String string )
    {
        switch ( string ) {
        
        case "name":
            return this .symmetrySystem .getName();

        case "renderingStyle":
            return this .symmetrySystem .getStyle() .getName();

        case "modelResourcePath":
            return this .symmetrySystem .getModelResourcePath();

        default:
            if ( string .startsWith( "orbitColor." ) )
            {
                String name = string .substring( "orbitColor." .length() );
                Direction dir = buildOrbits .getDirection( name );
                Color color = getColor( dir );
                return color .toString();
            }
            return super.getProperty( string );
        }
    }

    private SymmetrySystem symmetrySystem;
    public OrbitSet availableOrbits;
    public OrbitSet snapOrbits;
    public OrbitSet buildOrbits;
    public OrbitSet renderOrbits;
    private final CameraController.Snapper snapper;

    public OrbitSetController availableController;
    public OrbitSetController snapController;
    public OrbitSetController buildController;
    public OrbitSetController renderController;

    public Map<Direction, LengthController> orbitLengths = new HashMap<>();

    private final Map<String, Controller> symmetryToolFactories = new LinkedHashMap<>();
    private final Map<String, Controller> transformToolFactories = new LinkedHashMap<>();
    private final Map<String, Controller> linearMapToolFactories = new LinkedHashMap<>();
    private final RenderedModel renderedModel;

    public Symmetry getSymmetry()
    {
        return this .symmetrySystem .getSymmetry();
    }

    public CameraController.Snapper getSnapper()
    {
        return snapper;
    }

    public SymmetryController( Controller parent, SymmetrySystem model, RenderedModel mRenderedModel )
    {
        this .symmetrySystem = model;
        renderedModel = mRenderedModel;
        Symmetry symmetry = model .getSymmetry();
        availableOrbits = new OrbitSet( symmetry );
        snapOrbits = new OrbitSet( symmetry );
        buildOrbits = new OrbitSet( symmetry );
        renderOrbits = new OrbitSet( symmetry );
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
            renderOrbits .add( dir );
        }
        availableController = new OrbitSetController( availableOrbits, this .symmetrySystem .getOrbits(), this .symmetrySystem, false );
        this .addSubController( "availableOrbits", availableController );
        snapController = new OrbitSetController( snapOrbits, availableOrbits, this .symmetrySystem, false );
        this .addSubController( "snapOrbits", snapController );
        buildController = new OrbitSetController( buildOrbits, availableOrbits, this .symmetrySystem, true );
        this .addSubController( "buildOrbits", buildController );
        renderController = new OrbitSetController( renderOrbits, this .symmetrySystem .getOrbits(), this .symmetrySystem, false );
        this .addSubController( "renderOrbits", renderController );

        for ( Direction dir : this .symmetrySystem .getOrbits() ) {
            LengthController lengthModel = new LengthController( dir );
            buildController .addSubController( "length." + dir .getName(), lengthModel );
            orbitLengths .put( dir, lengthModel );
        }
        if ( parent .propertyIsTrue( "disable.known.directions" ) )
            this .symmetrySystem .disableKnownDirection();

        availableController .addPropertyListener( new PropertyChangeListener()
        {
            @Override
            public void propertyChange( PropertyChangeEvent event )
            {
                if ( "orbits" .equals( event .getPropertyName() ) )
                {
                    //                    properties() .firePropertyChange( event ); // just forwarding
                }
            }
        } );
    }

    @Override
    public String[] getCommandList( String listName )
    {
        switch ( listName ) {

        case "styles":

            return this .symmetrySystem .getStyleNames();

        case "orbits":

            String[] result = new String[ this .symmetrySystem .getOrbits() .size() ];
            int i = 0;
            for (Direction orbit : this .symmetrySystem .getOrbits()) {
                result[ i ] = orbit .getName();
                i++;
            }
            return result;

        case "symmetryToolFactories":

            // This will be called only once, before any relevant getSubController, so it is OK to do creations
            for ( Tool.Factory factory : this .symmetrySystem .getToolFactories( Tool.Kind.SYMMETRY ) )
                this .symmetryToolFactories .put( factory .getId(), new ToolFactoryController( factory ) );
            return this .symmetryToolFactories .keySet() .toArray( new String[]{} );

        case "transformToolFactories":

            // This will be called only once, before any relevant getSubController, so it is OK to do creations
            for ( Tool.Factory factory : this .symmetrySystem .getToolFactories( Tool.Kind.TRANSFORM ) )
                this .transformToolFactories .put( factory .getId(), new ToolFactoryController( factory ) );
            return this .transformToolFactories .keySet() .toArray( new String[]{} );

        case "linearMapToolFactories":

            // This will be called only once, before any relevant getSubController, so it is OK to do creations
            for ( Tool.Factory factory : this .symmetrySystem .getToolFactories( Tool.Kind.LINEAR_MAP ) )
                this .linearMapToolFactories .put( factory .getId(), new ToolFactoryController( factory ) );
            return this .linearMapToolFactories .keySet() .toArray( new String[]{} );

        case "builtInSymmetryTools":

            // This will be called only once, before any relevant getSubController, so it is OK to do creations
            List<String> toolNames = new ArrayList<>();
            for ( Tool tool : this .symmetrySystem .getPredefinedTools( Tool.Kind.SYMMETRY ) )
                toolNames .add( tool .getId() );
            return toolNames .toArray( new String[]{} );


        case "builtInTransformTools":

            // This will be called only once, before any relevant getSubController, so it is OK to do creations
            List<String> transformToolNames = new ArrayList<>();
            for ( Tool tool : this .symmetrySystem .getPredefinedTools( Tool.Kind.TRANSFORM ) )
                transformToolNames .add( tool .getId() );
            return transformToolNames .toArray( new String[]{} );

        default:
            return super .getCommandList( listName );
        }
    }

    @Override
    public Controller getSubController( String name )
    {
        switch ( name ) {

        case "availableOrbits":
            return availableController;

        case "snapOrbits":
            return snapController;

        case "buildOrbits":
            return buildController;

        case "renderOrbits":
            return renderController;

        default:
            if ( name .startsWith( "length." ) )
            {
                String dirName = name .substring( "length." .length() );
                Direction dir = this .symmetrySystem .getOrbits() .getDirection( dirName );
                return getLengthController( dir );
            }
            Controller result = this .symmetryToolFactories .get( name );
            if ( result != null )
                return result;
            result = this .transformToolFactories .get( name );
            if ( result != null )
                return result;
            result = this .linearMapToolFactories .get( name );
            if ( result != null )
                return result;
            return super .getSubController( name );
        }
    }

    @Override
    public void doAction( String action, ActionEvent e ) throws Exception
    {
        switch (action) {

        case "rZomeOrbits":
        case "predefinedOrbits":
        case "setAllDirections":
            availableController .doAction( action, e );
            break;

        case "ReplaceWithShape":
            action += "/" + this .symmetrySystem .getName() + ":" + this .symmetrySystem .getStyle() .getName();
            super .doAction( action, e );
            break;

        default:
            if ( action .startsWith( "setStyle." ) )
            {
                String styleName =  action .substring( "setStyle." .length() );
                this .symmetrySystem .setStyle( styleName );
                this .renderedModel .setShapes( this .symmetrySystem .getShapes() );
            }
            else {
                boolean handled = this .symmetrySystem .doAction( action );
                if ( ! handled )
                    super .doAction( action, e );
            }
            break;
        }
    }

    private LengthController getLengthController( Direction dir )
    {
        LengthController result = orbitLengths .get( dir );
        if ( result == null && dir != null )
        {
            result = new LengthController( dir );
            buildController .addSubController( "length." + dir .getName(), result );
            orbitLengths .put( dir, result );
            renderOrbits .add( dir );
            availableOrbits .add( dir );
        }
        return result;
    }

    public OrbitSet getOrbits()
    {
        return this .symmetrySystem .getOrbits();
    }

    // TODO: Can we get rid of this?  It is only needed by PreviewStrut.
    //   We should be able to accomplish the sync with PropertyChangeListeners
    public RenderedModel.OrbitSource getOrbitSource()
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
