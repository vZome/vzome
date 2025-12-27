/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class ReplaceWithShape extends com.vzome.core.editor.api.ChangeManifestations {
        public static NAME: string = "ReplaceWithShape";

        /*private*/ vef: string;

        /*private*/ shape: com.vzome.core.math.Polyhedron;

        /*private*/ ballOrStrut: com.vzome.core.model.Manifestation;

        /*private*/ symmetryShapes: string;

        /*private*/ editor: com.vzome.core.editor.api.EditorModel;

        /*private*/ replace(man: com.vzome.core.model.Manifestation, renderedObject: com.vzome.core.model.RenderedObject, shape: com.vzome.core.math.Polyhedron) {
            if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0))return;
            if (renderedObject != null){
                const orientation: com.vzome.core.algebra.AlgebraicMatrix = renderedObject.getOrientation();
                const vertexList: java.util.List<com.vzome.core.algebra.AlgebraicVector> = shape.getVertexList();
                for(let index=shape.getFaceSet().iterator();index.hasNext();) {
                    let face = index.next();
                    {
                        const vertices: com.vzome.core.construction.Point[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(face.size());
                        for(let i: number = 0; i < vertices.length; i++) {{
                            const vertexIndex: number = face.getVertex(i);
                            const vertex: com.vzome.core.algebra.AlgebraicVector = vertexList.get(vertexIndex);
                            vertices[i] = ReplaceWithShape.transformVertex(vertex, renderedObject.getLocationAV(), orientation);
                        };}
                        const polygon: com.vzome.core.construction.Polygon = new com.vzome.core.construction.PolygonFromVertices(vertices);
                        const panel: com.vzome.core.model.Manifestation = this.manifestConstruction(polygon);
                        this.select$com_vzome_core_model_Manifestation(panel);
                    }
                }
            }
            this.deleteManifestation(man);
        }

        /**
         * 
         */
        public perform() {
            if (this.symmetryShapes != null){
                const tokens: string[] = this.symmetryShapes.split(":");
                const symmetrySystem: com.vzome.core.editor.api.OrbitSource = (<com.vzome.core.editor.api.SymmetryAware><any>this.editor)['getSymmetrySystem$java_lang_String'](tokens[0]);
                const shapes: com.vzome.core.editor.api.Shapes = (<com.vzome.core.editor.SymmetrySystem><any>symmetrySystem).getStyle$java_lang_String(tokens[1]);
                const model: com.vzome.core.render.RenderedModel = new com.vzome.core.render.RenderedModel(symmetrySystem.getSymmetry().getField(), new ReplaceWithShape.ReplaceWithShape$0(this, symmetrySystem, shapes));
                if (this.ballOrStrut != null){
                    for(let index=this.mSelection.iterator();index.hasNext();) {
                        let man = index.next();
                        {
                            this.unselect$com_vzome_core_model_Manifestation(man);
                        }
                    }
                    this.redo();
                    const rm: com.vzome.core.render.RenderedManifestation = model.render(this.ballOrStrut);
                    this.replace(this.ballOrStrut, rm, rm.getShape());
                } else for(let index=this.mSelection.iterator();index.hasNext();) {
                    let man = index.next();
                    {
                        this.unselect$com_vzome_core_model_Manifestation(man);
                        const rm: com.vzome.core.render.RenderedManifestation = model.render(man);
                        this.replace(man, rm, rm.getShape());
                    }
                }
            } else {
                for(let index=this.mSelection.iterator();index.hasNext();) {
                    let man = index.next();
                    {
                        this.unselect$com_vzome_core_model_Manifestation(man);
                    }
                }
                this.redo();
                this.replace(this.ballOrStrut, (<com.vzome.core.model.HasRenderedObject><any>this.ballOrStrut).getRenderedObject(), this.shape);
            }
            super.perform();
        }

        /*private*/ static transformVertex(vertex: com.vzome.core.algebra.AlgebraicVector, offset: com.vzome.core.algebra.AlgebraicVector, orientation: com.vzome.core.algebra.AlgebraicMatrix): com.vzome.core.construction.Point {
            if (orientation != null)vertex = orientation.timesColumn(vertex);
            if (offset != null)vertex = vertex.plus(offset);
            return new com.vzome.core.construction.FreePoint(vertex);
        }

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.vef === undefined) { this.vef = null; }
            if (this.shape === undefined) { this.shape = null; }
            if (this.ballOrStrut === undefined) { this.ballOrStrut = null; }
            if (this.symmetryShapes === undefined) { this.symmetryShapes = null; }
            if (this.editor === undefined) { this.editor = null; }
            this.editor = editor;
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            const m: com.vzome.core.model.Manifestation = <com.vzome.core.model.Manifestation><any>props.get("picked");
            if (m != null){
                this.symmetryShapes = (<com.vzome.core.model.HasRenderedObject><any>m).getRenderedObject().getSymmetryShapes();
                this.ballOrStrut = m;
            } else this.symmetryShapes = <string>props.get("mode");
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            if (this.ballOrStrut != null){
                const construction: com.vzome.core.construction.Construction = this.ballOrStrut.getFirstConstruction();
                if (construction != null && construction instanceof <any>com.vzome.core.construction.Point)com.vzome.core.commands.XmlSaveFormat.serializePoint(element, "point", <com.vzome.core.construction.Point>construction); else com.vzome.core.commands.XmlSaveFormat.serializeSegment(element, "startSegment", "endSegment", <com.vzome.core.construction.Segment>construction);
            }
            if (this.shape != null){
                if (this.vef == null){
                    this.vef = com.vzome.core.algebra.VefVectorExporter.exportPolyhedron(this.shape);
                }
                const textNode: org.w3c.dom.Node = element.getOwnerDocument().createTextNode(com.vzome.core.commands.XmlSaveFormat.escapeNewlines(this.vef));
                element.appendChild(textNode);
            } else {
                element.setAttribute("shapes", this.symmetryShapes);
            }
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const attr: string = xml.getAttribute("shapes");
            if (attr != null && !/* isEmpty */(attr.length === 0)){
                this.symmetryShapes = attr;
            } else {
                this.vef = xml.getTextContent();
                this.shape = com.vzome.core.math.VefToPolyhedron.importPolyhedron(format.getField(), this.vef);
            }
            let construction: com.vzome.core.construction.Construction = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "point");
            if (construction == null)construction = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "startSegment", "endSegment");
            if (construction != null)this.ballOrStrut = this.getManifestation(construction);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return ReplaceWithShape.NAME;
        }
    }
    ReplaceWithShape["__class"] = "com.vzome.core.edits.ReplaceWithShape";


    export namespace ReplaceWithShape {

        export class ReplaceWithShape$0 implements com.vzome.core.editor.api.OrbitSource {
            public __parent: any;
            /* Default method injected from com.vzome.core.editor.api.OrbitSource */
            getOrientations$(): number[][] {
                return this.getOrientations(false);
            }
            /* Default method injected from com.vzome.core.editor.api.OrbitSource */
            getEmbedding(): number[] {
                const symmetry: com.vzome.core.math.symmetry.Symmetry = this.getSymmetry();
                const field: com.vzome.core.algebra.AlgebraicField = symmetry.getField();
                const embedding: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(16);
                for(let i: number = 0; i < 3; i++) {{
                    const columnSelect: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, i);
                    const colRV: com.vzome.core.math.RealVector = symmetry.embedInR3(columnSelect);
                    embedding[i * 4 + 0] = colRV.x;
                    embedding[i * 4 + 1] = colRV.y;
                    embedding[i * 4 + 2] = colRV.z;
                    embedding[i * 4 + 3] = 0.0;
                };}
                embedding[12] = 0.0;
                embedding[13] = 0.0;
                embedding[14] = 0.0;
                embedding[15] = 1.0;
                return embedding;
            }
            /* Default method injected from com.vzome.core.editor.api.OrbitSource */
            getZone(orbit: string, orientation: number): com.vzome.core.math.symmetry.Axis {
                return this.getSymmetry().getDirection(orbit).getAxis(com.vzome.core.math.symmetry.Symmetry.PLUS, orientation);
            }
            /* Default method injected from com.vzome.core.editor.api.OrbitSource */
            public getOrientations(rowMajor?: any): number[][] {
                if (((typeof rowMajor === 'boolean') || rowMajor === null)) {
                    let __args = arguments;
                    return <any>(() => {
                        const symmetry: com.vzome.core.math.symmetry.Symmetry = this.getSymmetry();
                        const field: com.vzome.core.algebra.AlgebraicField = symmetry.getField();
                        const order: number = symmetry.getChiralOrder();
                        const orientations: number[][] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order);
                        for(let orientation: number = 0; orientation < order; orientation++) {{
                            if (rowMajor){
                                orientations[orientation] = symmetry.getMatrix(orientation).getRowMajorRealElements();
                                continue;
                            }
                            const asFloats: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(16);
                            const transform: com.vzome.core.algebra.AlgebraicMatrix = symmetry.getMatrix(orientation);
                            for(let i: number = 0; i < 3; i++) {{
                                const columnSelect: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, i);
                                const columnI: com.vzome.core.algebra.AlgebraicVector = transform.timesColumn(columnSelect);
                                const colRV: com.vzome.core.math.RealVector = columnI.toRealVector();
                                asFloats[i * 4 + 0] = colRV.x;
                                asFloats[i * 4 + 1] = colRV.y;
                                asFloats[i * 4 + 2] = colRV.z;
                                asFloats[i * 4 + 3] = 0.0;
                            };}
                            asFloats[12] = 0.0;
                            asFloats[13] = 0.0;
                            asFloats[14] = 0.0;
                            asFloats[15] = 1.0;
                            orientations[orientation] = asFloats;
                        };}
                        return orientations;
                    })();
                } else if (rowMajor === undefined) {
                    return <any>this.getOrientations$();
                } else throw new Error('invalid overload');
            }
            /**
             * 
             * @return {*}
             */
            public getSymmetry(): com.vzome.core.math.symmetry.Symmetry {
                return this.symmetrySystem.getSymmetry();
            }

            /**
             * 
             * @return {*}
             */
            public getShapes(): com.vzome.core.editor.api.Shapes {
                return this.shapes;
            }

            /**
             * 
             * @return {com.vzome.core.math.symmetry.OrbitSet}
             */
            public getOrbits(): com.vzome.core.math.symmetry.OrbitSet {
                return this.symmetrySystem.getOrbits();
            }

            /**
             * 
             * @param {com.vzome.core.math.symmetry.Direction} orbit
             * @return {com.vzome.core.construction.Color}
             */
            public getColor(orbit: com.vzome.core.math.symmetry.Direction): com.vzome.core.construction.Color {
                return this.symmetrySystem.getColor(orbit);
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} vector
             * @return {com.vzome.core.math.symmetry.Axis}
             */
            public getAxis(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.symmetry.Axis {
                return this.symmetrySystem.getAxis(vector);
            }

            /**
             * 
             * @return {string}
             */
            public getName(): string {
                return this.symmetrySystem.getName();
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} vector
             * @return {com.vzome.core.construction.Color}
             */
            public getVectorColor(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.construction.Color {
                return this.symmetrySystem.getVectorColor(vector);
            }

            constructor(__parent: any, private symmetrySystem: any, private shapes: any) {
                this.__parent = __parent;
            }
        }
        ReplaceWithShape$0["__interfaces"] = ["com.vzome.core.editor.api.OrbitSource"];


    }

}

