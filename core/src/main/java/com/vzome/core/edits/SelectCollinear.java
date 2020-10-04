package com.vzome.core.edits;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Strut;
import com.vzome.xml.DomUtils;

/**
 * @author David Hall
 */
public class SelectCollinear extends ChangeManifestations {

    private AlgebraicVector vector1 = null;
    private AlgebraicVector vector2 = null;

    public SelectCollinear( EditorModel editor )
    {
        super( editor );
    }

    @Override
    public void configure( Map<String,Object> props )
    {
        Strut strut = (Strut) props .get( "picked" );
        if ( strut != null ) // first creation from the editor
        {
            this.vector1 = strut .getLocation();
            this.vector2 = strut .getEnd();
        }
    }

    @Override
    public void perform() throws Command.Failure
    {
        if(vector1 == null || vector2 == null) {
            Strut lastStrut = getLastSelectedStrut();
            if (lastStrut != null) {
                vector1 = lastStrut.getLocation();
                vector2 = lastStrut.getEnd();
            } else {
                for (Connector ball : getSelectedConnectors()) {
                    // use the last two balls selected if no strut was selected
                    vector1 = vector2;
                    vector2 = ball.getLocation();
                }
            }
        }

        if (vector1 == null || vector2 == null) {
            throw new Failure("select a strut or two balls as a reference.");
        }
        unselectAll();

        Set<Connector> balls = new TreeSet<>(); // auto sorted
        // Example of using FilteredIterator with Predicate parameter.
        for (Connector ball : getVisibleConnectors(this::isCollinearWith)) {
            balls.add(ball);
        }

        Set<Strut> struts = new TreeSet<>(); // auto sorted
        // Example of conventional "if" syntax within the loop.
        // Compare to the equivalent FilteredIterator predicate syntax above.
        for (Strut strut : getVisibleStruts()) {
            if (isCollinearWith(strut)) {
                struts.add(strut);
            }
        }

        // balls and struts to be selected are now in two sorted sets
        // so we can select them in a consistent canonical order, suitable
        // for subsequent use by other tools such as JoinPoints
        for (Strut strut : struts) {
            select(strut);
        }
        for (Connector ball : balls) {
            select(ball);
        }

        Level level = Level.FINER;
        if (logger.isLoggable(level)) {
            StringBuilder sb = new StringBuilder("Selected:\n");
            final String indent = "  ";
            for (Strut strut : struts) {
                sb.append(indent).append(strut.toString()).append("\n");
            }
            for (Connector ball : balls) {
                sb.append(indent).append(ball.toString()).append("\n");
            }
            logger.log(level, sb.toString());
        }

        super.perform();
    }

    private boolean isCollinearWith(Connector ball) {
        return isCollinear(ball.getLocation());
    }

    private boolean isCollinearWith(Strut strut) {
        return isCollinear(strut.getLocation())
                && isCollinear(strut.getEnd());
    }

    private boolean isCollinear(AlgebraicVector vec) {
        return AlgebraicVectors.areCollinear(vec, vector1, vector2);
    }

    @Override
    protected String getXmlElementName()
    {
        return "SelectCollinear";
    }

    @Override
    protected void getXmlAttributes(Element element) {
        // AlgebraicVector.toString() seems to be the counterpart to format.parseRationalVector()
        // in LoadVEF.getXmlAttributes() and CommandImportVEFData.getXml(),
        // but writing in that format doesn't seem to be re-readable here
        // so I added a new AlgebraicVector.toParsableString() method.
        // TODO: DJH: Test LoadVEF.getXmlAttributes() and CommandImportVEFData.getXml()
        // to be sure they can actually read the AlgebraicNumber formats that they write.
        // If not, then have them use AlgebraicVector.toParsableString() as well.
        // CommandImportVEFData has a note about quaternionVector only being read from XML
        // so I wonder if it's ever been written out to XML and re-read
        // or if it's only ever been manually added to the XML in a readable format 
        // and then read in without this issue ever showing up.
        DomUtils.addAttribute(element, "vector1", vector1.toParsableString());
        DomUtils.addAttribute(element, "vector2", vector2.toParsableString());
    }

    @Override
    protected void setXmlAttributes(Element xml, XmlSaveFormat format) throws Failure {
        vector1 = format.parseRationalVector(xml, "vector1");
        vector2 = format.parseRationalVector(xml, "vector2");
    }
}
