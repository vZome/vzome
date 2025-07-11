
package com.vzome.desktop.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.RenderedModel;
import com.vzome.desktop.api.Controller;

public class SymmetryController extends DefaultController
{
    @Override
    public String getProperty( String string )
    {
        switch ( string ) {
        
        case "label":
            return this .label;

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
                Direction dir = this .symmetrySystem .getOrbits() .getDirection( name );
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
    private final OrbitSnapper snapper;

    public OrbitSetController availableController;
    public OrbitSetController snapController;
    public OrbitSetController buildController;
    public OrbitSetController renderController;

    public Map<Direction, Controller> orbitLengths = new HashMap<>();

    private final Map<String, Controller> symmetryToolFactories = new LinkedHashMap<>();
    private final Map<String, Controller> transformToolFactories = new LinkedHashMap<>();
    private final Map<String, Controller> linearMapToolFactories = new LinkedHashMap<>();
    private final RenderedModel renderedModel;
    private final String label;

    public Symmetry getSymmetry()
    {
        return this .symmetrySystem .getSymmetry();
    }

    public OrbitSnapper getSnapper()
    {
        return snapper;
    }

    public SymmetryController( String label, Controller parent, SymmetrySystem model, RenderedModel mRenderedModel )
    {
        this.label = label;
        this .symmetrySystem = model;
        renderedModel = mRenderedModel;
        Symmetry symmetry = model .getSymmetry();
        availableOrbits = new OrbitSet( symmetry );
        snapOrbits = new OrbitSet( symmetry );
        buildOrbits = new OrbitSet( symmetry );
        renderOrbits = new OrbitSet( symmetry );
        snapper = new SymmetrySnapper( snapOrbits );
        for (Direction orbit : symmetry .getOrbitSet() .getDirections()) {
            if ( model .orbitIsStandard( orbit ) )
            {
                availableOrbits .add( orbit );
                snapOrbits .add( orbit );
                if ( model .orbitIsBuildDefault( orbit ) )
                {
                    buildOrbits .add( orbit );
                }
            }
            renderOrbits .add( orbit );
        }
        availableController = new OrbitSetController( availableOrbits, this .symmetrySystem .getOrbits(), this .symmetrySystem );
        this .addSubController( "availableOrbits", availableController );
        snapController = new OrbitSetController( snapOrbits, availableOrbits, this .symmetrySystem );
        this .addSubController( "snapOrbits", snapController );
        buildController = new OrbitSetController( buildOrbits, availableOrbits, this .symmetrySystem );
        this .addSubController( "buildOrbits", buildController );
        if ( parent .propertyIsTrue( "single.orbit" ) )
            try {
                buildController .doAction( "oneAtATime" );
            } catch (Exception e) {
                e.printStackTrace();
            }
        renderController = new OrbitSetController( renderOrbits, this .symmetrySystem .getOrbits(), this .symmetrySystem );
        this .addSubController( "renderOrbits", renderController );

        for ( Direction orbit : this .symmetrySystem .getOrbits() .getDirections() ) {
            AlgebraicNumber unitLength = this .symmetrySystem .getOrbitUnitLength( orbit );
            AlgebraicField field = symmetry .getField();
            Controller lengthModel = new LengthController( unitLength, field .one(), field );
            buildController .addSubController( "length." + orbit .getName(), lengthModel );
            orbitLengths .put( orbit, lengthModel );
        }
        if ( parent .propertyIsTrue( "disable.known.directions" ) )
            this .symmetrySystem .disableKnownDirection();

        availableController .addPropertyListener( buildController );
        availableController .addPropertyListener( snapController );
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
        
        String presetStyle = parent .getProperty( "defaultShapes." + model .getSymmetry() .getField() .getName() + "." + model .getName() );
        if ( presetStyle != null )
            this .symmetrySystem .setStyle( presetStyle );
    }

    @Override
    public String[] getCommandList( String listName )
    {
        switch ( listName ) {

        case "styles":

            return this .symmetrySystem .getStyleNames();

        case "orbitNames":
            ArrayList<String> list = new ArrayList<>();
            for (Direction dir : this .symmetrySystem .getSymmetry() .getDirections()) {
                if ( ! dir .isAutomatic() )
                    list .add( dir .getCanonicalName() );
            }
            return list .toArray( new String[]{} );

        case "orbits":

            String[] result = new String[ this .symmetrySystem .getOrbits() .size() ];
            int i = 0;
            for (Direction orbit : this .symmetrySystem .getOrbits() .getDirections()) {
                result[ i ] = orbit .getName();
                i++;
            }
            return result;

        case "symmetryToolFactories":

            if ( this .symmetryToolFactories .isEmpty() ) {
                for ( Tool.Factory factory : this .symmetrySystem .getToolFactories( Tool.Kind.SYMMETRY ) )
                    this .symmetryToolFactories .put( factory .getId(), new ToolFactoryController( factory ) );
            }
            return this .symmetryToolFactories .keySet() .toArray( new String[]{} );

        case "transformToolFactories":

            if ( this .transformToolFactories .isEmpty() ) {
                for ( Tool.Factory factory : this .symmetrySystem .getToolFactories( Tool.Kind.TRANSFORM ) )
                    this .transformToolFactories .put( factory .getId(), new ToolFactoryController( factory ) );
            }
            return this .transformToolFactories .keySet() .toArray( new String[]{} );

        case "linearMapToolFactories":

            if ( this .linearMapToolFactories .isEmpty() ) {
                for ( Tool.Factory factory : this .symmetrySystem .getToolFactories( Tool.Kind.LINEAR_MAP ) )
                    this .linearMapToolFactories .put( factory .getId(), new ToolFactoryController( factory ) );
            }
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
    public void doAction( String action ) throws Exception
    {
        switch (action) {

        case "rZomeOrbits":
        case "predefinedOrbits":
        case "setAllDirections":
            availableController .doAction( action );
            break;

        case "ReplaceWithShape":
            action += "/" + this .symmetrySystem .getName() + ":" + this .symmetrySystem .getStyle() .getName();
            super .doAction( action );
            break;
            
        case "resetOrbitColors":
            this .symmetrySystem .resetColors();
            availableController .firePropertyChange( "orbits", true, false );
            break;

        default:
            if ( action .startsWith( "setStyle." ) )
            {
                String styleName =  action .substring( "setStyle." .length() );
                this .symmetrySystem .setStyle( styleName );
                this .renderedModel .setShapes( this .symmetrySystem .getShapes() );
                firePropertyChange( "renderingStyle", null, styleName );
            }
            else {
                boolean handled = this .symmetrySystem .doAction( action );
                if ( ! handled )
                    super .doAction( action );
            }
            break;
        }
    }

    private Controller getLengthController( Direction orbit )
    {
        Controller result = orbitLengths .get( orbit );
        if ( result == null && orbit != null )
        {
            AlgebraicNumber unitLength = this .symmetrySystem .getOrbitUnitLength( orbit );
            AlgebraicField field = this .symmetrySystem .getSymmetry() .getField();
            result = new LengthController( unitLength, field .one(), field );
            buildController .addSubController( "length." + orbit .getName(), result );
            orbitLengths .put( orbit, result );
            renderOrbits .add( orbit );
            availableOrbits .add( orbit );
        }
        return result;
    }

    public OrbitSet getOrbits()
    {
        return this .symmetrySystem .getOrbits();
    }

    // TODO: Can we get rid of this?  It is only needed by PreviewStrut.
    //   We should be able to accomplish the sync with PropertyChangeListeners
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

    public void parseOrbitLength( String string, Map<String,Object> props )
    {
      // struts lengths may include fractions, 
      // so only split on the first two slashes to make 3 sections max. 
      String[] sections = string .split( "/", 3 );
      String mode = sections[ 0 ];
      props .put( "mode", mode );
      String orbitStr = sections[ 1 ];
      Direction orbit = this .getOrbits() .getDirection( orbitStr );
      props .put( "orbit", orbit );
      if ( mode .endsWith( "Struts" ) )
      {
          String lengthStr = sections[ 2 ];
          AlgebraicNumber unitScalar = orbit .getLengthInUnits( this .getSymmetry().getField().one() );
          AlgebraicNumber length = unitScalar .getField() .parseNumber( lengthStr );
          AlgebraicNumber rawLength = length .dividedBy( unitScalar );
          if ( rawLength != null )
              props .put( "length", rawLength );
      }
    }
}
