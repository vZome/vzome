package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.vorthmann.ui.CardPanel;
import org.vorthmann.ui.Controller;


@SuppressWarnings("serial")
public class StrutBuilderPanel extends JPanel
{
    private final Controller controller;
    private final CardPanel orbitCardPanel;
    private final NewLengthPanel lengthPanel;

    
    public StrutBuilderPanel( JFrame frame, final String[] symmNames, final Controller controller, MouseListener orbitPopup )
    {
        this .controller = controller;
        this .setLayout( new BorderLayout() );
        
        // here's the containment layout...
        {
        	JPanel constraintsPanel = new JPanel();
        	constraintsPanel .setLayout( new BorderLayout() );
        	{
        		orbitCardPanel = new CardPanel();
        		constraintsPanel .add( orbitCardPanel, BorderLayout.CENTER );
        		JPanel usePlanePanel = new JPanel();
        		usePlanePanel .setLayout( new BorderLayout() );
        		final JCheckBox usePlaneCheckbox = new JCheckBox( "Use working plane" );
        		usePlaneCheckbox .setEnabled( controller .propertyIsTrue( "workingPlaneDefined" ) );
        		usePlaneCheckbox .setSelected( controller .propertyIsTrue( "useWorkingPlane" ) );
        		usePlaneCheckbox .addActionListener( controller );
        		usePlaneCheckbox .setActionCommand( "toggleWorkingPlane" );
        		controller .addPropertyListener( new PropertyChangeListener() {
					
					@Override
					public void propertyChange( PropertyChangeEvent event )
					{
	                    if ( "workingPlaneDefined" .equals( event .getPropertyName() ) )
	                    {
	                		usePlaneCheckbox .setEnabled( controller .propertyIsTrue( "workingPlaneDefined" ) );
	                    }
	                    else if ( "useWorkingPlane" .equals( event .getPropertyName() ) )
	                    {
	                		usePlaneCheckbox .setSelected( controller .propertyIsTrue( "useWorkingPlane" ) );
	                    }
					}
				});
        		usePlanePanel .add( usePlaneCheckbox, BorderLayout.EAST );
        		constraintsPanel .add( usePlanePanel, BorderLayout.NORTH );
        	}
            this .add( constraintsPanel, BorderLayout.CENTER );
        }
        {
            lengthPanel = new NewLengthPanel( frame );
            controller .addPropertyListener( lengthPanel );
            lengthPanel .setBorder( BorderFactory .createTitledBorder( "strut size" ) );
            this .add( lengthPanel, BorderLayout.SOUTH );
        }

        // now add the cards for each symmetry
        for ( int i = 0; i < symmNames.length; i++ )
        {
            final String system = symmNames[ i ];
            final Controller symmController = this .controller .getSubController( "symmetry." + system );
            
            final Controller orbitController = symmController .getSubController( "buildOrbits" );
            {
                JPanel panel = new OrbitPanel( orbitController, symmController .getSubController( "availableOrbits" ), orbitPopup );
                panel .setBorder( BorderFactory .createTitledBorder( "strut directions" ) );
                orbitCardPanel .add( system, panel );
            }
            
            symmController .addPropertyListener( new PropertyChangeListener()
            {
                public void propertyChange( PropertyChangeEvent event )
                {
                    if ( "orbits" .equals( event .getPropertyName() ) )
                    {
//                        String dirName = orbitController .getProperty( "selectedOrbit" );
//                        if ( dirName == null )
//                            lengthCardPanel .setVisible( false );
//                        else {
//                            lengthCardPanel .setVisible( true );
//                            lengthCardPanel .showCard( system + "." + dirName );
//                        }
                    }
                }
            } );
            orbitController .addPropertyListener( new PropertyChangeListener()
            {
                public void propertyChange( PropertyChangeEvent evt )
                {
                    if ( "orbits" .equals( evt .getPropertyName() ) )
                        changeOrbit( orbitController );
                
                    if ( "selectedOrbit" .equals( evt .getPropertyName() ) )
                        changeOrbit( orbitController );
                }
            } );
        }
        String system = controller .getProperty( "symmetry" );
        changeSystem( system );

        controller .addPropertyListener( new PropertyChangeListener()
        {
            public void propertyChange( PropertyChangeEvent evt )
            {
                if ( "symmetry" .equals( evt .getPropertyName() ) )
                {
                    String system = (String) evt .getNewValue();
                    changeSystem( system );
                }
            }
        } );
    }

    private void changeSystem( String system )
    {
        orbitCardPanel .showCard( system );
        Controller symmController = this .controller .getSubController( "symmetry." + system );
        changeOrbit( symmController );
    }
    
    private void changeOrbit( Controller symmController )
    {
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
