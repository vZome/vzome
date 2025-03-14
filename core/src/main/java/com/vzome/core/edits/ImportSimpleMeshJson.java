package com.vzome.core.edits;

import java.io.IOException;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField.Registry;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.ManifestConstructions;
import com.vzome.core.math.Projection;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.model.SimpleMeshJson;
import com.vzome.xml.DomUtils;

public class ImportSimpleMeshJson extends ImportMesh
{
    protected boolean scaleAndProject = true;

    public ImportSimpleMeshJson( EditorModel editor )
    {
        super( editor );
    }

    @Override
    protected void parseMeshData( AlgebraicVector offset, ManifestConstructions events, Registry registry )
            throws IOException
    {
        // Legacy code dropped the 4th coordinate from the projection, implicitly because it never used
        //   the 4th coordinate, always making a 3D vector.  New code drops the 1st coordinate.
        boolean wFirst = false;

        if ( ! this.scaleAndProject ) {
            wFirst = true;
            AlgebraicField field = this.mManifestations .getField();
            this.scale = field.one();
            this .projection = new Projection.Default( field );
        }
        SimpleMeshJson .parse( meshData, offset, projection, scale, wFirst, events, registry );
    }

    @Override
    protected String getXmlElementName()
    {
        return "ImportSimpleMeshJson";
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        if ( scaleAndProject )
            DomUtils .addAttribute( element, "scaleAndProject", ((Boolean) scaleAndProject) .toString() );

        super.getXmlAttributes( element );
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
        String boolStr = xml .getAttribute( "scaleAndProject" );
        // Default is true, but legacy files must behave as if false
        if ( boolStr == null || ! boolStr .equals( "true" ) )
            scaleAndProject = false;

        super.setXmlAttributes( xml, format );
    }
}
