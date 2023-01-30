
package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.vzome.desktop.api.Controller;

@SuppressWarnings("serial")
public class LengthDialog extends EscapeDialog
{
	private final NumberPanel numberPanel;
	
    public LengthDialog( Frame frame, final Controller controller, String title, final ActionListener actions )
    {
        super( frame, title, true );
        Container content = getContentPane();
        content .setLayout( new BorderLayout() );

        numberPanel = new NumberPanel( controller );
        content .add( numberPanel, BorderLayout .CENTER );

        JPanel bottomPanel = new JPanel();
        bottomPanel .setComponentOrientation( ComponentOrientation.RIGHT_TO_LEFT );
        {
            JButton cancelButton = new JButton( "Cancel" );
            cancelButton .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    LengthDialog.this .setVisible( false );
                }
            } );
            bottomPanel .add( cancelButton );
        }
        {
            final JButton setButton = new JButton( "Set" );
            getRootPane() .setDefaultButton( setButton );
            setButton .addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    numberPanel .syncToModel();
                    LengthDialog .this .setVisible( false );
                    // tell the LengthController to pull the value from the NumberController
                    actions .actionPerformed( new ActionEvent( e .getSource(), e.getID(), "getCustomUnit" ));
                }
            } );
            bottomPanel .add( setButton );
        }
        content .add( bottomPanel, BorderLayout .SOUTH );

        setSize( new Dimension( 40 + numberPanel.totalLabelWidth(), 150 ) ); // adjust width to the number of irrationals
        setLocationRelativeTo( frame );
    }

    public void syncFromModel()
    {
        this .numberPanel .syncFromModel();
    }

}
