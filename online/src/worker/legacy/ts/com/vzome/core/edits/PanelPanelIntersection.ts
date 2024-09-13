/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * @author David Hall
     * @param {*} editor
     * @class
     * @extends com.vzome.core.editor.api.ChangeManifestations
     */
    export class PanelPanelIntersection extends com.vzome.core.editor.api.ChangeManifestations {
        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
        }

        /**
         * 
         */
        public perform() {
            let panel0: com.vzome.core.model.Panel = null;
            let panel1: com.vzome.core.model.Panel = null;
            let nPanels: number = 0;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        switch((nPanels++)) {
                        case 0:
                            panel0 = <com.vzome.core.model.Panel><any>man;
                            break;
                        case 1:
                            panel1 = <com.vzome.core.model.Panel><any>man;
                            break;
                        default:
                            break;
                        }
                    }
                }
            }
            if (nPanels !== 2){
                let msg: string;
                switch((nPanels)) {
                case 0:
                    msg = "No panels are selected.";
                    break;
                case 1:
                    msg = "One panel is selected.";
                    break;
                default:
                    msg = nPanels + " panels are selected.";
                    break;
                }
                this.fail(msg + " Two are required.");
            }
            if (com.vzome.core.algebra.AlgebraicVectors.areParallel(panel0['getNormal$'](), panel1['getNormal$']())){
                const vertices: java.util.List<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>());
                {
                    let array = [panel0, panel1];
                    for(let index = 0; index < array.length; index++) {
                        let panel = array[index];
                        {
                            for(let index1=panel.iterator();index1.hasNext();) {
                                let v = index1.next();
                                {
                                    vertices.add(v);
                                }
                            }
                        }
                    }
                }
                this.fail("Panels are " + (com.vzome.core.algebra.AlgebraicVectors.areCoplanar(vertices) ? "coplanar" : "parallel") + ".");
            }
            this.redo();
            const segment: com.vzome.core.construction.Segment = new com.vzome.core.construction.PolygonPolygonProjectionToSegment(PanelPanelIntersection.polygonFromPanel(panel0), PanelPanelIntersection.polygonFromPanel(panel1));
            const start: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(segment.getStart());
            const end: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(segment.getEnd());
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(segment));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(start));
            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(end));
            this.redo();
        }

        /*private*/ static polygonFromPanel(panel: com.vzome.core.model.Panel): com.vzome.core.construction.Polygon {
            const vertices: java.util.List<com.vzome.core.construction.Point> = <any>(new java.util.ArrayList<any>(panel.getVertexCount()));
            for(let index=panel.iterator();index.hasNext();) {
                let vector = index.next();
                {
                    vertices.add(new com.vzome.core.construction.FreePoint(vector));
                }
            }
            return new com.vzome.core.construction.PolygonFromVertices(vertices);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return /* getSimpleName */(c => typeof c === 'string' ? (<any>c).substring((<any>c).lastIndexOf('.')+1) : c["__class"] ? c["__class"].substring(c["__class"].lastIndexOf('.')+1) : c["name"].substring(c["name"].lastIndexOf('.')+1))((<any>this.constructor));
        }
    }
    PanelPanelIntersection["__class"] = "com.vzome.core.edits.PanelPanelIntersection";

}

