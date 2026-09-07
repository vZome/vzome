import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { XmlSaveFormat } from "./XmlSaveFormat.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { OrbitSet } from "../math/symmetry/OrbitSet.js";
import { QuaternionicSymmetry } from "../math/symmetry/QuaternionicSymmetry.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Properties } from "../../../../java/util/Properties.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class XmlSymmetryFormat extends XmlSaveFormat {
    static __static_initialized: boolean = false;
    static __static_initialize() { if (!XmlSymmetryFormat.__static_initialized) { XmlSymmetryFormat.__static_initialized = true; XmlSymmetryFormat.__static_initializer_0(); } }

    /*private*/ symmetries: OrbitSet.Field;

    static __com_vzome_core_commands_XmlSymmetryFormat_logger: java.util.logging.Logger; public static __com_vzome_core_commands_XmlSymmetryFormat_logger_$LI$(): java.util.logging.Logger { XmlSymmetryFormat.__static_initialize(); if (XmlSymmetryFormat.__com_vzome_core_commands_XmlSymmetryFormat_logger == null) { XmlSymmetryFormat.__com_vzome_core_commands_XmlSymmetryFormat_logger = java.util.logging.Logger.getLogger("com.vzome.core.commands.XmlSaveFormat"); }  return XmlSymmetryFormat.__com_vzome_core_commands_XmlSymmetryFormat_logger; }

    static  __static_initializer_0() {
        new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/2.0/", [XmlSaveFormat.PROJECT_4D, XmlSaveFormat.SELECTION_NOT_SAVED]);
        new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/2.0.1/", [XmlSaveFormat.PROJECT_4D, XmlSaveFormat.SELECTION_NOT_SAVED]);
        new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/2.0.2/", [XmlSaveFormat.SELECTION_NOT_SAVED]);
        new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/2.0.3/", [XmlSaveFormat.SELECTION_NOT_SAVED]);
        new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/2.1.0/", [XmlSaveFormat.SELECTION_NOT_SAVED, XmlSaveFormat.FORMAT_2_1_0]);
        new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/3.0.0/", [XmlSaveFormat.GROUPING_IN_SELECTION]);
        new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/4.0.0/", [XmlSaveFormat.RATIONAL_VECTORS, XmlSaveFormat.GROUPING_IN_SELECTION]);
        new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/5.0.0/", [XmlSaveFormat.RATIONAL_VECTORS, XmlSaveFormat.COMPACTED_COMMAND_EDITS]);
        new XmlSymmetryFormat(XmlSaveFormat.CURRENT_FORMAT, [XmlSaveFormat.RATIONAL_VECTORS, XmlSaveFormat.COMPACTED_COMMAND_EDITS, XmlSaveFormat.MULTIPLE_DESIGNS]);
    }

    public static getFormat(namespace: string): XmlSymmetryFormat {
        return <XmlSymmetryFormat>XmlSaveFormat.FORMATS_$LI$().get(namespace);
    }

    public initialize$com_vzome_core_algebra_AlgebraicField$com_vzome_core_math_symmetry_OrbitSet_Field$int$java_lang_String$java_util_Properties(field: AlgebraicField, symms: OrbitSet.Field, scale: number, writerVersion: string, props: Properties) {
        super.initialize(field, scale, writerVersion, props);
        this.symmetries = symms;
    }

    public initialize(field?: any, symms?: any, scale?: any, writerVersion?: any, props?: any) {
        if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((symms != null && (symms.constructor != null && symms.constructor["__interfaces"] != null && symms.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.OrbitSet.Field") >= 0)) || symms === null) && ((typeof scale === 'number') || scale === null) && ((typeof writerVersion === 'string') || writerVersion === null) && ((props != null && props instanceof <any>Properties) || props === null)) {
            return <any>this.initialize$com_vzome_core_algebra_AlgebraicField$com_vzome_core_math_symmetry_OrbitSet_Field$int$java_lang_String$java_util_Properties(field, symms, scale, writerVersion, props);
        } else if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((typeof symms === 'number') || symms === null) && ((typeof scale === 'string') || scale === null) && ((writerVersion != null && writerVersion instanceof <any>Properties) || writerVersion === null) && props === undefined) {
            super.initialize(field, symms, scale, writerVersion);
        } else throw new Error('invalid overload');
    }

    public constructor(version: string, capabilities: string[]) {
        super(version, capabilities);
        if (this.symmetries === undefined) { this.symmetries = null; }
    }

    public parseAlgebraicObject(valName: string, val: Element): any {
        if (valName === ("Symmetry")){
            const name: string = val.getAttribute("name");
            return this.parseSymmetry(name);
        } else if (valName === ("QuaternionicSymmetry")){
            const name: string = val.getAttribute("name");
            return this.getQuaternionicSymmetry(name);
        } else if (valName === ("Axis"))return this.parseAxis(val, "symm", "dir", "index", "sense"); else {
            return super.parseAlgebraicObject(valName, val);
        }
    }

    getQuaternionicSymmetry(name: string): QuaternionicSymmetry {
        return this.symmetries.getQuaternionSet(name);
    }

    public parseSymmetry(sname: string): Symmetry {
        const group: OrbitSet = this.symmetries.getGroup(sname);
        const symm: Symmetry = group.getSymmetry();
        if (symm == null){
            XmlSymmetryFormat.__com_vzome_core_commands_XmlSymmetryFormat_logger_$LI$().severe("UNSUPPORTED symmetry: " + sname);
            throw new java.lang.IllegalStateException("no symmetry with name=" + sname);
        } else return symm;
    }

    public static serializeAxis(xml: Element, symmAttr: string, dirAttr: string, indexAttr: string, senseAttr: string, axis: Axis) {
        let str: string = axis.getDirection().getSymmetry().getName();
        if (!("icosahedral" === str))DomUtils.addAttribute(xml, symmAttr, str);
        str = axis.getDirection().getName();
        if (!("blue" === str))DomUtils.addAttribute(xml, dirAttr, str);
        DomUtils.addAttribute(xml, indexAttr, /* toString */(''+(axis.getOrientation())));
        if (axis.getSense() !== Symmetry.PLUS)DomUtils.addAttribute(xml, "sense", "minus");
        if (!axis.isOutbound())DomUtils.addAttribute(xml, "outbound", "false");
    }

    public parseAxis(xml: Element, symmAttr: string, dirAttr: string, indexAttr: string, senseAttr: string): Axis {
        let sname: string = xml.getAttribute(symmAttr);
        if (sname == null || /* isEmpty */(sname.length === 0))sname = "icosahedral";
        const group: OrbitSet = this.symmetries.getGroup(sname);
        let aname: string = xml.getAttribute(dirAttr);
        if (aname == null || /* isEmpty */(aname.length === 0))aname = "blue"; else if (aname === ("tan"))aname = "sand"; else if (aname === ("spring"))aname = "apple";
        const iname: string = xml.getAttribute(indexAttr);
        const index: number = javaemul.internal.IntegerHelper.parseInt(iname);
        let sense: number = Symmetry.PLUS;
        if ("minus" === xml.getAttribute(senseAttr)){
            sense = Symmetry.MINUS;
        }
        let outbound: boolean = true;
        const outs: string = xml.getAttribute("outbound");
        if (outs != null && (outs === ("false")))outbound = false;
        const dir: Direction = group.getDirection(aname);
        if (dir == null){
            const msg: string = "Unsupported direction \'" + aname + "\' in " + sname + " symmetry";
            XmlSymmetryFormat.__com_vzome_core_commands_XmlSymmetryFormat_logger_$LI$().severe(msg);
            throw new java.lang.IllegalStateException(msg);
        }
        return dir.getAxis$int$int$boolean(sense, index, outbound);
    }
}
XmlSymmetryFormat["__class"] = "com.vzome.core.commands.XmlSymmetryFormat";

//  Run the Java static initializer eagerly at module load, as the monolithic
//  bundle did.  The lazy _$LI$ accessors are not enough for these classes:
//  e.g. XmlSymmetryFormat registers every format in FORMATS here, and
//  getFormat() reads that map without touching any accessor.
XmlSymmetryFormat.__static_initialize();
