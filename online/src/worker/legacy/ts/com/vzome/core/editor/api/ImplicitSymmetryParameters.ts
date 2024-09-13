/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export interface ImplicitSymmetryParameters extends com.vzome.core.editor.api.EditorModel {
        getCenterPoint(): com.vzome.core.construction.Point;

        setCenterPoint(point: com.vzome.core.construction.Construction);

        getSymmetrySegment(): com.vzome.core.construction.Segment;

        setSymmetrySegment(segment: com.vzome.core.construction.Segment);

        getSelectedConstruction(kind: any): com.vzome.core.construction.Construction;
    }
}

