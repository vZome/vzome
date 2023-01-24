package org.vorthmann.zome.ui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.vzome.desktop.api.Controller;

@SuppressWarnings("serial")
public class HyperlinkPanel extends JPanel
{
    private transient String url;
    private final JButton copyButton;

    public void setUrl( String url )
    {
        this.url = url;
    }

    public HyperlinkPanel( String label, Controller controller )
    {
        this( label, controller, true );
    }

    public HyperlinkPanel( String label, Controller controller, boolean showButton )
    {
        super();
        setLayout( new BorderLayout() );
        JLabel hyperlink = new JLabel( label );
        hyperlink .setMinimumSize( new Dimension( 200, 1 ) );
        hyperlink .setPreferredSize( new Dimension( 250, 30 ) );
        this .add( hyperlink, BorderLayout.CENTER );
        copyButton = new JButton( "Copy URL" );
        if ( showButton )
            this .add( copyButton, BorderLayout.EAST );
        else {
            JPanel spacer = new JPanel();
            spacer .setMinimumSize( new Dimension( 150, 20 ) );
            this .add( spacer, BorderLayout.EAST );
        }
        copyButton .addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                controller .setProperty( "clipboard", url );
            }
        });

        hyperlink .setForeground( Color.BLUE );
        hyperlink .setCursor(new Cursor( Cursor.HAND_CURSOR ) );

        hyperlink .addMouseListener( new MouseAdapter()
        {
            @Override
            public void mousePressed( MouseEvent e )
            {
                try {
                    Desktop .getDesktop() .browse( new URI( url ) );
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseExited( MouseEvent e )
            {
                hyperlink .setText( label );
            }

            @Override
            public void mouseEntered( MouseEvent e )
            {
                hyperlink .setText( "<html><a href=''>" + label + "</a></html>" );
            }
        });
    }

    public JButton getCopyButton()
    {
        return this .copyButton;
    }
}