package com.vzome.experiments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.vzome.api.Tool;
import com.vzome.api.Tool.InputBehaviors;
import com.vzome.api.Tool.OutputBehaviors;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.Point;
import com.vzome.core.editor.Application;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.IcosahedralToolFactory;
import com.vzome.core.editor.LinearMapTool;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedModel;

public class GenerateLinearMaps
{
	private Application app;
	private FieldApplication kind;
	private AlgebraicField field;
	private IcosahedralSymmetry symmetry;
	private Connector origin;
	private Direction red, yellow, blue, green;
	private final ArrayList<String> acceptableOrbitNames;

	public GenerateLinearMaps()
	{
		super();
		app = new Application( true, new Command.FailureChannel()
		{
			@Override
			public void reportFailure( Command.Failure f )
			{
				throw new RuntimeException( f .getMessage() );
			}
		}, new Properties() );

		kind = app .getDocumentKind( "golden" );
		field = kind .getField();
	    symmetry = (IcosahedralSymmetry) kind .getSymmetryPerspective( "icosahedral" ) .getSymmetry();
		red = symmetry .getDirection( "red" );
		yellow = symmetry .getDirection( "yellow" );
		blue = symmetry .getDirection( "blue" );
		green = symmetry .getDirection( "green" );
		this .acceptableOrbitNames = new ArrayList<String>();
		acceptableOrbitNames .add( "red" );
		acceptableOrbitNames .add( "yellow" );
		acceptableOrbitNames .add( "blue" );
		acceptableOrbitNames .add( "green" );
	}

	private transient DocumentModel doc;
	private transient Strut lastStrut;
	private transient Connector lastBall;
	private transient boolean goodOrbits, knownOrbits;
	private transient int fileNum = 0;
	private transient Set<Direction> orbits;

