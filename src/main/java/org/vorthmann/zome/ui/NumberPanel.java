package org.vorthmann.zome.ui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import org.vorthmann.ui.Controller;

public class NumberPanel extends JPanel
{
    private final JTextArea[] fields;
	private final Controller controller;
    
	public NumberPanel( Controller controller )
	{
		super();
		this.controller = controller;

        this .setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
        this .setBorder( BorderFactory .createEmptyBorder( 4, 4, 4, 4 ) );
        
        // TODO : add text fields
        String[] labels = controller .getCommandList( "labels" );
        String[] values = controller .getCommandList( "values" );
        fields = new JTextArea[ values .length ];

        JLabel label = new JLabel( "(  " );
        Font biggerFont = label .getFont() .deriveFont( 14f );
        Font biggestFont = label .getFont() .deriveFont( 20f );
        Dimension maxSize = new Dimension( 40, 20 );
        
        for ( int i = 0; i < values.length; i++ )
        {
            if ( i == 0 )
                label = new JLabel( "( " );
            else if ( i == 1 )
                label = new JLabel( "  +  " );
            else if ( i == values.length - 1 )
                label = new JLabel( labels[ i-1 ] + " ) / " );
            else
                label = new JLabel( labels[ i-1 ] + " + " );
            label .setFont( biggestFont );
            this .add( label );

            fields[ i ] = new JTextArea( values[ i ] );
            fields[ i ] .setBorder( BorderFactory .createBevelBorder( BevelBorder .LOWERED ) );
            fields[ i ] .setFont( biggerFont );
            fields[ i ] .setMaximumSize( maxSize );
            fields[ i ] .setPreferredSize( maxSize );
            this .add( fields[ i ] );
        }
	}
	
    public void syncFromModel()
    {
        String[] values = controller .getCommandList( "values" );
        for ( int i = 0; i < values.length; i++ )
            fields[ i ] .setText( values[ i ] );
    }

	public void syncToModel()
	{
        StringBuffer buf = new StringBuffer();
        for ( int i = 0; i < fields.length; i++ ) {
            buf .append( fields[ i ] .getText() );
            buf .append( " " );
        }
		controller .setProperty( "values", buf .toString() );
	}
}
