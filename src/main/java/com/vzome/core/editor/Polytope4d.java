
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.CommandUniformH4Polytope;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.Projection;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.symmetry.A4Group;
import com.vzome.core.math.symmetry.B4Group;
import com.vzome.core.math.symmetry.CoxeterGroup;
import com.vzome.core.math.symmetry.D4Group;
import com.vzome.core.math.symmetry.F4Group;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.WythoffConstruction;
import com.vzome.core.model.RealizedModel;

public class Polytope4d extends ChangeConstructions
{
    private int index;
    private final ModelRoot root;
    private final AlgebraicField field;
    private Projection proj;
    private Segment symmAxis;
    private String groupName;
    
    // new controls
    private int edgesToRender = 0xF;
    private AlgebraicNumber[] edgeScales = new AlgebraicNumber[4];
    private String renderGroupName;

    public Polytope4d( Selection selection, RealizedModel realized, ModelRoot root,
                        Segment symmAxis, int index, String groupName,
                        int edgesToRender, AlgebraicNumber[] edgeScales, String renderGroupName )
    {
        super( selection, realized, false );

        this.root = root;
        this.index = index;
        this.symmAxis = symmAxis;
        this.groupName = groupName;
        this.field = root .getField();
        
        this .renderGroupName = renderGroupName;
        this .edgesToRender = edgesToRender;
        this .edgeScales = edgeScales;
    }

    public Polytope4d( Selection selection, RealizedModel realized, ModelRoot root,
                        Segment symmAxis, int index, String groupName, boolean groupInSelection )
    {
        super( selection, realized, groupInSelection );

        this.root = root;
        this.index = index;
        this.symmAxis = symmAxis;
        this.groupName = groupName;
        this.field = root .getField();
        
        this .renderGroupName = groupName;
        this .edgesToRender = index;
        for (int i = 0; i < this .edgeScales .length; i++)
        {
            this .edgeScales[ i ] = this .field .createPower( 0 );
        }
    }

    protected String getXmlElementName()
    {
        return "Polytope4d";
    }

    public void getXmlAttributes( Element xml )
    {
        if ( symmAxis != null )
            XmlSaveFormat .serializeSegment( xml, "start", "end", symmAxis );
        DomUtils .addAttribute( xml, "group", this.groupName );
        DomUtils .addAttribute( xml, "wythoff", Integer .toString( this.index, 2 ) );
        if ( this .edgesToRender != 0xF )
        	DomUtils .addAttribute( xml, "renderEdges", Integer .toString( this.edgesToRender, 2 ) );
        if ( ! this .renderGroupName .equals( this .groupName ) )
        	DomUtils .addAttribute( xml, "renderGroup", this.renderGroupName );
    }

    public void setXmlAttributes( Element xml, XmlSaveFormat format )
    {
        String binary = xml .getAttribute( "wythoff" );
        this .index = Integer .parseInt( binary, 2 );
        String renderString = xml .getAttribute( "renderEdges" );
        this .edgesToRender = ( renderString==null || renderString .isEmpty() )? this .index : Integer .parseInt( renderString, 2 );
        this .groupName = xml .getAttribute( "group" );
        String rgString = xml .getAttribute( "renderGroup" );
        this .renderGroupName = ( rgString==null || rgString .isEmpty() )? this .groupName : rgString;
        
        if ( format .commandEditsCompacted() )
            this.symmAxis = format .parseSegment( xml, "start", "end" );
        else
        {
            Map attrs = format .loadCommandAttributes( xml, false );
            this.symmAxis = (Segment) attrs .get( "rotation" );
        }
    }
    
    
    public void perform()
    {
        if ( symmAxis == null )
            this.proj = new Projection .Default( field );
        else
            this.proj = new QuaternionProjection( field, null, symmAxis .getOffset() .scale( field .createPower( -5 ) ) );
        if ( "H4" .equals( groupName ) )
        {
            QuaternionicSymmetry qsymm = field .getQuaternionSymmetry( "H_4" ); 
            CommandUniformH4Polytope h4Builder = new CommandUniformH4Polytope( field, qsymm, 0 );
            h4Builder .generate( this .proj, this .index, this .edgesToRender, this .edgeScales, root, new ConstructionChanges()
            {
                public void constructionAdded( Construction c )
                {
                    // TODO refactor to replace this with a WythoffListener
                    addConstruction( c );
                    manifestConstruction( c );
                }

                public void constructionRevealed( Construction c ) {}
                
                public void constructionRemoved( Construction c ) {}
                
                public void constructionHidden( Construction c ) {}
            } );
        }
        else
        {
            CoxeterGroup group = null;
            if ( "A4" .equals( groupName ) )
                group = new A4Group( this.field ) ;
            else if ( "D4" .equals( groupName ) )
                group = new D4Group( this.field );
            else if ( "F4" .equals( groupName ) )
                group = new F4Group( this.field );
            else if ( groupName .startsWith( "B4" ) )
                group = new B4Group( this.field );
            WythoffConstruction .constructPolytope( group, this.index, this .edgesToRender, this .edgeScales, group, new WythoffListener() );
        }
        
        redo();
    }

    
    private class WythoffListener implements WythoffConstruction.Listener
    {
        private int numVertices = 0;
        Map vertices = new HashMap();

        public Object addEdge( Object p1, Object p2 )
        {
            Segment edge = new SegmentJoiningPoints( (Point) p1, (Point) p2 );
            addConstruction( edge );
            manifestConstruction( edge );
            return edge;
        }

        public Object addFace( Object[] vertices )
        {
            return null;
        }

        public Object addVertex( AlgebraicVector vertex )
        {
            Point p = (Point) vertices .get( vertex );
            if ( p == null )
            {
                AlgebraicVector projected = vertex;
                if ( proj != null )
                    projected = proj .projectImage( vertex, true );
                
                projected = projected .scale( field .createPower( 5 ) );

                p = new FreePoint( projected, root );
                p .setIndex( numVertices++ );
                addConstruction( p );
                manifestConstruction( p );
                vertices .put( vertex, p );
            }
            return p;
        }
    }

    public static String[] getSupportedGroups()
    {
        return new String[]{ "A4", "B4/C4", "D4", "F4", "H4" };
    }
}

