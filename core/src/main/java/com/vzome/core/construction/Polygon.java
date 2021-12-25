
package com.vzome.core.construction;

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.xml.DomUtils;


public abstract class Polygon extends Construction
{
    @Override
	public String toString() {
		return "polygon " + Arrays.toString(mVertices);
	}

	private AlgebraicVector[] mVertices;
    
    public Polygon( AlgebraicField field )
    {
        super( field );
    }
    
    public String getSignature()
    {
//    	System.err.println( "Warning, Polygon.getSignature() may not work in Javascript" );
        String[] strArray = Arrays.stream( mVertices ) .map( ( av ) -> av.toString() ) .toArray( String[]::new );
//        System.err.println( "unsorted: " + Arrays.toString( strArray ) );
        Arrays.sort( strArray ); // This sort implementation may be broken in Javascript
//        System.err.println( "  sorted: " + Arrays.toString( strArray ) );
        return Arrays.toString( strArray );
    }

    protected boolean setStateVariable( AlgebraicVector[] vertices, boolean impossible )
    {
        if ( impossible ) {
            // don't attempt to access other params
            if ( isImpossible() )
                return false;
            setImpossible( true );
            return true;
        }
        // TODO implement change control
//        if ( offset .equals( mOffset )
//        && ! isImpossible()
//        &&   start .equals( mStart ) )
//            return false;
        mVertices = vertices;
        setImpossible( false );
        return true;
    }
    
    @Override
    public Element getXml( Document doc )
    {
        Element result = doc .createElement( "polygon" );
        this .getXml( result, "vertex" );
        return result;
    }

    public void getXml( Element result, String vertexChildName )
    {
        for (AlgebraicVector vertex : mVertices) {
            Element child = result .getOwnerDocument() .createElement( vertexChildName );
            DomUtils.addAttribute(child, "at", vertex.getVectorExpression(AlgebraicField.ZOMIC_FORMAT));
            result .appendChild( child );
        }
    }
    
    @Override
    public boolean is3d()
    {
        for ( AlgebraicVector algebraicVector : mVertices ) {
            if ( algebraicVector .dimension() != 3 )
                return false;
        }
        return true;
    }

    public int getVertexCount()
    {
        return mVertices.length;
    }
    
    public AlgebraicVector getVertex(int i)
    {
        return mVertices[i];
    }
    
    public AlgebraicVector getNormal()
    {
        // TODO: Don't base this on the first 3 vectors and expect them to be non-collinear 
        return AlgebraicVectors.getNormal(mVertices[0], mVertices[1], mVertices[2]);
    }
    
    public AlgebraicVector getCentroid()
    {
        return AlgebraicVectors.getCentroid( mVertices );
    }

    public AlgebraicVector[] getVertices()
    {
        return Arrays .copyOf( this.mVertices, this.mVertices.length );
    }
}
