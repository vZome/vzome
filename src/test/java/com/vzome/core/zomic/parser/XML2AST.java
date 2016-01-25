
package com.vzome.core.zomic.parser;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.render.ZomicEventHandler;
import com.vzome.core.zomic.ZomicNamingConvention;
import com.vzome.core.zomic.program.ZomicStatement;
import com.vzome.core.zomic.program.Build;
import com.vzome.core.zomic.program.Label;
import com.vzome.core.zomic.program.Move;
import com.vzome.core.zomic.program.Nested;
import com.vzome.core.zomic.program.Permute;
import com.vzome.core.zomic.program.Reflect;
import com.vzome.core.zomic.program.Repeat;
import com.vzome.core.zomic.program.Rotate;
import com.vzome.core.zomic.program.Save;
import com.vzome.core.zomic.program.Scale;
import com.vzome.core.zomic.program.Symmetry;
import com.vzome.core.zomic.program.Walk;

@Deprecated
public class XML2AST extends DefaultHandler
{
	protected final Stack<ZomicStatement> m_stmts = new Stack<>();
    
    private transient int ones, taus, denominator, scale;
    
    private final IcosahedralSymmetry symm;
    
    private final ZomicNamingConvention naming;
    
	public XML2AST( IcosahedralSymmetry symm ) {
		super();
		    this.symm = symm;
		    naming = new ZomicNamingConvention( symm );

			m_stmts .push( new Walk() );
		}

	public ZomicStatement getProgram()
	{
		return m_stmts .firstElement();
	}

