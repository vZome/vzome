package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.Collection;

import com.vzome.core.model.Color;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.ManifestationChanges;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class SelectionSummary implements ManifestationChanges
{
	private int balls = 0;
	private int struts = 0;
	private int panels = 0;
	private Collection<Listener> listeners = new ArrayList<SelectionSummary.Listener>();
	private final Selection selection;
	
	public SelectionSummary( Selection selection )
	{
		this .selection = selection;
		selection .addListener( this );
	}
	
	public void notifyListeners()
	{
		for ( Listener listener : listeners ) {
			listener .selectionChanged( selection .size(), balls, struts, panels );
		}
	}
	
	@Override
	public void manifestationAdded( Manifestation m )
	{
		if ( m instanceof Connector )
			++ balls;
		else if ( m instanceof Strut )
			++ struts;
		else if ( m instanceof Panel )
			++ panels;
	}

	@Override
	public void manifestationRemoved( Manifestation m )
	{
		if ( m instanceof Connector )
			-- balls;
		else if ( m instanceof Strut )
			-- struts;
		else if ( m instanceof Panel )
			-- panels;
	}

	@Override
	public void manifestationColored( Manifestation m, Color color ) {}
	
	public void addListener( Listener listener )
	{
		this .listeners .add( listener );
	}
	
	public interface Listener
	{
		public void selectionChanged( int total, int balls, int struts, int panels );
	}
}
