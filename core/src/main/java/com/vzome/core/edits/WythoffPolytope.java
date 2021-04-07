package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.math.symmetry.GroupTransforms;

public class WythoffPolytope extends ChangeManifestations
{

    private GroupTransforms groupTransforms;
    private AlgebraicNumber[] edgeScales;
    
    public WythoffPolytope( EditorModel editor )
    {
	super(editor);
    }

    @Override
    public void configure( Map<String, Object> params )
    {
	AlgebraicMatrix simplexVerts = (AlgebraicMatrix) params .get("simplexVerts");
	int order = (int) params .get("order");
	this .groupTransforms = new GroupTransforms(simplexVerts, order);
    }

    @Override
    protected String getXmlElementName()
    {
        return "WythoffPolytope";
    }

        @Override
    public void getXmlAttributes( Element xml )
    {
	/*
        if ( quaternion != null )
            DomUtils .addAttribute( xml, "quaternion", quaternion .toParsableString() );        
        DomUtils .addAttribute( xml, "group", this.groupName );
        DomUtils .addAttribute( xml, "wythoff", Integer .toString( this.index, 2 ) );
        if ( this .edgesToRender != 0xF )
            DomUtils .addAttribute( xml, "renderEdges", Integer .toString( this.edgesToRender, 2 ) );
        if ( ! this .renderGroupName .equals( this .groupName ) )
            DomUtils .addAttribute( xml, "renderGroup", this.renderGroupName );
	*/
    }

    @Override
    public void setXmlAttributes( Element xml, XmlSaveFormat format )
    {
	/*
        String binary = xml .getAttribute( "wythoff" );
        this .index = Integer .parseInt( binary, 2 );
        String renderString = xml .getAttribute( "renderEdges" );
        this .edgesToRender = ( renderString==null || renderString .isEmpty() )? this .index : Integer .parseInt( renderString, 2 );
        this .groupName = xml .getAttribute( "group" );
        String rgString = xml .getAttribute( "renderGroup" );
        this .renderGroupName = ( rgString==null || rgString .isEmpty() )? this .groupName : rgString;

        String quatString = xml .getAttribute( "quaternion" );
        if ( quatString != null && ! "" .equals( quatString ) ) {
            // newest format
            if ( quatString .contains( "+" ) ) {
                // First, deal with the bug we had in serialization...
                quatString = quatString .replace( ',', ' ' );
                quatString = quatString .replace( '(', ' ' );
                quatString = quatString .replace( ')', ' ' );
                quatString = quatString .replace( '+', ' ' );
                char irrat = this .field .getIrrational( 0 ) .charAt( 0 );
                quatString = quatString .replace( irrat, ' ' );
                quatString = quatString + " 0 0 0";  // This is probably OK, but
                //  it is known to work only for those particular files I have seen,
                //  in which the X, Y, and Z coordinates are all zero.
            }
            this .quaternion = this .field .parseVector( quatString );
        }
        else {
            // legacy formats
            Segment segment = null;
            if ( format .commandEditsCompacted() )
                segment = format .parseSegment( xml, "start", "end" );
            else
            {
                AttributeMap attrs = format .loadCommandAttributes( xml );
                segment = (Segment) attrs .get( "rotation" );
            }
            if ( segment != null )
                this.quaternion = segment .getOffset() .inflateTo4d();
        }
	*/
    }

    @Override
    public void perform()
    {
	/*
        if ( quaternion == null )
            this.proj = new Projection .Default( field );
        else
            this.proj = new QuaternionProjection( field, null, quaternion .scale( field .createPower( -5 ) ) );
        this .symmetries .constructPolytope( groupName, this.index, this .edgesToRender, this .edgeScales, new WythoffListener() );
        redo();
	*/
    }

    /*
    private class WythoffListener implements WythoffConstruction.Listener
    {
        private int numVertices = 0;
        Map<String, Point> vertices = new HashMap<>();

        @Override
        public Object addEdge( Object p1, Object p2 )
        {
            Segment edge = new SegmentJoiningPoints( (Point) p1, (Point) p2 );
            manifestConstruction( edge );
            return edge;
        }

        @Override
        public Object addFace( Object[] vertices )
        {
            return null;
        }

        @Override
        public Object addVertex( AlgebraicVector vertex )
        {
            Point p = vertices .get( vertex.toString() );
            if ( p == null )
            {
                AlgebraicVector projected = vertex;
                if ( proj != null )
                    projected = proj .projectImage( vertex, true );

                projected = projected .scale( field .createPower( 5 ) );

                p = new FreePoint( projected );
                p .setIndex( numVertices++ );
                manifestConstruction( p );
                vertices .put( vertex.toString(), p );
            }
            return p;
        }
    }
    */
}
