package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandAxialSymmetry;
import com.vzome.core.commands.CommandQuaternionSymmetry;
import com.vzome.core.commands.CommandSymmetry;
import com.vzome.core.commands.CommandTetrahedralSymmetry;
import com.vzome.core.commands.CommandUniformH4Polytope;
import com.vzome.core.commands.CommandVanOss600Cell;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.AbstractSymmetry;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.math.symmetry.WythoffConstruction.Listener;
import com.vzome.core.render.Shapes;
import com.vzome.core.tools.AxialStretchTool;
import com.vzome.core.tools.AxialSymmetryToolFactory;
import com.vzome.core.tools.BookmarkTool;
import com.vzome.core.tools.IcosahedralToolFactory;
import com.vzome.core.tools.InversionTool;
import com.vzome.core.tools.LinearMapTool;
import com.vzome.core.tools.MirrorTool;
import com.vzome.core.tools.ModuleTool;
import com.vzome.core.tools.PlaneSelectionTool;
import com.vzome.core.tools.RotationTool;
import com.vzome.core.tools.ScalingTool;
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
 * @author vorth
 *
 */
public class GoldenFieldApplication extends DefaultFieldApplication
{        
    private final QuaternionicSymmetry H4 = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", getField() );
    private final QuaternionicSymmetry H4_ROT = new QuaternionicSymmetry( "H4_ROT", "com/vzome/core/math/symmetry/H4roots-rotationalSubgroup.vef", getField() );
    private final QuaternionicSymmetry T2 = new QuaternionicSymmetry( "2T", "com/vzome/core/math/symmetry/binaryTetrahedralGroup.vef", getField() );

    public GoldenFieldApplication()
    {
        super( new PentagonField() );

        OctahedralSymmetryPerspective octahedralPerspective = (OctahedralSymmetryPerspective) super .getDefaultSymmetryPerspective();
        AbstractSymmetry symm = (AbstractSymmetry) octahedralPerspective .getSymmetry();
        
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
        octahedralPerspective .addShapes( defaultShapes );
        octahedralPerspective .setDefaultGeometry( defaultShapes );
        octahedralPerspective .addShapes( new ExportedVEFShapes( null, "octahedralFast", "small octahedra", symm, null ) );
        octahedralPerspective .addShapes( new ExportedVEFShapes( null, "octahedralRealistic", "vZome logo", symm, defaultShapes ) );
    }

