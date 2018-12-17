package org.vorthmann.zome.app.impl;

import java.text.NumberFormat;
import java.util.TreeMap;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.editor.EditorModel;
import com.vzome.core.editor.Manifestations;
import com.vzome.core.editor.Selection;
import com.vzome.core.editor.SelectionSummary;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class MeasureController extends DefaultController implements SelectionSummary.Listener
{
	private Selection selection;
	
	private final TreeMap<String, String> measurements = new TreeMap<>();
	
	private static final String[] EMPTY = new String[] {};
	
	public MeasureController( EditorModel model )
	{
		this.selection = model .getSelection();
		model .addSelectionSummaryListener( this );
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
		
		if ( total == 2 && panels == 2 ) {
			this .measurements .clear();
			this .measurements .put( "angle", measureDihedralAngle() );
			newMeasurements = true;
		} else if ( total == 2 && struts == 2 ) {
			this .measurements .clear();
			this .measurements .put( "angle", measureAngle() );
			newMeasurements = true;
		} else if ( total == 2 && balls == 2 ) {
			this .measurements .clear();
			this .measurements .put( "distance", measureDistance() );
			newMeasurements = true;
		} else if ( this .measurements .size() != 0 ) {
			this .measurements .clear();
			newMeasurements = true;
		}
		
		if ( newMeasurements )
			this .properties() .firePropertyChange( "measures", false, true );
	}

	private String measureDistance()
	{
		Connector p1 = null, p2 = null;
		for ( Connector conn : Manifestations.getConnectors( this .selection ) ) {
			if ( p1 == null )
				p1 = conn;
			else
				p2 = conn;
		}
		RealVector offset = p1 .getLocation() .minus( p2 .getLocation() ) .toRealVector();
		double distance = offset .length();
		double inches = distance * Exporter3d.RZOME_INCH_SCALING;
		double cm = distance * Exporter3d.RZOME_CM_SCALING;
		NumberFormat formatter = NumberFormat .getInstance();
		formatter .setMaximumFractionDigits( 2 );
		return formatter .format( cm ) + " cm = " + formatter .format( inches ) + " in";
	}

	private String measureAngle()
	{
		Strut p1 = null, p2 = null;
		for ( Strut strut : Manifestations.getStruts( this .selection ) ) {
			if ( p1 == null )
				p1 = strut;
			else
				p2 = strut;
		}
		RealVector n1 = p1 .getOffset() .toRealVector();
		RealVector n2 = p2 .getOffset() .toRealVector();
		double dotProduct = n1 .dot( n2 );
		double radians = Math.acos( n1 .dot( n2 ) / ( n1 .length() * n2 .length() ) );
		double degrees = radians * 180d/ Math.PI;
		NumberFormat formatter = NumberFormat .getInstance();
		formatter .setMaximumFractionDigits( 2 );
		return formatter .format( degrees ) + "\u00B0 = " + formatter .format( radians ) + " radians";
	}

	private String measureDihedralAngle()
	{
		Panel p1 = null, p2 = null;
		for ( Panel panel : Manifestations.getPanels( this .selection ) ) {
			if ( p1 == null )
				p1 = panel;
			else
				p2 = panel;
		}
		RealVector n1 = p1 .getNormal() .toRealVector();
		RealVector n2 = p2 .getNormal() .toRealVector();
		double dotProduct = n1 .dot( n2 );
		double radians = Math.acos( n1 .dot( n2 ) / ( n1 .length() * n2 .length() ) );
		double degrees = radians * 180d/ Math.PI;
		NumberFormat formatter = NumberFormat .getInstance();
		formatter .setMaximumFractionDigits( 2 );
		return formatter .format( degrees ) + "\u00B0 = " + formatter .format( radians ) + " radians";
	}
}
