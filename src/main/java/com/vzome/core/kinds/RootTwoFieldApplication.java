package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.RootTwoField;
import com.vzome.core.editor.AxialSymmetryToolFactory;
import com.vzome.core.editor.BookmarkTool;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.InversionTool;
import com.vzome.core.editor.LinearMapTool;
import com.vzome.core.editor.MirrorTool;
import com.vzome.core.editor.ModuleTool;
import com.vzome.core.editor.OctahedralToolFactory;
import com.vzome.core.editor.PlaneSelectionTool;
import com.vzome.core.editor.RotationTool;
import com.vzome.core.editor.ScalingTool;
import com.vzome.core.editor.TetrahedralToolFactory;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.TranslationTool;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Shapes;
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
public class RootTwoFieldApplication implements FieldApplication
{
	private final AlgebraicField field = new RootTwoField();

	@Override
	public String getName()
	{
		return this .field .getName();
	}

	@Override
	public AlgebraicField getField()
	{
		return this .field;
	}

    private final SymmetryPerspective octahedralPerspective = new SymmetryPerspective()
    {
        private final OctahedralSymmetry symmetry = new OctahedralSymmetry( field, "blue", "small octahedra" )
        {
    		@Override
    		protected void createOtherOrbits()
    		{
    			createZoneOrbit( "yellow", 0, 4, new int[] { 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1 }, true );

    			createZoneOrbit( "green", 1, 8, new int[] { 0, 1, 1, 2, 0, 1, 1, 2, 0, 1, 0, 1 }, true );

    			createZoneOrbit( "brown", 0, NO_ROTATION, new int[] { 1, 1, 0, 1, 1, 1, 0, 1, 2, 1, 0, 1 }, true );
    		}
    	};
        
        private final AbstractShapes defaultShapes = new ExportedVEFShapes( null, "rootTwoSmall", "small octahedra", "small connectors", symmetry );
    	private final AbstractShapes tesseractix = new ExportedVEFShapes( null, "rootTwo", "Tesseractix", symmetry, defaultShapes );
    	private final AbstractShapes ornateShapes = new ExportedVEFShapes( null, "rootTwoBig", "ornate", symmetry, defaultShapes );
    			
		@Override
		public Symmetry getSymmetry()
		{
			return this .symmetry;
		}
		
		@Override
		public String getName()
		{
			return "octahedral";
		}

		@Override
		public List<Shapes> getGeometries()
		{
			return Arrays.asList( defaultShapes, tesseractix, ornateShapes );
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
				break;

			case LINEAR_MAP:
				result .add( new LinearMapTool.Factory( tools, this .symmetry ) );
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
	};


	/*
	 * This is not really a different symmetry, but it uses different colors and default shapes for the orbits.
	 * 
	 * Actually, it may be a different field, even, since I've learned that Synestructics does not really
	 * scale by root-two, but by doubling!  That means that octagons are still irregular in this system.
	 * It also raises some interesting questions about algebraic fields, and vZome's assumptions about them.
	 * Unlike the golden field, one cannot scale down in this field without using fractions; basically, this
	 * field is just the Integers!
	 */

	private final SymmetryPerspective synestructicsPerspective = new SymmetryPerspective()
    {
    	private final OctahedralSymmetry symmetry = new OctahedralSymmetry( field, "orange", "Synestructics" )
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
    				return this .getDirection( "orange" );

    			case RED:
    				return this .getDirection( "magenta" );

    			case YELLOW:
    				return this .getDirection( "yellow" );

    			default:
    				return null; // TODO pick/define an orbit that needs no correction
    			}
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
        
        private final AbstractShapes defaultShapes = new ExportedVEFShapes( null, "rootTwoSmall", "small octahedra", symmetry, null );
    	private final AbstractShapes smallOctaShapes = new ExportedVEFShapes( null, "rootTwoSmall", "small octahedra", symmetry, defaultShapes );
    	private final AbstractShapes synestructicsShapes = new ExportedVEFShapes( null, "rootTwo", "Synestructics", symmetry, defaultShapes );
    	private final AbstractShapes ornateShapes = new ExportedVEFShapes( null, "rootTwoBig", "ornate", symmetry, defaultShapes );
    	
		@Override
		public Symmetry getSymmetry()
		{
			return this .symmetry;
		}
		
		@Override
		public String getName()
		{
			return "synestructics";
		}

		@Override
		public List<Shapes> getGeometries()
		{
			return Arrays.asList( defaultShapes, smallOctaShapes, synestructicsShapes, ornateShapes );
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
				break;

			case LINEAR_MAP:
				result .add( new LinearMapTool.Factory( tools, this .symmetry ) );
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
	};

//	      mCommands .put( "octasymm-synestructics", new CommandSymmetry( symmetry ) );
//	      mCommands .put( "tetrasymm-synestructics", new CommandTetrahedralSymmetry( symmetry ) );
//	      mCommands .put( "axialsymm-synestructics", new CommandAxialSymmetry( symmetry ) );

	@Override
	public Collection<SymmetryPerspective> getSymmetryPerspectives()
	{
		return Arrays.asList( this .octahedralPerspective, this .synestructicsPerspective );
	}

	@Override
	public SymmetryPerspective getDefaultSymmetryPerspective()
	{
		return this .octahedralPerspective;
	}

	@Override
	public SymmetryPerspective getSymmetryPerspective( String symmName )
	{
		switch ( symmName ) {

		case "octahedral":
			return this .octahedralPerspective;

		case "synestructics":
			return this .synestructicsPerspective;

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
        // Any SymmetryTool factory here is good enough
        toolFactories .put( "SymmetryTool", new OctahedralToolFactory( tools, null ) );
        toolFactories .put( "RotationTool", new RotationTool.Factory( tools, null ) );
        toolFactories .put( "ScalingTool", new ScalingTool.Factory( tools, null ) );
        toolFactories .put( "InversionTool", new InversionTool.Factory( tools ) );
        toolFactories .put( "MirrorTool", new MirrorTool.Factory( tools ) );
        toolFactories .put( "TranslationTool", new TranslationTool.Factory( tools ) );
        toolFactories .put( "BookmarkTool", new BookmarkTool.Factory( tools ) );
        toolFactories .put( "LinearTransformTool", new LinearMapTool.Factory( tools, null ) );

        // These tool factories have to be available for loading legacy documents.
        
        toolFactories .put( "LinearMapTool", new LinearMapTool.Factory( tools, null ) );
        toolFactories .put( "ModuleTool", new ModuleTool.Factory( tools ) );
        toolFactories .put( "PlaneSelectionTool", new PlaneSelectionTool.Factory( tools ) );
    }
}
