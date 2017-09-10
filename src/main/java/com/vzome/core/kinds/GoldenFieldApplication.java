package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.AxialStretchTool;
import com.vzome.core.editor.AxialSymmetryToolFactory;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.IcosahedralToolFactory;
import com.vzome.core.editor.InversionTool;
import com.vzome.core.editor.LinearMapTool;
import com.vzome.core.editor.MirrorTool;
import com.vzome.core.editor.OctahedralToolFactory;
import com.vzome.core.editor.RotationTool;
import com.vzome.core.editor.ScalingTool;
import com.vzome.core.editor.TetrahedralToolFactory;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.TranslationTool;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
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
 * @author vorth
 *
 */
public class GoldenFieldApplication implements FieldApplication
{
	private final AlgebraicField field = new PentagonField();

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

    private final SymmetryPerspective icosahedralPerspective = new SymmetryPerspective()
    {
        private final IcosahedralSymmetry icosaSymm = new IcosahedralSymmetry( field, "solid connectors" );
        
        private final AbstractShapes icosadefaultShapes = new ExportedVEFShapes( null, "default", "solid connectors", icosaSymm );
    	private final AbstractShapes lifelikeShapes = new ExportedVEFShapes( null, "lifelike", "lifelike", icosaSymm, icosadefaultShapes );
    	private final AbstractShapes tinyShapes =  new ExportedVEFShapes( null, "tiny", "tiny connectors", icosaSymm );
    	private final AbstractShapes tinyDodecs = new ExportedVEFShapes( null, "dodecs", "small dodecahedra", "tiny dodecahedra", icosaSymm, tinyShapes );
    	private final AbstractShapes bigZome = new ExportedVEFShapes( null, "bigzome", "Big Zome", icosaSymm, tinyShapes );
    	private final AbstractShapes noTwist = new ExportedVEFShapes( null, "noTwist", "no-twist 121 zone", icosaSymm );
    	private final AbstractShapes vienne2 = new ExportedVEFShapes( null, "vienne2", "Vienne", icosaSymm, icosadefaultShapes );
    	private final AbstractShapes vienne3 = new ExportedVEFShapes( null, "vienne3", "Vienne lifelike", icosaSymm, vienne2 );
    	private final AbstractShapes vienne = new ExportedVEFShapes( null, "vienne", "Vienne 121 zone", icosaSymm );
    			
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
				result .add( new TranslationTool.Factory( tools ) );
				break;

			case LINEAR_MAP:
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, true, true ) );
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, false, true ) );
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, true, false ) );
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, true, false, false ) );
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, false, true, false ) );
				result .add( new AxialStretchTool.Factory( tools, this .icosaSymm, false, false, false ) );
				result .add( new LinearMapTool.Factory( tools, this .icosaSymm ) );
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
	};
	
    private final SymmetryPerspective octahedralPerspective = new SymmetryPerspective()
    {
        private final OctahedralSymmetry octaSymm = new OctahedralSymmetry( field, "blue", "trapezoids" )
        {
    		@Override
    		public Direction getSpecialOrbit( SpecialOrbit which )
    		{
    			switch ( which ) {

    			case BLUE:
    				return this .getDirection( "blue" );

    			case RED:
    				return this .getDirection( "green" );

    			case YELLOW:
    				return this .getDirection( "yellow" );

    			default:
    				return null; // TODO pick/define an orbit that needs no correction
    			}
    		}

    		@Override
    		protected void createOtherOrbits()
    		{
    			createZoneOrbit( "yellow", 0, 4, new int[] { 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1 }, true, false, mField
    					.createPower( - 1 ) );

    			createZoneOrbit( "green", 1, 8, new int[] { 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1 }, true, true, mField
    					.createRational( 2 ) );

    			createZoneOrbit( "lavender", 0, NO_ROTATION, new int[] { 2, 1, - 1, 1, 0, 1, 1, 1, 2, 1, - 1, 1 } );

    			createZoneOrbit( "olive", 0, NO_ROTATION, new int[] { 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, - 1, 1 } );

    			createZoneOrbit( "maroon", 0, NO_ROTATION, new int[] { - 1, 1, 1, 1, 3, 1, - 1, 1, 1, 1, - 1, 1 } );

    			createZoneOrbit( "brown", 0, NO_ROTATION, new int[] { - 1, 1, 1, 1, - 1, 1, 1, 1, - 2, 1, 2, 1 } );

    			createZoneOrbit( "red", 0, NO_ROTATION, new int[] { 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1 } );

    			createZoneOrbit( "purple", 0, NO_ROTATION, new int[] { 1, 1, 1, 1, 0, 1, 0, 1, - 1, 1, 0, 1 }, false, false, mField
    					.createPower( - 1 ) );

    			createZoneOrbit( "black", 0, NO_ROTATION, new int[] { 1, 2, 0, 1, 0, 1, 1, 2, - 1, 2, 1, 2 }, false, false, mField
    					.createRational( 2 ) );

    			createZoneOrbit( "turquoise", 0, NO_ROTATION, new int[] { 1, 1, 2, 1, 3, 1, 4, 1, 3, 1, 4, 1 } );
    		}
    	};
        
    	private final AbstractShapes octaDefaultShapes =  new ExportedVEFShapes( null, "octahedral", "trapezoids", octaSymm, null );
    	private final AbstractShapes octahedralFast = new ExportedVEFShapes( null, "octahedralFast", "small octahedra", octaSymm, null );
    	private final AbstractShapes octahedralRealistic = new ExportedVEFShapes( null, "octahedralRealistic", "vZome logo", octaSymm, octaDefaultShapes );
		
		@Override
		public Symmetry getSymmetry()
		{
			return octaSymm;
		}
		
		@Override
		public String getName()
		{
			return "octahedral";
		}

		@Override
		public List<Shapes> getGeometries()
		{
			return Arrays.asList( octaDefaultShapes, octahedralFast, octahedralRealistic );
		}
		
		@Override
		public Shapes getDefaultGeometry()
		{
			return this .octaDefaultShapes;
		}

		@Override
		public List<Tool.Factory> createToolFactories( Tool.Kind kind, ToolsModel tools )
		{
			List<Tool.Factory> result = new ArrayList<>();
			switch ( kind ) {

			case SYMMETRY:
				result .add( new OctahedralToolFactory( tools, this .octaSymm ) );
				result .add( new TetrahedralToolFactory( tools, this .octaSymm ) );
				result .add( new InversionTool.Factory( tools ) );
				result .add( new MirrorTool.Factory( tools ) );
				result .add( new AxialSymmetryToolFactory( tools, this .octaSymm ) );
				break;

			case TRANSFORM:
				result .add( new ScalingTool.Factory( tools, this .octaSymm ) );
				result .add( new RotationTool.Factory( tools, this .octaSymm ) );
				result .add( new TranslationTool.Factory( tools ) );
				break;

			case LINEAR_MAP:
				result .add( new LinearMapTool.Factory( tools, this .octaSymm ) );
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
				result .add( new OctahedralToolFactory( tools, this .octaSymm ) .createPredefinedTool( "octahedral around origin" ) );
				result .add( new TetrahedralToolFactory( tools, this .octaSymm ) .createPredefinedTool( "tetrahedral around origin" ) );
				result .add( new InversionTool.Factory( tools ) .createPredefinedTool( "reflection through origin" ) );
				result .add( new MirrorTool.Factory( tools ) .createPredefinedTool( "reflection through XY plane" ) );
//				result .add( new AxialSymmetryToolFactory( tools, this .octaSymm ) .createPredefinedTool( "symmetry around blue through origin" ) );
				break;

			case TRANSFORM:
				result .add( new ScalingTool.Factory( tools, this .octaSymm ) .createPredefinedTool( "scale down" ) );
				result .add( new ScalingTool.Factory( tools, this .octaSymm ) .createPredefinedTool( "scale up" ) );
//				result .add( new RotationTool.Factory( tools, this .octaSymm ) .createPredefinedTool( "rotate around blue through origin" ) );
				result .add( new TranslationTool.Factory( tools ) .createPredefinedTool( "b1 move along +X" ) );
				break;

			default:
				break;
			}
			return result;
		}
	};
	
    private final QuaternionicSymmetry H4 = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", field );
    private final QuaternionicSymmetry H4_ROT = new QuaternionicSymmetry( "H4_ROT", "com/vzome/core/math/symmetry/H4roots-rotationalSubgroup.vef", field );
    private final QuaternionicSymmetry T2 = new QuaternionicSymmetry( "2T", "com/vzome/core/math/symmetry/binaryTetrahedralGroup.vef", field );

