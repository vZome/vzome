
package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.vzome.desktop.api.Controller;

@SuppressWarnings("serial")
public class PolytopesDialog extends EscapeDialog
{
	private final JCheckBox[] renderCheckboxes = new JCheckBox[4];
    private final QuaternionPanel quaternionPanel;
    
    public PolytopesDialog( Frame frame, final Controller controller )
    {
        super( frame, "Generate a 4D Polytope", true );
        setLocationRelativeTo( frame );

        Container content = getContentPane();
        content .setLayout( new BorderLayout() );
        
        {
        	JPanel topHalf = new JPanel();
//        	topHalf .setBorder( BorderFactory .createBevelBorder( BevelBorder .LOWERED ) );
        	topHalf .setLayout( new BorderLayout() );
        	{
            	JPanel labelAndGroup = new JPanel();
            	labelAndGroup .setToolTipText( "4-dimensional Coxeter (reflection) groups." );
            	{
            		JLabel label = new JLabel( "symmetry group" );
            		labelAndGroup .add( label );
            	}
            	{
                    String[] groupNames = controller .getCommandList( "groups" );
                    String defaultGroup = controller .getProperty( "group" );
                    JComboBox<String> groups = new JComboBox<String>( groupNames );
                    groups .setSelectedItem( defaultGroup );
                    groups .setMaximumRowCount( 6 );
                    groups .addActionListener( new ActionListener()
                    {
                        @Override
                        public void actionPerformed( ActionEvent e )
                        {
                            JComboBox<?> combo = (JComboBox<?>) e.getSource();
                            String command = "setGroup." + combo .getSelectedItem().toString();
                            controller .actionPerformed( e .getSource(), command );
                        }
                    } );
                    labelAndGroup .add( groups );
            	}
            	topHalf .add( labelAndGroup, BorderLayout.NORTH );
            }
        	{
            	JPanel checkboxList = new JPanel();
            	checkboxList .setLayout( new BoxLayout( checkboxList, BoxLayout.PAGE_AXIS ) );
            	checkboxList .add( Box.createRigidArea( new Dimension( 0, 15 ) ) );
            	checkboxList .setToolTipText( "The group is based on four mirror planes intersecting the 3-sphere." );
            	{
            		JPanel checkboxesAndLabel = new JPanel();
            		checkboxesAndLabel .setLayout( new BorderLayout() );
            		checkboxesAndLabel .setToolTipText( "Which mirrors should create pairs of balls." );
            		{
            			JLabel label = new JLabel( "use mirror" );
            			label .setMinimumSize( new Dimension( 1, 100 ) );
            			label .setHorizontalAlignment( SwingConstants.RIGHT );
            			checkboxesAndLabel .add( label, BorderLayout.CENTER );
            		}
            		{
            			JPanel checkboxes = new JPanel();
            			checkboxes .setLayout( new BoxLayout( checkboxes, BoxLayout.LINE_AXIS ) );
            			checkboxes .add( Box.createRigidArea( new Dimension( 20, 0 ) ) );
            			for ( int i = 3; i >= 0; i-- ) {
            				final int edge = i;
            				JCheckBox checkbox = new JCheckBox();
            				boolean enabled = "true" .equals( controller .getProperty( "edge." + i ) );
            				checkbox .setSelected( enabled );
            				checkbox .setActionCommand( "edge." + i );
            				checkbox .addActionListener( new ActionListener()
            				{
            					@Override
            					public void actionPerformed( ActionEvent e )
            					{
            						controller .actionPerformed( e .getSource(), e .getActionCommand() );
            						boolean enabled = "true" .equals( controller .getProperty( "edge." + edge ) );
            						renderCheckboxes[ edge ] .setEnabled( enabled );
            						if ( enabled )
            						{
            							boolean render = "true" .equals( controller .getProperty( "render." + edge ) );
            							renderCheckboxes[ edge ] .setSelected( render );
            						}
            						else
            						{
            							renderCheckboxes[ edge ] .setSelected( false );
            						}
            					}
            				} );
            				checkboxes .add( checkbox );
            			}
            			checkboxes .add( Box.createRigidArea( new Dimension( 30, 0 ) ) );
            			checkboxesAndLabel .add( checkboxes, BorderLayout.EAST );
            		}
        			checkboxList .add( checkboxesAndLabel );
            	}
            	{
            		JPanel checkboxesAndLabel = new JPanel();
            		checkboxesAndLabel .setLayout( new BorderLayout() );
            		checkboxesAndLabel .setToolTipText( "Which mirrors should create struts." );
            		{
            			JLabel label = new JLabel( "create struts" );
            			label .setMinimumSize( new Dimension( 1, 100 ) );
            			label .setHorizontalAlignment( SwingConstants.RIGHT );
            			checkboxesAndLabel .add( label, BorderLayout.CENTER );
            		}
            		{
            			JPanel checkboxes = new JPanel();
            			checkboxes .setLayout( new BoxLayout( checkboxes, BoxLayout.LINE_AXIS ) );
            			checkboxes .setMinimumSize( new Dimension( 1, 80 ) );
            			checkboxes .add( Box.createRigidArea( new Dimension( 20, 0 ) ) );
            			for ( int i = 3; i >= 0; i-- ) {
            				renderCheckboxes[ i ] = new JCheckBox();
            				boolean enabled = "true" .equals( controller .getProperty( "edge." + i ) );
            				boolean render = "true" .equals( controller .getProperty( "render." + i ) );
            				renderCheckboxes[ i ] .setEnabled( enabled );
            				renderCheckboxes[ i ] .setSelected( enabled && render );
            				renderCheckboxes[ i ] .setActionCommand( "render." + i );
            				renderCheckboxes[ i ] .addActionListener( new ControllerActionListener(controller) );
            				checkboxes .add( renderCheckboxes[ i ] );
            			}
            			checkboxes .add( Box.createRigidArea( new Dimension( 30, 0 ) ) );
            			checkboxesAndLabel .add( checkboxes, BorderLayout.EAST );
            		}
        			checkboxList .add( checkboxesAndLabel );
            	}
            	checkboxList .add( Box.createRigidArea( new Dimension( 0, 15 ) ) );
            	topHalf .add( checkboxList, BorderLayout.CENTER );
            }
            content .add( topHalf, BorderLayout.NORTH );
        }
        	quaternionPanel = new QuaternionPanel( controller .getSubController( "quaternion" ) );
        	content .add( quaternionPanel, BorderLayout.CENTER );
                
        JPanel buttons = new JPanel();
        content .add( buttons, BorderLayout .SOUTH );
        
        JButton cancel = new JButton( "Cancel" );
        cancel .addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                PolytopesDialog.this .setVisible( false );
            }
        } );
        buttons .add( cancel );
        
        JButton build = new JButton( "Generate" );
        build .setActionCommand( "generate" );
        build .addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                quaternionPanel .syncToModel();
                PolytopesDialog.this .setVisible( false );
                controller .actionPerformed( e .getSource(), e .getActionCommand() );
            }
        } );
        buttons .add( build );
        setSize( new Dimension( 120 + quaternionPanel.totalLabelWidth(), 350 ) ); // adjust width to the number of irrationals
        setResizable( false );
    }

    @Override
	public void setVisible( boolean b )
    {
		if ( b ) {
		    quaternionPanel .syncFromModel();
		}
		super.setVisible(b);
	}
}
