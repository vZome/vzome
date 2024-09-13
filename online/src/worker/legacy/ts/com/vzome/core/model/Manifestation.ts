/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    export interface Manifestation extends com.vzome.core.model.GroupElement {
        getLocation(): com.vzome.core.algebra.AlgebraicVector;

        getConstructions(): java.util.Iterator<com.vzome.core.construction.Construction>;

        getFirstConstruction(): com.vzome.core.construction.Construction;

        getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element;

        isHidden(): boolean;

        isRendered(): boolean;

        toConstruction(): com.vzome.core.construction.Construction;

        getCentroid(): com.vzome.core.algebra.AlgebraicVector;

        isUnnecessary(): boolean;

        addConstruction(mConstruction: com.vzome.core.construction.Construction);

        removeConstruction(mConstruction: com.vzome.core.construction.Construction);

        setHidden(b: boolean);

        getContainer(): com.vzome.core.model.Group;

        getColor(): com.vzome.core.construction.Color;

        setColor(color: com.vzome.core.construction.Color);

        setRenderedObject(renderedObject: com.vzome.core.model.RenderedObject);

        setLabel(label: string);

        getLabel(): string;
    }
}

