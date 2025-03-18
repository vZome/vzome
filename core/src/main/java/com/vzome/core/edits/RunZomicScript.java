
package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.XmlSymmetryFormat;
import com.vzome.core.construction.Point;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.ImplicitSymmetryParameters;
import com.vzome.core.editor.api.ManifestConstructions;
import com.vzome.core.editor.api.SymmetryAware;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class RunZomicScript extends ChangeManifestations
{
    private String programText;
    private final Point origin;
    private IcosahedralSymmetry symm;

    public RunZomicScript( EditorModel editor )
    {
        super( editor );
        this.origin = ((ImplicitSymmetryParameters) editor) .getCenterPoint();
		this.symm = (IcosahedralSymmetry) ((SymmetryAware) editor) .getSymmetrySystem() .getSymmetry();
    }
    
    @Override
    public void configure( Map<String,Object> props ) 
    {
        this.programText = (String) props .get( "script" );
    }

	@Override
    protected String getXmlElementName()
    {
        return "RunZomicScript";
    }
	
	protected String getScriptDialect()
	{
	    return "zomic";
	}

	@Override
    protected void getXmlAttributes( Element element )
    {
        element .setTextContent( XmlSaveFormat .escapeNewlines( programText ) );
    }

	@Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format )
            throws Failure
    {
        programText = xml .getTextContent();
        this .symm = (IcosahedralSymmetry) ((XmlSymmetryFormat) format) .parseSymmetry( "icosahedral" );
    }

	@Override
    public void perform() throws Failure
    {
        Point offset = null;
        boolean pointFound = false;
		for (Manifestation man : mSelection) {
			if ( man instanceof Connector )
			{
				Point nextPoint = (Point) ((Connector) man) .getFirstConstruction();
				if ( ! pointFound )
				{
					pointFound = true;
					offset = nextPoint;
				}
				else
				{
					offset = null;
				}
			}
		}
        if ( offset == null )
            offset = origin;

        try {
            this .symm .interpretScript( programText, getScriptDialect(), offset, symm, new ManifestConstructions( this ) );
        } catch (Exception e) {
            throw new Failure( e.getMessage(), e );
        }

        redo();
    }
}
