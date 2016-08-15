package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.vorthmann.ui.Controller;

public class ToolConfigDialog extends JDialog implements ActionListener
{
    private final JButton iconButton;
    private final JLabel toolLabel;
    private final JTabbedPane tabs;
    private final JCheckBox selectInputsCheckbox, deleteInputsCheckbox, selectOutputsCheckbox, createOutputsCheckbox;
	private Controller controller;

    public ToolConfigDialog( JFrame frame )
    {
        super( frame, true );

        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        setUndecorated( true );
        setLayout( new BorderLayout() );

        getRootPane() .setBorder( BorderFactory .createTitledBorder( "tool configuration" ) );

        JPanel iconAndLabel = new JPanel();
        this .add( iconAndLabel, BorderLayout .NORTH );
        iconAndLabel .setLayout( new BorderLayout() );
        {
        	iconButton = new JButton();
            URL imageURL = getClass() .getResource( "/icons/tools/small/scaling.png" ); // any would work; will get replaced
        	Icon icon = new ImageIcon( imageURL );
        	iconButton .setIcon( icon );
        	Dimension dim = new Dimension( icon .getIconWidth()+1, icon .getIconHeight()+1 );
        	iconButton .setPreferredSize( dim );
        	iconButton .setMaximumSize( dim );
        	iconButton .setActionCommand( "apply" );
        	iconButton .addActionListener( new ActionListener()
            {
    			@Override
    			public void actionPerformed( ActionEvent e )
    			{
    				setVisible( false );
    			}
    		});
        	iconButton .addActionListener( this );
        	iconAndLabel .add( iconButton, BorderLayout .WEST );
        	toolLabel = new JLabel();
        	toolLabel .setHorizontalAlignment( SwingConstants .CENTER );
        	iconAndLabel .add( toolLabel, BorderLayout .CENTER );
        }
        tabs = new JTabbedPane();
        this .add( tabs, BorderLayout .SOUTH );
        {
        	JPanel behaviorPanel = new JPanel();
        	tabs .addTab( "behavior", behaviorPanel );
            tabs .setSelectedIndex( 0 );  // should be "behavior" tab
        	behaviorPanel .setLayout( new BorderLayout() );
        	{
        		JPanel inputsOutputs = new JPanel();
        		behaviorPanel .add( inputsOutputs, BorderLayout .CENTER );
        		inputsOutputs .setLayout( new GridLayout( 1, 2 ) );
        		{
        			JPanel inputs = new JPanel();
        			inputs .setBorder( BorderFactory .createTitledBorder( "inputs" ) );
        			inputsOutputs .add( inputs );
        			inputs .setLayout( new GridLayout( 2, 1 ) );
        			selectInputsCheckbox = new JCheckBox( "select" );
        			selectInputsCheckbox .setActionCommand( "selectInputs" );
        			selectInputsCheckbox .addActionListener( this );
        			inputs .add( selectInputsCheckbox );
        			deleteInputsCheckbox = new JCheckBox( "delete" );
        			deleteInputsCheckbox .setActionCommand( "deleteInputs" );
        			deleteInputsCheckbox .addActionListener( this );
        			inputs .add( deleteInputsCheckbox );
        		}
        		{
        			JPanel outputs = new JPanel();
        			outputs .setBorder( BorderFactory .createTitledBorder( "outputs" ) );
        			inputsOutputs .add( outputs );
        			outputs .setLayout( new GridLayout( 2, 1 ) );
        			selectOutputsCheckbox = new JCheckBox( "select" );
        			selectOutputsCheckbox .setActionCommand( "selectOutputs" );
        			selectOutputsCheckbox .addActionListener( this );
        			outputs .add( selectOutputsCheckbox );
        			createOutputsCheckbox = new JCheckBox( "create" );
        			createOutputsCheckbox .setActionCommand( "createOutputs" );
        			createOutputsCheckbox .addActionListener( this );
        			outputs .add( createOutputsCheckbox );
        		}
        	}
        	{
        		JPanel actionButtons = new JPanel();
        		behaviorPanel .add( actionButtons, BorderLayout .SOUTH );
        		JButton okButton = new JButton( "OK" );
        		actionButtons .add( okButton );
        		okButton .addActionListener( new ActionListener()
                {
        			@Override
        			public void actionPerformed( ActionEvent e )
        			{
        				setVisible( false );
        			}
        		});
        		JButton applyButton = new JButton( "Apply tool" );
        		actionButtons .add( applyButton );
        		applyButton .setActionCommand( "apply" );
        		applyButton .addActionListener( new ActionListener()
                {
        			@Override
        			public void actionPerformed( ActionEvent e )
        			{
        				setVisible( false );
        			}
        		});
        		applyButton .addActionListener( this );
        	}
        	
        	JButton showParamsButton = new JButton( "Show and select parameters" );
        	showParamsButton .setActionCommand( "selectParams" );
        	showParamsButton .addActionListener( this );
        	showParamsButton .addActionListener( new ActionListener()
            {
    			@Override
    			public void actionPerformed( ActionEvent e )
    			{
    				setVisible( false );
    			}
    		});
        	tabs .addTab( "parameters", showParamsButton );
        }
        pack();
    }
    
    public void showTool( JButton button, Controller controller )
    {
		this.controller = controller;
		iconButton .setIcon( button .getIcon() );
		toolLabel .setText( controller .getProperty( "label" ) );
		boolean selectInputs = controller .propertyIsTrue( "selectInputs" );
		selectInputsCheckbox .setSelected( selectInputs );
		boolean deleteInputs = controller .propertyIsTrue( "deleteInputs" );
		deleteInputsCheckbox .setSelected( deleteInputs );
		selectInputsCheckbox .setEnabled( ! deleteInputs );
		boolean selectOutputs = controller .propertyIsTrue( "selectOutputs" );
		selectOutputsCheckbox .setSelected( selectOutputs );
		boolean createOutputs = controller .propertyIsTrue( "createOutputs" );
		createOutputsCheckbox .setSelected( createOutputs );
		selectOutputsCheckbox .setEnabled( createOutputs );
        tabs .setSelectedIndex( 0 );  // should be "behavior" tab
        setLocationRelativeTo( button );
        setVisible( true );
    }

	@Override
	public void actionPerformed( ActionEvent e )
	{
		this .controller .actionPerformed( e );
	}
}