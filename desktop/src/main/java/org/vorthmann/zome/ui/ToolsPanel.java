
package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.vorthmann.ui.Controller;

public class ToolsPanel extends JPanel implements PropertyChangeListener
{
    private final Controller controller;
        
    private JPanel instances;
    
    private JScrollPane scroller;
        
    private static final Logger logger = Logger .getLogger( "org.vorthmann.zome.ui" );

    public ToolsPanel( final JFrame frame, final Controller controller )
    {
        this .controller = controller;
        controller .addPropertyListener( this );
                
        setLayout( new BorderLayout() );
        
        JPanel types = new JPanel();
        {
            JButton newButton = new JButton( "+" );
            newButton .addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent arg0 )
                {
                    final NewToolDialog newToolDialog = new NewToolDialog( frame, controller );
                    newToolDialog .setVisible( true );
                }
                
            } );
            newButton .setPreferredSize( new Dimension( 40, 20 ) );
            types .add( newButton );
        }
        
        add( types, BorderLayout .NORTH );
        
        instances = new JPanel();
        instances .setMaximumSize( new Dimension( 350, 0 ) );
//        instances .setLayout( new BoxLayout( instances, BoxLayout .Y_AXIS ) );
        instances .setLayout( new GridLayout( 0,4 ) );
        JPanel instancesBorder = new JPanel( new BorderLayout() );
        instancesBorder .add( instances, BorderLayout.PAGE_START );

        scroller = new JScrollPane( instancesBorder, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        add( scroller, BorderLayout .CENTER );
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        if ( evt .getPropertyName() .equals( "tool.instances" ) )
        {
            if ( evt .getOldValue() == null )
            {
                String idAndName = (String) evt .getNewValue(); // will be "group.N/label"

                int delim = idAndName .indexOf( "." );
                String group = idAndName .substring( 0, delim );
                delim = idAndName .indexOf( "/" );
                String name = idAndName .substring( delim + 1 );
                String iconPath = "/icons/tools/" + group + ".png";
                JButton button = new JButton();
                java.net.URL imgURL = LessonPanel.class .getResource( iconPath );
                if ( imgURL != null )
                    button .setIcon( new ImageIcon( imgURL ) );
                else {
                    button .setText( name );
                    logger .warning( "Couldn't find resource: " + iconPath );
                }
                button .addActionListener( new ControllerActionListener(controller) );
                button .setActionCommand( idAndName );
                Dimension dim = new Dimension( 55, 52 );
                button .setMinimumSize( dim );
                button .setPreferredSize( dim );
                button .setMaximumSize( dim );
                button .setToolTipText( name );

                instances .add( button );
                scroller .revalidate();
            }
        }
    }
}
