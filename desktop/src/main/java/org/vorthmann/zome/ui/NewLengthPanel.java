package org.vorthmann.zome.ui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vorthmann.ui.CardPanel;

import com.vzome.desktop.api.Controller;

public class NewLengthPanel extends JPanel implements PropertyChangeListener, ActionListener
{
    private static final Logger logger = Logger .getLogger( "org.vorthmann.zome.ui" );
    
    private final JButton scaleUp, scaleDown, superShortScale, shortScale, mediumScale, longScale;
    
    private final JRadioButton[] multiplierButtons;

    private JComboBox<String> multiplierCombo;

    private final JCheckBox halfCheckbox;

    private final CardPanel halfCardPanel;

    private final JLabel lengthText, sliderLabel;

    private final JSlider scaleSlider;

    private final JPanel sliderPanel;

    private final JComponent upDownButtons, scaleButtons;

    private final JPanel lengthDisplay;

    private boolean emitSliderEvents = true;

    /**
     * properties: unit, multiplier
     *
     * graphics: selectedOrbit
     */
    protected Controller controller;

    private LengthDialog lengthDialog;

    private final JFrame frame;

    @Override
    public void actionPerformed( ActionEvent e )
    {
        controller .actionPerformed( e .getSource(), e .getActionCommand() );
        renderLength();
    }

    public void setController( Controller controller )
    {
        if ( this .controller != null )
        {
//            ((CanvasTool) this .controller) .detach( this );  // cannot be in preview strut drag at this point
            this .controller .removePropertyListener( this );
        }
        this .controller = controller;
        if ( this .controller != null )
        {
            this .controller .addPropertyListener( this );
//            ((CanvasTool) this .controller) .attach( this );  // cannot be in preview strut drag at this point
        }
        lengthDialog = new LengthDialog( frame, controller .getSubController( "unit" ), "Custom Unit Strut Length", new ControllerActionListener(controller) );
        boolean value = "true" .equals( controller .getProperty( "showStrutScales" ) );
        lengthDisplay .setVisible( value );
        switchOrbit();
    }

