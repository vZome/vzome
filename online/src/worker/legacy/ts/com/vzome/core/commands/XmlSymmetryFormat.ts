/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    export class XmlSymmetryFormat extends com.vzome.core.commands.XmlSaveFormat {
        static __static_initialized: boolean = false;
        static __static_initialize() { if (!XmlSymmetryFormat.__static_initialized) { XmlSymmetryFormat.__static_initialized = true; XmlSymmetryFormat.__static_initializer_0(); } }

        /*private*/ symmetries: com.vzome.core.math.symmetry.OrbitSet.Field;

        static __com_vzome_core_commands_XmlSymmetryFormat_logger: java.util.logging.Logger; public static __com_vzome_core_commands_XmlSymmetryFormat_logger_$LI$(): java.util.logging.Logger { XmlSymmetryFormat.__static_initialize(); if (XmlSymmetryFormat.__com_vzome_core_commands_XmlSymmetryFormat_logger == null) { XmlSymmetryFormat.__com_vzome_core_commands_XmlSymmetryFormat_logger = java.util.logging.Logger.getLogger("com.vzome.core.commands.XmlSaveFormat"); }  return XmlSymmetryFormat.__com_vzome_core_commands_XmlSymmetryFormat_logger; }

        static  __static_initializer_0() {
            new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/2.0/", [com.vzome.core.commands.XmlSaveFormat.PROJECT_4D, com.vzome.core.commands.XmlSaveFormat.SELECTION_NOT_SAVED]);
            new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/2.0.1/", [com.vzome.core.commands.XmlSaveFormat.PROJECT_4D, com.vzome.core.commands.XmlSaveFormat.SELECTION_NOT_SAVED]);
            new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/2.0.2/", [com.vzome.core.commands.XmlSaveFormat.SELECTION_NOT_SAVED]);
            new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/2.0.3/", [com.vzome.core.commands.XmlSaveFormat.SELECTION_NOT_SAVED]);
            new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/2.1.0/", [com.vzome.core.commands.XmlSaveFormat.SELECTION_NOT_SAVED, com.vzome.core.commands.XmlSaveFormat.FORMAT_2_1_0]);
            new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/3.0.0/", [com.vzome.core.commands.XmlSaveFormat.GROUPING_IN_SELECTION]);
            new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/4.0.0/", [com.vzome.core.commands.XmlSaveFormat.RATIONAL_VECTORS, com.vzome.core.commands.XmlSaveFormat.GROUPING_IN_SELECTION]);
            new XmlSymmetryFormat("http://tns.vorthmann.org/vZome/5.0.0/", [com.vzome.core.commands.XmlSaveFormat.RATIONAL_VECTORS, com.vzome.core.commands.XmlSaveFormat.COMPACTED_COMMAND_EDITS]);
            new XmlSymmetryFormat(com.vzome.core.commands.XmlSaveFormat.CURRENT_FORMAT, [com.vzome.core.commands.XmlSaveFormat.RATIONAL_VECTORS, com.vzome.core.commands.XmlSaveFormat.COMPACTED_COMMAND_EDITS, com.vzome.core.commands.XmlSaveFormat.MULTIPLE_DESIGNS]);
        }

        public static getFormat(namespace: string): XmlSymmetryFormat {
            return <XmlSymmetryFormat>com.vzome.core.commands.XmlSaveFormat.FORMATS_$LI$().get(namespace);
        }

        public initialize$com_vzome_core_algebra_AlgebraicField$com_vzome_core_math_symmetry_OrbitSet_Field$int$java_lang_String$java_util_Properties(field: com.vzome.core.algebra.AlgebraicField, symms: com.vzome.core.math.symmetry.OrbitSet.Field, scale: number, writerVersion: string, props: java.util.Properties) {
            super.initialize(field, scale, writerVersion, props);
            this.symmetries = symms;
        }

        public initialize(field?: any, symms?: any, scale?: any, writerVersion?: any, props?: any) {
            if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((symms != null && (symms.constructor != null && symms.constructor["__interfaces"] != null && symms.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.OrbitSet.Field") >= 0)) || symms === null) && ((typeof scale === 'number') || scale === null) && ((typeof writerVersion === 'string') || writerVersion === null) && ((props != null && props instanceof <any>java.util.Properties) || props === null)) {
                return <any>this.initialize$com_vzome_core_algebra_AlgebraicField$com_vzome_core_math_symmetry_OrbitSet_Field$int$java_lang_String$java_util_Properties(field, symms, scale, writerVersion, props);
            } else if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((typeof symms === 'number') || symms === null) && ((typeof scale === 'string') || scale === null) && ((writerVersion != null && writerVersion instanceof <any>java.util.Properties) || writerVersion === null) && props === undefined) {
                super.initialize(field, symms, scale, writerVersion);
            } else throw new Error('invalid overload');
        }

        public constructor(version: string, capabilities: string[]) {
            super(version, capabilities);
            if (this.symmetries === undefined) { this.symmetries = null; }
        }

        public parseAlgebraicObject(valName: string, val: org.w3c.dom.Element): any {
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

        getQuaternionicSymmetry(name: string): com.vzome.core.math.symmetry.QuaternionicSymmetry {
            return this.symmetries.getQuaternionSet(name);
        }

        public parseSymmetry(sname: string): com.vzome.core.math.symmetry.Symmetry {
            const group: com.vzome.core.math.symmetry.OrbitSet = this.symmetries.getGroup(sname);
            const symm: com.vzome.core.math.symmetry.Symmetry = group.getSymmetry();
            if (symm == null){
                XmlSymmetryFormat.__com_vzome_core_commands_XmlSymmetryFormat_logger_$LI$().severe("UNSUPPORTED symmetry: " + sname);
                throw new java.lang.IllegalStateException("no symmetry with name=" + sname);
            } else return symm;
        }

        public static serializeAxis(xml: org.w3c.dom.Element, symmAttr: string, dirAttr: string, indexAttr: string, senseAttr: string, axis: com.vzome.core.math.symmetry.Axis) {
            let str: string = axis.getDirection().getSymmetry().getName();
            if (!("icosahedral" === str))com.vzome.xml.DomUtils.addAttribute(xml, symmAttr, str);
            str = axis.getDirection().getName();
            if (!("blue" === str))com.vzome.xml.DomUtils.addAttribute(xml, dirAttr, str);
            com.vzome.xml.DomUtils.addAttribute(xml, indexAttr, /* toString */(''+(axis.getOrientation())));
            if (axis.getSense() !== com.vzome.core.math.symmetry.Symmetry.PLUS)com.vzome.xml.DomUtils.addAttribute(xml, "sense", "minus");
            if (!axis.isOutbound())com.vzome.xml.DomUtils.addAttribute(xml, "outbound", "false");
        }

        public parseAxis(xml: org.w3c.dom.Element, symmAttr: string, dirAttr: string, indexAttr: string, senseAttr: string): com.vzome.core.math.symmetry.Axis {
            let sname: string = xml.getAttribute(symmAttr);
            if (sname == null || /* isEmpty */(sname.length === 0))sname = "icosahedral";
            const group: com.vzome.core.math.symmetry.OrbitSet = this.symmetries.getGroup(sname);
            let aname: string = xml.getAttribute(dirAttr);
            if (aname == null || /* isEmpty */(aname.length === 0))aname = "blue"; else if (aname === ("tan"))aname = "sand"; else if (aname === ("spring"))aname = "apple";
            const iname: string = xml.getAttribute(indexAttr);
            const index: number = javaemul.internal.IntegerHelper.parseInt(iname);
            let sense: number = com.vzome.core.math.symmetry.Symmetry.PLUS;
            if ("minus" === xml.getAttribute(senseAttr)){
                sense = com.vzome.core.math.symmetry.Symmetry.MINUS;
            }
            let outbound: boolean = true;
            const outs: string = xml.getAttribute("outbound");
            if (outs != null && (outs === ("false")))outbound = false;
            const dir: com.vzome.core.math.symmetry.Direction = group.getDirection(aname);
            if (dir == null){
                const msg: string = "Unsupported direction \'" + aname + "\' in " + sname + " symmetry";
                XmlSymmetryFormat.__com_vzome_core_commands_XmlSymmetryFormat_logger_$LI$().severe(msg);
                throw new java.lang.IllegalStateException(msg);
            }
            return dir.getAxis$int$int$boolean(sense, index, outbound);
        }
    }
    XmlSymmetryFormat["__class"] = "com.vzome.core.commands.XmlSymmetryFormat";

}


com.vzome.core.commands.XmlSymmetryFormat.__static_initialize();
