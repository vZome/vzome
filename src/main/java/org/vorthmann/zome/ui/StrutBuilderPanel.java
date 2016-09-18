package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.vorthmann.ui.Controller;


@SuppressWarnings("serial")
public class StrutBuilderPanel extends JPanel
{
    private final Controller controller;
    private final NewLengthPanel lengthPanel;
    private final OrbitPanel orbitPanel;
    
    public StrutBuilderPanel( JFrame frame, final String symmName, final Controller controller, ActionListener frameActions )
    {
        this .controller = controller;
        this .setLayout( new BorderLayout() );

        // here's the containment layout...
        
        	JPanel constraintsPanel = new JPanel();
        	constraintsPanel .setLayout( new BorderLayout() );
        	
                final Controller symmController = this .controller .getSubController( "symmetry." + symmName );
                {
                	orbitPanel = new OrbitPanel( symmController, "availableOrbits", "buildOrbits", true );
                	orbitPanel .setBorder( BorderFactory .createTitledBorder( "strut directions" ) );
                	orbitPanel .setToolTipText( "Click and drag on a ball to create a strut, using directions selected here." );
            		constraintsPanel .add( orbitPanel, BorderLayout.CENTER );
                }

                JPanel usePlanePanel = new JPanel();
        		usePlanePanel .setLayout( new BorderLayout() );
        		final JCheckBox usePlaneCheckbox = new JCheckBox( "Use working plane" );
        		usePlaneCheckbox .setEnabled( controller .propertyIsTrue( "workingPlaneDefined" ) );
        		usePlaneCheckbox .setSelected( controller .propertyIsTrue( "useWorkingPlane" ) );
        		usePlaneCheckbox .addActionListener( controller );
        		usePlaneCheckbox .setActionCommand( "toggleWorkingPlane" );
        		usePlanePanel .add( usePlaneCheckbox, BorderLayout.EAST );
        		constraintsPanel .add( usePlanePanel, BorderLayout.NORTH );
        	
            this .add( constraintsPanel, BorderLayout.CENTER );
        
        {
            lengthPanel = new NewLengthPanel( frame );
            controller .addPropertyListener( lengthPanel );
            lengthPanel .setBorder( BorderFactory .createTitledBorder( "strut size" ) );
            this .add( lengthPanel, BorderLayout.SOUTH );
        }
        
        this .controller .addPropertyListener( new PropertyChangeListener()
        {
            @Override
            public void propertyChange( PropertyChangeEvent event )
            {
            	switch ( event .getPropertyName() ) {

            	case "symmetry":
                    String system = (String) event .getNewValue();
                    Controller symmController = controller .getSubController( "symmetry." + system );
                    symmController .asController();
                    // currently on a worker thread, so schedule UI update on the EDT
                    EventQueue .invokeLater( new Runnable()
                    {
						@Override
						public void run()
						{
		                    systemChanged( symmController );
						}
					} );
					break;

            	case "useGraphicalViews":
            		orbitPanel .modeChanged( Boolean.TRUE .equals( event .getNewValue() ) );
					break;

            	case "workingPlaneDefined":
            		usePlaneCheckbox .setEnabled( Boolean.TRUE .equals( event .getNewValue() ) );
					break;

            	case "useWorkingPlane":
            		usePlaneCheckbox .setSelected( Boolean.TRUE .equals( event .getNewValue() ) );
					break;

				default:
					break;
				}
            }
        } );
        new SwingWorker<Controller, Object>()
        {
			@Override
			protected Controller doInBackground() throws Exception
			{
				return symmController .asController();
			}
			
			@Override
			protected void done()
			{
				try {
					systemChanged( get() );
				} catch ( InterruptedException | ExecutionException e ) {
					e.printStackTrace();
				}
				super.done();
			}
		} .execute();
    }

    private void systemChanged( Controller symmController )
    {
        orbitPanel .setController( symmController );
        final Controller orbitController = symmController .getSubController( "buildOrbits" );
        String dirName = orbitController .getProperty( "selectedOrbit" );
        if ( dirName == null )
            lengthPanel .setVisible( false );
        else {
            lengthPanel .setController( orbitController .getSubController( "currentLength" ) );
            lengthPanel .setVisible( true );
        }
    }
}
