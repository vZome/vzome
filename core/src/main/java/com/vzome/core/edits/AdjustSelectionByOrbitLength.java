package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.editor.api.ActionEnum;
import com.vzome.core.editor.api.ChangeSelection;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.SymmetryAware;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.xml.DomUtils;

/**
 * @author David Hall
 * This class is designed to be a generalized replacement for SelectSimilarSizeStruts
 * It allows struts and/or panels to be selected, deselected or ignored by orbit and optionally by length in orbit units
 * It can be used in place of SelectSimilarSizeStruts including the ability to parse the legacy XML.
 * SelectSimilarSizeStruts has been renamed as AdjustSelectionByOrbitLength and modified with the additional functionality.
 */
public class AdjustSelectionByOrbitLength extends ChangeSelection
{
    private Direction orbit;
    private AlgebraicNumber length;
    private OrbitSource symmetry;
    private ActionEnum strutAction = ActionEnum.IGNORE;
    private ActionEnum panelAction = ActionEnum.IGNORE;
    private final EditorModel editor;

    /**
     * This constructor is only used during deserialization, so it prepares for setXmlAttributes().
     * @param editor
     */
    public AdjustSelectionByOrbitLength( EditorModel editor )
    {
        super( editor .getSelection() );
        this.symmetry = ((SymmetryAware) editor) .getSymmetrySystem();
        this.editor = editor;
    }

    @Override
    public void configure( Map<String,Object> props )
    {
        String mode = (String) props .get( "mode" );
        Strut strut = (Strut) props .get( "picked" );
        this.orbit = (Direction) props .get( "orbit" );
        this.length = (AlgebraicNumber) props .get( "length" );

        if ( mode != null )
            switch ( mode ) {

            case "selectSimilarStruts":
                this.strutAction = ActionEnum.SELECT;
                break;

            case "selectSimilarPanels":
                this.panelAction = ActionEnum.SELECT;
                break;

            case "deselectSimilarStruts":
                this.strutAction = ActionEnum.DESELECT;
                break;

            case "deselectSimilarPanels":
                this.panelAction = ActionEnum.DESELECT;
                break;
            }

        if ( strut != null ) // first creation from the editor
        {
            AlgebraicVector offset = strut .getOffset();
            Axis zone = this.symmetry .getAxis( offset );
            this.orbit = zone .getOrbit();
            this.length = zone .getLength( offset );
        }
    }

    /*
    This class will only select struts of the same length AND ORBIT within the given symmetry.
    For example, if short blue struts radiate from the origin with icoshedral symmetry,
    then we switch to octahedral symmetry, some of the struts will then be on black axes
    according to the octahedral symmetry system. In that case, when one of the blue struts are selected
    as the reference for this command, only blue struts of the same length will be selected.
    None of the black struts will be selected even though they are the same length,
    since the black struts are no longer in a blue orbit.
    This behavior is intentional. The struts to be selected are dependent on the selected symmetry.
    In the case of panels, the length parameter is ignored and may be null. The orbit refers to the panel normal.
    If the length is null and the action is directed to a strut, then only the orbit will be considered and any length will be a match.
     */
    @Override
    public void perform() throws Failure
    {
        // if any action is a SELECT, then we have to use model
        // otherwise we will use mSelection because it may be a shorter list
        Iterable<Manifestation> whichManifestationSet = (
                strutAction == ActionEnum.SELECT ||
                panelAction == ActionEnum.SELECT)
                ? this .editor .getRealizedModel()
                : mSelection;

        for (Manifestation man : whichManifestationSet) {
            if ( man.isRendered() ) {
                if (man instanceof Strut) {
                    AlgebraicVector offset = ((Strut) man).getOffset();
                    Axis zone = symmetry.getAxis(offset);
                    if (zone.getOrbit() == this.orbit) {
                        if (this.length == null || this.length.equals(zone.getLength(offset))) {
                            adjustSelection(man, strutAction);
                        }
                    }
                }
                else if (man instanceof Panel) {
                    Axis zone = symmetry.getAxis(((Panel) man).getNormal());
                    if (zone.getOrbit() == this.orbit) {
                        adjustSelection(man, panelAction);
                    }
                }
            }
        }
        redo();
    }

    @Override
    protected String getXmlElementName()
    {
        return "AdjustSelectionByOrbitLength";
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        if (symmetry != null) {
            DomUtils.addAttribute(element, "symmetry", symmetry.getName());
        }
        if (orbit != null) {
            DomUtils.addAttribute(element, "orbit", orbit.getName());
        }
        if (length != null) {
            XmlSaveFormat.serializeNumber(element, "length", length);
        }
        element .setAttribute( "struts", strutAction.toString() );
        element .setAttribute( "panels", panelAction.toString() );
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format )
            throws Failure
    {
        this .symmetry = ((SymmetryAware) this .editor) .getSymmetrySystem( xml .getAttribute( "symmetry" ) );
        length = format.parseNumber(xml, "length");
        orbit = symmetry.getOrbits().getDirection(xml.getAttribute("orbit"));
        if(xml.getLocalName().equals("SelectSimilarSize")) {
            // this allows the xml from the legacy SelectSimilarSizeStruts to be parsed and handled by this class
            strutAction = ActionEnum.SELECT;
            panelAction = ActionEnum.IGNORE;
        } else {
            strutAction = ActionEnum.valueOf(xml.getAttribute( "struts" ));
            panelAction = ActionEnum.valueOf(xml.getAttribute( "panels" ));
        }
    }
}
