
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;


import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.VefToModel;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.PerspectiveProjection;
import com.vzome.core.math.Projection;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.TetrahedralProjection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class LoadVEF extends ChangeManifestations
{
    private String vefData;
    private AlgebraicNumber scale;
    private Projection projection;

    public LoadVEF( Selection selection, RealizedModel realized, String vefData, Projection projection, AlgebraicNumber scale )
    {
        super( selection, realized, false );
        this .vefData = vefData;
        if (scale == null) {
            scale = mManifestations.getField().one();
        }
        this .scale = scale;
        this.projection = projection;
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
        switch( projectionName ) {
            case "":
                // legacy support for QuaternionProjection with no projection attribute
                AlgebraicVector quaternion = format .parseRationalVector( xml, "quaternion" );
                projection = quaternion == null
                        ? null
                        : new QuaternionProjection(field, null, quaternion);
                        break;

            case "Quaternion":
                projection = new QuaternionProjection(field, null, null);
                break;

            case "Tetrahedral":
                projection = new TetrahedralProjection(field);
                break;

            case "Perspective":
                projection = new PerspectiveProjection(field, null);
                break;

            default:
                throw new Failure("Unsupported VEF projection: '" + projectionName + "'");
        }
        if(projection != null) {
            projection.setXmlAttributes(xml, format);
        }
    }

    @Override
    public void perform() throws Failure
    {
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
                    offset = null;
                }
            }
        }

        AlgebraicField field = this .mManifestations .getField();
        VefToModel v2m = new VefToModel( projection, new ManifestConstructions( this ), scale, offset );
        v2m .parseVEF( vefData, field );

        redo();
    }
}
