package org.vorthmann.zome.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.vorthmann.ui.Controller;

@SuppressWarnings("serial")
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

        JPopupMenu contextMenu = getContextMenu();
        setComponentPopupMenu(contextMenu);

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
            fields[ i ] .setComponentPopupMenu(contextMenu);
            this .add( fields[ i ] );

            labelsWidthTotal += (maxSize.width * 2); // leave space for the static text too
        }
        totalLabelWidth = labelsWidthTotal;
	}

	protected JPopupMenu getContextMenu() {
	    JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem(new AbstractAction("Show Decimal Value") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String msg = null;
                try {
                    syncToModel();
                    msg = controller.getProperty("value") + " = " + controller.getProperty("evaluate");
                    System.out.println(msg);
                }
                catch(NumberFormatException ex) {
                    msg = ex.getClass().getSimpleName() + " " + ex.getMessage();
                }
                JOptionPane.showMessageDialog( null, msg, "Decimal Value", JOptionPane.PLAIN_MESSAGE );
            }
        }));
        popup.addSeparator();
	    popup.add(new JMenuItem(new AbstractAction("Reset to Zero") {
	        @Override
            public void actionPerformed(ActionEvent ae) {
                controller.setProperty("zero", null);
                syncFromModel();
	        }
	    }));
        popup.add(new JMenuItem(new AbstractAction("Set to One") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                controller.setProperty("one", null);
                syncFromModel();
            }
        }));
        popup.add(new JMenuItem(new AbstractAction("Negate") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                controller.setProperty("negate", null);
                syncFromModel();
            }
        }));
	    return popup;
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
