
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.StringTokenizer;

import org.vorthmann.ui.DefaultController;
import org.vorthmann.ui.SwingWorker;

import com.vzome.core.editor.LessonModel;
import com.vzome.core.editor.Snapshot;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.ThumbnailRenderer;
import com.vzome.desktop.controller.CameraController;

public class LessonController extends DefaultController
{
    private final LessonModel model;
    
    private final CameraController vpm;
    
    private boolean listening = true;
        
    public LessonController( LessonModel model, CameraController vpm )
    {
    	this .model = model;
        this .vpm = vpm;
//        pages .add( new PageController( "How to save notes", DEFAULT_CONTENT, vpm .getView() ) );
        model .addPropertyChangeListener( new PropertyChangeListener()
        {	
            @Override
			public void propertyChange( PropertyChangeEvent change )
			{
                firePropertyChange( change ); // forward to the view
			}
		} );
    }
    
    @Override
    public void doAction( String action, ActionEvent e ) throws Exception
    {
        if ( ! listening )
            // guard against events that originate here
            return;
        
        if ( "duplicatePage" .equals( action ) )
        {
            // this is the duplicate button in article mode
        	model .duplicatePage( this.vpm .getView() );
            setProperty( "edited", "true" );
        }
        else if ( "deletePage" .equals( action ) )
        {
            model .deletePage();
            setProperty( "edited", "true" );
        }
        else if ( action .startsWith( "elementSelected-" ) )
        {
            // action defined by ListPanel
            String pageNumStr = action .substring( "elementSelected-" .length() );
            model .goToPage( Integer .parseInt( pageNumStr ) );
        }
        else if ( action .startsWith( "elementChanged-" ) )
        {
            // no-op, the thumbnail image was added
        }
        else if ( action .startsWith( "elementMoved-" ) )
        {
            // action defined by ListPanel
            String moveStr = action .substring( "elementMoved-" .length() );
            StringTokenizer tokens = new StringTokenizer( moveStr, ">" );
            String from = tokens .nextToken();
            int fromNum = Integer .parseInt( from );
            String to = tokens .nextToken();
            int toNum = Integer .parseInt( to );
            model .movePage( fromNum, toNum );
        }
        else if ( "firstPage" .equals( action ) )
        {
            model .goToFirstPage();
        }
        else if ( "previousPage" .equals( action ) )
        {
        	model .goToPreviousPage();
        }
        else if ( "nextPage" .equals( action ) )
        {
        	model .goToNextPage();
        }
        else if ( "lastPage" .equals( action ) )
        {
        	model .goToLastPage();
        }
        else if ( "setView" .equals( action ) )
        {
        	model .setView( vpm .getView() );
            setProperty( "edited", "true" );
        }
        else if ( action .startsWith( "usePageView-" ) )
        {
            String pageStr = action .substring( "usePageView-".length() );
            int num = Integer .parseInt( pageStr );
            Camera newView = model .getPageView( num );
            if ( ! newView .equals( this.vpm .getView() ) )
                vpm .restoreView( newView );
        }
        else if ( action .startsWith( "copyPageView-" ) )
        {
            String pageStr = action .substring( "copyPageView-".length() );
            int num = Integer .parseInt( pageStr );
            Camera newView = model .getPageView( num );
            vpm .copyView( newView );
        }
        else if ( "restoreSnapshot" .equals( action ) )
        {
        	model .refresh();
        }
        else if ( action .startsWith( "insertUpdateEvent" ) )
        {
            StringTokenizer tokens = new StringTokenizer( action );
            tokens .nextToken(); // skip action
            String which = tokens .nextToken();
            String original = "title" .equals( which )? model .getTitle() : model .getContent();
            int offset = Integer .parseInt( tokens .nextToken() );
            String text = tokens .nextToken( "" ) .substring( 1 );  // get the rest, strip off the leading space
            StringBuffer buff = new StringBuffer();
            buff .append( original .substring( 0, offset ) );
            buff .append( text );
            buff .append( original .substring( offset ) );
            if ( "title" .equals( which ) )
                model .setTitle(buff .toString());
            else
                model .setContent(buff .toString());
        }
        else if ( action .startsWith( "removeUpdateEvent" ) )
        {
            StringTokenizer tokens = new StringTokenizer( action );
            tokens .nextToken(); // skip action
            String which = tokens .nextToken();
            String original = "title" .equals( which )? model .getTitle() : model .getContent();
            int offset = Integer .parseInt( tokens .nextToken() );
            int length = Integer .parseInt( tokens .nextToken() );
            StringBuffer buff = new StringBuffer();
            buff .append( original .substring( 0, offset ) );
            buff .append( original .substring( offset + length ) );
            if ( "title" .equals( which ) )
                model .setTitle(buff .toString());
            else
                model .setContent(buff .toString());
        }
        else
            super.doAction( action, e );
    }
    
    @Override
    public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
    {
        Integer item = (Integer) e .getSource();
        boolean[] result = new boolean[ menu.length ];
        boolean samePage = model .onPage( item .intValue() );
        for ( int i = 0; i < result.length; i++ ) {
			if ( menu[ i ] .startsWith( "usePageView-" ) )
				result[ i ] = ! samePage;
			else if ( "setView" .equals( menu[ i ] ) )
				result[ i ] = samePage;
			else if ( menu[ i ] .startsWith( "copyPageView-" ) )
				result[ i ] = true;
			else
				result[ i ] = true;
		}
        return result;
    }

    @Override
    public String getProperty( String propName )
    {
        if ( "has.pages" .equals( propName ) )
            return Boolean .toString( ! model .isEmpty() );

        if ( "onFirstPage" .equals( propName ) )
            return Boolean .toString( model .onFirstPage() );

        if ( "onLastPage" .equals( propName ) )
            return Boolean .toString( model .onLastPage() );
        
        if ( "num.pages" .equals( propName ) )
            return Integer .toString( model. size() );
        
        if ( "page.num" .equals( propName ) )
            return Integer .toString( model .getPageNum() );
        
        if ( "title" .equals( propName ) )
            return model .getTitle();

        if ( "content" .equals( propName ) )
            return model .getContent();
        
        return super.getProperty( propName );
    }
    
    public DocumentController getEditorController()
    {
        return (DocumentController) this .mNextController;
    }

    public void renderThumbnails( final Snapshot.Recorder recorder, final ThumbnailRenderer renderer )
    {
        SwingWorker worker = new SwingWorker("vZome-LessonController")
        {
            @Override
            public Object construct()
            {
                synchronized ( renderer )
                {
                    for (int i = 0; i < model .size(); i++)
                    {
                    	model .updateThumbnail( i, recorder, renderer );
                    }
                }
                return null;
            };
            @Override
            public void finished() {}
        };
        worker .start();  //Start the background thread
    }
}
