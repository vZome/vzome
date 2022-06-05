
package com.vzome.core.editor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.editor.Snapshot.SnapshotAction;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.ThumbnailRenderer;
import com.vzome.xml.DomUtils;
import com.vzome.core.viewing.Camera;


public class LessonModel implements Iterable<PageModel>
{
    private List<PageModel> pages = new ArrayList<>( 5 );

    private int pageNum = -1;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport( this );

    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        propertyChangeSupport .addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener )
    {
        propertyChangeSupport .removePropertyChangeListener( listener );
    }

    protected void firePropertyChange( String propertyName, Object oldValue, Object newValue )
    {
        propertyChangeSupport .firePropertyChange( propertyName, oldValue, newValue );
    }

    protected void firePropertyChange( String propertyName, int oldValue, int newValue )
    {
        propertyChangeSupport .firePropertyChange( propertyName, oldValue, newValue );
    }

    protected void firePropertyChange( String propertyName, boolean oldValue, boolean newValue )
    {
        propertyChangeSupport .firePropertyChange( propertyName, oldValue, newValue );
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "notes" );
        result .setAttribute( "xmlns:xml", "http://www.w3.org/XML/1998/namespace" );

        for (PageModel page : pages) {
            result .appendChild( page .getXml( doc ) );
        }
        return result;
    }

    public void setXml( Element notesXml, int defaultEditNum, Camera defaultView )
    {
        pages .clear();

        NodeList nodes = notesXml .getChildNodes();
        for ( int i = 0; i < nodes .getLength(); i++ ) {
            Node node = nodes .item( i );
            if ( node instanceof Element ) {
                Element page = (Element) node;
                String title = page .getAttribute( "title" );
                Element viewElem = DomUtils .getFirstChildElement( page, "ViewModel" );
                Camera view = ( viewElem == null )? defaultView : new Camera( viewElem );
                Element contentElem = DomUtils .getFirstChildElement( page, "content" );
                String content = contentElem .getTextContent();
                String snapshot = page .getAttribute( "snapshot" );
                int snapshotIndex;
                if ( snapshot == null || snapshot .isEmpty() )
                {
                    String num = page .getAttribute( "editNum" );
                    int edit = ( num==null || num .isEmpty() )? defaultEditNum : Integer .parseInt( num );
                    snapshotIndex = -edit; // mark this for replacement in the next cycle
                }
                else
                    snapshotIndex = Integer .parseInt( snapshot );
                PageModel model = new PageModel( title, content, view, snapshotIndex );
                pages .add( model );
            }
        }
    }

    /**
     * This is called after the document has loaded, so we can update the UI in a normalized fashion.
     */
    public void firePropertyChanges()
    {
        // Since this code is running on a background thread, we can safely fire these changes,
        //   knowing that the LessonController will schedule EDT work to handle the changes
        for ( int i = 0; i < pages .size(); i++ ) {
            firePropertyChange( "newElementAddedAt-" + i, false, true );
            if ( i == 0 ) {
                firePropertyChange( "has.pages", false, true );
                // Don't wait... we want to show the first page while the others are being added
                goToPage( 0 );
            } 
            firePropertyChange( "thumbnailChanged", -1, i );
        }
    }

    public void addPage( String name, String string, Camera view, int snapshot )
    {
        PageModel page = new PageModel( name, string, view, snapshot );
        pages .add( page );
        pageNum = 0;
    }

    @Override
    public Iterator<PageModel> iterator()
    {
        return pages .iterator();
    }

    public void goToPage( final int newPageNum )
    {
        pageNum = newPageNum;
        final PageModel newPage = pages .get( newPageNum );

        Camera newView = newPage .getView();
        firePropertyChange( "currentView", null, newView );

        int newSnapshot = newPage .getSnapshot();
        firePropertyChange( "currentSnapshot", -1, newSnapshot );

        // UI will call getProperty as a result
        firePropertyChange( "currentPage", -1, newPageNum );
    }

    public void duplicatePage( Camera view )
    {
        int newPageNum = pageNum + 1;
        PageModel page = pages .get( pageNum );
        int snap = page .getSnapshot();
        page = new PageModel( "", "", view, snap );
        pages .add( newPageNum, page );
        firePropertyChange( "newElementAddedAt-" + newPageNum, false, true );
        goToPage( newPageNum );
        firePropertyChange( "thumbnailChanged", -1, newPageNum );
    }

    public void newSnapshotPage( int snapshotId, Camera view )
    {
        int newPageNum = pages .size();
        PageModel pc = new PageModel( "", "", view, snapshotId );
        pages .add( newPageNum, pc );

        firePropertyChange( "newElementAddedAt-" + newPageNum, false, true );
        goToPage( newPageNum );
        firePropertyChange( "thumbnailChanged", -1, newPageNum );
        if ( pages .size() == 1 )
            firePropertyChange( "has.pages", false, true );
    }

    public void deletePage()
    {
        int newPageNum = ( pageNum == pages .size() - 1 )? ( pageNum - 1 ) : pageNum;
        pages .remove( pageNum );
        firePropertyChange( "pageRemovedAt-" + pageNum, false, true );
        if ( pages .isEmpty() )
        {
            pageNum = -1;
            firePropertyChange( "has.pages", true, false );
        }
        else
        {
            goToPage( newPageNum );
        }
    }

    public int size()
    {
        return pages .size();
    }

    public void movePage( int fromNum, int toNum )
    {
        PageModel moving = pages .remove( fromNum );
        pages .add( toNum, moving );
        goToPage( toNum );
    }

    public boolean isEmpty()
    {
        return pages .isEmpty();
    }

    public boolean onFirstPage()
    {
        return pageNum == 0;
    }

    public boolean onLastPage()
    {
        return pageNum == pages. size() - 1;
    }

    public int getPageNum()
    {
        return pageNum;
    }

    public String getTitle()
    {
        PageModel page = pages .get( pageNum );
        return page .getTitle();
    }

    public String getContent()
    {
        PageModel page = pages .get( pageNum );
        return page .getContent();
    }

    public void setTitle( String string )
    {
        PageModel page = pages .get( pageNum );
        page .setTitle( string );
    }

    public void setContent( String string )
    {
        PageModel page = pages .get( pageNum );
        page .setContent( string );
    }

    public void goToFirstPage()
    {
        this .goToPage( 0 );
    }

    public void goToPreviousPage()
    {
        if ( pageNum > 0 )
            this .goToPage( pageNum - 1 );
    }

    public void goToNextPage()
    {
        if ( pageNum < pages .size() - 1 )
            this .goToPage( pageNum + 1 );
    }

    public void goToLastPage()
    {
        this .goToPage( pages .size() - 1 );
    }

    public void setView( Camera view )
    {
        PageModel page = pages .get( pageNum );
        page .setView( view );
        page .setThumbnailCurrent( false );
        goToPage( pageNum );
        firePropertyChange( "thumbnailChanged", -1, pageNum );
    }

    public void refresh()
    {
        if ( pageNum >= 0 )
            goToPage( pageNum );
    }

    public Camera getPageView( int num )
    {
        PageModel page = pages .get( num );
        return page .getView();
    }

    public boolean onPage( int page )
    {
        return pageNum == page;
    }

    private static final Logger logger = Logger.getLogger( "com.vzome.core.thumbnails" );

    public void updateThumbnail( final int pageNum, final Snapshot.Recorder recorder, final ThumbnailRenderer renderer )
    {
        final PageModel page = pages .get( pageNum );
        if ( page .thumbnailIsCurrent() )
            return;
        page .setThumbnailCurrent( true );

        recorder .actOnSnapshot( page .getSnapshot(), new SnapshotAction() {

            @Override
            public void actOnSnapshot( RenderedModel snapshot )
            {
                //		        firePropertyChange( "newElementAddedAt-" + pageNum, false, true );
                renderer .captureSnapshot(snapshot, page .getView(), 80, new ThumbnailRenderer.Listener()
                {
                    @Override
                    public void thumbnailReady( Object thumbnail )
                    {
                        if ( logger .isLoggable( Level.FINER ) )
                            logger .finer( "thumbnailReady: " + page .getSnapshot() + " for page " + pageNum );

                        page .setThumbnail( thumbnail );
                        firePropertyChange( "thumbnailChanged-" + pageNum, null, thumbnail );
                    }
                } );
            }
        });
    }
}
