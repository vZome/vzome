package com.vzome.core.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.edits.Delete;
import com.vzome.core.math.Projection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.ConnectorImpl;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.RealizedModelImpl;

public class DeleteTest {

	@Test
	public void testPerform()
	{
		AlgebraicField field = new PentagonField();
		Selection selection = new SelectionImpl();
		RealizedModel realized = new RealizedModelImpl( field, new Projection .Default( field ) );
        EditorModel editorModel = new EditorModel()
        {
            @Override
            public RealizedModel getRealizedModel() { return realized; }

            @Override
            public Selection getSelection() { return selection; }
        };
		assertEquals( 0, realized .size() );

		AlgebraicVector loc = field .basisVector( 3, 2 );
		Connector ball = new ConnectorImpl( loc );
		realized .add( ball );
		selection .select( ball );
		assertEquals( 1, realized .size() );
		assertFalse( selection .size() == 0 );
		
		Delete cmd = new Delete( editorModel );
		try {
			cmd .perform();
		} catch ( Failure e ) {
			fail( "Delete perform failed" );
		}
		assertEquals( 0, realized .size() );
		assertTrue( selection .size() == 0 );
	}
	
	@Test
	public void testEmpty()
	{
		AlgebraicField field = new PentagonField();
		Selection selection = new SelectionImpl();
		RealizedModelImpl realized = new RealizedModelImpl( field, new Projection .Default( field ) );
        EditorModel editorModel = new EditorModel()
        {
            @Override
            public RealizedModel getRealizedModel() { return realized; }

            @Override
            public Selection getSelection() { return selection; }
        };
		assertEquals( 0, realized .size() );
		assertTrue( selection .size() == 0 );

		AlgebraicVector loc = field .basisVector( 3, 2 );
		Connector ball = new ConnectorImpl( loc );
		realized .add( ball );
		assertEquals( 1, realized .size() );
		assertTrue( selection .size() == 0 );
		
		Delete cmd = new Delete( editorModel );
		try {
			cmd .perform();
		} catch ( Failure e ) {
			fail( "Delete perform failed" );
		}
		assertEquals( 1, realized .size() );
		assertTrue( selection .size() == 0 );
	}
}
