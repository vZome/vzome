package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.MoveAndRotate;
import com.vzome.core.construction.Transformation;
import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.model.Manifestation;

public class FreeMove extends ChangeManifestations
{
    private Manifestation objectToMove;
    private Transformation moveAndRotate;

    public FreeMove( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
    }

    @Override
    public void configure( Map<String,Object> props )
    {
        Manifestation object = (Manifestation) props .get( "picked" );
        if ( object != null ) // first creation from the editor
        {
            this .objectToMove = object;
        }
        AlgebraicVector destination = (AlgebraicVector) props .get( "location" );
        AlgebraicMatrix rotation = (AlgebraicMatrix) props .get( "rotation" );
        if ( destination != null && rotation != null ) // first creation from the editor
        {
            this .moveAndRotate = new MoveAndRotate( rotation, object .getLocation(), destination );
        }
    }

    @Override
    public void perform() throws Command.Failure
    {
        Construction c = this .objectToMove .toConstruction();

        // Since vZome code is not really expecting locations and rotations to change, we will
        //   implement this as a delete + create.
        this .deleteManifestation( this .objectToMove );
        this .manifestConstruction( this .moveAndRotate .transform( c ) );
        
        super .perform();
    }

    @Override
    protected String getXmlElementName()
    {
        return "FreeMove";
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
//        DomUtils.addAttribute( element, "vector1", vector1 .toParsableString() );
//        DomUtils.addAttribute( element, "vector2", vector2 .toParsableString() );
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Failure
    {
//        vector1 = format.parseRationalVector( xml, "vector1" );
//        vector2 = format.parseRationalVector( xml, "vector2" );
    }
}
