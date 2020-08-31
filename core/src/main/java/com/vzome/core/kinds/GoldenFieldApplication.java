package com.vzome.core.kinds;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandTauDivision;
import com.vzome.core.commands.CommandUniformH4Polytope;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.AbstractSymmetry;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.math.symmetry.WythoffConstruction;
import com.vzome.core.tools.AxialStretchTool;
import com.vzome.core.tools.IcosahedralToolFactory;
import com.vzome.core.viewing.AbstractShapes;
import com.vzome.core.viewing.ExportedVEFShapes;

/**
 * Everything here is stateless, or at worst, a cache (like Shapes).
 * An instance of this can be shared by many DocumentModels.
 * This is why it does not have tool factories, though it does
 * dictate what tool factories will be present.
 * 
 * @author vorth
 *
 */
public class GoldenFieldApplication extends DefaultFieldApplication
{        
    private final IcosahedralSymmetryPerspective icosahedralPerspective;

    public GoldenFieldApplication()
    {
        super( new PentagonField() );

        this.icosahedralPerspective = new IcosahedralSymmetryPerspective(this.getField());
        
        OctahedralSymmetryPerspective octahedralPerspective = (OctahedralSymmetryPerspective) super .getDefaultSymmetryPerspective();
        AbstractSymmetry symm = octahedralPerspective .getSymmetry();
        
        symm .createZoneOrbit( "yellow", 0, 4, new int[][] { {0,1, 1,1}, {0,1, 1,1}, {0,1, 1,1} }, true, false, getField()
                .createPower( - 1 ) );

        symm .createZoneOrbit( "green", 1, 8, new int[][] { {1,1, 0,1}, {1,1, 0,1}, {0,1, 0,1} }, true, true, getField()
                .createRational( 2 ) );

        symm .createZoneOrbit( "lavender", 0, Symmetry .NO_ROTATION, new int[][] { {2,1, - 1,1}, {0,1, 1,1}, {2,1, -1,1} } );

        symm .createZoneOrbit( "olive", 0, Symmetry .NO_ROTATION, new int[][] { {0,1, 1,1}, {0,1, 1,1}, {2,1, -1,1} } );

        symm .createZoneOrbit( "maroon", 0, Symmetry .NO_ROTATION, new int[][] { {-1,1, 1,1}, {3,1, -1,1}, {1,1, -1,1} } );

        symm .createZoneOrbit( "brown", 0, Symmetry .NO_ROTATION, new int[][] { {-1,1, 1,1}, {-1,1, 1,1}, {-2,1, 2,1} } );

        symm .createZoneOrbit( "red", 0, Symmetry .NO_ROTATION, new int[][] { {0,1, 1,1}, {1,1, 0,1}, {0,1, 0,1} } );

        symm .createZoneOrbit( "purple", 0, Symmetry .NO_ROTATION, new int[][] { {1,1, 1,1}, {0,1, 0,1}, {-1,1, 0,1} }, false, false, getField()
                .createPower( - 1 ) );

        symm .createZoneOrbit( "black", 0, Symmetry .NO_ROTATION, new int[][] { {1,2, 0,1}, {0,1, 1,2}, {-1,2, 1,2} }, false, false, getField()
                .createRational( 2 ) );

        symm .createZoneOrbit( "turquoise", 0, Symmetry .NO_ROTATION, new int[][] { {1,1, 2,1}, {3,1, 4,1}, {3,1, 4,1} } );

        AbstractShapes defaultShapes = new ExportedVEFShapes( null, "octahedral", "trapezoids", symm, null );
        octahedralPerspective .setDefaultGeometry( defaultShapes );
        octahedralPerspective .addShapes( new ExportedVEFShapes( null, "octahedralFast", "small octahedra", symm, null ) );
        octahedralPerspective .addShapes( new ExportedVEFShapes( null, "octahedralRealistic", "vZome logo", symm, defaultShapes ) );
    }

    @Override
    public Collection<SymmetryPerspective> getSymmetryPerspectives()
    {
        return Arrays.asList( this .icosahedralPerspective, super .getDefaultSymmetryPerspective() );
    }

    @Override
    public SymmetryPerspective getDefaultSymmetryPerspective()
    {
        return this .icosahedralPerspective;
    }

    @Override
    public SymmetryPerspective getSymmetryPerspective( String symmName )
    {
        switch ( symmName ) {

        case "icosahedral":
            return this .icosahedralPerspective;

        default:
            return super .getSymmetryPerspective( symmName );
        }
    }

    @Override
    public QuaternionicSymmetry getQuaternionSymmetry( String name )
    {
        return icosahedralPerspective .getQuaternionSymmetry( name );
    }

    @Override
    public void registerToolFactories( Map<String, Factory> toolFactories, ToolsModel tools )
    {
        // register the default tool factories
        super.registerToolFactories( toolFactories, tools );
        
        // add any tools that are unique for this field
        IcosahedralSymmetry symm = icosahedralPerspective .getSymmetry();
        // symm matters for this one, since it is final in the tool
        toolFactories .put( "AxialStretchTool", new AxialStretchTool.Factory( tools, symm, false, false, false ) );

        // this one has to replace the same-named factory in the base class
        toolFactories .put( "SymmetryTool", new IcosahedralToolFactory( tools, symm ) );
    }

    private CommandUniformH4Polytope h4Builder = null;
    
    @Override
    public void constructPolytope( String groupName, int index, int edgesToRender, AlgebraicNumber[] edgeScales, WythoffConstruction.Listener listener )
    {
        switch ( groupName ) {

        case "H4":
            if ( this .h4Builder == null ) {
                QuaternionicSymmetry qsymm = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", this .getField() );
                this .h4Builder = new CommandUniformH4Polytope( this .getField(), qsymm, 0 );
            }
            this .h4Builder .generate( index, edgesToRender, edgeScales, listener );
            break;

        default:
            super .constructPolytope( groupName, index, edgesToRender, edgeScales, listener );
            break;
        }
    }
    
    private final Command cmdTauDivide = new CommandTauDivision();

    @Override
    public Command getLegacyCommand( String action )
    {
        switch ( action ) {
        case "tauDivide":
            return cmdTauDivide;
            
        default:
            return super.getLegacyCommand( action );
        }
    }
}
