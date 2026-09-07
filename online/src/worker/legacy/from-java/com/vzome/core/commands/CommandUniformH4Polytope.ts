import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Quaternion } from "../algebra/Quaternion.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { CommandTransform } from "./CommandTransform.js";
import { XmlSaveFormat } from "./XmlSaveFormat.js";
import { XmlSymmetryFormat } from "./XmlSymmetryFormat.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { Projection } from "../math/Projection.js";
import { QuaternionProjection } from "../math/QuaternionProjection.js";
import { QuaternionicSymmetry } from "../math/symmetry/QuaternionicSymmetry.js";
import { WythoffConstruction } from "../math/symmetry/WythoffConstruction.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author Scott Vorthmann
 * @param {*} field
 * @param {QuaternionicSymmetry} qsymm
 * @param {number} index
 * @class
 * @extends CommandTransform
 */
export class CommandUniformH4Polytope extends CommandTransform {
    /**
     * 
     * @param {AttributeMap} attributes
     * @param {XmlSaveFormat} format
     */
    public setFixedAttributes(attributes: AttributeMap, format: XmlSaveFormat) {
        super.setFixedAttributes(attributes, format);
        this.field = format.getField();
        this.symm = this.h4Symms.get(this.field.getName());
        if (this.symm == null){
            this.symm = new CommandUniformH4Polytope.H4Symmetry(this.field);
            this.h4Symms.put(this.field.getName(), this.symm);
        }
        this.mRoots = (<XmlSymmetryFormat>format).getQuaternionicSymmetry("H_4").getRoots();
    }

    /*private*/ h4Symms: java.util.Map<string, CommandUniformH4Polytope.H4Symmetry>;

    public static POLYTOPE_INDEX_ATTR_NAME: string = "polytope.index";

    /*private*/ mRoots: Quaternion[];

    /*private*/ mPolytopeIndex: number;

