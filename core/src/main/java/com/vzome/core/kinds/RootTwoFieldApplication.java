package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.RootTwoField;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.AbstractSymmetry;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.tools.AxialSymmetryToolFactory;
import com.vzome.core.tools.InversionTool;
import com.vzome.core.tools.LinearMapTool;
import com.vzome.core.tools.MirrorTool;
import com.vzome.core.tools.OctahedralToolFactory;
import com.vzome.core.tools.ProjectionTool;
import com.vzome.core.tools.RotationTool;
import com.vzome.core.tools.ScalingTool;
import com.vzome.core.tools.StereographicProjectionTool;
import com.vzome.core.tools.TetrahedralToolFactory;
import com.vzome.core.tools.TranslationTool;
import com.vzome.core.viewing.AbstractShapes;
import com.vzome.core.viewing.ExportedVEFShapes;

/**
 * Everything here is stateless, or at worst, a cache (like Shapes).
 * An instance of this can be shared by many DocumentModels.
 * This is why it does not have tool factories, though it does
 * dictate what tool factories will be present.
 * 
 * @author Scott Vorthmann
 *
 */
public class RootTwoFieldApplication extends DefaultFieldApplication
{
    public RootTwoFieldApplication()
    {
        super( new RootTwoField() );

        OctahedralSymmetryPerspective octahedralPerspective = (OctahedralSymmetryPerspective) super .getDefaultSymmetryPerspective();

        AbstractSymmetry symmetry = octahedralPerspective .getSymmetry();

        symmetry .createZoneOrbit( "yellow", 0, 4, new int[][] { {1,1, 0,1}, {1,1, 0,1}, {1,1, 0,1} }, true );
        symmetry .createZoneOrbit( "green",  1, 8, new int[][] { {0,1, 1,2}, {0,1, 1,2}, {0,1, 0,1} }, true );
        symmetry .createZoneOrbit( "brown",  0, Symmetry .NO_ROTATION, new int[][] { {1,1, 0,1}, {1,1, 0,1}, {2,1, 0,1} }, true );

        AbstractShapes defaultShapes = new ExportedVEFShapes( null, "rootTwoSmall", "small octahedra", "small connectors", symmetry );
        octahedralPerspective .setDefaultGeometry( defaultShapes );
        octahedralPerspective .addShapes( defaultShapes );
        octahedralPerspective .addShapes( new ExportedVEFShapes( null, "rootTwo", "Tesseractix", symmetry, defaultShapes ) );
        octahedralPerspective .addShapes( new ExportedVEFShapes( null, "rootTwoBig", "ornate", symmetry, defaultShapes ) );
    }

    /*
     * This is not really a different symmetry, but it uses different colors and default shapes for the orbits.
     * 
     * Actually, it may be a different field, even, since I've learned that Synestructics does not really
     * scale by root-two, but by doubling!  That means that octagons are still irregular in this system.
     * It also raises some interesting questions about algebraic fields, and vZome's assumptions about them.
     * Unlike the golden field, one cannot scale down in this field without using fractions; basically, this
     * field is just the Integers!
     */

    private final Symmetry synestructicsSymmetry = new OctahedralSymmetry( getField(), "orange" )
    {
        @Override
        public String getName()
        {
            return "synestructics";
        }

        @Override
        public Direction getSpecialOrbit( SpecialOrbit which )
        {
            switch ( which ) {

            case BLUE:
                return this .getDirection( this. frameColor );

            case RED:
                return this .getDirection( "magenta" );

            case YELLOW:
                return this .getDirection( "yellow" );

            default:
                return null; // TODO pick/define an orbit that needs no correction
            }
        }

        @Override
        public AlgebraicVector[] getOrbitTriangle()
        {
            AlgebraicVector magentaVertex = this .getDirection( "magenta" ) .getPrototype();
            AlgebraicVector orangeVertex = this .getDirection( this. frameColor ) .getPrototype();
            AlgebraicVector yellowVertex = this .getDirection( "yellow" ) .getPrototype();
            return new AlgebraicVector[] { magentaVertex, orangeVertex, yellowVertex };
        }

        @Override
        protected void createOtherOrbits()
        {
            AlgebraicVector v = new AlgebraicVector( this .mField .one(), this .mField .one(), this .mField .one() );
            createZoneOrbit( "yellow", 0, 4, v, true );

            AlgebraicNumber sqrt2 = this .mField .createPower( 1 );
            AlgebraicNumber half = this .mField .createRational( 1, 2 );
            v = new AlgebraicVector( sqrt2, sqrt2, this .mField .zero() ) .scale( half );
            createZoneOrbit( "magenta", 1, 8, v, true );

            v = new AlgebraicVector( this .mField .one(), this .mField .one(), this .mField .one() .plus( this .mField .one() ) );
            createZoneOrbit( "brown", 0, NO_ROTATION, v, true );
        }
    };
    