//    {
//
//    	if ( enableCommands ) {
//    		mCommands .put( "icosasymm-golden", new CommandSymmetry( icosaSymm ) );
//    		mCommands .put( "tetrasymm-golden", new CommandTetrahedralSymmetry( icosaSymm ) );
//    		mCommands .put( "axialsymm-icosa", new CommandAxialSymmetry( icosaSymm ) );
//    		mCommands .put( "h4symmetry", new CommandQuaternionSymmetry( H4, H4 ) );
//    		mCommands .put( "h4rotations", new CommandQuaternionSymmetry( H4_ROT, H4_ROT ) );
//    		mCommands .put( "IxTsymmetry", new CommandQuaternionSymmetry( H4, T2 ) );
//    		mCommands .put( "TxTsymmetry", new CommandQuaternionSymmetry( T2, T2 ) );
//    		mCommands .put( "vanOss600cell", new CommandVanOss600Cell() );
//    		mCommands .put( "runZomicScript", true );
//    		mCommands .put( "runPythonScript", true );
//    		for ( int p = 0x1; p <= 0xF; p++ ) {
//    			String dynkin = Integer.toString( p, 2 );
//    			dynkin = "0000" .substring( dynkin.length() ) + dynkin;
//    			mCommands .put( "h4polytope_" + dynkin, new CommandUniformH4Polytope( field, H4, p ) );
//    		}
//    	}
//
//
//    	if ( enableCommands ) {
//    		mCommands .put( "octasymm-golden", new CommandSymmetry( octaSymm ) );
//    		mCommands .put( "axialsymm-octa", new CommandAxialSymmetry( octaSymm ) );
//    	}
//    }

	@Override
	public Collection<SymmetryPerspective> getSymmetryPerspectives()
	{
		return Arrays.asList( this .icosahedralPerspective, this .octahedralPerspective );
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

		case "octahedral":
			return this .octahedralPerspective;

		default:
			return null;
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

    public static void main( String[] args )
    {
    	final float EPSILON = 5E-10f;

        Symmetry icosa = new IcosahedralSymmetry( new PentagonField(), "solid connectors" );
        Direction blue = icosa .getDirection( "blue" );
        
        Map<Float, Axis[]> blueAngles = new HashMap<>();
        RealVector baseRv = null;
        Axis baseZone = null;
        for (Axis zone : blue) {
            RealVector rv = zone .normal() .toRealVector() .normalize();
            if ( baseRv == null )
            {
                baseRv = rv;
                baseZone = zone;
            }
            else
            {
                float cos = Math .abs( (float) rv .dot( baseRv ) );
                // don't want zero angle (zones are the same)
                if ( cos > EPSILON && cos != 1f )
                {
                    double angle = 180f *  Math .acos( cos ) / Math .PI;
                    blueAngles .put( new Float( angle ), new Axis[]{ baseZone, zone } );
                }
            }
        }
        for (Float angle : blueAngles .keySet()) {
            System .out. print( angle + "  " );
            Axis[] zones = blueAngles .get( angle );
            System .out .print( zones[0] .getOrientation() + " " );
            System .out .println( zones[1] .getOrientation() + " " );
        }
    }
}
