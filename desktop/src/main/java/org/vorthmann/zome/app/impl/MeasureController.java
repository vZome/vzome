package org.vorthmann.zome.app.impl;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.editor.Manifestations;
import com.vzome.core.editor.Selection;
import com.vzome.core.editor.SelectionSummary;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedModel;

public class MeasureController extends DefaultController implements SelectionSummary.Listener
{
	private final Selection selection;
	
	// LinkedHashMap preserves insertion order rather than auto-sorting
	private final Map<String, String> measurements = new LinkedHashMap<>();
	
	private final RenderedModel renderedModel;

    private final NumberFormat twoPlaces = NumberFormat .getInstance();
    private final NumberFormat fourPlaces = NumberFormat .getInstance();

	public MeasureController( EditorModel model, RenderedModel renderedModel )
	{
	    this .renderedModel = renderedModel;
	    this .selection = model .getSelection();
	    model .addSelectionSummaryListener( this );
        this .twoPlaces .setMaximumFractionDigits( 2 );
        this .fourPlaces .setMaximumFractionDigits( 4 );
	}

	@Override
	public String[] getCommandList( String listName )
	{
		return this .measurements .keySet() .toArray( new String[this .measurements .keySet().size()] );
	}

	@Override
	public String getProperty( String key )
	{
		return this .measurements .get( key );
	}

	@Override
	public void selectionChanged( int total, int balls, int struts, int panels )
	{
        this .measurements .clear();
	    if( total != 0 ) {
	        if(balls != 0) {
	            this .measurements .put( "balls", Integer.toString(balls) );
	        }
	        if(struts != 0) {
	            this .measurements .put( "struts", Integer.toString(struts) );
	        }
	        if(panels != 0) {
	            this .measurements .put( "panels", Integer.toString(panels) );
	        }
	        
	        if( total == 1 || total == 2 ) {
	            this .measurements .put( "", "" ); // visual separator
	        }
            
            if( total == 1 ) {
                if( panels == 1 ) {
                    Panel panel = Manifestations.getPanels( this .selection ).next();
                    this .measurements .put( "vertices", Integer.toString(panel.getVertexCount()) );
                } else if ( struts == 1 ) {
                    Strut strut = Manifestations.getStruts( this .selection ).next();
                    double cm = this .renderedModel .measureLengthCm( strut );
                    this .measurements .put( "length (cm)", twoPlaces .format( cm ) + " cm" );
                    double in = cm / 2.54;
                    this .measurements .put( "length (in)", twoPlaces .format( in ) + " in" );
                } else if ( balls == 1 ) {
                    Connector conn = Manifestations.getConnectors( this .selection ).next();
                    this .measurements .put( "location", conn.getLocation().toString());
                }
            } else if( total == 2 ) {
        		if( panels == 2 ) {
        			Panel p1 = null, p2 = null;
        			for ( Panel panel : Manifestations.getPanels( this .selection ) ) {
        				if ( p1 == null )
        					p1 = panel;
        				else
        					p2 = panel;
        			}
        			double radians = this .renderedModel .measureDihedralAngle( p1, p2 );
        			this .reportAngles( radians );
        		} else if ( struts == 2 ) {
        	        Strut s1 = null, s2 = null;
        	        for ( Strut strut : Manifestations.getStruts( this .selection ) ) {
        	            if ( s1 == null )
        	                s1 = strut;
        	            else
        	                s2 = strut;
        	        }
                    Set<AlgebraicVector> points = new HashSet<>();
                    points.add(s1.getLocation());
                    points.add(s1.getEnd());
                    points.add(s2.getLocation());
                    points.add(s2.getEnd());
                    if(points.size() > 3) {
                        this .measurements .put( "coplanar", AlgebraicVectors.areCoplanar(points) ? "yes" : "no" );
                    }
                    double radians = this .renderedModel .measureAngle( s1, s2 );
                    this .reportAngles( radians );
        		} else if ( balls == 2 ) {
        			Connector b1 = null, b2 = null;
        			for ( Connector conn : Manifestations.getConnectors( this .selection ) ) {
        				if ( b1 == null )
        					b1 = conn;
        				else
        					b2 = conn;
        			}
        			double cm = this .renderedModel .measureDistanceCm( b1, b2 );
        			this .measurements .put( "distance (cm)", twoPlaces .format( cm ) + " cm" );
        			double in = cm / 2.54;
        			this .measurements .put( "distance (in)", twoPlaces .format( in ) + " in" );
        		}
            }
        }
		this .properties() .firePropertyChange( "measures", false, true );
	}

	private void reportAngles( double radians )
	{
	    if(Double.isFinite(radians)) {
    	    double fraction = radians / Math.PI;
            double supplement = 1.0 - fraction;
    		this .measurements .put( "radians", fourPlaces .format( fraction ) + "\u03C0" );
            this .measurements .put( "radians (supplement)", fourPlaces .format( supplement ) + "\u03C0" );
            this .measurements .put( "degrees", twoPlaces .format( fraction * 180 ) + "\u00B0" );
            this .measurements .put( "degrees (supplement)", twoPlaces .format( supplement * 180 ) + "\u00B0" );
	    } else {
	        // This shouldn't happen, but if it does, at least we get a visual clue
            this .measurements .put( "angle", Double.toString(radians) );
	    }
	}
}
