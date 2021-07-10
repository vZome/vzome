
package com.vzome.core.commands;

import java.util.Properties;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.xml.DomUtils;

public class XmlSymmetryFormat extends XmlSaveFormat
{
    private transient OrbitSet.Field symmetries;

    private static final Logger logger = Logger .getLogger( "com.vzome.core.commands.XmlSaveFormat" );
    
    static {
        new XmlSymmetryFormat( "http://tns.vorthmann.org/vZome/2.0/",   new String[]{ PROJECT_4D, SELECTION_NOT_SAVED } );
        new XmlSymmetryFormat( "http://tns.vorthmann.org/vZome/2.0.1/", new String[]{ PROJECT_4D, SELECTION_NOT_SAVED } );
        new XmlSymmetryFormat( "http://tns.vorthmann.org/vZome/2.0.2/", new String[]{ SELECTION_NOT_SAVED } );
        new XmlSymmetryFormat( "http://tns.vorthmann.org/vZome/2.0.3/", new String[]{ SELECTION_NOT_SAVED } );
        new XmlSymmetryFormat( "http://tns.vorthmann.org/vZome/2.1.0/", new String[]{ SELECTION_NOT_SAVED, FORMAT_2_1_0 } );
        
        // the format for vZome 2.1
        new XmlSymmetryFormat( "http://tns.vorthmann.org/vZome/3.0.0/", new String[]{ GROUPING_IN_SELECTION } );
        
        // the format for vZome 3.0 alpha, elem names still determined by UndoableEdit, but using AlgebraicField and rationals
        new XmlSymmetryFormat( "http://tns.vorthmann.org/vZome/4.0.0/", new String[]{ RATIONAL_VECTORS, GROUPING_IN_SELECTION } );
        
        // the format for vZome 3.0 beta, compacting CommandEdits
        new XmlSymmetryFormat( "http://tns.vorthmann.org/vZome/5.0.0/", new String[]{ RATIONAL_VECTORS, COMPACTED_COMMAND_EDITS } );
        
        // the format for vZome 4.0, including multiple designs
        new XmlSymmetryFormat( CURRENT_FORMAT, new String[]{ RATIONAL_VECTORS, COMPACTED_COMMAND_EDITS, MULTIPLE_DESIGNS } );
    }
    
    public static XmlSymmetryFormat getFormat( String namespace )
    {
        return (XmlSymmetryFormat) FORMATS .get( namespace );
    }

    public void initialize( AlgebraicField field, OrbitSet.Field symms, int scale,
            String writerVersion, Properties props )
    {
        super .initialize( field, scale, writerVersion, props );
        this .symmetries = symms;
    }

    public XmlSymmetryFormat( String version, String[] capabilities )
    {
        super( version, capabilities );
    }

    public Object parseAlgebraicObject( String valName, Element val )
    {
        if ( valName .equals( "Symmetry" ) ) {
            String name = val .getAttribute( "name" );
            return parseSymmetry( name );
        }
        else if ( valName .equals( "QuaternionicSymmetry" ) ) {
            String name = val .getAttribute( "name" );
            return getQuaternionicSymmetry( name );
        }
        else if ( valName .equals( "Axis" ) )
            return parseAxis( val, "symm", "dir", "index", "sense" );
        else {
            return super .parseAlgebraicObject( valName, val );
        }
    }
    
    QuaternionicSymmetry getQuaternionicSymmetry( String name )
    {
        return symmetries .getQuaternionSet( name );
    }
    
    public Symmetry parseSymmetry( String sname )
    {
        OrbitSet group = symmetries .getGroup( sname );  //  Symmetry symm = parseSymmetry( sname );
        Symmetry symm = group .getSymmetry();
        if ( symm == null )
        {
            logger .severe( "UNSUPPORTED symmetry: " + sname );
            throw new IllegalStateException( "no symmetry with name=" + sname );
        }
        else
            return symm;
    }
    
    public static final void serializeAxis( Element xml, String symmAttr, String dirAttr, String indexAttr, String senseAttr, Axis axis )
    {
        String str = axis .getDirection() .getSymmetry() .getName();
        if ( ! "icosahedral" .equals( str ) )
            DomUtils .addAttribute( xml, symmAttr, str );
        str = axis .getDirection() .getName();
        if ( ! "blue" .equals( str ) )
            DomUtils .addAttribute( xml, dirAttr, str );
            DomUtils .addAttribute( xml, indexAttr, Integer .toString( axis .getOrientation() ) );
        if ( axis .getSense() != Symmetry.PLUS )
            DomUtils .addAttribute( xml, "sense", "minus" );
        if ( ! axis .isOutbound() )
            DomUtils .addAttribute( xml, "outbound", "false" );
    }

    public final Axis parseAxis( Element xml, String symmAttr, String dirAttr, String indexAttr, String senseAttr )
    {
        String sname = xml .getAttribute( symmAttr );
        if ( sname == null || sname .isEmpty() )
            sname = "icosahedral";
        OrbitSet group = symmetries .getGroup( sname );  //  Symmetry symm = parseSymmetry( sname );
        String aname = xml .getAttribute( dirAttr );
        if ( aname == null || aname .isEmpty() )
            aname = "blue";
        else if ( aname .equals( "tan" ) )
            aname = "sand";
        else if ( aname .equals( "spring" ) )
            aname = "apple";
        String iname = xml .getAttribute( indexAttr );
        int index = Integer .parseInt( iname );
        int sense = Symmetry .PLUS;
        if ( "minus" .equals( xml .getAttribute( senseAttr ) )) { // NOTE: used to say "index < 0"
            sense = Symmetry .MINUS;
//            index *= -1;
        }
        boolean outbound = true;
        String outs = xml .getAttribute( "outbound" );
        if ( outs != null && outs .equals( "false" ) )
            outbound = false;
        Direction dir = group .getDirection( aname );
        if(dir == null) {
            String msg = "Unsupported direction '" + aname + "' in " + sname + " symmetry";
            logger .severe( msg );
            throw new IllegalStateException( msg );
        }
        return dir .getAxis( sense, index, outbound );
    }
}
