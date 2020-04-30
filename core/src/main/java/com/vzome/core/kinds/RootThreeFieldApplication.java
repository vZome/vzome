package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandSymmetry;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.AbstractSymmetry;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.DodecagonalSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.tools.AxialSymmetryToolFactory;
import com.vzome.core.tools.InversionTool;
import com.vzome.core.tools.LinearMapTool;
import com.vzome.core.tools.MirrorTool;
import com.vzome.core.tools.ProjectionTool;
import com.vzome.core.tools.RotationTool;
import com.vzome.core.tools.ScalingTool;
import com.vzome.core.tools.StereographicProjectionTool;
import com.vzome.core.tools.SymmetryTool;
import com.vzome.core.tools.TranslationTool;
import com.vzome.core.viewing.AbstractShapes;
import com.vzome.core.viewing.DodecagonalShapes;
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
public class RootThreeFieldApplication extends DefaultFieldApplication
{
    public RootThreeFieldApplication()
    {
        super( new RootThreeField() );
        
        OctahedralSymmetryPerspective octahedralPerspective = (OctahedralSymmetryPerspective) super .getDefaultSymmetryPerspective();
        
        AbstractSymmetry symm = octahedralPerspective .getSymmetry();

        symm .createZoneOrbit( "red",   0, Symmetry .NO_ROTATION, new int[][] { {1,1, 1,2}, {1,2, 0,1}, {0,1, 0,1} }, true );
        symm .createZoneOrbit( "brown", 0, Symmetry .NO_ROTATION, new int[][] { {1,1, 0,1}, {1,1, 0,1}, {2,1, 0,1} } );

        AbstractShapes defaultShapes = new ExportedVEFShapes( null, "rootThreeOctaSmall", "small octahedra", "small connectors", symm );
        octahedralPerspective .addShapes( defaultShapes );
        octahedralPerspective .setDefaultGeometry( defaultShapes );
    }

    private final SymmetryPerspective dodecagonalPerspective = new AbstractSymmetryPerspective(new DodecagonalSymmetry( getField() ))
    {
        {
            AbstractShapes defaultShapes = new ExportedVEFShapes( null, "dodecagon3d", "prisms", symmetry );
            AbstractShapes hexagonShapes = new DodecagonalShapes( "dodecagonal", "hexagons", "flat hexagons", symmetry );
            // this is the order they will appear in the menu
            setDefaultGeometry( defaultShapes );
            addShapes(hexagonShapes);
        }

        @Override
        public String getName()
        {
            return "dodecagonal";
        }

        @Override
        public boolean orbitIsBuildDefault( Direction orbit )
        {
            switch ( orbit .getName() ) {

            case "blue":
            case "green":
                return true;

            default:
                return false;
            }
        }
        
        @Override
        public AlgebraicNumber getOrbitUnitLength( Direction orbit )
        {
            switch ( orbit .getName() ) {

            case "blue":
            case "green":
                return getField() .createPower( 2 );

            default:
                return super .getOrbitUnitLength( orbit );
            }
        }

        @Override
        public List<Tool.Factory> createToolFactories( Tool.Kind kind, ToolsModel tools )
        {
            List<Tool.Factory> result = new ArrayList<>();
            switch ( kind ) {

            case SYMMETRY:
                result .add( new SymmetryTool.Factory( tools, this .symmetry ) );
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
                result .add( new SymmetryTool.Factory( tools, this .symmetry ) .createPredefinedTool( "dodecagonal antiprism around origin" ) );
                result .add( new InversionTool.Factory( tools ) .createPredefinedTool( "reflection through origin" ) );
                result .add( new MirrorTool.Factory( tools ) .createPredefinedTool( "reflection through XY plane" ) );
                result .add( new AxialSymmetryToolFactory( tools, this .symmetry ) .createPredefinedTool( "symmetry around red through origin" ) );
                break;

            case TRANSFORM:
                result .add( new ScalingTool.Factory( tools, this .symmetry ) .createPredefinedTool( "scale down" ) );
                result .add( new ScalingTool.Factory( tools, this .symmetry ) .createPredefinedTool( "scale up" ) );
                result .add( new RotationTool.Factory( tools, this .symmetry ) .createPredefinedTool( "rotate around red through origin" ) );
                result .add( new TranslationTool.Factory( tools ) .createPredefinedTool( "b1 move along +X" ) );
                break;

            default:
                break;
            }
            return result;
        }

        private final Command dodecagonsymm = new CommandSymmetry( this .symmetry );

        @Override
        public Command getLegacyCommand( String action )
        {
            switch ( action ) {
            case "dodecagonsymm":
                return dodecagonsymm;

            default:
                return super.getLegacyCommand(action);
            }
        }

        @Override
        public String getModelResourcePath()
        {
            return "org/vorthmann/zome/app/dodecagonal.vZome";
        }
    };

    @Override
    public SymmetryPerspective getDefaultSymmetryPerspective()
    {
        return this .dodecagonalPerspective;
    }

    @Override
    public Collection<SymmetryPerspective> getSymmetryPerspectives()
    {
        return Arrays.asList( super .getDefaultSymmetryPerspective(), this .dodecagonalPerspective );
    }

    @Override
    public SymmetryPerspective getSymmetryPerspective( String symmName )
    {
        switch ( symmName ) {

        case "dodecagonal":
            return this .dodecagonalPerspective;

        default:
            return super .getSymmetryPerspective( symmName );
        }
    }
}
