package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vorthmann.ui.Controller;
import org.vorthmann.ui.ReorderableJList;


public class PagelistPanel extends JPanel implements PropertyChangeListener
{
    private static final Logger logger = Logger.getLogger( "org.vorthmann.zome.thumbnails" );

    private final JList<ImageIcon> list;

    private DefaultListModel<ImageIcon> listModel;
    
    private final Controller controller;

    private static final String dupeString = "Add";

    private static final String remString = "Remove";

    private JButton removeButton;

    private JButton addButton;

    private final boolean isEditor;
    
    private transient int popupItem;

    private class ListMoves implements ListDataListener, ReorderableJList.ListMoveListener
    {
        // this object is where we can correlate the events from ReorderableJList
        
        private boolean moving = false;
        
        private int startIndex = -1;
        
        @Override
        public void contentsChanged( ListDataEvent lde )
        {
            String action = "elementChanged-" + lde .getIndex0();
            PagelistPanel .this .controller .actionPerformed( PagelistPanel.this, action );
        }
        @Override
        public void intervalAdded( ListDataEvent lde )
        {
            if ( moving )
            {
                String action = "elementMoved-" + startIndex + ">" + lde .getIndex0();
                PagelistPanel .this .controller .actionPerformed( PagelistPanel.this, action );
            }
        }
        @Override
        public void intervalRemoved( ListDataEvent lde )
        {
            if ( moving )
                startIndex = lde .getIndex0();
        }
        @Override
        public void startMove()
        {
            moving = true;
        }
        @Override
        public void endMove()
        {
            moving = false;
            startIndex = -1;
        }
    }


    private class ThumbnailSelectionRenderer extends JLabel implements ListCellRenderer<ImageIcon>
    {
        public ThumbnailSelectionRenderer()
        {
            setOpaque( true );
            setHorizontalAlignment( CENTER );
            setVerticalAlignment( CENTER );
            setBorder( BorderFactory .createEmptyBorder( 1, 7, 1, 0 ) );
        }
 
        /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        @Override
        public Component getListCellRendererComponent( JList<? extends ImageIcon> list, ImageIcon value, int index, boolean isSelected, boolean cellHasFocus )
        {
            if (isSelected) {
                setBackground( list .getSelectionBackground() );
                setForeground( list .getSelectionForeground() );
            } else {
                setBackground( list .getBackground() );
                setForeground( list .getForeground() );
            }
            setIcon( value );
            return this;
        }
    }

    private final class ContextualMenuMouseListener extends MouseAdapter
    {
        private final Controller controller;

        private final ContextualMenu pickerPopup;

        private ContextualMenuMouseListener( Controller controller, ContextualMenu pickerPopup )
        {
            this.controller = controller;
            this.pickerPopup = pickerPopup;
        }

        @Override
        public void mousePressed( MouseEvent e )
        {
            maybeShowPopup( e );
        }

        @Override
        public void mouseReleased( MouseEvent e )
        {
            maybeShowPopup( e );
        }

        private void maybeShowPopup( MouseEvent e )
        {
            if ( e.isPopupTrigger() ) {
                Object source = e .getSource();
                popupItem = list .locationToIndex( e.getPoint() );
                e .setSource( popupItem );
                pickerPopup .enableActions( controller, e );
                e .setSource( source );
                pickerPopup .show( e.getComponent(), e.getX(), e.getY() );
            }
        }
    }

    private JMenuItem createMenuItem
    (
        String text,
        String actionCommand
    )
    {
        JMenuItem menuItem = new JMenuItem( text );
        menuItem .setEnabled( true );
        menuItem .setActionCommand( actionCommand );
        return menuItem;
    }

