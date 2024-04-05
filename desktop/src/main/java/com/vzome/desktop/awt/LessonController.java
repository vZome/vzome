
package com.vzome.desktop.awt;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.StringTokenizer;

import javax.swing.event.SwingPropertyChangeSupport;

import com.vzome.core.editor.LessonModel;
import com.vzome.core.editor.Snapshot;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.ThumbnailRenderer;
import com.vzome.desktop.controller.CameraController;

/*
 * Thoughts on how to rationalize creation and update of the article mode editor:
 * 
 * We can ignore the document-created and document-with-no-pages-loaded scenarios, since
 * we know we don't want to create the LessonPanel yet nor enable the mode switch.
 * 
 * The remaining scenarios are:
 * 
 *   - document-with-pages-loaded
 *   - first-page-captured
 *   - subsequent-page-captured
 *   
 * Logically, to normalize things as much as possible, we should refactor these as:
 * 
 *   - lesson-model-created  (triggers creation of the UI)
 *   - lesson-page-added
 *   
 * The load case just automates the first event and a series of the second.
 * 
 * If we are careful to schedule those events as distinct actions on the EDT,
 * they will serialize nicely.  We can do this using the SwingPropertyChangeSupport,
 * as long as the scheduling code executes on a worker thread.  (If spcs .firePropertyChange
 * happens on the EDT, the handling is synchronous and immediate!)
 * 
 * A better option: always create the lesson UI with the document UI, just don't enable it.
 * This is a simplification, since we don't need the lesson UI to listen to "has.pages" at all.
 * That simplification is certainly worth the tiny overhead of creating the Swing objects.
 * Therefore, we don't need an event to create the lesson UI.  We will only need
 */

public class LessonController extends DefaultGraphicsController
{
	private final PropertyChangeSupport spcs = new SwingPropertyChangeSupport( this, true ); // events will always be handled on EDT

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
                spcs .firePropertyChange( change ); // forward to the view, but on the EDT
            }
        } );
    }

	@Override
	public void addPropertyListener( PropertyChangeListener listener )
	{
		this .spcs .addPropertyChangeListener( listener );
	}

	@Override
	public void removePropertyListener( PropertyChangeListener listener )
	{
		this .spcs .removePropertyChangeListener( listener );
	}

    @Override
    public void doAction( String action ) throws Exception
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
        else if ( "toggleReverse" .equals( action ) )
        {
            model .toggleReverse();
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
        else if ( "setViewAll" .equals( action ) )
        {
            model .setViewAll( vpm .getView() );
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
            super.doAction( action );
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
            // This should not be called for UI creation, so we can do that concurrently with model loading
            return Boolean .toString( ! model .isEmpty() );

        if ( "reverse" .equals( propName ) )
            return Boolean .toString( model .isReverse() );

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
        // Now we know that the entire history has been loaded and all Snapshots discovered,
        //  so it is safe to fire off the property changes to updated the lesson UI
        model .firePropertyChanges();
        
        // This method is invoked on a background (worker) thread, so we don't need another thread
        //   to do this without affecting the main UI.  I'm not sure we still need the synchronized block.
        synchronized ( renderer )
        {
            for (int i = 0; i < model .size(); i++)
            {
                model .updateThumbnail( i, recorder, renderer );
            }
        }
    }
}
