package org.vorthmann.zome.ui;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.vorthmann.ui.Controller;

public class NumberPanel extends JPanel
{
    private final JTextPane[] fields;
	private final Controller controller;
    private final int totalLabelWidth;
    
	public NumberPanel( Controller controller )
	{
		super();
		this.controller = controller;

        this .setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
        this .setBorder( BorderFactory .createEmptyBorder( 4, 4, 4, 4 ) );
        
        String[] labels = controller .getCommandList( "labels" );
        String[] values = controller .getCommandList( "values" );
        fields = new JTextPane[ values .length ];

        Dimension maxSize = new Dimension( 40, 20 );

        SimpleAttributeSet fieldAttribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(fieldAttribs, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setFontSize(fieldAttribs, 14);

        int labelsWidthTotal = 0;
        for ( int i = 0; i < values.length; i++ )
        {
            JLabel label;
            if ( i == 0 )
                label = new JLabel( "( " );
            else if ( i == 1 )
                label = new JLabel( "  +  " );
            else if ( i == values.length - 1 )
                label = new JLabel( labels[ i-1 ] + " ) / " );
            else
                label = new JLabel( labels[ i-1 ] + " + " );

            label .setFont( label .getFont() .deriveFont( 20f ) );
            this .add( label );

            fields[ i ] = new JTextPane();
            fields[ i ] .setText( values[ i ] );
            fields[ i ]. setParagraphAttributes(fieldAttribs, true);
            fields[ i ] .setBorder( BorderFactory .createBevelBorder( BevelBorder .LOWERED ) );
            fields[ i ] .setMaximumSize( maxSize );
            fields[ i ] .setPreferredSize( maxSize );
            this .add( fields[ i ] );

            labelsWidthTotal += (maxSize.width * 2); // leave space for the static text too
        }
        totalLabelWidth = labelsWidthTotal;
	}

    public int totalLabelWidth() {
        return totalLabelWidth;
    }
    
    public void syncFromModel()
    {
        String[] values = controller .getCommandList( "values" );
        for ( int i = 0; i < values.length; i++ )
            fields[ i ] .setText( values[ i ] );
    }

	public void syncToModel()
	{
        StringBuilder buf = new StringBuilder();
        for (JTextPane field : fields) {
            buf.append(field.getText());
            buf .append( " " );
        }
		controller .setProperty( "values", buf .toString() );
	}
}
