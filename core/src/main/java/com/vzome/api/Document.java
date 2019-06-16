
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import java.util.HashSet;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;

public class Document
{
	final DocumentModel delegate;
	private final Set<Ball> balls = new HashSet<>();
	private final Set<Strut> struts = new HashSet<>();

	public Document( DocumentModel delegate )
	{
		this .delegate = delegate;		
		for ( Manifestation manifestation : this .delegate .getRealizedModel() )
			if ( ! manifestation .isHidden() )
			{
				if ( manifestation instanceof Connector )
					balls .add( new Ball( (Connector) manifestation ) );
				else if ( manifestation instanceof com.vzome.core.model.Strut )
					struts .add( new Strut( (com.vzome.core.model.Strut) manifestation ) );
			}
	}

	public Set<Ball> getBalls()
	{
		return this .balls;
	}

	public Set<Strut> getStruts()
	{
		return this .struts;
	}
	
//	public Command newCommand()
//	{
//		ApiEdit edit = (ApiEdit) this .delegate .createEdit( "apiProxy" );
//		return new Command( edit .createDelegate() );
//	}
	
	public RenderedModel getRenderedModel()
	{
	    return this .delegate .getRenderedModel();
	}
	
	public Camera getViewModel()
	{
	    return this .delegate .getCamera();
	}
	
	public float[][] getOrientations()
	{
		AlgebraicField field = this .delegate .getField();
		Symmetry symmetry = this .delegate .getSymmetrySystem() .getSymmetry();
		int order = symmetry .getChiralOrder();
		float[][] result = new float[order][];
		for ( int orientation = 0; orientation < order; orientation++ )
		{
			float[] asFloats = new float[ 16 ];
			AlgebraicMatrix transform = symmetry .getMatrix( orientation );
	        for ( int i = 0; i < 3; i++ )
	        {
	            AlgebraicVector columnSelect = field .basisVector( 3, i );
	            AlgebraicVector columnI = transform .timesColumn( columnSelect );
	            RealVector colRV = columnI .toRealVector();
	            asFloats[ i*4+0 ] = (float) colRV.x;
	            asFloats[ i*4+1 ] = (float) colRV.y;
	            asFloats[ i*4+2 ] = (float) colRV.z;
	            asFloats[ i*4+3 ] = 0f;
	        }
	        asFloats[ 12 ] = 0f;
	        asFloats[ 13 ] = 0f;
	        asFloats[ 14 ] = 0f;
	        asFloats[ 15 ] = 1f;
	        result[ orientation ] = asFloats;
		}
		return result;
	}

	public DocumentModel getDocumentModel()
	{
		return this .delegate;
	}
}