    public NewLengthPanel( JFrame frame, Controller initialController )
    {
        this .frame = frame;
        this .controller = initialController;

        this .setLayout( new BorderLayout() );
        JPanel mainPanel = new JPanel( new BorderLayout() );
        this .add( mainPanel, BorderLayout.CENTER );
        String[] labels = this .controller .getCommandList( "field.multipliers" );
        this .multiplierButtons = new JRadioButton[ labels.length ];
        {
            JPanel coloredBackground = new JPanel( new BorderLayout() );
            mainPanel .add( coloredBackground, BorderLayout .CENTER );
            {
                JPanel scalarPanel = new JPanel( new BorderLayout() );
                scalarPanel .add( new Label( "Scale by: " ), BorderLayout.WEST );
                if ( labels .length == 1 ) {
                    scalarPanel .add( new Label( labels[ 0 ] ), BorderLayout.CENTER );
                } else if ( labels .length > 3 ) {
                    // only 3 radio buttons will fit so use a combo box instead
                    multiplierCombo = new JComboBox<>(labels);
                    // works fine without setting the initial selected index to 0 
                    // but if we decide to, then do it before attaching any ActionListener
                    // multiplierCombo.setSelectedIndex(0); 
                    multiplierCombo.setActionCommand("setMultiplier.");
                    ActionListener actionListener = this;
                    multiplierCombo.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JComboBox<?> source = (JComboBox<?>) e.getSource();
                            String command = e.getActionCommand() + source.getSelectedIndex();                  
                            actionListener.actionPerformed( new ActionEvent(source, e.getID(), command, e.getWhen(), e.getModifiers()) );
                        }
                    });
                    scalarPanel.add(multiplierCombo);
                } else {
                    JPanel scalarButtonsPanel = new JPanel();
                    scalarPanel .add( scalarButtonsPanel, BorderLayout.CENTER );

                    ButtonGroup group = new ButtonGroup();
                    for ( int i = 0; i < labels.length; i++ ) {
                        String scalar = labels[ i ];
                        this .multiplierButtons[ i ] = new JRadioButton( scalar );
                        scalarButtonsPanel .add( this .multiplierButtons[ i ] );
                        group .add( this .multiplierButtons[ i ] );
                        this .multiplierButtons[ i ] .setActionCommand( "setMultiplier." + i );
                        this .multiplierButtons[ i ] .addActionListener( this );
                    }
                    this .multiplierButtons[ 0 ] .setSelected( true );
                }
                coloredBackground .add( scalarPanel, BorderLayout.NORTH );
            }
            {
                // left side: scale up/down buttons
                upDownButtons = new JPanel();
                upDownButtons .setLayout( new GridLayout( 2, 1 ) );
                {
                    scaleUp = createButton( "scaleUp", new ActionListener()
                    {
                        @Override
                        public void actionPerformed( ActionEvent e )
                        {
                            int value = scaleSlider .getValue();
                            if ( value < scaleSlider .getMaximum() )
                                scaleSlider .setValue( value + 1 );
                        }
                    } );
                    upDownButtons .add( scaleUp );
                    scaleDown = createButton( "scaleDown", new ActionListener()
                    {
                        @Override
                        public void actionPerformed( ActionEvent e )
                        {
                            int value = scaleSlider .getValue();
                            if ( value > scaleSlider .getMinimum() )
                                scaleSlider .setValue( value - 1 );
                        }
                    } );
                    upDownButtons .add( scaleDown );
                }
                coloredBackground .add( upDownButtons, BorderLayout .WEST );
            }
            {
                // center: scale slider

                sliderPanel = new JPanel( new BorderLayout() );
                sliderPanel .setBorder( BorderFactory .createEmptyBorder( 15, 15, 15, 15 ) );
                // scale slider
                scaleSlider = new JSlider( JSlider .VERTICAL, -6, 6, 0 );
                scaleSlider .setMajorTickSpacing( 1 );
                scaleSlider .setPaintTicks( true );
                sliderLabel = new JLabel( "unit" );
                Hashtable<Integer, JComponent > labelTable = new Hashtable<>();
                labelTable .put( 0, sliderLabel );
                scaleSlider .setLabelTable( labelTable );

                scaleSlider .setPaintLabels( true );
                scaleSlider .setToolTipText( "adjust scale for new struts" );

                scaleSlider .setPreferredSize( new Dimension( 20, 100 ) );
                scaleSlider .setSnapToTicks( true );
                scaleSlider .addChangeListener( new ChangeListener()
                {
                    @Override
                    public  void stateChanged( ChangeEvent e )
                    {
                        // it is essential to report state changes only when they are initiated here,
                        //  rather than in response to incoming property changes
                        if ( emitSliderEvents )
                            controller .setProperty( "scale", Integer .toString( scaleSlider .getValue() ) );
                    }
                } );
                sliderPanel .add( scaleSlider, BorderLayout .CENTER );

                coloredBackground .add( sliderPanel, BorderLayout .CENTER );
            }
            {
                // right side: fixed scale buttons
                scaleButtons = new JPanel();
                scaleButtons .setLayout( new SpringLayout() );
                {
                    halfCardPanel = new CardPanel();
                    {
                        final JPanel halfPlaceholder = new JPanel();
                        halfPlaceholder .setPreferredSize( new Dimension( 25, 25 ) );
                        halfCardPanel .add( "full", halfPlaceholder );
                        halfCheckbox = new JCheckBox( "half   " );
                        halfCheckbox .addActionListener( this );
                        halfCheckbox .setActionCommand( "toggleHalf" );
                        halfCardPanel .add( "half", halfCheckbox );
                    }
                    scaleButtons .add( halfCardPanel );
                    superShortScale = createButton( "supershort", "b0", this );
                    shortScale = createButton( "short", "b1", this );
                    mediumScale = createButton( "medium", "b2", this );
                    longScale = createButton( "long", "b3", this );
                    // set preferred width wide enough that the buttons don't resize
                    // when the button text changes (e.g. from "b0" to "shorter")
                    Dimension preferredSize = superShortScale.getPreferredSize();
                    preferredSize.width = 80;
                    superShortScale.setPreferredSize(preferredSize);
                    shortScale.setPreferredSize(preferredSize);
                    mediumScale.setPreferredSize(preferredSize);
                    longScale.setPreferredSize(preferredSize);
                    scaleButtons .add( longScale );
                    scaleButtons .add( mediumScale );
                    scaleButtons .add( shortScale );
                    scaleButtons .add( superShortScale );
                }
                SpringUtilities .makeCompactGrid( scaleButtons,
                        5, 1, //rows, cols
                        5, 5,        //initX, initY
                        6, 6);       //xPad, yPad
                mainPanel .add( scaleButtons, BorderLayout .EAST );
            }

            lengthDisplay = new JPanel( new BorderLayout() );
            lengthDisplay .setBorder( BorderFactory .createEmptyBorder( 4, 3, 4, 0 ) );
            {
                JPanel valuesColumn = new JPanel( new GridLayout( 2, 1 ) );
                {
                    lengthText = new JLabel();
                    Font biggerFont = lengthText .getFont() .deriveFont( 14f );
                    lengthText .setFont( biggerFont );
                    lengthText .setHorizontalAlignment( SwingConstants .LEADING );
                    valuesColumn .add( lengthText );
                }
                lengthDisplay .add( lengthText, BorderLayout .CENTER );
            }
            {
                JPanel buttonsPanel = new JPanel( new GridLayout( 2, 1 ) );
                {
                    JButton editButton = new JButton( "custom" );
//                    editButton .setEnabled( false );
                    editButton .setToolTipText( "customize the unit length" );
                    editButton .addActionListener( new ActionListener()
                    {
                        @Override
                        public void actionPerformed( ActionEvent e )
                        {
                            // Tell the LengthController to push the custom unit to the NumberController
                            initialController .actionPerformed( e .getSource(), "setCustomUnit" );
                            // ... then update the dialog
                            lengthDialog .syncFromModel();
                            lengthDialog .setVisible( true );
                        }} );
                    buttonsPanel .add( editButton );
                }
                {
                    JButton rezeroButton = createButton( "newZeroScale", "rezero", this );
                    rezeroButton .setToolTipText( "set this length as the unit length" );
                    buttonsPanel .add( rezeroButton );
                }
                JPanel buttonsColumn = new JPanel( new BorderLayout() );
                buttonsColumn .add( buttonsPanel, BorderLayout.NORTH );
                lengthDisplay .add( buttonsColumn, BorderLayout .EAST );
            }
            this .add( lengthDisplay, BorderLayout .SOUTH );
            lengthDisplay .setVisible( false );
        }
    }

    protected static JButton createButton( String actionCommand, ActionListener listener )
    {
        String iconPath = "/org/vorthmann/zome/ui/" + actionCommand + ".gif";
        JButton button = new JButton();
        java.net.URL imgURL = LessonPanel.class .getResource( iconPath );
        if ( imgURL != null )
            button .setIcon( new ImageIcon( imgURL ) );
        else
            logger .log( Level.WARNING, "Couldn't find resource: " + iconPath );
        button .addActionListener( listener );
        button .setActionCommand( actionCommand );
        return button;
    }

    protected static JButton createButton( String actionCommand, String label, ActionListener listener )
    {
        JButton button = new JButton( label );
        button .addActionListener( listener );
        button .setActionCommand( actionCommand );
        return button;
    }

    private void switchOrbit()
    {
        if ( "true" .equals( controller .getProperty( "halfSizes" ) ) )
            halfCardPanel .showCard( "half" );
        else
            halfCardPanel .showCard( "full" );

        longScale .setText( controller .getProperty( "scaleName.long" ) );
        mediumScale .setText( controller .getProperty( "scaleName.medium" ) );
        shortScale .setText( controller .getProperty( "scaleName.short" ) );
        superShortScale .setText( controller .getProperty( "scaleName.superShort" ) );
        renderLength();

        String colorHex = controller .getProperty( "color" );
        if ( colorHex != null )
        {
            // this can happen when deselecting the circled orbit in non-single mode
            Color color = Color .decode( colorHex );
            upDownButtons .setBackground( color );
            sliderPanel .setBorder( BorderFactory .createMatteBorder( 15, 15, 15, 15, color ) );
        }

        repaint();
    }

    private void renderLength()
    {
        halfCheckbox .setSelected( "true" .equals( controller .getProperty( "half" ) ) );

        // it is essential to report state changes only when they are initiated here,
        //  rather than in response to incoming property changes
        emitSliderEvents = false;
        scaleSlider .setValue( Integer .parseInt( controller .getProperty( "scale" ) ) );
        emitSliderEvents = true;

        String multiplierStr = controller.getProperty("multiplier");
        if(!multiplierStr.isEmpty()) {
            int multiplier = Integer.parseInt(multiplierStr);
            if (multiplierButtons[0] != null) {
                for (int j = 0; j < multiplierButtons.length; j++) {
                    multiplierButtons[j].setSelected(j == multiplier);
                }
            }
            if (multiplierCombo != null) {
                multiplierCombo.setSelectedIndex(multiplier);
            }
        }

        lengthText .setText( "<html>unit  =  <b>" + controller .getProperty( "unitText" ) + "</b>" +
           "<br/>" + controller .getProperty( "scaleFactorHtml" ) + "unit<br/>   =  <b>" + controller .getProperty( "lengthText" ) + "</b></html>" );
        boolean custom = Boolean .parseBoolean( controller .getProperty( "unitIsCustom" ) );
        if ( custom && ! lengthDisplay .isVisible() )
        {
            controller .setProperty( "showStrutScales", "true" );
        }
    }

    @Override
    public void propertyChange( PropertyChangeEvent e )
    {
        switch (e.getPropertyName()) {

        case "length":
            renderLength();
            break;

        case "selectedOrbit":
            // this first one should fall "up" to the symmetry controller
            Controller newController = controller .getSubController( "buildOrbits" );
            newController = controller .getSubController( "currentLength" );
            // newController will be null if all directions are disabled.
            // in that case, just retain the controller from the previously selected orbit.
            if( newController != null ) {
                // we didn't set this.controller directly, since we need to disconnect from it first
                setController( newController );
            }
            break;

        case "showStrutScales":
            lengthDisplay .setVisible( (Boolean) e.getNewValue() );
            break;
        }
    }

}