    private final SymmetryPerspective synestructicsPerspective = new AbstractSymmetryPerspective(synestructicsSymmetry)
    {
        {
            AbstractShapes defaultShapes = new ExportedVEFShapes( null, "rootTwoSmall", "small octahedra", symmetry, null );
            AbstractShapes synestructicsShapes = new ExportedVEFShapes( null, "rootTwo", "Synestructics", symmetry, defaultShapes );
            AbstractShapes ornateShapes = new ExportedVEFShapes( null, "rootTwoBig", "ornate", symmetry, defaultShapes );
            // this is the order they will appear in the menu
            setDefaultGeometry(defaultShapes);
            addShapes(synestructicsShapes);
            addShapes(ornateShapes);
        }

        @Override
        public List<Tool.Factory> createToolFactories( Tool.Kind kind, ToolsModel tools )
        {
            List<Tool.Factory> result = new ArrayList<>();
            switch ( kind ) {

            case SYMMETRY:
                result .add( new OctahedralToolFactory( tools, this .symmetry ) );
                result .add( new TetrahedralToolFactory( tools, this .symmetry ) );
                result .add( new InversionTool.Factory( tools ) );
                result .add( new MirrorTool.Factory( tools ) );
                result .add( new AxialSymmetryToolFactory( tools, this .symmetry ) );
                break;

            case TRANSFORM:
                result .add( new ScalingTool.Factory( tools, this .symmetry ) );
                result .add( new RotationTool.Factory( tools, this .symmetry ) );
                result .add( new TranslationTool.Factory( tools ) );
                result .add( new ProjectionTool.Factory( tools ) );
                result .add( new StereographicProjectionTool.Factory( tools ) );
                break;

            case LINEAR_MAP:
                result .add( new LinearMapTool.Factory( tools, this .symmetry, false ) );
                break;

            default:
                break;
            }
            return result;
        }

        @Override
        public List<Tool> predefineTools( Tool.Kind kind, ToolsModel tools )
        {
            List<Tool> result = new ArrayList<>();
            switch ( kind ) {

            case SYMMETRY:
                result .add( new OctahedralToolFactory( tools, this .symmetry ) .createPredefinedTool( "octahedral around origin" ) );
                result .add( new TetrahedralToolFactory( tools, this .symmetry ) .createPredefinedTool( "tetrahedral around origin" ) );
                result .add( new InversionTool.Factory( tools ) .createPredefinedTool( "reflection through origin" ) );
                result .add( new MirrorTool.Factory( tools ) .createPredefinedTool( "reflection through XY plane" ) );
                result .add( new AxialSymmetryToolFactory( tools, this .symmetry ) .createPredefinedTool( "symmetry around green through origin" ) );
                break;

            case TRANSFORM:
                result .add( new ScalingTool.Factory( tools, this .symmetry ) .createPredefinedTool( "scale down" ) );
                result .add( new ScalingTool.Factory( tools, this .symmetry ) .createPredefinedTool( "scale up" ) );
                result .add( new RotationTool.Factory( tools, this .symmetry ) .createPredefinedTool( "rotate around green through origin" ) );
                result .add( new TranslationTool.Factory( tools ) .createPredefinedTool( "b1 move along +X" ) );
                break;

            default:
                break;
            }
            return result;
        }

        @Override
        public String getModelResourcePath()
        {
            return "org/vorthmann/zome/app/octahedral-vef.vZome";
        }
    };

    @Override
    public Collection<SymmetryPerspective> getSymmetryPerspectives()
    {
        return Arrays.asList( super .getDefaultSymmetryPerspective(), this .synestructicsPerspective );
    }

    @Override
    public SymmetryPerspective getSymmetryPerspective( String symmName )
    {
        switch ( symmName ) {

        case "synestructics":
            return this .synestructicsPerspective;

        default:
            return super .getSymmetryPerspective( symmName );
        }
    }
}
