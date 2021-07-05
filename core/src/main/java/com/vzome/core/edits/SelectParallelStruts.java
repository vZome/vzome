package com.vzome.core.edits;

import java.util.Map;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.XmlSymmetryFormat;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.SymmetryAware;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.model.Strut;
import com.vzome.xml.DomUtils;

/**
 * @author David Hall
 */
public class SelectParallelStruts extends ChangeManifestations
{
    private OrbitSource symmetry;
    private Direction orbit;
    private Axis axis;
    private final EditorModel editor;

    /**
     * called from the main menu and when opening a file
     * @param symmetry
     * @param selection
     * @param model
     */
    public SelectParallelStruts( EditorModel editor )
    {
        super( editor );
        this.editor = editor;
        this.symmetry = ((SymmetryAware) editor) .getSymmetrySystem();
    }

    @Override
    public void configure( Map<String,Object> props )
    {
        Strut strut = (Strut) props .get( "picked" );
        if ( strut != null ) // first creation from the editor
        {
            this.axis = symmetry .getAxis( strut .getOffset() );
            this.orbit = this.axis .getOrbit();
        }
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
            Strut lastStrut = getLastSelectedStrut();
            if (lastStrut != null) {
                AlgebraicVector offset = lastStrut.getOffset();
                this.orbit = symmetry.getAxis(offset).getOrbit();
                this.axis = orbit.getAxis(offset);
            }
        }

        if (orbit == null || axis == null) {
            throw new Failure("select a reference strut.");
        }
        unselectAll();

        // The logic for getting oppositeAxis is copied from Direction.getAxis(AlgebraicVector)
        // TODO: We should probably have a symmetry method to ensure that we get the opposite axis correctly 
        // instead of duplicating this code, or worse yet, forgetting to copy it
        // and not considering the principalReflection when applicable (e.g. any antiprism symmetry). 
        Axis oppositeAxis = symmetry.getSymmetry().getPrincipalReflection() == null
            // the traditional way... anti-parallel means just flip the sense
            ? orbit.getAxis( (( axis .getSense() + 1 ) % 2), axis .getOrientation() )
            // anti-parallel mean flip inbound to outbound or vice-versa
            : orbit.getAxis( axis .getSense(), axis .getOrientation(), ! axis .isOutbound() );
            
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
            XmlSymmetryFormat .serializeAxis( element, "symm", "dir", "index", "sense", axis );
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format )
            throws Failure
    {
        this.symmetry = ((SymmetryAware) this .editor) .getSymmetrySystem( xml .getAttribute( "symmetry" ) );
        orbit = this.symmetry .getOrbits() .getDirection( xml .getAttribute( "orbit" ) );
        axis = ((XmlSymmetryFormat) format) .parseAxis( xml, "symm", "dir", "index", "sense" );
    }
}
