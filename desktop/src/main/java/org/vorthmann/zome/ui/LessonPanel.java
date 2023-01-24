package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.vzome.desktop.api.Controller;
import com.vzome.desktop.awt.GraphicsController;

@SuppressWarnings("serial")
public class LessonPanel extends JPanel implements PropertyChangeListener
{
    private static final Logger logger = Logger.getLogger( "org.vorthmann.zome.ui" );
    
    private final Controller mController;
        
    private final JTextArea titleArea, contentArea;

    private final TextEvents titleEvents = new TextEvents( "title" );
    private final TextEvents contentEvents = new TextEvents( "content" );
    
    private final boolean isEditor;
    
	public LessonPanel( final GraphicsController controller )
	{
        mController = controller;
        mController .addPropertyListener( this );
        
        this.isEditor = controller .userHasEntitlement( "lesson.edit" ) && ! controller .propertyIsTrue( "reader.preview" );

        this .setLayout( new BorderLayout() );
        {
            JPanel pagelistPanel = new PagelistPanel( controller );
            this .add( pagelistPanel, BorderLayout.LINE_START );

            // here's the containment hierarchy
            JPanel pagePanel = new JPanel( new BorderLayout() );
            if ( ! this .isEditor )
            {
                pagePanel .setBorder( BorderFactory .createEmptyBorder( 10, 12, 10, 12 ) );
                pagePanel .setBackground( Color .WHITE );
            }
            {
                {
                    JPanel titlePanel = new JPanel( new BorderLayout() );
                    titleArea = new JTextArea( 1, 15 );
                    titleArea .setBorder( BorderFactory .createLineBorder( Color.GRAY ) );
                    titleArea .setLineWrap( true );
                    titleArea .setWrapStyleWord( true );
                    //            titleArea .setMaximumSize( new Dimension( 500, 20 ) );
                    //            titleArea .setMinimumSize( new Dimension( 500, 20 ) );
                    if ( this.isEditor )
                        titlePanel .setBorder( BorderFactory .createTitledBorder( "page title" ) );
                    else
                    {
//                        titlePanel .setBorder( BorderFactory .createEmptyBorder( 4, 4, 4, 4 ) );
                        Font biggestFont = titleArea .getFont() .deriveFont( Font.BOLD, 16f );
                        titleArea .setBorder( BorderFactory .createEmptyBorder() );
                        titleArea .setFont( biggestFont );
                        titleArea .setEditable( false );
                    }
                    titlePanel .add( titleArea, BorderLayout.CENTER );
                    pagePanel .add( titlePanel, BorderLayout.NORTH );
                }
                {
                    JPanel panel = new JPanel( new BorderLayout() );
                    //            panel .setMinimumSize( new Dimension( 500, 800 ) );
                    //            panel .setPreferredSize( new Dimension( 500, 800 ) );
                    {
                        contentArea = new JTextArea( 20, 15 );
                        // no border necessary with scroller
                        //                contentArea .setBorder( BorderFactory .createLineBorder( Color.GRAY ) );
                        contentArea .setLineWrap( true );
                        contentArea .setWrapStyleWord( true );
                        contentArea .setMaximumSize( new Dimension( 170, 800 ) );
                        if ( this.isEditor )
                        {
                            panel .setBorder( BorderFactory .createTitledBorder( "page content" ) );
                            contentArea .getDocument() .addUndoableEditListener( new UndoableEditListener() {

                                @Override
                                public void undoableEditHappened( UndoableEditEvent e )
                                {
                                    e .getEdit();
                                }
                            } );
                        }
                        else
                        {
                            panel .setBorder( BorderFactory .createEmptyBorder() );
                            contentArea .setBorder( BorderFactory .createEmptyBorder() );
                            contentArea .setEditable( false );
                        }
                        JScrollPane contentScroller = new JScrollPane( contentArea );
                        contentScroller .setBorder( BorderFactory .createEmptyBorder() );
                        panel .add( contentScroller, BorderLayout.CENTER );
                    }
                    pagePanel .add( panel, BorderLayout.CENTER );
                }
                // Do NOT try to update the page state.  We generally want UI creation to
                //  be independent of the document state, so that we have no concern about
                //  model changes that might be happening concurrently on a worker thread.
                //  Document model loading should schedule property change events that will
                //  update the UI in due course.
//                if ( mController .propertyIsTrue( "has.pages" ) )
//                    updatePageState();
            }
            this .add( pagePanel, BorderLayout.CENTER );
        }
    }

    public void updatePageState()
    {
        Document title = new PlainDocument();
        Document content = new PlainDocument();
        String titleString = mController .getProperty( "title" );
        try {
            title .insertString( 0, titleString, null );
            content .insertString( 0, mController .getProperty( "content" ), null );
        } catch ( BadLocationException e ) {
            logger .log( Level.SEVERE, "Bad textArea location", e );
        }
        title .addDocumentListener( titleEvents );
        content .addDocumentListener( contentEvents );
        titleArea .setDocument( title );
        contentArea .setDocument( content );
    }

    // DocumentListener methods
    
    private class TextEvents implements DocumentListener
    {
        private final String whichField;
        
        public TextEvents( String whichField )
        {
            this.whichField = whichField;
        }
        
        @Override
        public void changedUpdate( DocumentEvent e )
        {}

        @Override
        public void insertUpdate( DocumentEvent e )
        {
            int start = e .getOffset();
            int len = e .getLength();
            Document doc = e .getDocument();
            try {
                String text = doc .getText( start, len );
                mController .actionPerformed( LessonPanel.this, "insertUpdateEvent " + whichField + " " + start + " " + text );
            } catch ( BadLocationException e1 ) {
                logger .log( Level.SEVERE, "Bad textArea location", e1 );
            } catch ( Exception e2 ) {
                logger .log( Level.SEVERE, "unable to insert text", e2 );
            }
        }

        @Override
        public void removeUpdate( DocumentEvent e )
        {
            int start = e .getOffset();
            int len = e .getLength();
            try {
            	mController .actionPerformed( LessonPanel.this, "removeUpdateEvent " + whichField + " " + start + " " + len );
            } catch ( Exception e1 ) {
                logger .log( Level.SEVERE, "unable to remove text", e1 );
            }
        }
    }

    @Override
    public void propertyChange( PropertyChangeEvent e )
    {
        if ( e .getPropertyName() .equals( "currentPage" ) )
        {
            updatePageState();
        }
    }
    
}
