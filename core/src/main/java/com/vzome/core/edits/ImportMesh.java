
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import java.io.IOException;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicField.Registry;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Point;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.ManifestConstructions;
import com.vzome.core.editor.api.SymmetryAware;
import com.vzome.core.math.PerspectiveProjection;
import com.vzome.core.math.Projection;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.SixCubeProjection;
import com.vzome.core.math.TetrahedralProjection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.xml.DomUtils;

public abstract class ImportMesh extends ChangeManifestations
{
    protected String meshData;
    protected AlgebraicNumber scale;
    protected Projection projection;
    protected final EditorModel editor;

    public ImportMesh( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
        this.editor = editor;
    }

    @Override
    public void configure( Map<String, Object> params )
    {
        AlgebraicField field = ((SymmetryAware) editor) .getSymmetrySystem() .getField();
        meshData = (String) params .get( "vef" );
        projection = (Projection) params .get( "projection" );
        scale = (AlgebraicNumber) params .get( "scale" );
        if (scale == null) {
            scale = field .one();
        }

        String mode = (String) params .getOrDefault( "mode", "" );

        switch ( mode ) {
        case "":
            break;
            
        case "clipboard":
            // TODO: refactor this to subclasses.  There is no need to support more than one format for paste.
            if( meshData != null && ! meshData.startsWith("vZome VEF" ) && ! meshData.startsWith( "{" ) )
                // Although older VEF formats don't all include the header and could possibly be successfully pasted here,
                // we're going to limit it to at least something that includes a valid VEF header.
                // We won't check the version number so we can still paste formats older than VERSION_W_FIRST
                // as long as they at least include the minimal header.
                // NOW SUPPORTING JSON ALSO!
                meshData = null; //disable the edit
            break;

        case "Quaternion":
            // projection should have been set from params
            break;
        
        default:
            setProjection( mode, field );
            break;
        }
    }

    @Override
    public boolean isNoOp()
    {
        return meshData == null;
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        if ( scale != null )
            DomUtils .addAttribute( element, "scale", scale .toString( AlgebraicField.ZOMIC_FORMAT ) );
        if ( projection != null ) {
            DomUtils .addAttribute( element, "projection", projection.getProjectionName() );
            projection.getXmlAttributes(element);
        }
        Node textNode = element .getOwnerDocument() .createTextNode( XmlSaveFormat .escapeNewlines( meshData ) );
        element .appendChild( textNode );
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format )
            throws Failure
    {
        AlgebraicField field = format.getField();
        scale = format .parseRationalNumber( xml, "scale" );
        if(scale == null) {
            scale = field.one();
        }
        meshData = xml .getTextContent();
        final String projectionName = xml.getAttribute("projection");
        if( "" .equals( projectionName ) ) {
            // legacy support for QuaternionProjection with no projection attribute
            AlgebraicVector quaternion = format .parseRationalVector( xml, "quaternion" );
            projection = quaternion == null
                    ? null
                            : new QuaternionProjection(field, null, quaternion);
        } else {
            setProjection( projectionName, field );
        }
        if( projection != null ) {
            projection.setXmlAttributes(xml);
        }
    }
    
    protected void setProjection( String projectionName, AlgebraicField field )
    {
        // TODO: decouple the projection classes
        switch( projectionName ) {

        case "Quaternion":
            projection = new QuaternionProjection(field, null, null);
            break;

        case "SixCube":
            projection = new SixCubeProjection(field);
            break;

        case "Tetrahedral":
            projection = new TetrahedralProjection(field);
            break;

        case "Perspective":
            projection = new PerspectiveProjection(field, null);
            break;
        }
    }

    @Override
    public void perform() throws Failure
    {
        if( meshData == null )
            return;
        
        AlgebraicVector offset = null;
        boolean pointFound = false;
        for (Manifestation man : mSelection) {
            if ( man instanceof Connector )
            {
                Point nextPoint = (Point) ((Connector) man) .getFirstConstruction();
                if ( ! pointFound )
                {
                    pointFound = true;
                    offset = nextPoint .getLocation();
                }
                else
                {
                    // multiple balls are selected. Ignore them all and proceed as if none were selected
                    offset = null;
                    break; // no need to loop thru any more
                }
            }
            if ( this .deselectInputs() ) // no need to be backward-compatible
                unselect( man );
        }

        AlgebraicField field = this .mManifestations .getField();
        ManifestConstructions events = new ManifestConstructions( this );
        AlgebraicField.Registry registry = new AlgebraicField.Registry()
        {
            @Override
            public AlgebraicField getField( String name )
            {
                if ( field .getName() .equals( name ) )
                    return field;
                else
                    return null;
            }
        };
        
        if ( this .projection == null )
            this .projection = new Projection.Default( field );

        try {
            this .parseMeshData( offset, events, registry );
        } catch ( IOException e ) {
            throw new Failure( "The selected file has incorrect content for this import.\n" + e .getMessage() );
        }

        redo();
    }

    protected abstract void parseMeshData( AlgebraicVector offset, ManifestConstructions events, Registry registry ) throws IOException;

    protected boolean deselectInputs()
    {
        return true;
    }
}
