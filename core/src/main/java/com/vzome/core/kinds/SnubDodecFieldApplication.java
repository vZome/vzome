package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.SnubDodecField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandAxialSymmetry;
import com.vzome.core.commands.CommandSymmetry;
import com.vzome.core.commands.CommandTetrahedralSymmetry;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
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
public class SnubDodecFieldApplication extends DefaultFieldApplication
{
	public SnubDodecFieldApplication()
	{
        super( new SnubDodecField() );
    }

	@Override
	public String getName()
	{
		return this .getField() .getName();
	}

    private final SymmetryPerspective icosahedralPerspective = new SymmetryPerspective()
    {
        private final IcosahedralSymmetry symmetry = new IcosahedralSymmetry( getField(), "solid connectors" )
        {
            @Override
            protected void createOtherOrbits()
            {
                super .createOtherOrbits();
                /*
                 * 

          PENTAGON
          4 + phi*-4 + xi*0 + phi*xi*0 + xi^2*-2 + phi*xi^2*2, -4 + phi*0 + xi*0 + phi*xi*0 + xi^2*2 + phi*xi^2*0, 0 + phi*0 + xi*0 + phi*xi*0 + xi^2*0 + phi*xi^2*2
          4 -4 0 0 -2 2 -4 0 0 0 2 0 0 0 0 0 0 2
          (2,-2,0,0,-4,4) (0,2,0,0,0,-4) (2,0,0,0,0,0)


          TRIANGLE
          0 + phi*-4 + xi*-2 + phi*xi*0 + xi^2*0 + phi*xi^2*2, -4 + phi*4 + xi*0 + phi*xi*-2 + xi^2*2 + phi*xi^2*-2, -4 + phi*0 + xi*-2 + phi*xi*-2 + xi^2*2 + phi*xi^2*0
          0 -4 -2 0 0 2 -4 4 0 -2 2 -2 -4 0 -2 -2 2 0
          (2,0,0,-2,-4,0) (-2,2,-2,0,4,-4) (0,2,-2,-2,0,-4)


          DIAGONAL
          8 + phi*0 + xi*0 + phi*xi*4 + xi^2*-4 + phi*xi^2*0, 0 + phi*-4 + xi*0 + phi*xi*0 + xi^2*0 + phi*xi^2*0, 0 + phi*0 + xi*0 + phi*xi*0 + xi^2*0 + phi*xi^2*0
          8 0 0 4 -4 0 0 -4 0 0 0 0 0 0 0 0 0 0
          (0,-4,4,0,0,8) (0,0,0,0,-4,0) (0,0,0,0,0,0)

                 */      
                AlgebraicNumber scale = mField .createPower( -3 );
                createZoneOrbit( "snubPentagon", 0, NO_ROTATION, rationalVector( new int[]{ 4,-4,0,0,-2,2,  -4,0,0,0,2,0,  0,0,0,0,0,2 } ), false, false, scale ) .withCorrection();
                createZoneOrbit( "snubTriangle", 0, NO_ROTATION, rationalVector( new int[]{ 0,-4,-2,0,0,2,  -4,4,0,-2,2,-2,  -4,0,-2,-2,2,0 } ), false, false, scale ) .withCorrection();
                createZoneOrbit( "snubDiagonal", 0, NO_ROTATION, rationalVector( new int[]{ 8,0,0,4,-4,0,  0,-4,0,0,0,0,  0,0,0,0,0,0 } ), false, false, scale ) .withCorrection();
            }
        };
        {
            symmetry .computeOrbitDots();
        }

        private final AbstractShapes defaultShapes = new ExportedVEFShapes( null, "default", "solid connectors", symmetry );
        private final AbstractShapes lifelikeShapes = new ExportedVEFShapes( null, "lifelike", "lifelike", symmetry, defaultShapes );
        private final AbstractShapes tinyShapes =  new ExportedVEFShapes( null, "tiny", "tiny connectors", symmetry );

        private final Command icosasymm = new CommandSymmetry( symmetry );
        private final Command tetrasymm = new CommandTetrahedralSymmetry( symmetry );
        private final Command axialsymm = new CommandAxialSymmetry( symmetry );

        @Override
        public Symmetry getSymmetry()
        {
            return this .symmetry;
        }

        @Override
        public String getName()
        {
            return "icosahedral";
        }

        @Override
        public List<Shapes> getGeometries()
        {
            return Arrays.asList( defaultShapes, lifelikeShapes, tinyShapes );
        }

        @Override
        public Shapes getDefaultGeometry()
        {
            return this .defaultShapes;
        }

        @Override
        public List<Tool.Factory> createToolFactories( Tool.Kind kind, ToolsModel tools )
        {
            List<Tool.Factory> result = new ArrayList<>();
            switch ( kind ) {

            case SYMMETRY:
                result .add( new IcosahedralToolFactory( tools, this .symmetry ) );
                result .add( new TetrahedralToolFactory( tools, this .symmetry ) );
                result .add( new InversionTool.Factory( tools ) );
                result .add( new MirrorTool.Factory( tools ) );
                result .add( new AxialSymmetryToolFactory( tools, this .symmetry ) );
                break;

            case TRANSFORM:
                result .add( new ScalingTool.Factory( tools, this .symmetry ) );
                result .add( new RotationTool.Factory( tools, this .symmetry ) );
                result .add( new TranslationTool.Factory( tools ) );
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
                result .add( new IcosahedralToolFactory( tools, this .symmetry ) .createPredefinedTool( "icosahedral around origin" ) );
                result .add( new TetrahedralToolFactory( tools, this .symmetry ) .createPredefinedTool( "tetrahedral around origin" ) );
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

        @Override
        public Command getLegacyCommand( String action )
        {
            switch ( action ) {
            case "icosasymm"    : return icosasymm;
            case "tetrasymm"    : return tetrasymm;
            case "axialsymm"    : return axialsymm;
            default:
                return null;
            }
        }

        @Override
        public String getModelResourcePath()
        {
            return "org/vorthmann/zome/app/icosahedral-vef.vZome";
        }
    };
	

	@Override
	public Collection<SymmetryPerspective> getSymmetryPerspectives()
	{
		return Arrays.asList( this .icosahedralPerspective );
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
			return null;
		}
	}

	@Override
	public QuaternionicSymmetry getQuaternionSymmetry( String name )
	{
		return null;
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

	@Override
	public void constructPolytope( String groupName, int index,
			int edgesToRender, AlgebraicNumber[] edgeScales, Listener listener ) {}
}
