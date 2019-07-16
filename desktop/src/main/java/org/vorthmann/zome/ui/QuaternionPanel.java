package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.vorthmann.ui.Controller;

public class QuaternionPanel extends JPanel
{   
    private final NumberPanel[] numberPanels = new NumberPanel[4];

    public QuaternionPanel( Controller controller )
    {
        super();

        this .setBorder( BorderFactory .createTitledBorder( "rotation quaternion" ) );
        this .setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
        this .setToolTipText( "To pre-set the i, j, and k values, " +
                                        "select a single strut before opening this dialog." );
        String[] coords = new String[]{ "w", "x", "y", "z" };
        String[] labels = new String[]{ "       ", "+ i * ", "+ j * ", "+ k * " };
        for ( int j = 0; j < labels.length; j++ ) {
            String labelStr = labels[ j ];
            JPanel coordinate = new JPanel();
            coordinate .setLayout( new BoxLayout( coordinate, BoxLayout.LINE_AXIS ) );
            {
                JLabel label = new JLabel( labelStr );
                Font biggestFont = label .getFont() .deriveFont( 20f );
                label .setFont( biggestFont );
                label .setHorizontalAlignment( SwingConstants.LEFT );
                coordinate .add( label, BorderLayout.WEST );
            }
            {
                numberPanels[ j ] = new NumberPanel( controller .getSubController( coords[ j ]) );
                coordinate .add( numberPanels[ j ], BorderLayout.CENTER );
            }
            this .add( coordinate );
        }
    }

    public int totalLabelWidth()
    {
        return numberPanels[ 0 ] .totalLabelWidth();
    }

    public void syncFromModel()
    {
        for ( NumberPanel numberPanel : numberPanels ) {
            numberPanel .syncFromModel();
        }
    }

    public void syncToModel()
    {
        for ( NumberPanel numberPanel : numberPanels ) {
            numberPanel .syncToModel();
        }
    }
}
