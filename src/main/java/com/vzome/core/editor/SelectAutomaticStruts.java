package com.vzome.core.editor;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.generic.Predicate;
import com.vzome.core.math.DomUtils;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

/**
 * @author David Hall
 */
public class SelectAutomaticStruts extends ChangeManifestations {

	protected final SymmetrySystem symmetry;
	
	public SelectAutomaticStruts(SymmetrySystem symm, Selection selection, RealizedModel model) {
		super(selection, model);
		this.symmetry = symm;
	}

    // See the note in SelectSimilarSizeStruts regarding different symmetries. The same logic applies here.
	@Override
	public void perform() throws Failure {
        unselectAll();
        for( Strut strut : getVisibleStruts(isAutomaticStrut) ) {
            select(strut);
		}
		super.perform();
	}
	
	private final Predicate<Strut> isAutomaticStrut = new Predicate<Strut>()
	{	
	    public boolean test( Strut strut ) {
	        return symmetry.getAxis(strut.getOffset()).getOrbit().isAutomatic();
	    }
	};
    
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

    // Note that symmetry is read from the XML and passed to the c'tor
    // unlike the normal pattern of deserializing the XML here.
    // See the explanation in DocumentModel.createEdit()
    // There's really no need to override setXmlAttributes in this case
    // since there is nothing else to deserialize. but it does serve as a reminder
    // if this code is used as a pattern for another subclass of ChangeSelection.
    @Override
    protected void setXmlAttributes(Element xml, XmlSaveFormat format)
            throws Failure {
    }
}
