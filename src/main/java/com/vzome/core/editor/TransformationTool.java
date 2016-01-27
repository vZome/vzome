
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.Transformation;
import com.vzome.core.construction.TransformedPoint;
import com.vzome.core.construction.TransformedPolygon;
import com.vzome.core.construction.TransformedSegment;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public abstract class TransformationTool extends ChangeManifestations implements Tool
{
    public void prepare( ChangeManifestations applyTool ) {}

	public void complete( ChangeManifestations applyTool ) {}

	public boolean isSticky()
    {
        return true;
    }
    
    public boolean needsInput()
    {
    	return true;
    }

    public void perform() throws Failure
    {
        defineTool();
    }

    protected void defineTool()
    {
        tools .addTool( this );
    }

    private String name;
    
    protected Transformation[] transforms;
    
    private Tool.Registry tools;
    
    protected Point originPoint;

    public TransformationTool( String name, Selection selection, RealizedModel realized, Tool.Registry tools, Point originPoint )
    {
        super( selection, realized, false );
        
        if ( name != null && name .endsWith( DEFAULT_NAME_TRIGGER ) )
            this .name = name .replaceAll( DEFAULT_NAME_TRIGGER, getDefaultName( name ) );
        else
            this.name = name;
        this.tools = tools;
        this.originPoint = originPoint;
    }
    
    public static String DEFAULT_NAME_TRIGGER = "____DEFAULT";
    
    protected abstract String getDefaultName( String template );
    
    protected boolean isAutomatic()
    {
        int dot = this .name .indexOf( "." );
        int slash = this .name .indexOf( "/" );
        String id = this .name .substring( dot, slash );
        return ".auto" .equals( id );
    }

    public void performEdit( Construction c, ChangeManifestations applyTool )
    {
        for (Transformation transform : transforms) {
            Construction result = null;
            if (c instanceof Point) {
                result = new TransformedPoint(transform, (Point) c);
            } else if (c instanceof Segment) {
                result = new TransformedSegment(transform, (Segment) c);
            } else if (c instanceof Polygon) {
                result = new TransformedPolygon(transform, (Polygon) c);
            } else {
                // TODO handle other constructions 
            }
            if ( result == null )
                continue;
            applyTool .manifestConstruction( result );
        }
        applyTool .redo();
    }
    
	public void performSelect( Manifestation man, ChangeManifestations applyTool ) {};

    public void redo()
    {
        // TODO manifest a symmetry construction... that is why this class extends ChangeConstructions
        // this edit is now sticky (not really undoable)
//        tools .addTool( this );
    }

    public void undo()
    {
        // this edit is now sticky (not really undoable)
//        tools .removeTool( this );
    }

    public String getName()
    {
        return name;
    }
    
    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "name", this.name );
    }

    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        this.name = element .getAttribute( "name" );
    }
}
