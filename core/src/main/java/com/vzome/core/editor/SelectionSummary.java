package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.construction.Color;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.ManifestationChanges;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class SelectionSummary implements ManifestationChanges
{
    private static final Logger LOGGER = Logger.getLogger( new Throwable().getStackTrace()[0].getClassName() );
    
	private int balls = 0;
	private int struts = 0;
	private int panels = 0;
	private Collection<Listener> listeners = new ArrayList<SelectionSummary.Listener>();
	private final Selection selection;
	
	public SelectionSummary( Selection selection )
	{
		this .selection = selection;
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
		verifyCounts();
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
		verifyCounts();
	}
	
	private void verifyCounts() {
	    if(balls + struts + panels != selection .size()) {
	        // Somehow the counts got out of sync.
	        // Not sure yet how this happens, but I have seen that it does occasionaly.
	        // When it does, we get NPEs in MeasureController.selectionChanged()
	        // and there is no way to resync it without closing the document.
	        // DJH - For now, I'm just going to log the error and correct the counts until I can find the root cause.
	        if(LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Incorrect total for balls, struts and panels: "
                        + balls + " + " + struts + " + " + panels + " != " + selection .size());
            }
            // I'm assuming (maybe incorrecly) that neither manifestationAdded nor manifestationRemoved
            // will be called on a different thread while we're in this loop.
	        balls = struts = panels = 0;
	        for(Manifestation m : selection) {
	            // A call to manifestationAdded() will be recursive. Don't want that.
	            // Instead, duplicate the pertinent code.
	            if ( m instanceof Connector )
	                ++ balls;
	            else if ( m instanceof Strut )
	                ++ struts;
	            else if ( m instanceof Panel )
	                ++ panels;
	        }
            if(LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("SelectionSummary resynced on thread: " + Thread.currentThread() + ". " 
                        + balls + " + " + struts + " + " + panels + " = " + selection .size());
            }
	    }
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