    private final SymmetryPerspective icosahedralPerspective = new SymmetryPerspective()
    {
        private final IcosahedralSymmetry icosaSymm = new IcosahedralSymmetry( getField(), "solid connectors" );
        {
            icosaSymm .computeOrbitDots();
        }

        private final AbstractShapes icosadefaultShapes = new ExportedVEFShapes( null, "default", "solid connectors", icosaSymm );
        private final AbstractShapes lifelikeShapes = new ExportedVEFShapes( null, "lifelike", "lifelike", icosaSymm, icosadefaultShapes );
        private final AbstractShapes tinyShapes =  new ExportedVEFShapes( null, "tiny", "tiny connectors", icosaSymm );
        private final AbstractShapes tinyDodecs = new ExportedVEFShapes( null, "dodecs", "small dodecahedra", "tiny dodecahedra", icosaSymm, tinyShapes );
        private final AbstractShapes bigZome = new ExportedVEFShapes( null, "bigzome", "Big Zome", icosaSymm, tinyShapes );
        private final AbstractShapes noTwist = new ExportedVEFShapes( null, "noTwist", "no-twist 121 zone", icosaSymm );
        private final AbstractShapes vienne2 = new ExportedVEFShapes( null, "vienne2", "Vienne", icosaSymm, icosadefaultShapes );
        private final AbstractShapes vienne3 = new ExportedVEFShapes( null, "vienne3", "Vienne lifelike", icosaSymm, vienne2 );
        private final AbstractShapes vienne = new ExportedVEFShapes( null, "vienne", "Vienne 121 zone", icosaSymm );

        private final Command icosasymm = new CommandSymmetry( icosaSymm );
        private final Command tetrasymm = new CommandTetrahedralSymmetry( icosaSymm );
        private final Command axialsymm = new CommandAxialSymmetry( icosaSymm );
        private final Command h4symmetry = new CommandQuaternionSymmetry( H4, H4 );
        private final Command h4rotations = new CommandQuaternionSymmetry( H4_ROT, H4_ROT );
        private final Command IxTsymmetry = new CommandQuaternionSymmetry( H4, T2 );
        private final Command TxTsymmetry = new CommandQuaternionSymmetry( T2, T2 );
        private final Command vanOss600cell = new CommandVanOss600Cell();
        private final Command octasymm = new CommandSymmetry( new OctahedralSymmetry( getField(), "", "" ) );

        @Override
        public Symmetry getSymmetry()
        {
            return this .icosaSymm;
        }
        
        @Override
        public String getName()
        {
            return "icosahedral";
        }

        @Override
        public List<Shapes> getGeometries()
        {
            return Arrays.asList( icosadefaultShapes, lifelikeShapes, tinyShapes, tinyDodecs, bigZome, noTwist, vienne2, vienne3, vienne );
        }
        
        @Override
        public Shapes getDefaultGeometry()
        {
            return this .icosadefaultShapes;
        }

        @Override
        public List<Tool.Factory> createToolFactories( Tool.Kind kind, ToolsModel tools )
        {
            List<Tool.Factory> result = new ArrayList<>();
            switch ( kind ) {

            case SYMMETRY:
                result .add( new IcosahedralToolFactory( tools, this .icosaSymm ) );
                result .add( new TetrahedralToolFactory( tools, this .icosaSymm ) );
                result .add( new InversionTool.Factory( tools ) );
                result .add( new MirrorTool.Factory( tools ) );
                result .add( new AxialSymmetryToolFactory( tools, this .icosaSymm ) );
                break;

            case TRANSFORM:
                result .add( new ScalingTool.Factory( tools, this .icosaSymm ) );
                result .add( new RotationTool.Factory( tools, this .icosaSymm ) );
//                 result .add( new PlaneSelectionTool.Factory( tools ) );
                result .add( new TranslationTool.Factory( tools ) );
                break;

            case LINEAR_MAP:
                result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, true, true ) );
                result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, false, true ) );
                result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, true, false ) );
                result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, false, false ) );
                result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, false, true, false ) );
                result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, false, false, false ) );
                result .add( new LinearMapTool.Factory( tools, this .icosaSymm, false ) );
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
                result .add( new IcosahedralToolFactory( tools, this .icosaSymm ) .createPredefinedTool( "icosahedral around origin" ) );
                result .add( new TetrahedralToolFactory( tools, this .icosaSymm ) .createPredefinedTool( "tetrahedral around origin" ) );
                result .add( new InversionTool.Factory( tools ) .createPredefinedTool( "reflection through origin" ) );
                result .add( new MirrorTool.Factory( tools ) .createPredefinedTool( "reflection through XY plane" ) );
                result .add( new AxialSymmetryToolFactory( tools, this .icosaSymm ) .createPredefinedTool( "symmetry around red through origin" ) );
                break;

            case TRANSFORM:
                result .add( new ScalingTool.Factory( tools, this .icosaSymm ) .createPredefinedTool( "scale down" ) );
                result .add( new ScalingTool.Factory( tools, this .icosaSymm ) .createPredefinedTool( "scale up" ) );
                result .add( new RotationTool.Factory( tools, this .icosaSymm ) .createPredefinedTool( "rotate around red through origin" ) );
                result .add( new TranslationTool.Factory( tools ) .createPredefinedTool( "b1 move along +X" ) );
                break;

            default:
                break;
            }
            return result;
        }

        @Override
        public Command getLegacyCommand( String action )
        {
            switch ( action ) {
            case "icosasymm"    : return icosasymm;
            case "tetrasymm"    : return tetrasymm;
            case "axialsymm"    : return axialsymm;
            case "h4symmetry"   : return h4symmetry;
            case "h4rotations"  : return h4rotations;
            case "IxTsymmetry"  : return IxTsymmetry;
            case "TxTsymmetry"  : return TxTsymmetry;
            case "vanOss600cell": return vanOss600cell;
            case "octasymm"     : return octasymm;
            default             : return null;
            }
        }

        @Override
        public String getModelResourcePath()
        {
            return "org/vorthmann/zome/app/icosahedral-vef.vZome";

//        if ( propertyIsTrue( "rzome.trackball" ) )
//            return "org/vorthmann/zome/app/rZomeTrackball-vef.vZome";
//        else if ( userHasEntitlement( "developer.extras" ) )
//            return "org/vorthmann/zome/app/icosahedral-developer.vZome";
        }
    };

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
        switch ( name ) {

        case "H_4":
            return this .H4;

        case "H4_ROT":
            return this .H4_ROT;

        case "2T":
            return this .T2;

        default:
            return null;
        }
    }

    @Override
    public void registerToolFactories( Map<String, Factory> toolFactories, ToolsModel tools )
    {
        IcosahedralSymmetry symm = (IcosahedralSymmetry) icosahedralPerspective .getSymmetry();
        // symm matters for this one, since it is final in the tool
        toolFactories .put( "AxialStretchTool", new AxialStretchTool.Factory( tools, symm, false, false, false ) );
        
        // We might as well use symm in the rest, though it will be overwritten by SymmetryTool.setXmlAttributes()
        toolFactories .put( "SymmetryTool", new IcosahedralToolFactory( tools, symm ) );
        toolFactories .put( "RotationTool", new RotationTool.Factory( tools, symm ) );
        toolFactories .put( "ScalingTool", new ScalingTool.Factory( tools, symm ) );
        toolFactories .put( "InversionTool", new InversionTool.Factory( tools ) );
        toolFactories .put( "MirrorTool", new MirrorTool.Factory( tools ) );
        toolFactories .put( "TranslationTool", new TranslationTool.Factory( tools ) );
        toolFactories .put( "BookmarkTool", new BookmarkTool.Factory( tools ) );
        toolFactories .put( "LinearTransformTool", new LinearMapTool.Factory( tools, null, false ) );
        
        // These tool factories have to be available for loading legacy documents.
        
        toolFactories .put( "LinearMapTool", new LinearMapTool.Factory( tools, null, true ) );
        toolFactories .put( "ModuleTool", new ModuleTool.Factory( tools ) );
        toolFactories .put( "PlaneSelectionTool", new PlaneSelectionTool.Factory( tools ) );
    }

    private CommandUniformH4Polytope h4Builder = null;
    
    @Override
    public void constructPolytope( String groupName, int index, int edgesToRender, AlgebraicNumber[] edgeScales, Listener listener )
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
}
