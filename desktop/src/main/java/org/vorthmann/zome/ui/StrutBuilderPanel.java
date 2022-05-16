package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.vzome.desktop.api.Controller;
import com.vzome.desktop.awt.GraphicsController;


@SuppressWarnings("serial")
public class StrutBuilderPanel extends JPanel
{
    private final Controller controller;
    private final NewLengthPanel lengthPanel;
    private final OrbitPanel orbitPanel;

    public StrutBuilderPanel( JFrame frame, final String symmName, final Controller controller, ControlActions enabler )
    {
        this .controller = controller;
        this .setLayout( new BorderLayout() );

        // here's the containment layout...

        JPanel constraintsPanel = new JPanel();
        constraintsPanel .setLayout( new BorderLayout() );

        final Controller symmController = this .controller .getSubController( "symmetry." + symmName );

        final GraphicsController orbitController = (GraphicsController) symmController .getSubController( "buildOrbits" );
        {
            orbitPanel = new OrbitPanel( orbitController, (GraphicsController) symmController .getSubController( "availableOrbits" ), enabler );
            orbitPanel .setBorder( BorderFactory .createTitledBorder( "strut directions" ) );
            orbitPanel .setToolTipText( "Click and drag on a ball to create a strut, using directions selected here." );
            constraintsPanel .add( orbitPanel, BorderLayout.CENTER );
        }

        JPanel usePlanePanel = new JPanel();
        usePlanePanel .setLayout( new BorderLayout() );
        final JCheckBox usePlaneCheckbox = new JCheckBox( "Use working plane" );
        usePlaneCheckbox .setEnabled( controller .propertyIsTrue( "workingPlaneDefined" ) );
        usePlaneCheckbox .setSelected( controller .propertyIsTrue( "useWorkingPlane" ) );
        usePlaneCheckbox .addActionListener( new ControllerActionListener(controller) );
        usePlaneCheckbox .setActionCommand( "toggleWorkingPlane" );
        usePlanePanel .add( usePlaneCheckbox, BorderLayout.EAST );
        constraintsPanel .add( usePlanePanel, BorderLayout.NORTH );

        this .add( constraintsPanel, BorderLayout.CENTER );

        {
            lengthPanel = new NewLengthPanel( frame, (GraphicsController) orbitController .getSubController( "currentLength" ) );
            controller .addPropertyListener( lengthPanel );
            lengthPanel .setBorder( BorderFactory .createTitledBorder( "strut size" ) );
            this .add( lengthPanel, BorderLayout.SOUTH );
        }

        systemChanged( symmName );

        this .controller .addPropertyListener( new PropertyChangeListener()
        {
            @Override
            public void propertyChange( PropertyChangeEvent event )
            {
                switch ( event .getPropertyName() ) {

                case "symmetry":
                    String system = (String) event .getNewValue();
                    systemChanged( system );
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
    }

    private void systemChanged( String system )
    {
        Controller symmController = this .controller .getSubController( "symmetry." + system );
        orbitPanel .systemChanged( (GraphicsController) symmController .getSubController( "buildOrbits" ), (GraphicsController) symmController .getSubController( "availableOrbits" ) );
        final Controller orbitController = symmController .getSubController( "buildOrbits" );
        String dirName = orbitController .getProperty( "selectedOrbit" );
        if ( dirName == null )
            lengthPanel .setVisible( false );
        else {
            lengthPanel .setController( (GraphicsController) orbitController .getSubController( "currentLength" ) );
            lengthPanel .setVisible( true );
        }
    }
}
