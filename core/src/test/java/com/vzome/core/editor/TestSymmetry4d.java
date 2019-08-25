package com.vzome.core.editor;

import org.junit.Assert;
import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.edits.Symmetry4d;
import com.vzome.core.math.Projection;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class TestSymmetry4d {

	@Test
	public void test()
	{
		Selection selection = new Selection();
		AlgebraicField golden = new PentagonField();
		RealizedModel realized = new RealizedModel( golden, new Projection.Default( golden  ) );
		
		AlgebraicVector location = golden .createVector( new int[][]{ {0,1, 0,1}, {2,1, 0,1}, {2,1, 0,1}, {0,1, 0,1} } );
		Construction pt = new FreePoint( location );
		Manifestation man = realized .findConstruction( pt );
		realized .add( man );
		realized .show( man );
		man .addConstruction( pt );
		selection .select( man );
		
		QuaternionicSymmetry h4Symm = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", golden );
		try {
			new Symmetry4d( selection, realized, h4Symm, h4Symm ) .perform();
			Assert .assertEquals( 330, realized .size() );
		} catch ( Failure e ) {
			Assert .fail( e .toString() );
		}
	}

}