	public 
	void startElement( String namespaceURI, String localName,
	String qName, Attributes atts ) throws SAXException
	{
		ZomicStatement newStmt = null;
		ZomicStatement currStmt = m_stmts .peek();
		if ( "through" .equals( localName ) ) {
			Permute permute = null;
			if ( currStmt instanceof Symmetry ){
				permute = new Reflect();
				((Symmetry) currStmt) .setPermute( permute );
			}
			else
				permute = (Permute) currStmt;
			String blueAxis = atts .getValue( "index" );
			if ( blueAxis != null )
				permute .setAxis( naming .getAxis( "blue", blueAxis ) );
			return ;
		}
		if ( "around" .equals( localName ) ) {
			((Symmetry) currStmt) .setPermute( new Rotate( null, -1 ) );
			return ;
		}
		if ( "red" .equals( localName )
        || "green" .equals( localName )
        || "orange" .equals( localName )
        || "purple" .equals( localName )
		|| "yellow" .equals( localName )
		|| "blue" .equals( localName )
		|| "black" .equals( localName ) ) {
			String index = atts .getValue( "index" );
			Axis axis;
			try  {
				axis = naming .getAxis( localName, index );
				String check = naming .getName( axis );
				if ( axis != naming .getAxis( localName, check ) )
				    System .out .println( localName + " " + index + " mapped to " + check );
			} catch( RuntimeException ze ) {
				throw new SAXException( "bad axis specification: " + localName + " " + index );
			}
			if ( currStmt instanceof Symmetry ) {
				((Symmetry) currStmt) .getPermute() .setAxis( axis );
				return ;
			}
			if ( currStmt instanceof Rotate ) {
				((Rotate) currStmt) .setAxis( axis );
				return ;
			}
			// if we get here, it is a "strut" statement
            AlgebraicNumber len = this .symm .getField() .one();
			if ( scale == -99 ) {
			    // will use as a marker to indicate variability, see ZomicPolyhedronModelInterpreter.LocationTracker.step()
			    len = this .symm .getField() .zero();
			}
            else {
                if ( "blue" .equals( localName ) || "green" .equals( localName ) ) {
                    ones = ones * 2;
                    taus = taus * 2;
                }
                else if ( denominator == 2 )
                    throw new SAXException( "half struts only allowed for blue and green lines" );
                if ( "yellow" .equals( localName ) || "purple" .equals( localName ) )
                    --scale;
                len = symm .getField() .createAlgebraicNumber( ones, taus, denominator, scale );
            }
            newStmt = new Move( axis, len );
			// fall through to the push
		}
		do{
			if ( "model" .equals( localName ) ) {
				newStmt = new Walk();
				break;
			}
            if ( "strut" .equals( localName ) )
            {
				String size = atts .getValue( "scale" );
                scale = size == null? ZomicNamingConvention .MEDIUM : parseInt( size );
                if ( scale != -99 ) {
                    denominator = (atts .getValue( "half" ) != null)? 2 : 1;
                    size = atts .getValue( "lengthOnes" );
                    ones = size == null? 1 : parseInt( size );
                    size = atts .getValue( "lengthPhis" );
                    taus = size == null? 0 : parseInt( size );
                }
				return;  // no newStmt to push, will happen on next startElement... see above
			}
			if ( "repeat" .equals( localName ) ) {
				newStmt = new Repeat( parseInt( atts .getValue( "count" ) ) );
				break;
			}
			if ( "scale" .equals( localName ) ) {
                String size = atts .getValue( "size" );
                int scale = parseInt( size );
                size = atts .getValue( "lengthOnes" );
                int ones = size == null? 1 : parseInt( size );
                size = atts .getValue( "lengthPhis" );
                int phis = size == null? 0 : parseInt( size );
                AlgebraicNumber length = symm .getField() .createAlgebraicNumber( ones, phis, 1, scale );
				newStmt = new Scale( length );
				break;
			}
			if ( "save" .equals( localName ) ) {
				newStmt = new Save( parseState( atts .getValue( "state" ) ) );
				break;
			}
			if ( "label" .equals( localName ) ) {
				newStmt = new Label( atts .getValue( "id" ) );
				break;
			}
			if ( "build" .equals( localName ) ) {
				boolean building = "on" .equals( atts .getValue( "build" ) );
				boolean destroying = "on" .equals( atts .getValue( "destroy" ) );
				newStmt = new Build( building, destroying );
				break;
			}
			if ( "reflect" .equals( localName ) ) {
				newStmt = new Reflect();
				break;
			}
			if ( "rotate" .equals( localName ) ) {
				String steps = atts .getValue( "steps" );
				if ( steps == null )
					newStmt = new Rotate( null, 1 );
				else
					newStmt = new Rotate( null, parseInt( steps ) );
				break;
			}
			if ( "symmetry" .equals( localName ) ) {
				newStmt = new Symmetry();
				break;
			}
			break;
		}
		while ( false );
		if ( currStmt instanceof Walk )
			((Walk) currStmt) .addStatement( newStmt );
		else
			((Nested) currStmt) .setBody( newStmt );
		m_stmts .push( newStmt );
	}

	public static int parseInt( String intString ) {
		if ( intString .startsWith( "+" ) ) {
			intString = intString .substring( 1 );
		}
		return Integer .parseInt( intString );
	}

    public static int parseState( String state )
    {
        if ( "orientation" .equals( state ) )
            return ZomicEventHandler .ORIENTATION;
        if ( "scale" .equals( state ) )
            return ZomicEventHandler .SCALE;
        if ( "location" .equals( state ) )
            return ZomicEventHandler .LOCATION;
        if ( "build" .equals( state ) )
            return ZomicEventHandler .ACTION;
        return ZomicEventHandler .ALL;
    }

	public 
	void endElement( String namespaceURI, String localName, String qName )
    throws SAXException
    {
		if ( "through" .equals( localName ) || "around" .equals( localName )
		|| "red" .equals( localName )
		|| "yellow" .equals( localName )
		|| "blue" .equals( localName )
		|| "green" .equals( localName )
		|| "orange" .equals( localName )
		|| "black" .equals( localName )
        || "purple" .equals( localName ) ) {
			return ;
		}
		m_stmts .pop();
	}


}