    static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (CommandUniformH4Polytope.logger == null) { CommandUniformH4Polytope.logger = java.util.logging.Logger.getLogger("com.vzome.core.commands.h4polytope"); }  return CommandUniformH4Polytope.logger; }

    /*private*/ field: AlgebraicField;

    /*private*/ symm: CommandUniformH4Polytope.H4Symmetry;

    public constructor(field?: any, qsymm?: any, index?: any) {
        if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((qsymm != null && qsymm instanceof <any>QuaternionicSymmetry) || qsymm === null) && ((typeof index === 'number') || index === null)) {
            let __args = arguments;
            super();
            if (this.mRoots === undefined) { this.mRoots = null; } 
            if (this.field === undefined) { this.field = null; } 
            if (this.symm === undefined) { this.symm = null; } 
            this.h4Symms = <any>(new java.util.HashMap<any, any>());
            this.mPolytopeIndex = -1;
            this.quaternionVector = null;
            this.mPolytopeIndex = index;
            this.field = field;
            this.symm = new CommandUniformH4Polytope.H4Symmetry(field);
            this.mRoots = qsymm.getRoots();
        } else if (field === undefined && qsymm === undefined && index === undefined) {
            let __args = arguments;
            super();
            if (this.mRoots === undefined) { this.mRoots = null; } 
            if (this.field === undefined) { this.field = null; } 
            if (this.symm === undefined) { this.symm = null; } 
            this.h4Symms = <any>(new java.util.HashMap<any, any>());
            this.mPolytopeIndex = -1;
            this.quaternionVector = null;
        } else throw new Error('invalid overload');
    }

    /*private*/ quaternionVector: AlgebraicVector;

    /**
     * Only called when migrating a 2.0 model file.
     * @param {AlgebraicVector} offset
     */
    public setQuaternion(offset: AlgebraicVector) {
        this.quaternionVector = offset;
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     * @return {AttributeMap}
     */
    public setXml(xml: Element, format: XmlSaveFormat): AttributeMap {
        const attrs: AttributeMap = super.setXml(xml, format);
        this.quaternionVector = format.parseRationalVector(xml, "quaternion");
        return attrs;
    }

    /**
     * 
     * @param {*} result
     * @param {AttributeMap} attributes
     */
    public getXml(result: Element, attributes: AttributeMap) {
        if (this.quaternionVector != null)DomUtils.addAttribute(result, "quaternion", this.quaternionVector.toParsableString());
        super.getXml(result, attributes);
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandTransform.GROUP_ATTR_SIGNATURE_$LI$();
    }

    /**
     * 
     * @param {string} attrName
     * @return {boolean}
     */
    public attributeIs3D(attrName: string): boolean {
        if ("symmetry.axis.segment" === attrName)return false; else return true;
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attributes
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attributes: AttributeMap, effects: ConstructionChanges): ConstructionList {
        const SCALE_DOWN_5: AlgebraicNumber = this.field['createPower$int'](-5);
        let proj: Projection = new Projection.Default(this.field);
        let leftQuat: AlgebraicVector = null;
        let rightQuat: AlgebraicVector = null;
        if (parameters.size() === 0){
            rightQuat = this.quaternionVector;
            const symmAxis: Segment = <Segment>attributes.get(CommandTransform.SYMMETRY_AXIS_ATTR_NAME);
            if (rightQuat == null)rightQuat = (symmAxis == null) ? null : symmAxis.getOffset();
            if (rightQuat != null)rightQuat = rightQuat.scale(SCALE_DOWN_5);
        } else {
            let numSegs: number = 0;
            for(let index=parameters.iterator();index.hasNext();) {
                let cons = index.next();
                {
                    if (cons != null && cons instanceof <any>Segment){
                        const seg: Segment = <Segment>cons;
                        if (++numSegs === 1)rightQuat = seg.getOffset().scale(SCALE_DOWN_5); else if (numSegs === 2)leftQuat = seg.getOffset().scale(SCALE_DOWN_5); else throw new Command.Failure("Too many struts to specify quaternion multiplication.");
                    }
                }
            }
        }
        if (rightQuat != null)proj = new QuaternionProjection(this.field, leftQuat, rightQuat);
        if (this.mPolytopeIndex < 0){
            const indexObj: number = <number>attributes.get(CommandUniformH4Polytope.POLYTOPE_INDEX_ATTR_NAME);
            this.mPolytopeIndex = indexObj;
        } else attributes.put(CommandUniformH4Polytope.POLYTOPE_INDEX_ATTR_NAME, this.mPolytopeIndex);
        this.generate(this.mPolytopeIndex, this.mPolytopeIndex, null, new CommandUniformH4Polytope.ConstructionChangesAdapter(effects, proj, this.field['createPower$int'](5)));
        return new ConstructionList();
    }

    public generate(index: number, renderEdges: number, edgeScales: AlgebraicNumber[], listener: WythoffConstruction.Listener) {
        const reflections: AlgebraicVector[] = [null, null, null, null];
        let prototype: AlgebraicVector = this.symm.getPrototype(index);
        if (edgeScales != null){
            prototype = this.field.origin(4);
            for(let b: number = 0; b < 4; b++) {{
                const mask: number = 1 << b;
                const test: number = index & mask;
                if (test !== 0){
                    const contribution: AlgebraicVector = this.symm.getCoRoot(b).scale(edgeScales[b]);
                    prototype = prototype.plus(contribution);
                }
            };}
        }
        for(let mirror: number = 0; mirror < 4; mirror++) {if ((renderEdges & (1 << mirror)) !== 0)reflections[mirror] = this.symm.reflect(mirror, prototype);;}
        for(let index1 = 0; index1 < this.mRoots.length; index1++) {
            let outerRoot = this.mRoots[index1];
            {
                for(let index2 = 0; index2 < this.mRoots.length; index2++) {
                    let innerRoot = this.mRoots[index2];
                    {
                        let vertex: AlgebraicVector = outerRoot.rightMultiply(prototype);
                        vertex = innerRoot.leftMultiply(vertex);
                        const p1: any = listener.addVertex(vertex);
                        for(let mirror: number = 0; mirror < 4; mirror++) {{
                            if (reflections[mirror] != null){
                                let other: AlgebraicVector = outerRoot.rightMultiply(reflections[mirror]);
                                other = innerRoot.leftMultiply(other);
                                if (!other.equals(vertex)){
                                    const p2: any = listener.addVertex(other);
                                    listener.addEdge(p1, p2);
                                }
                            }
                        };}
                    }
                }
            }
        }
    }
}
CommandUniformH4Polytope["__class"] = "com.vzome.core.commands.CommandUniformH4Polytope";
CommandUniformH4Polytope["__interfaces"] = ["com.vzome.core.commands.Command"];



