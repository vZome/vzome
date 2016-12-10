package com.vzome.fields.heptagon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.BigRational;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.Selection;
import com.vzome.core.model.RealizedModel;

/**
 * This program generates the vertices of the Gosset polytope, as embedded in an 8-dimensional
 * subspace of Z^9... the orthogonal complement of (1,1,1,1,1,1,1,1,1).  David Richter helped
 * by providing the 8th root, the "sore thumb" of the Coxeter diagram for E8.  All roots have
 * norm=2.
 * 
 * I selected this embedding in Z^9 because it will very naturally project to a 3D vector space
 * over the Heptagon field, which is order-3.  In that projection, the polytope should retain
 * a high proportion of its symmetries.
 * 
 * @author Scott Vorthmann
 *
 */
public class HeptagonGossetPolytope extends ChangeManifestations
{
	private final AlgebraicVector[] ROOTS = new AlgebraicVector[ 8 ];
	private final AlgebraicNumber scale;
	private final AlgebraicField field;
	private final Set<AlgebraicVector> vertices = new HashSet<AlgebraicVector>();
	private final Map<AlgebraicVector, Point> vertices3d = new HashMap<AlgebraicVector, Point>();
	private final Queue<AlgebraicVector> worklist = new LinkedList<AlgebraicVector>();
	
	private int numEdges = 0, numVertices = 0;
	
	public HeptagonGossetPolytope( Selection selection, RealizedModel model )
	{
		super( selection, model );
		field = model .getField();
		AlgebraicNumber zero = field .zero();
		AlgebraicNumber one = field .one();
		AlgebraicNumber negOne = one .negate();
		AlgebraicNumber negTwo = one .plus( one ) .negate();
		ROOTS[ 0 ] = new AlgebraicVector( one, negOne, zero, zero, zero, zero, zero, zero, zero );
		ROOTS[ 1 ] = new AlgebraicVector( zero, one, negOne, zero, zero, zero, zero, zero, zero );
		ROOTS[ 2 ] = new AlgebraicVector( zero, zero, one, negOne, zero, zero, zero, zero, zero );
		ROOTS[ 3 ] = new AlgebraicVector( zero, zero, zero, one, negOne, zero, zero, zero, zero );
		ROOTS[ 4 ] = new AlgebraicVector( zero, zero, zero, zero, one, negOne, zero, zero, zero );
		ROOTS[ 5 ] = new AlgebraicVector( zero, zero, zero, zero, zero, one, negOne, zero, zero );
		ROOTS[ 6 ] = new AlgebraicVector( zero, zero, zero, zero, zero, zero, one, negOne, zero );
		ROOTS[ 7 ] = new AlgebraicVector( one, one, one, one, one, negTwo, negTwo, negTwo, negOne ) .scale( field .createRational( 1, 3 ) .negate() );
		scale = field .createPower( 4 );
	}
	
	public void perform()
	{
		this .generateEdge( null, ROOTS[ 0 ] );
		while ( ! worklist .isEmpty() ) {
			AlgebraicVector v = worklist .remove();
			for ( AlgebraicVector root : ROOTS ) {
				AlgebraicVector reflection = reflect( v, root );
				this .generateEdge( v, reflection );
			}
		}
		redo();
	}
	
	private void generateEdge( AlgebraicVector vertex, AlgebraicVector reflection )
	{
		Point p;
		if ( vertices .add( reflection ) )
		{
			worklist .add( reflection );
            p = new FreePoint( this .projectTo3d( reflection ) .scale( this .scale ) );
            this .vertices3d .put( reflection, p );
            manifestConstruction( p );
			System .out .println( "vertex " + ++numVertices );
		}
		else
			p = this .vertices3d .get( reflection );
		if ( vertex != null )
		{
			Point p2 = this .vertices3d .get( vertex );
			Segment seg = new SegmentJoiningPoints( p2, p );
			manifestConstruction( seg );
			System .out .println( "edge " + ++numEdges );
		}
	}
	
	private static AlgebraicVector reflect( AlgebraicVector vector, AlgebraicVector root )
	{
		AlgebraicNumber multiplier = vector .dot( root );
		// norm of root is 2, so I don't need to divide by that norm and multiply by 2
		return vector .minus( root .scale( multiplier ) );
	}
	
	private AlgebraicVector projectTo3d( AlgebraicVector vector9d )
	{
		BigRational[] factors = new BigRational[]{
				vector9d .getComponent( 0 ) .getFactors()[ 0 ],
				vector9d .getComponent( 1 ) .getFactors()[ 0 ],
				vector9d .getComponent( 2 ) .getFactors()[ 0 ]
		};
		AlgebraicNumber x = this .field .createAlgebraicNumber( factors );
		factors = new BigRational[]{
				vector9d .getComponent( 3 ) .getFactors()[ 0 ],
				vector9d .getComponent( 4 ) .getFactors()[ 0 ],
				vector9d .getComponent( 5 ) .getFactors()[ 0 ]
		};
		AlgebraicNumber y = this .field .createAlgebraicNumber( factors );
		factors = new BigRational[]{
				vector9d .getComponent( 6 ) .getFactors()[ 0 ],
				vector9d .getComponent( 7 ) .getFactors()[ 0 ],
				vector9d .getComponent( 8 ) .getFactors()[ 0 ]
		};
		AlgebraicNumber z = this .field .createAlgebraicNumber( factors );
		return new AlgebraicVector( x, y, z );
	}

	@Override
	protected String getXmlElementName()
	{
		return "HeptagonGossetPolytope";
	}
}