    public PagelistPanel( final Controller controller )
    {
        super( new BorderLayout() );
        controller .addPropertyListener( this );
                
        this .controller = controller;
        ControllerActionListener actionListener = new ControllerActionListener( controller );

        this.isEditor = controller .userHasEntitlement( "lesson.edit" ) && ! controller .propertyIsTrue( "reader.preview" );

        ContextualMenu pageviewPopupMenu = new ContextualMenu();
        pageviewPopupMenu .setLightWeightPopupEnabled( false );
        if ( this .isEditor )
        {
            JMenuItem menuItem = createMenuItem( "Save Current View to Page", "setView" );
            menuItem .addActionListener( actionListener );
            pageviewPopupMenu .add( menuItem );
        }

        JMenuItem menuItem = createMenuItem( "Show This Page's View", "usePageView" );
        menuItem .addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                controller .actionPerformed( PagelistPanel.this, "usePageView-" + popupItem );
            }
        } );
        pageviewPopupMenu .add( menuItem );

        menuItem = createMenuItem( "Copy This Page's View", "copyPageView" );
        menuItem .addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                controller .actionPerformed( PagelistPanel.this, "copyPageView-" + popupItem );
            }
        } );
        pageviewPopupMenu .add( menuItem );
        
        MouseListener pageviewPopup = new ContextualMenuMouseListener( controller, pageviewPopupMenu );

        listModel = new DefaultListModel<>();
        
        ListMoves moves = new ListMoves();
        listModel .addListDataListener( moves );

        // Create the list and put it in a scroll pane.
        list = isEditor 
                ? new ReorderableJList<>( listModel, moves, ImageIcon.class ) 
                : new JList<>( listModel );
        list .addMouseListener( pageviewPopup );
        JScrollPane listScrollPane = new JScrollPane( list );
        
        if ( ! isEditor )
            list .setCellRenderer( new ThumbnailSelectionRenderer() );

        list .addListSelectionListener( new ListSelectionListener()
        {
            @Override
            public void valueChanged( ListSelectionEvent lse )
            {
                if ( lse .getValueIsAdjusting() )
                    return;
                syncController();
            }
        } );
        list .addMouseListener( new MouseAdapter()
        {
            // we need this so that an extra click can be used to restore the page view
            @Override
            public void mouseClicked( MouseEvent e )
            {
                if ( SwingUtilities.isRightMouseButton( e ) )
                    return;
                syncController();
            }
        });
        add( listScrollPane, BorderLayout.CENTER );

        if ( this.isEditor )
        {
            addButton = new JButton( dupeString );
            addButton.setActionCommand( "duplicatePage" );
            addButton.addActionListener( actionListener );
            addButton.setEnabled( true );

            removeButton = new JButton( remString );
            removeButton.setActionCommand( "deletePage" );
            removeButton.addActionListener( actionListener );
            removeButton.setEnabled( controller .propertyIsTrue( "has.pages" ) );

            // Create a panel that uses BoxLayout.
            JPanel buttonPane = new JPanel();
            buttonPane .setLayout( new BoxLayout( buttonPane, BoxLayout.PAGE_AXIS ) );
            buttonPane .add( addButton );
            buttonPane .add( removeButton );
            buttonPane .setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
            add( buttonPane, BorderLayout.PAGE_END );
        }
        list .setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        list .setVisibleRowCount( 12 );
        
        // The controller is not initialized when this constructor runs, so we defer this.
        SwingUtilities .invokeLater( new Runnable()
        {    
            @Override
            public void run() {
                int initialCount = Integer .parseInt( controller .getProperty( "num.pages" ) );
                for ( int i = 0; i < initialCount; i++ )
                {
                    ImageIcon icon = new ImageIcon( new BufferedImage( 80, 70, BufferedImage .TYPE_INT_RGB ) );
                    listModel .addElement( icon );
                }
                list .setSelectedIndex( 0 );
            }
        });
    }
    
    private void syncController()
    {
        int selected = this .list .getSelectedIndex();
        if ( selected < 0 )
            return;
        String action = "elementSelected-" + selected;
        this .controller .actionPerformed( PagelistPanel.this, action );
    }
    
    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        if ( evt .getPropertyName() .equals( "currentPage" ) )
        {
            Integer pageNum = (Integer) evt .getNewValue();
            int num = pageNum .intValue();
            list .setSelectedIndex( num );
            list .ensureIndexIsVisible( num );
        }
        else if ( "has.pages" .equals( evt .getPropertyName() ) )
        {
            if ( removeButton != null )
            {
                boolean enable = evt .getNewValue() .toString() .equals( "true" );
                removeButton .setEnabled( enable );
            }
        }
        else if ( evt .getPropertyName() .startsWith( "newElementAddedAt-" ) )
        {
            String pageNum = evt .getPropertyName() .substring( "newElementAddedAt-".length() );
            int num = Integer .parseInt( pageNum );
            ImageIcon icon = new ImageIcon( new BufferedImage( 80, 70, BufferedImage .TYPE_INT_RGB ) );
            listModel .insertElementAt( icon, num );
            list .setSelectedIndex( num );
        }
        else if ( evt .getPropertyName() .startsWith( "pageRemovedAt-" ) )
        {
            String pageNum = evt .getPropertyName() .substring( "pageRemovedAt-".length() );
            int num = Integer .parseInt( pageNum );
            listModel .remove( num );
        }
        else if ( evt .getPropertyName() .startsWith( "thumbnailChanged-" ) )
        {
            String editNumber = evt .getPropertyName() .substring( "thumbnailChanged-".length() );
            final int num = Integer .parseInt( editNumber );
            BufferedImage iconImage = (BufferedImage) evt .getNewValue();

            if ( logger .isLoggable( Level.FINER ) )
                logger .finer( "thumbnailRendered: " + iconImage + " for page " + num );

            ImageIcon icon = new ImageIcon( iconImage );
            if ( num >= listModel .size() )
                listModel .insertElementAt( icon, num );
            else
                listModel .setElementAt( icon, num );
        }
   }
}