export namespace CommandUniformH4Polytope {

    export class H4Symmetry {
        mPrototypes: AlgebraicVector[];

        mMirrors: Quaternion[];

        coRoots: AlgebraicVector[];

        public constructor(field: AlgebraicField) {
            this.mPrototypes = (s => { let a=[]; while(s-->0) a.push(null); return a; })(15);
            this.mMirrors = [null, null, null, null];
            this.coRoots = [null, null, null, null];
            const ONE: AlgebraicNumber = field['createRational$long'](1);
            const NEG_ONE: AlgebraicNumber = field['createRational$long'](-1);
            const TWO: AlgebraicNumber = field['createRational$long'](2);
            const A: AlgebraicNumber = field['createAlgebraicNumber$int$int$int$int'](1, -1, 1, 0);
            const B: AlgebraicNumber = field['createAlgebraicNumber$int$int$int$int'](0, 1, 1, 0);
            let temp: AlgebraicVector = field.origin(4);
            temp.setComponent(1, A.dividedBy(TWO));
            temp.setComponent(2, ONE.dividedBy(TWO));
            temp.setComponent(3, B.dividedBy(TWO));
            this.mMirrors[3] = new Quaternion(field, temp);
            temp = field.origin(4);
            temp.setComponent(3, NEG_ONE);
            this.mMirrors[2] = new Quaternion(field, temp);
            temp = field.origin(4);
            temp.setComponent(1, ONE.dividedBy(TWO));
            temp.setComponent(2, NEG_ONE.dividedBy(TWO));
            temp.setComponent(3, ONE.dividedBy(TWO));
            temp.setComponent(0, NEG_ONE.dividedBy(TWO));
            this.mMirrors[1] = new Quaternion(field, temp);
            temp = field.origin(4);
            temp.setComponent(0, ONE);
            this.mMirrors[0] = new Quaternion(field, temp);
            const B2: AlgebraicNumber = field['createAlgebraicNumber$int$int$int$int'](0, 2, 1, 0);
            this.coRoots[3] = field.origin(4);
            this.coRoots[3].setComponent(1, B2);
            this.coRoots[3].setComponent(2, B2);
            this.coRoots[2] = field.origin(4);
            this.coRoots[2].setComponent(1, B2['plus$com_vzome_core_algebra_AlgebraicNumber'](ONE));
            this.coRoots[2].setComponent(2, B['plus$com_vzome_core_algebra_AlgebraicNumber'](TWO));
            this.coRoots[2].setComponent(3, A);
            this.coRoots[1] = field.origin(4);
            this.coRoots[1].setComponent(1, B2);
            this.coRoots[1].setComponent(2, TWO);
            this.coRoots[0] = field.origin(4);
            this.coRoots[0].setComponent(1, B);
            this.coRoots[0].setComponent(2, ONE);
            this.coRoots[0].setComponent(0, A.negate());
            if (CommandUniformH4Polytope.logger_$LI$().isLoggable(java.util.logging.Level.FINE))for(let i: number = 0; i < 4; i++) {{
                const buf: java.lang.StringBuffer = new java.lang.StringBuffer();
                this.coRoots[i].getVectorExpression$java_lang_StringBuffer$int(buf, AlgebraicField.DEFAULT_FORMAT);
                CommandUniformH4Polytope.logger_$LI$().fine(buf.toString());
            };}
            const origin: AlgebraicVector = field.origin(4);
            for(let index: number = 1; index <= 15; index++) {{
                let vertex: AlgebraicVector = origin;
                for(let b: number = 0; b < 4; b++) {{
                    const mask: number = 1 << b;
                    const test: number = index & mask;
                    if (test !== 0){
                        vertex = vertex.plus(this.coRoots[b]);
                    }
                };}
                this.mPrototypes[index - 1] = vertex;
            };}
        }

        public getPrototype(index: number): AlgebraicVector {
            return this.mPrototypes[index - 1];
        }

        public reflect(mirror: number, prototype: AlgebraicVector): AlgebraicVector {
            return this.mMirrors[mirror].reflect(prototype);
        }

        public getCoRoot(i: number): AlgebraicVector {
            return this.coRoots[i];
        }
    }
    H4Symmetry["__class"] = "com.vzome.core.commands.CommandUniformH4Polytope.H4Symmetry";


