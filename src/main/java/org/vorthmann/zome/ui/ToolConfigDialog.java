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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.vorthmann.ui.Controller;

import com.jogamp.newt.event.KeyEvent;

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

        //setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        //setUndecorated( true );
        setResizable( false );
        setLayout( new BorderLayout() );
        setTitle( "tool configuration" );

        ActionListener closer = new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                setVisible( false );
            }
        };

        getRootPane() .registerKeyboardAction( closer, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), JComponent.WHEN_IN_FOCUSED_WINDOW );

        JPanel iconAndLabel = new JPanel();
        this .add( iconAndLabel, BorderLayout .NORTH );
        iconAndLabel .setLayout( new BorderLayout() );
        {
        	iconButton = new JButton();
            URL imageURL = getClass() .getResource( "/icons/tools/small/scaling.png" ); // any would work; will get replaced
        	Icon icon = new ImageIcon( imageURL );
        	iconButton .setIcon( icon );
        	Dimension dim = new Dimension( icon .getIconWidth()+13, icon .getIconHeight()+13 );
        	iconButton .setPreferredSize( dim );
        	iconButton .setMaximumSize( dim );
        	iconButton .setActionCommand( "apply" );
        	iconButton .addActionListener( this );
        	iconButton .addActionListener( closer );
        	iconAndLabel .add( iconButton, BorderLayout .WEST );
        	toolLabel = new JLabel();
        	toolLabel .setHorizontalAlignment( SwingConstants .CENTER );
        	iconAndLabel .add( toolLabel, BorderLayout .CENTER );
        }
        tabs = new JTabbedPane();
        this .add( tabs, BorderLayout .CENTER );
        {
        	JPanel inputsOutputs = new JPanel();
        	tabs .addTab( "behavior", inputsOutputs );
        	tabs .setSelectedIndex( 0 );  // should be "behavior" tab
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

        	JPanel showParamsPanel = new JPanel();
        	tabs .add( "parameters", showParamsPanel );
        	showParamsPanel .setLayout( new BorderLayout() );
        	JButton showParamsButton = new JButton( "Show and select parameters" );
        	showParamsPanel .add( showParamsButton, BorderLayout .SOUTH );
        	showParamsButton .setActionCommand( "selectParams" );
        	showParamsButton .addActionListener( this );
        	showParamsButton .addActionListener( closer );
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