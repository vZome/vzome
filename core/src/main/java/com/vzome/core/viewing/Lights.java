/*
 * Created on Jul 22, 2004
 */
package com.vzome.core.viewing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.mesh.Color;
import com.vzome.xml.DomUtils;

/**
 * This is really a SceneModel
 * @author Scott Vorthmann
 *
 */
public class Lights //extends DefaultController
{
    private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
    
    public void addPropertyListener( PropertyChangeListener listener )
    {
        pcs .addPropertyChangeListener( listener );
    }

    public void removePropertyListener( PropertyChangeListener listener )
    {
        pcs .removePropertyChangeListener( listener );
    }

    public void setProperty( String cmd, Object value )
    {
        if ( "backgroundColor".equals( cmd ) ) {
            this .backgroundColor = new Color( Integer .parseInt( (String) value, 16 ) );
            pcs .firePropertyChange( cmd, null, value );
        }
    }

    protected final List<Color> mDirectionalLightColors = new ArrayList<>(3);

	protected final List<Vector3f> mDirectionalLightVectors = new ArrayList<>(3);
	
	protected Color mAmbientLightColor;
    
    private Color backgroundColor;
    

    public Lights()
    {
        super();
    }


    public Lights( Lights prototype )
    {
        this();
        
        this .backgroundColor = prototype .backgroundColor;
        this .mAmbientLightColor = prototype .mAmbientLightColor;
        verifyListSizesMatch();
        for ( int i = 0; i < prototype.mDirectionalLightVectors.size(); i++ ) {
            Vector3f pos = prototype.mDirectionalLightVectors.get(i);
            Color color = prototype.mDirectionalLightColors.get(i);
            addDirectionLight( color, pos );
        }
    }


    public Lights( Element element )
    {
        this();
        String str = element .getAttribute( "background" );
        this .backgroundColor = Color .parseColor( str );
        str = element .getAttribute( "ambientLight" );
        this .mAmbientLightColor = Color .parseColor( str );
        NodeList nodes = element .getChildNodes();
        for ( int i = 0; i < nodes .getLength(); i++ ) {
            Node node = nodes .item( i );
            if ( node instanceof Element ) {
                Element viewElem = (Element) node;
                str = viewElem .getAttribute( "color" );
                Color color = Color .parseColor( str );
                Vector3f pos = new Vector3f( 
                        Float .parseFloat( viewElem .getAttribute( "x" ) ),  
                        Float .parseFloat( viewElem .getAttribute( "y" ) ),  
                        Float .parseFloat( viewElem .getAttribute( "z" ) )
                );
                addDirectionLight( color, pos );
            }
        }
    }

    public int size() {
        verifyListSizesMatch();
        return mDirectionalLightVectors.size();
    }
    
    private void verifyListSizesMatch() {
        if( mDirectionalLightVectors.size() != mDirectionalLightColors.size() ) {
            throw new IllegalStateException("List sizes should match." 
                    + " mDirectionalLightVectors.size() = " +  mDirectionalLightVectors.size()
                    + " mDirectionalLightColors.size() = " + mDirectionalLightColors.size() 
            );
        }
    }
	
	public final void addDirectionLight( Color color, Vector3f dir )
	{
		mDirectionalLightColors .add( color );
		mDirectionalLightVectors .add( dir );
	}
	
	
	public void setAmbientColor( Color color )
	{
	    mAmbientLightColor = color;
	}

	public Color getAmbientColor()
	{
		return mAmbientLightColor;
	}
	
	public Color getDirectionalLight( int i, Vector3f direction )
	{
		direction .set( mDirectionalLightVectors .get( i ) );
		return mDirectionalLightColors .get( i );
	}
    
    public Color getBackgroundColor()
    {
        return this .backgroundColor;
    }
    
    public void setBackgroundColor( Color color )
    {
        this .backgroundColor = color;
    }

    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "sceneModel" );
        DomUtils .addAttribute( result, "ambientLight", mAmbientLightColor .toString() );
        DomUtils .addAttribute( result, "background", backgroundColor .toString() );
        verifyListSizesMatch();
        for ( int i = 0; i < mDirectionalLightVectors.size(); i++ ) {
            Vector3f pos = mDirectionalLightVectors.get(i);
            Color color = mDirectionalLightColors .get(i);
            Element child = doc .createElement( "directionalLight" );
            DomUtils .addAttribute( child, "x", Float .toString( pos .x ) );
            DomUtils .addAttribute( child, "y", Float .toString( pos .y ) );
            DomUtils .addAttribute( child, "z", Float .toString( pos .z ) );
            DomUtils .addAttribute( child, "color", color .toString() );
            result .appendChild( child );
        }
        return result;
    }
}
