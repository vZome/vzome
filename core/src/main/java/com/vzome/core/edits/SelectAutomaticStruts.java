package com.vzome.core.edits;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.SymmetryAware;
import com.vzome.core.model.Strut;
import com.vzome.xml.DomUtils;

/**
 * @author David Hall
 */
public class SelectAutomaticStruts extends ChangeManifestations {

	protected OrbitSource symmetry;
    private final EditorModel editor;
	
	public SelectAutomaticStruts( EditorModel editor )
    {
        super( editor );
        this.editor = editor;
		this.symmetry = ((SymmetryAware) editor) .getSymmetrySystem();
	}

    // See the note in SelectSimilarSizeStruts regarding different symmetries. The same logic applies here.
	@Override
	public void perform() throws Failure {
        unselectAll();
        for( Strut strut : getVisibleStruts( this::isAutomaticStrut ) ) {
            select(strut);
		}
		super.perform();
	}
	
    private boolean isAutomaticStrut(Strut strut)
    {
        return symmetry .getAxis( strut.getOffset() ) .getOrbit() .isAutomatic();
    }
    
	@Override
	protected String getXmlElementName() {
		return "SelectAutomaticStruts";
	}

    @Override
    protected void getXmlAttributes(Element element) {
        if (symmetry != null) {
            DomUtils.addAttribute(element, "symmetry", symmetry.getName());
        }
    }

    @Override
    protected void setXmlAttributes(Element xml, XmlSaveFormat format)
            throws Failure
    {
        this .symmetry = ((SymmetryAware) this .editor) .getSymmetrySystem( xml .getAttribute( "symmetry" ) );
    }
}
