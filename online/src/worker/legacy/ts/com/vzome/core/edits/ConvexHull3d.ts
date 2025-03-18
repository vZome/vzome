/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class ConvexHull3d extends com.vzome.core.edits.ConvexHull {
        public static NAME: string = "ConvexHull3d";

        /*private*/ mode: string;

        /*private*/ generateStruts: boolean;

        /*private*/ generatePanels: boolean;

        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
            this.mode = null;
            this.generateStruts = true;
            this.generatePanels = true;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return ConvexHull3d.NAME;
        }

        /**
         * 
         * @return {boolean}
         */
        public isNoOp(): boolean {
            return (this.generateStruts || this.generatePanels) ? super.isNoOp() : true;
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            this.setMode(<string>props.get("mode"));
        }

        /*private*/ setMode(newMode: string) {
            this.mode = "";
            this.generateStruts = true;
            this.generatePanels = true;
            if (newMode != null)switch((newMode)) {
            case "":
                this.mode = newMode;
                this.generateStruts = true;
                this.generatePanels = true;
                break;
            case "noPanels":
                this.mode = newMode;
                this.generateStruts = true;
                this.generatePanels = false;
                break;
            case "onlyPanels":
                this.mode = newMode;
                this.generateStruts = false;
                this.generatePanels = true;
                break;
            default:
                if (com.vzome.core.editor.api.ChangeSelection.logger_$LI$().isLoggable(java.util.logging.Level.WARNING)){
                    com.vzome.core.editor.api.ChangeSelection.logger_$LI$().warning(ConvexHull3d.NAME + ": Ignoring unknown mode: \"" + newMode + "\".");
                }
                break;
            }
        }

        /**
         * 
         */
        public perform() {
            const hull3d: com.vzome.core.math.convexhull.QuickHull3D = new com.vzome.core.math.convexhull.QuickHull3D();
            hull3d.build$java_util_Collection(this.getSelectedVertexSet(true));
            this.redo();
            const vertices: com.vzome.core.algebra.AlgebraicVector[] = hull3d.getVertices$();
            const pointMap: java.util.Map<com.vzome.core.algebra.AlgebraicVector, com.vzome.core.construction.Point> = <any>(new java.util.HashMap<any, any>(vertices.length));
            for(let index = 0; index < vertices.length; index++) {
                let vertex = vertices[index];
                {
                    const point: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(vertex);
                    pointMap.put(vertex, point);
                    this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(point));
                }
            }
            const faces: number[][] = hull3d.getFaces$();
            for(let index = 0; index < faces.length; index++) {
                let face = faces[index];
                {
                    const points: com.vzome.core.construction.Point[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(face.length);
                    let startIndex: number = face[face.length - 1];
                    let start: com.vzome.core.construction.Point = pointMap.get(vertices[startIndex]);
                    for(let i: number = 0; i < face.length; i++) {{
                        const endIndex: number = startIndex;
                        startIndex = face[i];
                        const end: com.vzome.core.construction.Point = points[i] = pointMap.get(vertices[startIndex]);
                        if (this.generateStruts && (startIndex < endIndex)){
                            this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.SegmentJoiningPoints(start, end)));
                        }
                        start = end;
                    };}
                    if (this.generatePanels){
                        this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(new com.vzome.core.construction.PolygonFromVertices(points)));
                    }
                }
            }
            this.redo();
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            if (this.mode != null){
                element.setAttribute("mode", this.mode);
            }
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.setMode(xml.getAttribute("mode"));
        }
    }
    ConvexHull3d["__class"] = "com.vzome.core.edits.ConvexHull3d";

}