	private boolean tryMapping( Axis a1, AlgebraicNumber l1, Axis a2, AlgebraicNumber l2, Axis a3, AlgebraicNumber l3 )
	{
		this .doc = this .app .createDocument( "golden" );
		this .doc .setRenderedModel( new RenderedModel( kind .getField(), doc .getSymmetrySystem() )
		{
			@Override
			public void manifestationAdded( Manifestation m )
			{
				if ( m instanceof Connector )
					lastBall = (Connector) m;
				else if ( m instanceof Strut ) {
					lastStrut = (Strut) m;
					AlgebraicVector offset = lastStrut .getOffset();
					if ( offset .isOrigin() ) {
						return;
					}
					Axis axis = getOrbitSource() .getAxis( offset );
					if ( axis == null )
					{
						knownOrbits = false;
						goodOrbits = false;
						return;
					}
					Direction orbit = axis .getDirection();
					if ( orbit .isAutomatic() )
					{
						knownOrbits = false;
						goodOrbits = false;
						return;
					}
					orbits .add( orbit );
					if ( ! acceptableOrbitNames .contains( orbit .getName() ) )
						goodOrbits = false;
				}
			}
		} );
		origin = lastBall();
		try {
			goodOrbits = true;
			knownOrbits = true;
			orbits = new HashSet<Direction>();

			Strut strut1 = strut( origin, a1, l1 );
			Strut strut2 = strut( origin, a2, l2 );
			select( strut1 );
			select( strut2 );
			goodOrbits = true;
			knownOrbits = true;
			doc .doEdit( "affinePentagon" );

			if ( !knownOrbits ) {
				deselect();
				//writeFile( "bad" );
				return false; // short-circuit because a1 and a2 guarantee white struts, regardless of a3
			}

			doc. getHistoryModel() .undo();
			deselect();

			AlgebraicNumber blueShort = blue .getUnitLength() .times( field .createPower( 2 ) );
			// these three are adjacent edges of a dodecahedron
			select( strut( origin, blue .getAxis( 0, 0 ), blueShort ) );
			select( strut( origin, blue .getAxis( 0, 55 ), blueShort ) );
			select( strut( origin, blue .getAxis( 0, 7 ), blueShort ) );
			select( strut1 );
			select( strut2 );
			select( strut( origin, a3, l3 ) );
			select( origin ); // center for the transform
			Tool.Factory factory = new LinearMapTool.Factory( doc .getToolsModel(), symmetry, false );
			Tool mappingTool = factory .createTool();

			deselect();
			
            factory = new IcosahedralToolFactory( doc .getToolsModel(), symmetry );
			Tool symmetryTool = factory .createTool();

			deselect();
			
			strut( origin, blue .getAxis( 0, 0 ), blueShort .times( field .createPower( 3 ) ) );
			Connector p1 = lastBall();
			select( lastStrut );
			delete();
			strut( origin, blue .getAxis( 0, 8 ), blueShort .times( field .createPower( 3 ) ) );
			Connector p2 = lastBall();
			select( lastStrut );
			delete();

			select( p1 );
			select( p2 );
			join();
			
			select( p2 ); // input for the transform
			symmetryTool .apply( EnumSet.of( InputBehaviors.SELECT ), EnumSet.noneOf(OutputBehaviors.class) );
			mappingTool .apply( EnumSet.of( InputBehaviors.DELETE ), EnumSet.noneOf(OutputBehaviors.class) );

			deselect();
			
			if ( knownOrbits ) {
				int size = orbits .size();
				String fname = axisName( a1 ) + l1 .toString() + "-"
						+ axisName( a2 ) + l2 .toString() + "-"
						+ axisName( a3 ) + l3 .toString() + "-";
				String folder = orbits .size() + "/";
				if ( orbits .size() <= 11 ) {
					StringBuilder sb = new StringBuilder();
					for ( Direction orbit : orbits ) {
						sb .append( orbit .getName() + "-" );
					}
					folder += sb .toString();
				}
				new File( "search/" + folder ) .mkdirs();
				String name = "search/" + folder + "/" + fname + fileNum++ + ".vZome";
				System .out .println( "%%%%%%%%%%%%%% " + name );
				OutputStream out = new FileOutputStream( name );
				doc .serialize( out );
				out .close();
			}
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
		return true;
	}
	
	private String axisName( Axis a )
	{
		return a .getDirection() .getName() + a .getOrientation() + "-";
	}
		
	private void join()
	{
		doc .doEdit( "joinballs" );
	}

	private void delete()
	{
		doc .doEdit( "delete" );
	}

	private void select( Manifestation m )
	{
		doc .doPickEdit( m, "SelectManifestation" );
	}

	private void deselect()
	{
		doc .doEdit( "DeselectAll" );
	}
	
	private Connector lastBall()
	{
		return lastBall;
	}

	private Strut strut( Connector start, Axis zone, AlgebraicNumber length )
	{
		Point point = (Point) start .getConstructions() .next();
		doc .createStrut( point, zone, length );
		return lastStrut;
	}
	
	private void generate()
	{
		AlgebraicNumber[] scales = new AlgebraicNumber[]{ field .createPower( 1 ), field .createPower( 2 ), field .createPower( 3 ) };
		
		/*
		 * TODO:
		 *  - full search
		 *  - check all three pairs for pentagons
		 */

		GenerateLinearMaps generator = new GenerateLinearMaps();
		for ( Direction orbit1 : symmetry ) {
			if ( orbit1 .isStandard() )
				for ( AlgebraicNumber scale1 : scales ) {
					AlgebraicNumber l1 = orbit1 .getUnitLength() .times( scale1 );
					for ( Direction orbit2 : symmetry ) {
						if ( orbit2 .isStandard() )
							for ( AlgebraicNumber scale2 : scales ) {
								AlgebraicNumber l2 = orbit2 .getUnitLength() .times( scale2 );
								for ( Axis zone2 : orbit2 ) {
									for ( Axis zone3 : blue ) {
										boolean result = generator .tryMapping(
												orbit1 .getAxis( 0, 1 ), l1,
												zone2, l2,
												zone3, blue .getUnitLength() .times( scales[ 0 ] )
												);
										if ( ! result )
											break;
									}
								}
							}
					}
				}
		}
	}
	
	public static void main( String[] args )
	{
		new GenerateLinearMaps() .generate();
	}
}
