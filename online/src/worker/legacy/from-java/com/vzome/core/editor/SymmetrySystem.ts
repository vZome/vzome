import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AbstractCommand } from "../commands/AbstractCommand.js";
import { Command } from "../commands/Command.js";
import { Color } from "../construction/Color.js";
import { CommandEdit } from "./CommandEdit.js";
import { SymmetryPerspective } from "./SymmetryPerspective.js";
import { ToolsModel } from "./ToolsModel.js";
import { Context } from "./api/Context.js";
import { EditorModel } from "./api/EditorModel.js";
import { OrbitSource } from "./api/OrbitSource.js";
import { Shapes } from "./api/Shapes.js";
import { Polyhedron } from "../math/Polyhedron.js";
import { RealVector } from "../math/RealVector.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { OrbitSet } from "../math/symmetry/OrbitSet.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { Colors } from "../render/Colors.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Document } from "../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../org/w3c/dom/Element.js";
import { Node } from "../../../../org/w3c/dom/Node.js";
import { NodeList } from "../../../../org/w3c/dom/NodeList.js";

export class SymmetrySystem implements OrbitSource {
    /* Default method injected from OrbitSource */
    public getOrientations(rowMajor?: any): number[][] {
        if (((typeof rowMajor === 'boolean') || rowMajor === null)) {
            let __args = arguments;
            if (this.symmetry === undefined) { this.symmetry = null; } 
            if (this.orbits === undefined) { this.orbits = null; } 
            if (this.shapes === undefined) { this.shapes = null; } 
            if (this.symmetryPerspective === undefined) { this.symmetryPerspective = null; } 
            if (this.context === undefined) { this.context = null; } 
            if (this.editor === undefined) { this.editor = null; } 
            if (this.colors === undefined) { this.colors = null; } 
            this.nextNewAxis = 0;
            this.orbitColors = <any>(new java.util.HashMap<any, any>());
            this.vectorToAxis = <any>(new java.util.HashMap<any, any>());
            this.noKnownDirections = false;
            this.toolFactoryLists = <any>(new java.util.HashMap<any, any>());
            this.toolLists = <any>(new java.util.HashMap<any, any>());
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
    static LOGGER: java.util.logging.Logger; public static LOGGER_$LI$(): java.util.logging.Logger { if (SymmetrySystem.LOGGER == null) { SymmetrySystem.LOGGER = java.util.logging.Logger.getLogger("com.vzome.core.editor"); }  return SymmetrySystem.LOGGER; }

    /*private*/ nextNewAxis: number;

    /*private*/ symmetry: Symmetry;

    /*private*/ orbits: OrbitSet;

    /*private*/ orbitColors: java.util.Map<string, Color>;

    /*private*/ shapes: Shapes;

    /*private*/ vectorToAxis: java.util.Map<string, Axis>;

    /*private*/ noKnownDirections: boolean;

    /*private*/ symmetryPerspective: SymmetryPerspective;

    /*private*/ toolFactoryLists: java.util.Map<Tool.Kind, java.util.List<Tool.Factory>>;

    /*private*/ toolLists: java.util.Map<Tool.Kind, java.util.List<Tool>>;

    /*private*/ context: Context;

    /*private*/ editor: EditorModel;

    /*private*/ colors: Colors;

    public constructor(symmXml: Element, symmetryPerspective: SymmetryPerspective, context: Context, colors: Colors, allowNonstandard: boolean) {
        this.nextNewAxis = 0;
        if (this.symmetry === undefined) { this.symmetry = null; }
        if (this.orbits === undefined) { this.orbits = null; }
        this.orbitColors = <any>(new java.util.HashMap<any, any>());
        if (this.shapes === undefined) { this.shapes = null; }
        this.vectorToAxis = <any>(new java.util.HashMap<any, any>());
        this.noKnownDirections = false;
        if (this.symmetryPerspective === undefined) { this.symmetryPerspective = null; }
        this.toolFactoryLists = <any>(new java.util.HashMap<any, any>());
        this.toolLists = <any>(new java.util.HashMap<any, any>());
        if (this.context === undefined) { this.context = null; }
        if (this.editor === undefined) { this.editor = null; }
        if (this.colors === undefined) { this.colors = null; }
        this.symmetryPerspective = symmetryPerspective;
        this.context = context;
        this.colors = colors;
        this.symmetry = symmetryPerspective.getSymmetry();
        let styleName: string = symmetryPerspective.getDefaultGeometry().getName();
        this.orbits = new OrbitSet(this.symmetry);
        if (symmXml == null){
            for(let index=this.symmetry.getOrbitSet().getDirections().iterator();index.hasNext();) {
                let orbit = index.next();
                {
                    if (symmetryPerspective.orbitIsStandard(orbit) || allowNonstandard)this.orbits.add(orbit);
                    const color: Color = colors.getColor(Colors.DIRECTION_$LI$() + orbit.getName());
                    this.orbitColors.put(orbit.getName(), color);
                }
            }
        } else {
            styleName = symmXml.getAttribute("renderingStyle");
            const nodes: NodeList = symmXml.getChildNodes();
            for(let i: number = 0; i < nodes.getLength(); i++) {{
                const node: Node = nodes.item(i);
                if (node != null && (node.constructor != null && node.constructor["__interfaces"] != null && node.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                    const dirElem: Element = <Element><any>node;
                    const name: string = dirElem.getAttribute("name");
                    let orbit: Direction = null;
                    const nums: string = dirElem.getAttribute("prototype");
                    if (nums != null && !/* isEmpty */(nums.length === 0)){
                        try {
                            const prototype: AlgebraicVector = this.symmetry.getField().parseVector(nums);
                            orbit = this.symmetry.createNewZoneOrbit(name, 0, Symmetry.NO_ROTATION, prototype);
                        } catch(e) {
                            if (SymmetrySystem.LOGGER_$LI$().isLoggable(java.util.logging.Level.INFO)){
                                const msg: string = "Integer overflow while recreating automatic orbit: " + name + ". Failed to parseVector(\'" + nums + "\').";
                                SymmetrySystem.LOGGER_$LI$().info(msg);
                            }
                            continue;
                        }
                        orbit.setAutomatic(true);
                        try {
                            const autoNum: number = javaemul.internal.IntegerHelper.parseInt(name);
                            this.nextNewAxis = Math.max(this.nextNewAxis, autoNum + 1);
                        } catch(e) {
                            SymmetrySystem.LOGGER_$LI$().fine(e.message);
                        }
                    } else {
                        orbit = this.symmetry.getDirection(name);
                        if (orbit == null){
                            continue;
                        }
                    }
                    this.orbits.add(orbit);
                    let color: Color = colors.getColor(Colors.DIRECTION_$LI$() + orbit.getCanonicalName());
                    const str: string = dirElem.getAttribute("color");
                    if (str != null && !/* isEmpty */(str.length === 0) && !(str === ("255,255,255"))){
                        color = Color.parseColor(str);
                    }
                    this.orbitColors.put(orbit.getName(), color);
                    this.orbitColors.put(orbit.getCanonicalName(), color);
                }
            };}
            for(let index=this.symmetry.getOrbitSet().getDirections().iterator();index.hasNext();) {
                let orbit = index.next();
                {
                    if (this.orbits.contains(orbit))continue;
                    if (orbit.isStandard() || allowNonstandard)this.orbits.add(orbit);
                    const color: Color = colors.getColor(Colors.DIRECTION_$LI$() + orbit.getCanonicalName());
                    this.orbitColors.put(orbit.getName(), color);
                    this.orbitColors.put(orbit.getCanonicalName(), color);
                }
            }
        }
        this.setStyle(styleName);
    }

    public setEditorModel(editor: EditorModel) {
        this.editor = editor;
    }

    public createToolFactories(tools: ToolsModel) {
        {
            let array = /* Enum.values */function() { let result: Tool.Kind[] = []; for(let val in Tool.Kind) { if (!isNaN(<any>val)) { result.push(parseInt(val,10)); } } return result; }();
            for(let index = 0; index < array.length; index++) {
                let kind = array[index];
                {
                    const list: java.util.List<Tool.Factory> = this.symmetryPerspective.createToolFactories(kind, tools);
                    this.toolFactoryLists.put(kind, list);
                    const toolList: java.util.List<Tool> = this.symmetryPerspective.predefineTools(kind, tools);
                    this.toolLists.put(kind, toolList);
                }
            }
        }
    }

    /**
     * 
     * @return {string}
     */
    public getName(): string {
        return this.symmetry.getName();
    }

    /**
     * 
     * @param {AlgebraicVector} vector
     * @return {Axis}
     */
    public getAxis(vector: AlgebraicVector): Axis {
        if (vector.isOrigin()){
            return null;
        }
        let line: Axis = this.vectorToAxis.get(vector.toString());
        if (line != null)return line;
        if (!this.noKnownDirections){
            line = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_math_symmetry_OrbitSet'](vector, this.orbits);
            if (line != null){
                this.vectorToAxis.put(vector.toString(), line);
                return line;
            }
        }
        const dir: Direction = this.createAnonymousOrbit(vector);
        line = dir.getAxis$com_vzome_core_algebra_AlgebraicVector(vector);
        this.vectorToAxis.put(vector.toString(), line);
        return line;
    }

    public createAnonymousOrbit(vector: AlgebraicVector): Direction {
        const symm: Symmetry = this.orbits.getSymmetry();
        const field: AlgebraicField = symm.getField();
        const longer: AlgebraicNumber = field['createPower$int'](1);
        const shorter: AlgebraicNumber = field['createPower$int'](-1);
        const rv: RealVector = vector.toRealVector();
        let longVector: AlgebraicVector = vector;
        let shortVector: AlgebraicVector = vector;
        let longLen: number = 2.0;
        let shortLen: number = 2.0;
        const len: number = rv.length();
        if (len > 2.0){
            longLen = len;
            longVector = vector;
            while((longLen > 2.0)) {{
                shortVector = longVector.scale(shorter);
                shortLen = shortVector.toRealVector().length();
                if (shortLen <= 2.0)break;
                longLen = shortLen;
                longVector = shortVector;
            }};
        } else {
            shortLen = len;
            shortVector = vector;
            while((shortLen <= 2.0)) {{
                longVector = shortVector.scale(longer);
                longLen = longVector.toRealVector().length();
                if (longLen > 2.0)break;
                shortLen = longLen;
                shortVector = longVector;
            }};
        }
        if ((2.0 / shortLen) > longLen)vector = longVector; else vector = shortVector;
        const colorName: string = "" + this.nextNewAxis++;
        const orbit: Direction = symm.createNewZoneOrbit(colorName, 0, Symmetry.NO_ROTATION, vector);
        orbit.setAutomatic(true);
        this.orbits.add(orbit);
        let color: Color = this.colors.getColor(Colors.DIRECTION_$LI$() + orbit.getCanonicalName());
        if (color == null)color = Color.WHITE_$LI$();
        this.orbitColors.put(orbit.getName(), color);
        this.orbitColors.put(orbit.getCanonicalName(), color);
        return orbit;
    }

    /**
     * 
     * @param {AlgebraicVector} vector
     * @return {Color}
     */
    public getVectorColor(vector: AlgebraicVector): Color {
        if (vector == null || vector.isOrigin()){
            return this.colors.getColor(Colors.CONNECTOR_$LI$());
        }
        let line: Axis = this.vectorToAxis.get(vector.toString());
        if (line == null){
            line = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_math_symmetry_OrbitSet'](vector, this.orbits);
        }
        return (line == null) ? Color.WHITE_$LI$() : this.getColor(line.getDirection());
    }

    /**
     * 
     * @param {Direction} orbit
     * @return {Color}
     */
    public getColor(orbit: Direction): Color {
        if (orbit == null)return this.colors.getColor(Colors.CONNECTOR_$LI$());
        let shapeColor: Color = this.shapes.getColor(orbit);
        if (shapeColor == null)shapeColor = this.orbitColors.get(orbit.getName());
        if (shapeColor == null)return Color.WHITE_$LI$();
        return shapeColor;
    }

    /**
     * 
     * @return {*}
     */
    public getSymmetry(): Symmetry {
        return this.symmetry;
    }

    /**
     * 
     * @return {OrbitSet}
     */
    public getOrbits(): OrbitSet {
        return this.orbits;
    }

    public disableKnownDirection() {
        this.noKnownDirections = true;
    }

    public getRenderingStyle(): Shapes {
        return this.shapes;
    }

    public getXml(doc: Document): Element {
        const result: Element = doc.createElement("SymmetrySystem");
        DomUtils.addAttribute(result, "name", this.getSymmetry().getName());
        DomUtils.addAttribute(result, "renderingStyle", this.shapes.getName());
        for(let index=this.orbits.getDirections().iterator();index.hasNext();) {
            let dir = index.next();
            {
                const dirElem: Element = doc.createElement("Direction");
                if (dir.isAutomatic())DomUtils.addAttribute(dirElem, "prototype", dir.getPrototype().getVectorExpression$int(AlgebraicField.ZOMIC_FORMAT));
                DomUtils.addAttribute(dirElem, "name", dir.getName());
                DomUtils.addAttribute(dirElem, "orbit", dir.getCanonicalName());
                {
                    const color: Color = this.getColor(dir);
                    if (color != null)DomUtils.addAttribute(dirElem, "color", color.toString());
                };
                result.appendChild(dirElem);
            }
        }
        return result;
    }

    public getStyle$java_lang_String(styleName: string): Shapes {
        const found: java.util.Optional<Shapes> = this.symmetryPerspective.getGeometries().stream().filter((e) => (styleName === e.getName()) || (styleName === e.getAlias()) || (styleName === e.getPackage())).findFirst();
        if (found.isPresent())return found.get(); else return null;
    }

    public getStyle(styleName?: any): Shapes {
        if (((typeof styleName === 'string') || styleName === null)) {
            return <any>this.getStyle$java_lang_String(styleName);
        } else if (styleName === undefined) {
            return <any>this.getStyle$();
        } else throw new Error('invalid overload');
    }

    public setStyle(styleName: string) {
        const result: Shapes = this.getStyle$java_lang_String(styleName);
        if (result != null)this.shapes = result; else {
            SymmetrySystem.LOGGER_$LI$().warning("UNKNOWN STYLE NAME: " + styleName);
            this.shapes = this.symmetryPerspective.getDefaultGeometry();
        }
    }

    public getStyleNames(): string[] {
        return this.symmetryPerspective.getGeometries().stream().map<any>((e) => e.getName()).toArray<any>((arg0) => { return new Array<string>(arg0) });
    }

    public getStyle$(): Shapes {
        return this.shapes;
    }

    /**
     * 
     * @return {*}
     */
    public getShapes(): Shapes {
        return this.shapes;
    }

    public getShape$com_vzome_core_algebra_AlgebraicVector(offset: AlgebraicVector): Polyhedron {
        return this.getShape$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_editor_api_Shapes(offset, this.shapes);
    }

    public getShape$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_editor_api_Shapes(offset: AlgebraicVector, shapes: Shapes): Polyhedron {
        if (offset == null)return shapes.getConnectorShape(); else {
            if (offset.isOrigin())return null;
            const axis: Axis = this.getAxis(offset);
            if (axis == null)return null;
            const orbit: Direction = axis.getDirection();
            const len: AlgebraicNumber = axis.getLength(offset);
            return shapes.getStrutShape(orbit, len);
        }
    }

    public getShape(offset?: any, shapes?: any): Polyhedron {
        if (((offset != null && offset instanceof <any>AlgebraicVector) || offset === null) && ((shapes != null && (shapes.constructor != null && shapes.constructor["__interfaces"] != null && shapes.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.Shapes") >= 0)) || shapes === null)) {
            return <any>this.getShape$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_editor_api_Shapes(offset, shapes);
        } else if (((offset != null && offset instanceof <any>AlgebraicVector) || offset === null) && shapes === undefined) {
            return <any>this.getShape$com_vzome_core_algebra_AlgebraicVector(offset);
        } else throw new Error('invalid overload');
    }

    public getToolFactories(kind: Tool.Kind): java.util.List<Tool.Factory> {
        return this.toolFactoryLists.get(kind);
    }

    public getPredefinedTools(kind: Tool.Kind): java.util.List<Tool> {
        return this.toolLists.get(kind);
    }

    public doAction(action: string): boolean {
        const command: Command = this.symmetryPerspective.getLegacyCommand(action);
        if (command != null){
            const edit: CommandEdit = new CommandEdit(<AbstractCommand><any>command, this.editor);
            this.context.performAndRecord(edit);
            return true;
        }
        return false;
    }

    public getModelResourcePath(): string {
        return this.symmetryPerspective.getModelResourcePath();
    }

    public orbitIsStandard(orbit: Direction): boolean {
        return this.symmetryPerspective.orbitIsStandard(orbit);
    }

    public orbitIsBuildDefault(orbit: Direction): boolean {
        return this.symmetryPerspective.orbitIsBuildDefault(orbit);
    }

    public getOrbitUnitLength(orbit: Direction): AlgebraicNumber {
        return this.symmetryPerspective.getOrbitUnitLength(orbit);
    }

    public resetColors() {
        for(let index=this.symmetry.getOrbitSet().getDirections().iterator();index.hasNext();) {
            let orbit = index.next();
            {
                const color: Color = this.colors.getColor(Colors.DIRECTION_$LI$() + orbit.getName());
                this.orbitColors.put(orbit.getName(), color);
            }
        }
    }
}
SymmetrySystem["__class"] = "com.vzome.core.editor.SymmetrySystem";
SymmetrySystem["__interfaces"] = ["com.vzome.core.editor.api.OrbitSource"];