    export class ConstructionChangesAdapter implements WythoffConstruction.Listener {
        vertices: java.util.Map<AlgebraicVector, Point>;

        effects: ConstructionChanges;

        proj: Projection;

        scale: AlgebraicNumber;

        edges: java.util.Set<CommandUniformH4Polytope.Edge>;

        constructor(effects: ConstructionChanges, proj: Projection, scale: AlgebraicNumber) {
            this.vertices = <any>(new java.util.HashMap<any, any>());
            if (this.effects === undefined) { this.effects = null; }
            if (this.proj === undefined) { this.proj = null; }
            if (this.scale === undefined) { this.scale = null; }
            this.edges = <any>(new java.util.HashSet<any>());
            this.effects = effects;
            this.proj = proj;
            this.scale = scale;
        }

        /**
         * 
         * @param {*} v1
         * @param {*} v2
         * @return {*}
         */
        public addEdge(v1: any, v2: any): any {
            const p1: Point = <Point>v1;
            const p2: Point = <Point>v2;
            const edge: CommandUniformH4Polytope.Edge = new CommandUniformH4Polytope.Edge(p1.getIndex(), p2.getIndex());
            if (this.edges.contains(edge))return null;
            this.edges.add(edge);
            this.effects['constructionAdded$com_vzome_core_construction_Construction'](new SegmentJoiningPoints(p1, p2));
            return edge;
        }

        /**
         * 
         * @param {java.lang.Object[]} vertices
         * @return {*}
         */
        public addFace(vertices: any[]): any {
            return null;
        }

        /**
         * 
         * @param {AlgebraicVector} vertex
         * @return {*}
         */
        public addVertex(vertex: AlgebraicVector): any {
            let p: Point = this.vertices.get(vertex);
            if (p == null){
                let projected: AlgebraicVector = vertex;
                CommandUniformH4Polytope.logger_$LI$().finer("before   : ");
                this.printGoldenVector(projected);
                if (this.proj != null)projected = this.proj.projectImage(projected, true);
                CommandUniformH4Polytope.logger_$LI$().finer("projected: ");
                this.printGoldenVector(projected);
                projected = projected.scale(this.scale);
                CommandUniformH4Polytope.logger_$LI$().finer("scaled   : ");
                this.printGoldenVector(projected);
                p = new FreePoint(projected);
                p.setIndex(this.vertices.size());
                this.effects['constructionAdded$com_vzome_core_construction_Construction'](p);
                this.vertices.put(vertex, p);
            }
            return p;
        }

        printGoldenVector(gv: AlgebraicVector) {
            if (CommandUniformH4Polytope.logger_$LI$().isLoggable(java.util.logging.Level.FINER)){
                const buf: java.lang.StringBuffer = new java.lang.StringBuffer();
                gv.getVectorExpression$java_lang_StringBuffer$int(buf, AlgebraicField.DEFAULT_FORMAT);
                CommandUniformH4Polytope.logger_$LI$().finer(buf.toString());
            }
        }
    }
    ConstructionChangesAdapter["__class"] = "com.vzome.core.commands.CommandUniformH4Polytope.ConstructionChangesAdapter";
    ConstructionChangesAdapter["__interfaces"] = ["com.vzome.core.math.symmetry.WythoffConstruction.Listener"];



    export class Edge {
        p1: number;

        p2: number;

        public constructor(p1: number, p2: number) {
            if (this.p1 === undefined) { this.p1 = 0; }
            if (this.p2 === undefined) { this.p2 = 0; }
            this.p1 = p1;
            this.p2 = p2;
        }

        /**
         * 
         * @param {*} obj
         * @return {boolean}
         */
        public equals(obj: any): boolean {
            if (!(obj != null && obj instanceof <any>CommandUniformH4Polytope.Edge))return false;
            const that: CommandUniformH4Polytope.Edge = <CommandUniformH4Polytope.Edge>obj;
            if (this.p1 === that.p1 && this.p2 === that.p2)return true;
            if (this.p1 === that.p2 && this.p2 === that.p1)return true;
            return false;
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            return this.p1 ^ this.p2;
        }
    }
    Edge["__class"] = "com.vzome.core.commands.CommandUniformH4Polytope.Edge";

}
