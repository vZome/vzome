/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class RealizeMetaParts extends com.vzome.core.editor.api.ChangeManifestations {
        public static NAME: string = "realizeMetaParts";

        /**
         * 
         */
        public perform() {
            let scale: com.vzome.core.algebra.AlgebraicNumber = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    const rm: com.vzome.core.model.RenderedObject = (<com.vzome.core.model.HasRenderedObject><any>man).getRenderedObject();
                    if (rm != null){
                        const shape: com.vzome.core.math.Polyhedron = rm.getShape();
                        if (scale == null){
                            const field: com.vzome.core.algebra.AlgebraicField = shape.getField();
                            scale = field['createPower$int'](5);
                        }
                        const orientation: com.vzome.core.algebra.AlgebraicMatrix = rm.getOrientation();
                        const vertexList: java.util.List<com.vzome.core.algebra.AlgebraicVector> = shape.getVertexList();
                        for(let index=shape.getVertexList().iterator();index.hasNext();) {
                            let vertex = index.next();
                            {
                                const vertexPt: com.vzome.core.construction.Point = this.transformVertex(vertex, man.getLocation(), scale, orientation);
                                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(vertexPt));
                            }
                        }
                        for(let index=shape.getFaceSet().iterator();index.hasNext();) {
                            let face = index.next();
                            {
                                const vertices: com.vzome.core.construction.Point[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(face.size());
                                for(let i: number = 0; i < vertices.length; i++) {{
                                    const vertexIndex: number = face.getVertex(i);
                                    const vertex: com.vzome.core.algebra.AlgebraicVector = vertexList.get(vertexIndex);
                                    vertices[i] = this.transformVertex(vertex, man.getLocation(), scale, orientation);
                                };}
                                const polygon: com.vzome.core.construction.Polygon = new com.vzome.core.construction.PolygonFromVertices(vertices);
                                this.select$com_vzome_core_model_Manifestation(this.manifestConstruction(polygon));
                            }
                        }
                    }
                }
            }
            this.redo();
        }

        /*private*/ transformVertex(vertex: com.vzome.core.algebra.AlgebraicVector, offset: com.vzome.core.algebra.AlgebraicVector, scale: com.vzome.core.algebra.AlgebraicNumber, orientation: com.vzome.core.algebra.AlgebraicMatrix): com.vzome.core.construction.Point {
            if (orientation != null)vertex = orientation.timesColumn(vertex);
            if (offset != null)vertex = vertex.plus(offset);
            return new com.vzome.core.construction.FreePoint(vertex.scale(scale));
        }

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return RealizeMetaParts.NAME;
        }
    }
    RealizeMetaParts["__class"] = "com.vzome.core.edits.RealizeMetaParts";

}

