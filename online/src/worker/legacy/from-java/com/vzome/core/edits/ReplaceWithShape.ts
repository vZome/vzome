import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { VefVectorExporter } from "../algebra/VefVectorExporter.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Color } from "../construction/Color.js";
import { Construction } from "../construction/Construction.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Polygon } from "../construction/Polygon.js";
import { PolygonFromVertices } from "../construction/PolygonFromVertices.js";
import { Segment } from "../construction/Segment.js";
import { SymmetrySystem } from "../editor/SymmetrySystem.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { OrbitSource } from "../editor/api/OrbitSource.js";
import { Shapes } from "../editor/api/Shapes.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { Polyhedron } from "../math/Polyhedron.js";
import { RealVector } from "../math/RealVector.js";
import { VefToPolyhedron } from "../math/VefToPolyhedron.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { OrbitSet } from "../math/symmetry/OrbitSet.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { HasRenderedObject } from "../model/HasRenderedObject.js";
import { Manifestation } from "../model/Manifestation.js";
import { RenderedObject } from "../model/RenderedObject.js";
import { RenderedManifestation } from "../render/RenderedManifestation.js";
import { RenderedModel } from "../render/RenderedModel.js";
import { Element } from "../../../../org/w3c/dom/Element.js";
import { Node } from "../../../../org/w3c/dom/Node.js";

export class ReplaceWithShape extends ChangeManifestations {
    public static NAME: string = "ReplaceWithShape";

    /*private*/ vef: string;

    /*private*/ shape: Polyhedron;

    /*private*/ ballOrStrut: Manifestation;

    /*private*/ symmetryShapes: string;

    /*private*/ editor: EditorModel;

