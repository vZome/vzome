package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.vorthmann.ui.CardPanel;
import org.vorthmann.ui.Controller;

import com.jogamp.newt.event.KeyEvent;

public class ToolConfigDialog extends JDialog implements ActionListener
{
    private final JButton iconButton, hideButton, showParamsButton;
    private final JTextField toolName;
    private final JLabel toolLabel;
	private final CardPanel namePanel;
    private final JTabbedPane tabs;
    private final JCheckBox selectInputsCheckbox, deleteInputsCheckbox, selectOutputsCheckbox, createOutputsCheckbox;
	private Controller controller;
	private PropertyChangeListener checkboxChanges;
	private final ActionListener closer;
	
    public ToolConfigDialog( JFrame frame, boolean forBookmark )
    {
        super( frame, true );

        //        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        WindowListener closeEvents = new WindowAdapter()
        {
            @Override
            public void windowClosed( WindowEvent e )
            {
                closer .actionPerformed( new ActionEvent( e .getSource(), 0, null ) );
                super .windowClosed( e );
            }

        };
        addWindowListener( closeEvents );

        //setUndecorated( true );
        setResizable( false );
        setLayout( new BorderLayout() );
        setTitle( "tool management" );

        // The old key modifiers, from core, in case we want to bring back transient overrides.
        //        selectInputs = ( modes & ActionEvent.SHIFT_MASK ) != 0;
        //        deselectOutputs = ( modes & ActionEvent.ALT_MASK ) != 0;
        //        justSelect = ( modes & ActionEvent.META_MASK ) != 0;
        //        deleteInputs = ( modes & ActionEvent.CTRL_MASK ) != 0;

        checkboxChanges = new PropertyChangeListener()
        {
            @Override
            public void propertyChange( PropertyChangeEvent evt )
            {
                if ( ! ( evt .getNewValue() instanceof String ) )
                    return;
                boolean value = Boolean .parseBoolean( (String) evt .getNewValue() );
                switch ( evt .getPropertyName() ) {

                case "deleteInputs":
                    selectInputsCheckbox .setEnabled( !value );
                    break;

                case "selectInputs":
                    selectInputsCheckbox .setSelected( value );
                    break;

                case "selectOutputs":
                    selectOutputsCheckbox .setSelected( value );
                    break;

                case "createOutputs":
                    selectOutputsCheckbox .setEnabled( value );
                    break;

                default:
                    break;
                }
            }
        };

        closer = new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                setVisible( false );
                tabs .setSelectedIndex( 0 );  // should be "behavior" tab
                controller .removePropertyListener( checkboxChanges );
                controller = null;
            }
        };

        getRootPane() .registerKeyboardAction( closer, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), JComponent.WHEN_IN_FOCUSED_WINDOW );

        this .setMinimumSize( new Dimension( 320, 25 ) );

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
            iconAndLabel .add( iconButton, BorderLayout .WEST );
            namePanel = new CardPanel();
            {
                toolName = new JTextField();
                toolName .setHorizontalAlignment( SwingConstants .CENTER );
                toolName .addActionListener( new ActionListener()
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        String name = toolName .getText();
                        controller .setProperty( "label", name );
                        closer .actionPerformed( e );
                    }
                });
                namePanel .add( "editable", toolName );

                toolLabel = new JLabel();
                toolLabel .setHorizontalAlignment( SwingConstants .CENTER );
                namePanel .add( "builtin", toolLabel );
            }
            iconAndLabel .add( namePanel, BorderLayout .CENTER );
        }
        tabs = new JTabbedPane();
        this .add( tabs, BorderLayout .CENTER );
        {
            JPanel inputsOutputs = new JPanel();
            tabs .addTab( "behavior", inputsOutputs );
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
            tabs .add( "configuration", showParamsPanel );
            tabs .setSelectedIndex( 0 );  // should be "behavior" tab for tool, "configuration" for bookmark
            showParamsPanel .setLayout( new BorderLayout() );
            this .hideButton = new JButton( "Remove tool" );
            showParamsPanel .add( this .hideButton, BorderLayout .NORTH );
            this .hideButton .setActionCommand( "hideTool" );
            this .hideButton .addActionListener( this );
            this .showParamsButton = new JButton( "Show and select parameters" );
            showParamsPanel .add( showParamsButton, BorderLayout .SOUTH );
            showParamsButton .setActionCommand( "selectParams" );
            showParamsButton .addActionListener( this );
        }
        pack();
    }
    
    public void showTool( JButton button, Controller controller )
    {
		this.controller = controller;
		this.controller .addPropertyListener( checkboxChanges );
		iconButton .setIcon( button .getIcon() );
		String label = controller .getProperty( "label" ) ;
		toolName .setText( label );
		toolLabel .setText(label );
		boolean predefined = controller .propertyIsTrue( "predefined" );
		boolean isBookmark = "bookmark" .equals( controller .getProperty( "kind" ) );

		namePanel .showCard( predefined? "builtin" : "editable" );
        this .hideButton .setEnabled( ! predefined );
        this .showParamsButton .setEnabled( ! predefined );

        boolean selectInputs = controller .propertyIsTrue( "selectInputs" );
		selectInputsCheckbox .setSelected( selectInputs );
		selectInputsCheckbox .setEnabled( ! predefined && ! isBookmark );
		
		boolean deleteInputs = controller .propertyIsTrue( "deleteInputs" );
		deleteInputsCheckbox .setSelected( deleteInputs );
		selectInputsCheckbox .setEnabled( ! predefined && ! isBookmark && ! deleteInputs );
		deleteInputsCheckbox .setEnabled( ! predefined && ! isBookmark );
		
		boolean selectOutputs = controller .propertyIsTrue( "selectOutputs" );
        selectOutputsCheckbox .setSelected( selectOutputs );
		
        boolean createOutputs = controller .propertyIsTrue( "createOutputs" );
		createOutputsCheckbox .setSelected( createOutputs );
		createOutputsCheckbox .setEnabled( ! isBookmark );
		selectOutputsCheckbox .setEnabled( createOutputs && ! isBookmark );
        
		tabs .setSelectedIndex( 0 );  // should be "behavior" tab
        setLocationRelativeTo( button );
        setVisible( true );
    }

	@Override
	public void actionPerformed( ActionEvent e )
	{
		this .controller .actionPerformed( e .getSource(), e .getActionCommand() );
		switch ( e .getActionCommand() ) {
		
        case "apply":
        case "hideTool":
		case "selectParams":
			closer .actionPerformed( e );
			break;

		default:
			break;
		}
	}
}