package com.vzome.core.editor;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.math.DomUtils;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;
import org.w3c.dom.Element;

/**
 * @author David Hall
 */
public class SelectAutomaticStruts extends ChangeManifestations {

	protected final SymmetrySystem symmetry;
	
	public SelectAutomaticStruts(SymmetrySystem symm, Selection selection, RealizedModel model) {
		super(selection, model);
		this.symmetry = symm;
	}
	
	@Override
	public void perform() throws Failure {
        unselectAll();
        for( Strut strut : getVisibleStruts(this::isAutomaticStrut) ) {
            select(strut);
		}
		super.perform();
	}
	
    private boolean isAutomaticStrut(Strut strut) {
        return symmetry.getAxis(strut.getOffset()).getOrbit().isAutomatic();
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
}
