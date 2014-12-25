package org.vorthmann.zome.ui;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vorthmann.ui.Controller;

public class LengthPanel extends JPanel implements PropertyChangeListener
{
    protected SpinnerNumberModel mScaleModel, mTausModel, mOnesModel, mDivisorModel;
    
    /**
     * properties: scale, taus, ones, divisor 
     * 
     * graphics: selectedOrbit
     */
    protected Controller controller;
    
    public LengthPanel( final Controller controller )
    {
        super( new BorderLayout() );
        JPanel table = new JPanel( new SpringLayout() );
        add( table, BorderLayout .NORTH );
        
        this.controller = controller;
        controller .addPropertyListener( this );

        int value = Integer .parseInt( controller .getProperty( "scale" ) );
        mScaleModel = new SpinnerNumberModel( value, //initial value
                -20, //min
                20, //max
                1);  //step
        value = Integer .parseInt( controller .getProperty( "taus" ) );
        mTausModel = new SpinnerNumberModel( value, //initial value
                -999, //min
                999, //max
                1);  //step
        value = Integer .parseInt( controller .getProperty( "ones" ) );
        mOnesModel = new SpinnerNumberModel( value, //initial value
                -999, //min
                999, //max
                1);  //step
        value = Integer .parseInt( controller .getProperty( "divisor" ) );
        mDivisorModel = new SpinnerNumberModel( value, //initial value
                1, //min
                999, //max
                1);  //step

        mScaleModel .addChangeListener( new ChangeListener()
        {
            public void stateChanged( ChangeEvent e )
            {
                LengthPanel.this .controller .setProperty( "scale", mScaleModel .getValue() .toString() );
            }
        } );

        mTausModel .addChangeListener( new ChangeListener()
        {
            public void stateChanged( ChangeEvent e )
            {
                LengthPanel.this .controller .setProperty( "taus", mTausModel .getValue() .toString() );
            }
        } );

        mOnesModel .addChangeListener( new ChangeListener()
        {
            public void stateChanged( ChangeEvent e )
            {
                LengthPanel.this .controller .setProperty( "ones", mOnesModel .getValue() .toString() );
            }
        } );

        mDivisorModel .addChangeListener( new ChangeListener()
        {
            public void stateChanged( ChangeEvent e )
            {
                LengthPanel.this .controller .setProperty( "divisor", mDivisorModel .getValue() .toString() );
            }
        } );
        
        this.setBorder( new TitledBorder( null, "Length", 4, 2,
                new java.awt.Font( "Dialog", 1, 12 ), new java.awt.Color(
                        0, 0, 0 ) ) );
        
        JButton button = new JButton( "Short" );
        button .addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent arg0 )
            {
                setStandardLength( 3 );
                LengthPanel.this .repaint();
            }} );
        table .add( button );

        //Add the first label-spinner pair.
        addLabeledSpinner( table, "Scale", mScaleModel );
        
        button = new JButton( "Medium" );
        button .addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent arg0 )
            {
                setStandardLength( 4 );
                LengthPanel.this .repaint();
            }} );
        table .add( button );

        //Add second label-spinner pair.
        addLabeledSpinner( table, "Tau *", mTausModel );
        
        button = new JButton( "Long" );
        button .addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent arg0 )
            {
                setStandardLength( 5 );
                LengthPanel.this .repaint();
            }} );
        table .add( button );


        //Add the third label-spinner pair.
        addLabeledSpinner( table, "+ 1 *", mOnesModel );

        final JPanel orbitSwatch = new JPanel()
        {
            public void paintComponent( Graphics graphics )
            {
                LengthPanel.this .controller .repaintGraphics( "selectedOrbit", graphics, getSize() );
            }
        };
        table .add( orbitSwatch );

        //Add the third label-spinner pair.
        addLabeledSpinner( table, "   /", mDivisorModel );

        //Lay out the panel.
        SpringUtilities.makeCompactGrid( table,
                                        4, 3, //rows, cols
                                        10, 10,        //initX, initY
                                        6, 10);       //xPad, yPad
    }

    private void setStandardLength( int scale )
    {
        mScaleModel .setValue( new Integer( scale ) );
        mTausModel .setValue( new Integer( 0 ) );
        mOnesModel .setValue( new Integer( 1 ) );
        mDivisorModel .setValue( new Integer( 1 ) );
    }
    

    static protected JSpinner addLabeledSpinner( Container c, String label, SpinnerModel model )
    {
        JLabel l = new JLabel(label);
        c.add(l);

        JSpinner spinner = new JSpinner(model);
        l.setLabelFor(spinner);
        c.add(spinner);

        return spinner;
    }

    public void propertyChange( PropertyChangeEvent e )
    {
        if ( "scale" .equals( e .getPropertyName() ) )
            mScaleModel .setValue( (Integer) e .getNewValue() );
        if ( "taus" .equals( e .getPropertyName() ) )
            mTausModel .setValue( (Integer) e .getNewValue() );
        if ( "ones" .equals( e .getPropertyName() ) )
            mOnesModel .setValue( (Integer) e .getNewValue() );
        if ( "divisor" .equals( e .getPropertyName() ) )
            mDivisorModel .setValue( (Integer) e .getNewValue() );
        if ( "selectedOrbit" .equals( e .getPropertyName() ) )
        {
            mScaleModel .setValue( new Integer( Integer .parseInt( controller .getProperty( "scale" ) ) ) );
            mTausModel .setValue( new Integer( Integer .parseInt( controller .getProperty( "taus" ) ) ) );
            mOnesModel .setValue( new Integer( Integer .parseInt( controller .getProperty( "ones" ) ) ) );
            mDivisorModel .setValue( new Integer( Integer .parseInt( controller .getProperty( "divisor" ) ) ) );
            repaint();
        }
    }

}
