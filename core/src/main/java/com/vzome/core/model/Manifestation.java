package com.vzome.core.model;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;

public interface Manifestation extends GroupElement {

    AlgebraicVector getLocation();

    Iterator<Construction> getConstructions();
    
    Construction getFirstConstruction();

    Element getXml(Document doc);

    boolean isHidden();

    boolean isRendered();

    Construction toConstruction();

    AlgebraicVector getCentroid();
    
    boolean isUnnecessary();

    void addConstruction(Construction mConstruction);

    void removeConstruction(Construction mConstruction);

    void setHidden(boolean b);

    Group getContainer();
    
    Color getColor();
    
    void setColor( Color color );

    void setRenderedObject( RenderedObject renderedObject );
    
    public void setLabel( String label );
    
    String getLabel();
}
