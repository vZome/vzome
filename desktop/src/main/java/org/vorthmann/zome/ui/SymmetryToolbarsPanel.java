package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.vorthmann.ui.Controller;

public class SymmetryToolbarsPanel extends JPanel
{
	private final ButtonFactory factory;
	private final JToolBar secondToolbar;
	private final Map<String,JButton> toolButtons = new HashMap<>(); // to support hiding tools
	private final ToolConfigDialog toolConfigDialog;
	private final Controller controller;

	public interface ButtonFactory {
		
		public JButton makeIconButton( String tooltip, String imgLocation );
	}

	public SymmetryToolbarsPanel( Controller symmController, Controller toolsController, ControlActions enabler, ButtonFactory factory )
	{
		super();
		this .controller = symmController;
		this .factory = factory;

        this .toolConfigDialog = new ToolConfigDialog( (JFrame) this.getParent(), false );

        this .setLayout( new GridLayout( 2, 1 ) );
        
        JPanel topRow = new JPanel();
        topRow .setLayout( new BorderLayout() );
        this .add( topRow );
        
		JToolBar firstToolbar = new JToolBar();
		firstToolbar .setFloatable( false );
		firstToolbar .setOrientation( JToolBar.HORIZONTAL );
//        firstToolbar .setToolTipText( "Click on objects to select them, and enable creation of new tools accordingly." );
		JScrollPane firstScroller = new JScrollPane( firstToolbar, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        firstScroller .setBorder( null );
        topRow .add( firstScroller, BorderLayout.WEST );

        AbstractButton button;

        String[] symmetryFactories = symmController .getCommandList( "symmetryToolFactories" );
        for ( String symmetryFactory : symmetryFactories ) {
            firstToolbar .add( newToolButton( symmController, symmetryFactory ) );
		}
        
        firstToolbar .addSeparator();
        
        String[] transformFactories = symmController .getCommandList( "transformToolFactories" );
        for ( String transformFactory : transformFactories ) {
            firstToolbar .add( newToolButton( symmController, transformFactory ) );
		}                
        
        firstToolbar .addSeparator();

        String[] linearMapFactories = symmController .getCommandList( "linearMapToolFactories" );
        for ( String transformFactory : linearMapFactories ) {
            firstToolbar .add( newToolButton( symmController, transformFactory ) );
		}                

        AbstractButton shareButton = makeEditButton( enabler, "Share", "Share using Github and vZome Online" );
        topRow .add( shareButton, BorderLayout.EAST );
        controller .addPropertyListener( new PropertyChangeListener()
        {
            @Override
            public void propertyChange( PropertyChangeEvent evt )
            {
                if ( "window.file" .equals( evt .getPropertyName() ) )
                {
                    shareButton .setEnabled( evt .getNewValue() != null );
                }
            }
        });

        this .secondToolbar = new JToolBar();
        secondToolbar .setFloatable( false );
        secondToolbar .setOrientation( JToolBar.HORIZONTAL );
//        secondToolbar .setToolTipText( "All commands and tools apply to the currently selected objects." );
        JScrollPane secondScroller = new JScrollPane( secondToolbar, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        secondScroller .setBorder( null );
        this .add( secondScroller );

        button = makeEditButton( enabler, "Delete", "Delete selected objects" );
        secondToolbar .add( button );
        button = makeEditButton( enabler, "hideball", "Hide selected objects" );
        secondToolbar .add( button );
        button = makeEditButton( enabler, "setItemColor", "Set color of selected objects" );
        secondToolbar .add( button );
  
        secondToolbar .addSeparator();

        button = makeEditButton( enabler, "JoinPoints/CLOSED_LOOP", "Connect balls in a loop" );
        secondToolbar .add( button );
        button = makeEditButton( enabler, "JoinPoints/CHAIN_BALLS", "Connect balls in a chain" );
        secondToolbar .add( button );
        button = makeEditButton( enabler, "JoinPoints/ALL_TO_LAST", "Connect all balls to last selected" );
        secondToolbar .add( button );
        button = makeEditButton( enabler, "JoinPoints/ALL_POSSIBLE", "Connect balls in all possible ways" );
        secondToolbar .add( button );
        button = makeEditButton( enabler, "panel", "Make a panel polygon" );
        secondToolbar .add( button );
        button = makeEditButton( enabler, "NewCentroid", "Construct centroid of points" );
        secondToolbar .add( button );
        
        secondToolbar .addSeparator();
        
        String[] builtInSymmetryTools = symmController .getCommandList( "builtInSymmetryTools" );
        for ( String builtInSymmetryTool : builtInSymmetryTools ) {
            addTool( toolsController .getSubController( builtInSymmetryTool ) );
		}
        
        secondToolbar .addSeparator();

        String[] builtInTransformTools = symmController .getCommandList( "builtInTransformTools" );
        for ( String builtInTransformTool : builtInTransformTools ) {
            addTool( toolsController .getSubController( builtInTransformTool ) );
		}
       
        secondToolbar .addSeparator();
	}

	private static final String TOOLTIP_PREFIX = "<html><b>";
	private static final String TOOLTIP_SUFFIX = "</b><br><br><p>Right-click to configure this tool.</p></html>";

	public void addTool( Controller controller )
	{
		if ( controller == null )
			// the field may not support the tool that was requested
			return;
		String label = controller .getProperty( "label" );
		String kind = controller .getProperty( "kind" );
        String iconPath = "/icons/tools/small/" + kind + ".png";
        String tooltip = TOOLTIP_PREFIX + label + TOOLTIP_SUFFIX;
        JButton button = this .factory .makeIconButton( tooltip, iconPath );
		button .setActionCommand( "apply" );
		button .addActionListener( new ControllerActionListener(controller) );
		button .addMouseListener( new MouseAdapter()
		{
			@Override
			public void mousePressed( MouseEvent e )
			{
				maybeShowPopup( e );
			}

			@Override
			public void mouseReleased( MouseEvent e )
			{
				maybeShowPopup( e );
			}

			private void maybeShowPopup( MouseEvent e )
			{
				if ( e.isPopupTrigger() ) {
					toolConfigDialog .showTool( button, controller );
				}
			}
		} );
		controller .addPropertyListener( new PropertyChangeListener()
		{
			@Override
			public void propertyChange( PropertyChangeEvent evt )
			{
				if ( "label" .equals( evt .getPropertyName() ) )
				{
					String label = (String) evt .getNewValue();
					String tooltip = TOOLTIP_PREFIX + label + TOOLTIP_SUFFIX;
					button .setToolTipText( tooltip );
				}
			}
		});
		toolButtons .put( controller .getProperty( "id" ), button );
		secondToolbar .add( button );
    }
	
	public void hideTool( String id )
	{
	    JButton button = toolButtons .remove( id );
	    if ( button != null ) { // may be null during deserialization
	        secondToolbar .remove( button );
	        secondToolbar .revalidate();
	        secondToolbar .repaint();
	    }
	}

	private AbstractButton newToolButton( Controller symmController, String group )
	{
		String iconPath = "/icons/tools/newTool/" + group + ".png";
		Controller buttonController = symmController .getSubController( group );
		String title = buttonController .getProperty( "title" );
		String helpHtml = buttonController .getProperty( "tooltip" );
		String html = "<html><img src=\"" + ModelPanel.class.getResource( iconPath ) + "\">&nbsp;&nbsp;<b>" + title
					+ "</b><br><br>" + helpHtml + "</html>";
		final JButton button = factory .makeIconButton( html, iconPath );
		button .setActionCommand( "createTool" );
		button .addActionListener( new ControllerActionListener(buttonController) );
		button .setEnabled( buttonController != null && buttonController .propertyIsTrue( "enabled" ) );
		buttonController .addPropertyListener( new PropertyChangeListener()
		{
			@Override
			public void propertyChange( PropertyChangeEvent evt )
			{
				switch ( evt .getPropertyName() ) {

				case "enabled":
					button .setEnabled( (Boolean) evt .getNewValue() );
					break;
				}
			}
		});
		return button;
	}
	
	private AbstractButton makeEditButton( ControlActions enabler, String command, String tooltip )
	{
		AbstractButton button = factory .makeIconButton( tooltip, "/icons/tools/small/" + command + ".png" );
		button = enabler .setButtonAction( command, this .controller, button );
		return button;
	}

}
