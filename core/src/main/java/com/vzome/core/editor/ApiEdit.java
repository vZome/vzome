package com.vzome.core.editor;

import com.vzome.api.Command;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

/**
 * An edit class designed to support use as a delegate for the public API Command.
 * 
 * @author vorth
 *
 */
public class ApiEdit extends ChangeManifestations
{
	public ApiEdit( Selection selection, RealizedModel realized, Point origin )
	{
		super( selection, realized );
	}
	
	private Selection getSelection()
	{
		return this .mSelection;
	}
	
	public Command.Delegate createDelegate()
	{
		return new ApiDelegate( this );
	}
	
	@Override
	protected String getXmlElementName()
	{
		throw new IllegalStateException( "ApiEdit is designed for use outside of vZome, so should never get saved." );
	}

	/**
	 * This class is used in RunPythonScript also.
	 * @author vorth
	 *
	 */
	private static class ApiDelegate implements Command.Delegate
	{
		private final ApiEdit edit;

		public ApiDelegate( ApiEdit edit )
		{
			this .edit = edit;
		}
		
        private Manifestation manifest( Construction c )
        {
            Manifestation man = edit .manifestConstruction( c );
            edit .redo();
            return man;
        }

		@Override
		public AlgebraicField getField()
		{
			return edit .mManifestations .getField();
		}

		@Override
		public Selection getInputs()
		{
			Selection inputsUnselected = new Selection();
	        for ( Manifestation man : this .edit .getSelection() ) {
            	inputsUnselected .select( man );
			}
	        for ( Manifestation man : inputsUnselected ) {
	        	this .edit .getSelection() .unselect( man );
			}
	        this .edit .redo();  // the unselects must be cleared, in case any are reselected
			return inputsUnselected;
		}

		@Override
		public Connector addBall( AlgebraicVector loc )
		{
			Point p1 = new FreePoint( loc );
			return (Connector) manifest( p1 );
		}

		@Override
		public Strut addStrut( AlgebraicVector start, AlgebraicVector end )
		{
			Point p1 = new FreePoint( start );
			Point p2 = new FreePoint( end );
			Segment s = new SegmentJoiningPoints( p1, p2 );
			return (Strut) manifest( s );
		}

		@Override
		public Panel addPanel( AlgebraicVector... vertices )
		{
			Point[] points = new Point[ vertices .length ];
			int i = 0;
			for ( AlgebraicVector vertex : vertices ) {
				points[ i ] = new FreePoint( vertex );
				++i;
			}
			return (Panel) manifest( new PolygonFromVertices( points ) );
		}

		@Override
		public void select( Manifestation man )
		{
			this .edit .select( man );
		}
	}
}
