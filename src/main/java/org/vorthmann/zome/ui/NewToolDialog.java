
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vorthmann.ui.Controller;

public class NewToolDialog extends EscapeDialog implements ListSelectionListener
{
    private final JTextField namePanel;
    private final JTextArea description;
    private final Controller controller;
    private final JList<String> toolsList;
    private int toolNum;
    private String toolId;

    public NewToolDialog( Frame frame, Controller controller )
    {
        super( frame, "Create New Tool", true );
        Container content = getContentPane();
        content .setLayout( new BorderLayout() );

        this .controller = controller;
        {
            toolsList = new JList<>( controller .getCommandList( "tool.templates" ) );
            {
                toolsList .addListSelectionListener( this );
                toolsList .setPreferredSize( new Dimension( 150, 300 ) );
                toolsList .setSelectionMode( ListSelectionModel .SINGLE_SELECTION );
                toolsList .setVisibleRowCount( -1 );
                toolsList .setLayoutOrientation( JList. VERTICAL );
            }
            JScrollPane scroller = new JScrollPane( toolsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
            content .add( scroller, BorderLayout.WEST );
            
            JPanel centerPanel = new JPanel();
            centerPanel .setLayout( new BorderLayout() );
            {
                JPanel descrPanel = new JPanel( new BorderLayout() );
                TitledBorder border = BorderFactory .createTitledBorder( "description" );
                border .setTitleJustification( TitledBorder .RIGHT );
                descrPanel .setBorder( border );

                description = new JTextArea( 3, 1 );
                description .setText( "Here you would see a description of the tool action." );
                description .setLineWrap( true );
                description .setWrapStyleWord( true );
                description .setEditable( false );
                descrPanel .add( description );
                centerPanel .add( descrPanel, BorderLayout.NORTH );

                JPanel parameters = new JPanel();
                border = BorderFactory .createTitledBorder( "parameters" );
                parameters .setBorder( border );
                {
                    
                }
                centerPanel .add( parameters, BorderLayout.CENTER );
                
                JPanel actionPanel = new JPanel( new BorderLayout() );
                {
                    JPanel labelPanel = new JPanel();
                    {
                        JLabel label = new JLabel( "name: " );
                        labelPanel .add( label, BorderLayout.WEST );
                        namePanel = new JTextField( 17 );
                        labelPanel .add( namePanel, BorderLayout.CENTER );
                    }
                    actionPanel .add( labelPanel, BorderLayout.CENTER );
                    JPanel buttons = new JPanel();
                    {
                        buttons .setComponentOrientation( ComponentOrientation.RIGHT_TO_LEFT );
                        final JButton okButton = new JButton( "Create" );
                        getRootPane() .setDefaultButton( okButton );
                        okButton .addActionListener( new ActionListener(){

                            @Override
                            public void actionPerformed( ActionEvent e )
                            {
                                String toolName = namePanel .getText();
                                ActionEvent ae = new ActionEvent( okButton, ActionEvent.ACTION_PERFORMED,
                                        "newTool/" + toolId + "/" + toolName );
                                NewToolDialog .this .controller .actionPerformed( ae );
                                NewToolDialog .this .setVisible( false );
                            }
                        } );
                        buttons .add( okButton );
                        
                        JButton cancelButton = new JButton( "Cancel" );
                        cancelButton .addActionListener( new ActionListener(){

                            @Override
                            public void actionPerformed( ActionEvent e )
                            {
                                NewToolDialog.this .setVisible( false );
                            }
                        } );
                        buttons .add( cancelButton );
                    }
                    actionPanel .add( buttons, BorderLayout.SOUTH );
                }
                centerPanel .add( actionPanel, BorderLayout.SOUTH );
            }
            content .add( centerPanel, BorderLayout.CENTER );
        }
        toolsList .setSelectedIndex( 0 );

        setSize( new Dimension( 550, 550 ) );
        setLocationRelativeTo( frame );
    }

    @Override
    public void valueChanged( ListSelectionEvent e )
    {
        if ( e. getValueIsAdjusting() == false)
            adjustText();
    }
    
    private void adjustText()
    {
        String groupName = toolsList .getSelectedValue();
        toolId = groupName + "." + toolNum;
        namePanel .setText( toolId );
        // TODO set description, parameters using a subcontroller
        String descrText = controller .getProperty( "tool.description." + groupName );
        description .setText( descrText );
    }

    public void setToolNum( int i )
    {
        toolNum = i;
        adjustText();
    }

}
