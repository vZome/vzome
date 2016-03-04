package com.vzome.core.editor;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;
import org.w3c.dom.Element;

/**
 * @author David Hall
 */
public class SelectParallelStruts extends ChangeManifestations
{
    private final SymmetrySystem symmetry;
    private Direction orbit;
    private Axis axis;

    /**
     * called from the main menu and when opening a file
     * @param symmetry
     * @param selection
     * @param model
     */
    public SelectParallelStruts(SymmetrySystem symmetry, Selection selection, RealizedModel model)
    {
        super( selection, model );
        this.symmetry = symmetry;
        Strut lastStrut = getLastSelectedStrut();
        if (lastStrut != null) {
            AlgebraicVector offset = lastStrut.getOffset();
            this.orbit = symmetry.getAxis(offset).getOrbit();
            this.axis = orbit.getAxis(offset);
        } else {
            this.orbit = null;
            this.axis = null;
        }
    }

    /**
     * called from the strut context menu
     * @param symmetry
     * @param selection
     * @param model
     * @param strut
     */
    public SelectParallelStruts(SymmetrySystem symmetry, Selection selection, RealizedModel model, Strut strut) {
        super( selection, model );
        this.symmetry = symmetry;
        this.axis = symmetry.getAxis( strut .getOffset() );
        this.orbit = this.axis .getOrbit();
    }

	// Normal usage cases include invocation from:
    // 1) Main menu with 1 or more struts selected (Last selected strut will be used as input)
    // 2) Strut context menu
    // 3) Undo and Redo operations
    // 4) Reopening a saved vZome file (persisted as XML)
    //
    // Ensure that we can safely do nothing (with no exceptions thrown) when invoked from:
    // 1) Main menu with nothing selected
    // 2) Main menu with balls and/or panels selected but no strut selected
    @Override
    public void perform() throws Failure {
        if (orbit == null || axis == null) {
            throw new Failure("select a reference strut.");
        }
        unselectAll();

        int opposite = ( axis .getSense() + 1 ) % 2;
        Axis oppositeAxis = orbit. getAxis( opposite, axis .getOrientation() );

        for (Strut strut : getStruts()) {
            Axis strutAxis = symmetry.getAxis(strut .getOffset());
            if (strutAxis != null && strutAxis.getOrbit().equals(orbit)) {
                if ( strutAxis.equals(axis) || strutAxis.equals(oppositeAxis) ) {
                    select(strut);
                }
            }
        }
        super .perform();
    }

    @Override
    protected String getXmlElementName()
    {
        return "SelectParallelStruts";
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        if ( symmetry != null )
            DomUtils .addAttribute( element, "symmetry", symmetry .getName() );
        if ( orbit != null )
            DomUtils .addAttribute( element, "orbit", orbit .getName() );
        if ( axis != null )
            axis.getXML(element);
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format )
            throws Failure
    {
        orbit = symmetry .getOrbits() .getDirection( xml .getAttribute( "orbit" ) );
        if(orbit == null) {
            throw new Failure("Can't read orbit from xml.");
        }
        String strSense = xml.getAttribute("sense");
        int sense = (strSense != null && "minus".equals(strSense.toLowerCase()))
                ? Symmetry.MINUS
                : Symmetry.PLUS;
        int index = Integer.parseInt(xml.getAttribute("index"));
        this.axis = orbit.getAxis(sense, index);
    }
}
