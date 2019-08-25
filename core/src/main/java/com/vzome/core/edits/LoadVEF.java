
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;


import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.VefToModel;
import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.editor.ManifestConstructions;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.PerspectiveProjection;
import com.vzome.core.math.Projection;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.SixCubeProjection;
import com.vzome.core.math.TetrahedralProjection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class LoadVEF extends ChangeManifestations
{
    private String vefData;
    private AlgebraicNumber scale;
    private Projection projection;
    private final EditorModel editor;

    public LoadVEF( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
        this.editor = editor;
    }

    @Override
    public void configure( Map<String, Object> params )
    {
        AlgebraicField field = editor .getKind() .getField();
        vefData = (String) params .get( "vef" );
        projection = (Projection) params .get( "projection" );
        scale = (AlgebraicNumber) params .get( "scale" );
        if (scale == null) {
            scale = field .one();
        }

        String projectionName = (String) params .get( "mode" );
        switch ( projectionName ) {

        case "clipboard":
            if( vefData != null && ! vefData.startsWith("vZome VEF" ))
                // Although older VEF formats don't all include the header and could possibly be successfully pasted here,
                // we're going to limit it to at least something that includes a valid VEF header.
                // We won't check the version number so we can still paste formats older than VERSION_W_FIRST
                // as long as they at least include the minimal header.
                vefData = null; //disable the edit
            break;

        case "Quaternion":
            // projection should have been set from params
            break;
        
        default:
            setProjection( projectionName, field );
            break;
        }
    }

    @Override
    public boolean isNoOp()
    {
        return vefData == null;
    }

    @Override
    protected String getXmlElementName()
    {
        return "LoadVEF";
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
        Node textNode = element .getOwnerDocument() .createTextNode( XmlSaveFormat .escapeNewlines( vefData ) );
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
        vefData = xml .getTextContent();
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
            projection.setXmlAttributes(xml, format);
        }
    }
    
    private void setProjection( String projectionName, AlgebraicField field )
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
        if( vefData == null )
            return;

        AlgebraicVector offset = null;
        boolean pointFound = false;
        for (Manifestation man : mSelection) {
            if ( man instanceof Connector )
            {
                Point nextPoint = (Point) ((Connector) man) .getConstructions() .next();
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
        }

        AlgebraicField field = this .mManifestations .getField();
        VefToModel v2m = new VefToModel( projection, new ManifestConstructions( this ), scale, offset );
        v2m .parseVEF( vefData, field );

        redo();
    }
}
