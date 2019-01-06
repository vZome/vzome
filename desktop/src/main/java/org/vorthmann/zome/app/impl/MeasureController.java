package org.vorthmann.zome.app.impl;

import java.text.NumberFormat;
import java.util.TreeMap;

import org.vorthmann.ui.DefaultController;

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
	
	private final TreeMap<String, String> measurements = new TreeMap<>();
	
	private static final String[] EMPTY = new String[] {};
	
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
		return this .measurements .keySet() .toArray( EMPTY );
	}

	@Override
	public String getProperty( String key )
	{
		return this .measurements .get( key );
	}

	@Override
	public void selectionChanged( int total, int balls, int struts, int panels )
	{
		boolean newMeasurements = false;

		if ( total == 2 && panels == 2 )
		{
			this .measurements .clear();
			Panel p1 = null, p2 = null;
			for ( Panel panel : Manifestations.getPanels( this .selection ) ) {
				if ( p1 == null )
					p1 = panel;
				else
					p2 = panel;
			}
			double radians = this .renderedModel .measureDihedralAngle( p1, p2 );
			this .reportAngles( radians );
			newMeasurements = true;
		}
		else if ( total == 2 && struts == 2 )
		{
			this .measurements .clear();
	        Strut p1 = null, p2 = null;
	        for ( Strut strut : Manifestations.getStruts( this .selection ) ) {
	            if ( p1 == null )
	                p1 = strut;
	            else
	                p2 = strut;
	        }
            double radians = this .renderedModel .measureAngle( p1, p2 );
            this .reportAngles( radians );
			newMeasurements = true;
		}
		else if ( total == 2 && balls == 2 )
		{
			this .measurements .clear();
			Connector p1 = null, p2 = null;
			for ( Connector conn : Manifestations.getConnectors( this .selection ) ) {
				if ( p1 == null )
					p1 = conn;
				else
					p2 = conn;
			}
			double cm = this .renderedModel .measureDistanceCm( p1, p2 );
			this .measurements .put( "distance (cm)", twoPlaces .format( cm ) + " cm" );
			double in = cm / 2.54;
			this .measurements .put( "distance (in)", twoPlaces .format( in ) + " in" );
			newMeasurements = true;
		}
		else if ( this .measurements .size() != 0 )
		{
			this .measurements .clear();
			newMeasurements = true;
		}
		
		if ( newMeasurements )
			this .properties() .firePropertyChange( "measures", false, true );
	}

	private void reportAngles( double radians )
	{
	    double fraction = radians / Math.PI;
        double supplement = 1.0 - fraction;
		this .measurements .put( "radians", fourPlaces .format( fraction ) + "\u03C0" );
        this .measurements .put( "radians (supplement)", fourPlaces .format( supplement ) + "\u03C0" );
        this .measurements .put( "degrees", twoPlaces .format( fraction * 180 ) + "\u00B0" );
        this .measurements .put( "degrees (supplement)", twoPlaces .format( supplement * 180 ) + "\u00B0" );
	}
}