    /*private*/ replace(man: Manifestation, renderedObject: RenderedObject, shape: Polyhedron) {
        if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0))return;
        if (renderedObject != null){
            const orientation: AlgebraicMatrix = renderedObject.getOrientation();
            const vertexList: java.util.List<AlgebraicVector> = shape.getVertexList();
            for(let index=shape.getFaceSet().iterator();index.hasNext();) {
                let face = index.next();
                {
                    const vertices: Point[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(face.size());
                    for(let i: number = 0; i < vertices.length; i++) {{
                        const vertexIndex: number = face.getVertex(i);
                        const vertex: AlgebraicVector = vertexList.get(vertexIndex);
                        vertices[i] = ReplaceWithShape.transformVertex(vertex, renderedObject.getLocationAV(), orientation);
                    };}
                    const polygon: Polygon = new PolygonFromVertices(vertices);
                    const panel: Manifestation = this.manifestConstruction(polygon);
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
            const symmetrySystem: OrbitSource = (<SymmetryAware><any>this.editor)['getSymmetrySystem$java_lang_String'](tokens[0]);
            const shapes: Shapes = (<SymmetrySystem><any>symmetrySystem).getStyle$java_lang_String(tokens[1]);
            const model: RenderedModel = new RenderedModel(symmetrySystem.getSymmetry().getField(), new ReplaceWithShape.ReplaceWithShape$0(this, symmetrySystem, shapes));
            if (this.ballOrStrut != null){
                for(let index=this.mSelection.iterator();index.hasNext();) {
                    let man = index.next();
                    {
                        this.unselect$com_vzome_core_model_Manifestation(man);
                    }
                }
                this.redo();
                const rm: RenderedManifestation = model.render(this.ballOrStrut);
                this.replace(this.ballOrStrut, rm, rm.getShape());
            } else for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    const rm: RenderedManifestation = model.render(man);
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
            this.replace(this.ballOrStrut, (<HasRenderedObject><any>this.ballOrStrut).getRenderedObject(), this.shape);
        }
        super.perform();
    }

    /*private*/ static transformVertex(vertex: AlgebraicVector, offset: AlgebraicVector, orientation: AlgebraicMatrix): Point {
        if (orientation != null)vertex = orientation.timesColumn(vertex);
        if (offset != null)vertex = vertex.plus(offset);
        return new FreePoint(vertex);
    }

    public constructor(editor: EditorModel) {
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
        const m: Manifestation = <Manifestation><any>props.get("picked");
        if (m != null){
            this.symmetryShapes = (<HasRenderedObject><any>m).getRenderedObject().getSymmetryShapes();
            this.ballOrStrut = m;
        } else this.symmetryShapes = <string>props.get("mode");
    }

    /**
     * 
     * @param {*} element
     */
    getXmlAttributes(element: Element) {
        if (this.ballOrStrut != null){
            const construction: Construction = this.ballOrStrut.getFirstConstruction();
            if (construction != null && construction instanceof <any>Point)XmlSaveFormat.serializePoint(element, "point", <Point>construction); else XmlSaveFormat.serializeSegment(element, "startSegment", "endSegment", <Segment>construction);
        }
        if (this.shape != null){
            if (this.vef == null){
                this.vef = VefVectorExporter.exportPolyhedron(this.shape);
            }
            const textNode: Node = element.getOwnerDocument().createTextNode(XmlSaveFormat.escapeNewlines(this.vef));
            element.appendChild(textNode);
        } else {
            element.setAttribute("shapes", this.symmetryShapes);
        }
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        const attr: string = xml.getAttribute("shapes");
        if (attr != null && !/* isEmpty */(attr.length === 0)){
            this.symmetryShapes = attr;
        } else {
            this.vef = xml.getTextContent();
            this.shape = VefToPolyhedron.importPolyhedron(format.getField(), this.vef);
        }
        let construction: Construction = format.parsePoint$org_w3c_dom_Element$java_lang_String(xml, "point");
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

    export class ReplaceWithShape$0 implements OrbitSource {
        public __parent: any;
        /* Default method injected from OrbitSource */
        public getOrientations(rowMajor?: any): number[][] {
            if (((typeof rowMajor === 'boolean') || rowMajor === null)) {
                let __args = arguments;
                return <any>(() => {
                    const symmetry: Symmetry = this.getSymmetry();
                    const field: AlgebraicField = symmetry.getField();
                    const order: number = symmetry.getChiralOrder();
                    const orientations: number[][] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order);
                    for(let orientation: number = 0; orientation < order; orientation++) {{
                        if (rowMajor){
                            orientations[orientation] = symmetry.getMatrix(orientation).getRowMajorRealElements();
                            continue;
                        }
                        const asFloats: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(16);
                        const transform: AlgebraicMatrix = symmetry.getMatrix(orientation);
                        for(let i: number = 0; i < 3; i++) {{
                            const columnSelect: AlgebraicVector = field.basisVector(3, i);
                            const columnI: AlgebraicVector = transform.timesColumn(columnSelect);
                            const colRV: RealVector = columnI.toRealVector();
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
        /* Default method injected from OrbitSource */
        getOrientations$(): number[][] {
            return this.getOrientations(false);
        }
        /* Default method injected from OrbitSource */
        getZone(orbit: string, orientation: number): Axis {
            return this.getSymmetry().getDirection(orbit).getAxis(Symmetry.PLUS, orientation);
        }
        /* Default method injected from OrbitSource */
        getEmbedding(): number[] {
            const symmetry: Symmetry = this.getSymmetry();
            const field: AlgebraicField = symmetry.getField();
            const embedding: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(16);
            for(let i: number = 0; i < 3; i++) {{
                const columnSelect: AlgebraicVector = field.basisVector(3, i);
                const colRV: RealVector = symmetry.embedInR3(columnSelect);
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
        /**
         * 
         * @return {*}
         */
        public getSymmetry(): Symmetry {
            return this.symmetrySystem.getSymmetry();
        }

        /**
         * 
         * @return {*}
         */
        public getShapes(): Shapes {
            return this.shapes;
        }

        /**
         * 
         * @return {OrbitSet}
         */
        public getOrbits(): OrbitSet {
            return this.symmetrySystem.getOrbits();
        }

        /**
         * 
         * @param {Direction} orbit
         * @return {Color}
         */
        public getColor(orbit: Direction): Color {
            return this.symmetrySystem.getColor(orbit);
        }

        /**
         * 
         * @param {AlgebraicVector} vector
         * @return {Axis}
         */
        public getAxis(vector: AlgebraicVector): Axis {
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
         * @param {AlgebraicVector} vector
         * @return {Color}
         */
        public getVectorColor(vector: AlgebraicVector): Color {
            return this.symmetrySystem.getVectorColor(vector);
        }

        constructor(__parent: any, private symmetrySystem: any, private shapes: any) {
            this.__parent = __parent;
        }
    }
    ReplaceWithShape$0["__interfaces"] = ["com.vzome.core.editor.api.OrbitSource"];


}
