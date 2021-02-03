package org.vorthmann.zome.ui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class HyperlinkPanel extends JPanel
{
    private transient String url;

    public HyperlinkPanel( String label, String url )
    {
        this( label );
        this .setUrl( url );
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public HyperlinkPanel( String label )
    {
        super();
        setLayout( new BorderLayout() );
        JLabel hyperlink = new JLabel( label );
        hyperlink .setMinimumSize( new Dimension( 200, 1 ) );
        hyperlink .setPreferredSize( new Dimension( 250, 30 ) );
        this .add( hyperlink, BorderLayout.CENTER );
        JButton copyButton = new JButton( "Copy URL to share" );
        this .add( copyButton, BorderLayout.EAST );

        hyperlink .setForeground( Color.BLUE );
        hyperlink .setCursor(new Cursor( Cursor.HAND_CURSOR ) );

        hyperlink .addMouseListener( new MouseAdapter()
        {
            @Override
            public void mouseClicked( MouseEvent e )
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
    
    public static void main( String[] args )
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JFrame frame = new JFrame( "test HyperlinkPanel" );

                frame .setLayout( new FlowLayout() );
                frame .getContentPane() .add( new HyperlinkPanel( "View design in vZome Online", "https://vzome.com/app/?url=https://gist.github.com/vorth/38156e77483dedf579183a64aced92a3/raw/492a2050f8545726b2cd8901beefcbf33b4762ed/2nd-gist-auto-share.vZome" ) );
                frame .getContentPane() .add( new HyperlinkPanel( "View gist page", "https://gist.github.com/vorth/38156e77483dedf579183a64aced92a3/" ) );
                frame .getContentPane() .add( new HyperlinkPanel( "View gist raw content", "https://gist.github.com/vorth/38156e77483dedf579183a64aced92a3/raw/492a2050f8545726b2cd8901beefcbf33b4762ed/2nd-gist-auto-share.vZome" ) );
                frame .setSize( 500, 200 );
                frame .setLocationRelativeTo( null );
                frame .setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                frame .setVisible( true );
            }
        });;
    }
}