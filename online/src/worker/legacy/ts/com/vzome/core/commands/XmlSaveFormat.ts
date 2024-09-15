/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    export class XmlSaveFormat {
        mProject4d: boolean;

        mSelectionNotSaved: boolean;

        mRationalVectors: boolean;

        mGroupingInSelection: boolean;

        /*private*/ mField: com.vzome.core.algebra.AlgebraicField;

        /*private*/ mScale: number;

        /*private*/ mMultiplier: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ writerVersion: string;

        /*private*/ version: string;

        /*private*/ capabilities: java.util.Set<string>;

        /*private*/ properties: java.util.Properties;

        public static CURRENT_FORMAT: string = "http://xml.vzome.com/vZome/4.0.0/";

        static PROJECT_4D: string = "project-4D-to-3D";

        static SELECTION_NOT_SAVED: string = "selection-not-saved";

        static FORMAT_2_1_0: string = "interim-210-format";

        static GROUPING_IN_SELECTION: string = "grouping-in-selection";

        static RATIONAL_VECTORS: string = "rational-vectors";

        static COMPACTED_COMMAND_EDITS: string = "compacted-command-edits";

        static MULTIPLE_DESIGNS: string = "multiple-designs";

        static FORMATS: java.util.Map<string, XmlSaveFormat>; public static FORMATS_$LI$(): java.util.Map<string, XmlSaveFormat> { if (XmlSaveFormat.FORMATS == null) { XmlSaveFormat.FORMATS = <any>(new java.util.HashMap<any, any>()); }  return XmlSaveFormat.FORMATS; }

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (XmlSaveFormat.logger == null) { XmlSaveFormat.logger = java.util.logging.Logger.getLogger("com.vzome.core.commands.XmlSaveFormat"); }  return XmlSaveFormat.logger; }

        public static UNKNOWN_COMMAND: string = "unknown.command";

        /**
         * Initialize.
         * 
         * If you're tempted to add another parameter, see if you can make it a property instead.
         * 
         * Shouldn't we just replace all the parameters with one Controller object?
         * 
         * @param root
         * @param {*} field
         * @param symms
         * @param {number} scale
         * @param {string} writerVersion
         * @param {java.util.Properties} props
         */
        public initialize(field: com.vzome.core.algebra.AlgebraicField, scale: number, writerVersion: string, props: java.util.Properties) {
            this.properties = props;
            this.writerVersion = writerVersion;
            if ((writerVersion == null) || /* isEmpty */(writerVersion.length === 0))this.writerVersion = "before 2.1 Beta 7";
            this.mField = field;
            this.mScale = scale;
            if (scale === 0)this.mMultiplier = null; else this.mMultiplier = field['createPower$int'](scale);
        }

        constructor(version: string, capabilities: string[]) {
            if (this.mProject4d === undefined) { this.mProject4d = false; }
            if (this.mSelectionNotSaved === undefined) { this.mSelectionNotSaved = false; }
            if (this.mRationalVectors === undefined) { this.mRationalVectors = false; }
            if (this.mGroupingInSelection === undefined) { this.mGroupingInSelection = false; }
            if (this.mField === undefined) { this.mField = null; }
            if (this.mScale === undefined) { this.mScale = 0; }
            if (this.mMultiplier === undefined) { this.mMultiplier = null; }
            if (this.writerVersion === undefined) { this.writerVersion = null; }
            if (this.version === undefined) { this.version = null; }
            this.capabilities = <any>(new java.util.HashSet<any>());
            if (this.properties === undefined) { this.properties = null; }
            this.version = version;
            this.capabilities.addAll(java.util.Arrays.asList<any>(capabilities));
            this.mProject4d = this.capabilities.contains(XmlSaveFormat.PROJECT_4D);
            this.mSelectionNotSaved = this.capabilities.contains(XmlSaveFormat.SELECTION_NOT_SAVED);
            this.mRationalVectors = this.capabilities.contains(XmlSaveFormat.RATIONAL_VECTORS);
            this.mGroupingInSelection = this.capabilities.contains(XmlSaveFormat.GROUPING_IN_SELECTION);
            XmlSaveFormat.FORMATS_$LI$().put(version, this);
        }

        getVersion(): string {
            return this.version;
        }

        public isMigration(): boolean {
            return !this.multipleDesigns();
        }

        public selectionsNotSaved(): boolean {
            return this.mSelectionNotSaved;
        }

        public rationalVectors(): boolean {
            return this.mRationalVectors;
        }

        public actionHistory(): boolean {
            return false;
        }

        public commandEditsCompacted(): boolean {
            return this.capabilities.contains(XmlSaveFormat.COMPACTED_COMMAND_EDITS);
        }

        public multipleDesigns(): boolean {
            return this.capabilities.contains(XmlSaveFormat.MULTIPLE_DESIGNS);
        }

        public groupingDoneInSelection(): boolean {
            return this.mGroupingInSelection && !("2.1.3" === this.writerVersion);
        }

        public groupingRecursive(): boolean {
            return !this.groupingDoneInSelection() || ("2.1.2" === this.writerVersion);
        }

        public interim210format(): boolean {
            return this.capabilities.contains(XmlSaveFormat.FORMAT_2_1_0);
        }

        parseAlgebraicVector(elem: org.w3c.dom.Element): com.vzome.core.algebra.AlgebraicVector {
            let val: string = elem.getAttribute("x");
            const x: com.vzome.core.algebra.AlgebraicNumber = (val == null || /* isEmpty */(val.length === 0)) ? this.mField.zero() : this.mField.parseLegacyNumber(val);
            val = elem.getAttribute("y");
            const y: com.vzome.core.algebra.AlgebraicNumber = (val == null || /* isEmpty */(val.length === 0)) ? this.mField.zero() : this.mField.parseLegacyNumber(val);
            val = elem.getAttribute("z");
            const z: com.vzome.core.algebra.AlgebraicNumber = (val == null || /* isEmpty */(val.length === 0)) ? this.mField.zero() : this.mField.parseLegacyNumber(val);
            val = elem.getAttribute("w");
            const threeD: boolean = val == null || /* isEmpty */(val.length === 0);
            let w: com.vzome.core.algebra.AlgebraicNumber = null;
            if (!threeD)w = this.mField.parseLegacyNumber(val);
            let value: com.vzome.core.algebra.AlgebraicVector = threeD ? new com.vzome.core.algebra.AlgebraicVector(x, y, z) : new com.vzome.core.algebra.AlgebraicVector(w, x, y, z);
            if (this.mProject4d && !threeD){
                if (!w.isZero() && XmlSaveFormat.logger_$LI$().isLoggable(java.util.logging.Level.WARNING))XmlSaveFormat.logger_$LI$().warning("stripping non-zero W component from " + value.toString());
                value = this.mField.projectTo3d(value, true);
            }
            if (this.mMultiplier != null)value = value.scale(this.mMultiplier);
            return value;
        }

        public static NOT_AN_ATTRIBUTE: any; public static NOT_AN_ATTRIBUTE_$LI$(): any { if (XmlSaveFormat.NOT_AN_ATTRIBUTE == null) { XmlSaveFormat.NOT_AN_ATTRIBUTE = <any>new Object(); }  return XmlSaveFormat.NOT_AN_ATTRIBUTE; }

        public parseAlgebraicObject(valName: string, val: org.w3c.dom.Element): any {
            let value: any = XmlSaveFormat.NOT_AN_ATTRIBUTE_$LI$();
            if (valName === ("Null"))value = null; else if (valName === ("RationalVector")){
                const nums: string = val.getAttribute("nums");
                if (nums == null || /* isEmpty */(nums.length === 0))return this.mField.origin(3);
                const denoms: string = val.getAttribute("denoms");
                let tokens: java.util.StringTokenizer = new java.util.StringTokenizer(nums);
                const result: number[] = [0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1];
                for(let i: number = 0; i < this.mField.getOrder(); i++) {{
                    const token: string = tokens.nextToken();
                    if (token == null)throw new java.lang.IllegalStateException("RationalVector nums too short for field " + this.mField.getName());
                    result[i * 2] = javaemul.internal.IntegerHelper.parseInt(token);
                };}
                if (denoms != null && !/* isEmpty */(denoms.length === 0)){
                    tokens = new java.util.StringTokenizer(denoms);
                    for(let i: number = 0; i < this.mField.getOrder(); i++) {{
                        const token: string = tokens.nextToken();
                        if (token == null)throw new java.lang.IllegalStateException("RationalVector denoms too short for field " + this.mField.getName());
                        result[i * 2 + 1] = javaemul.internal.IntegerHelper.parseInt(token);
                    };}
                }
                const oneThirdLen: number = (result.length / 3|0);
                const twoThirdLen: number = oneThirdLen * 2;
                const result3d: number[][] = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return 0; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([3, oneThirdLen]);
                for(let i: number = 0; i < oneThirdLen; i++) {{
                    result3d[0][i] = result[i];
                    result3d[1][i] = result[i + oneThirdLen];
                    result3d[2][i] = result[i + twoThirdLen];
                };}
                value = this.mField.createVector(result3d);
            } else if (valName === ("GoldenVector"))value = this.parseAlgebraicVector(val); else if (valName === ("Boolean")){
                const gnum: string = val.getAttribute("value");
                value = javaemul.internal.BooleanHelper.parseBoolean(gnum);
            } else if (valName === ("Integer")){
                const gnum: string = val.getAttribute("value");
                value = javaemul.internal.IntegerHelper.parseInt(gnum);
            } else if ((valName === ("GoldenNumber")) || (valName === ("IntegralNumber"))){
                const gnum: string = val.getAttribute("value");
                value = this.mField.parseLegacyNumber(gnum);
                if (this.mMultiplier != null)value = (<com.vzome.core.algebra.AlgebraicNumber><any>value)['times$com_vzome_core_algebra_AlgebraicNumber'](this.mMultiplier);
            } else if (valName === ("String"))value = val.getTextContent();
            return value;
        }

        public parseConstruction$java_lang_String$org_w3c_dom_Element(apName: string, attrOrParam: org.w3c.dom.Element): com.vzome.core.construction.Construction {
            return this.parseConstruction$java_lang_String$org_w3c_dom_Element$boolean(apName, attrOrParam, false);
        }

        public parseConstruction$java_lang_String$org_w3c_dom_Element$boolean(apName: string, attrOrParam: org.w3c.dom.Element, projectTo3d: boolean): com.vzome.core.construction.Construction {
            let c: com.vzome.core.construction.Construction = null;
            if (apName === ("point")){
                let loc: com.vzome.core.algebra.AlgebraicVector = this.parseAlgebraicVector(attrOrParam);
                if (projectTo3d)loc = loc.projectTo3d(true);
                c = new com.vzome.core.construction.FreePoint(loc);
            } else if (apName === ("segment")){
                const start: org.w3c.dom.Element = com.vzome.xml.DomUtils.getFirstChildElement$org_w3c_dom_Element$java_lang_String(attrOrParam, "start");
                const end: org.w3c.dom.Element = com.vzome.xml.DomUtils.getFirstChildElement$org_w3c_dom_Element$java_lang_String(attrOrParam, "end");
                let sloc: com.vzome.core.algebra.AlgebraicVector = this.parseAlgebraicVector(start);
                let eloc: com.vzome.core.algebra.AlgebraicVector = this.parseAlgebraicVector(end);
                if (projectTo3d){
                    sloc = sloc.projectTo3d(true);
                    eloc = eloc.projectTo3d(true);
                }
                c = new com.vzome.core.construction.SegmentJoiningPoints(new com.vzome.core.construction.FreePoint(sloc), new com.vzome.core.construction.FreePoint(eloc));
            } else if (apName === ("polygon")){
                const kids: org.w3c.dom.NodeList = attrOrParam.getElementsByTagName("vertex");
                const pts: com.vzome.core.construction.Point[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(kids.getLength());
                for(let k: number = 0; k < kids.getLength(); k++) {{
                    let loc: com.vzome.core.algebra.AlgebraicVector = this.parseAlgebraicVector(<org.w3c.dom.Element><any>kids.item(k));
                    if (projectTo3d)loc = loc.projectTo3d(true);
                    pts[k] = new com.vzome.core.construction.FreePoint(loc);
                };}
                c = new com.vzome.core.construction.PolygonFromVertices(pts);
            }
            return c;
        }

        /**
         * this is for the old format (before rationalVectors)
         * @param {string} apName
         * @param {*} attrOrParam
         * @param {boolean} projectTo3d
         * @return {com.vzome.core.construction.Construction}
         */
        public parseConstruction(apName?: any, attrOrParam?: any, projectTo3d?: any): com.vzome.core.construction.Construction {
            if (((typeof apName === 'string') || apName === null) && ((attrOrParam != null && (attrOrParam.constructor != null && attrOrParam.constructor["__interfaces"] != null && attrOrParam.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || attrOrParam === null) && ((typeof projectTo3d === 'boolean') || projectTo3d === null)) {
                return <any>this.parseConstruction$java_lang_String$org_w3c_dom_Element$boolean(apName, attrOrParam, projectTo3d);
            } else if (((typeof apName === 'string') || apName === null) && ((attrOrParam != null && (attrOrParam.constructor != null && attrOrParam.constructor["__interfaces"] != null && attrOrParam.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || attrOrParam === null) && projectTo3d === undefined) {
                return <any>this.parseConstruction$java_lang_String$org_w3c_dom_Element(apName, attrOrParam);
            } else throw new Error('invalid overload');
        }

        public loadCommandAttributes$org_w3c_dom_Element(editElem: org.w3c.dom.Element): com.vzome.core.commands.AttributeMap {
            return this.loadCommandAttributes$org_w3c_dom_Element$boolean(editElem, false);
        }

        public loadCommandAttributes$org_w3c_dom_Element$boolean(editElem: org.w3c.dom.Element, projectTo3d: boolean): com.vzome.core.commands.AttributeMap {
            const attrs: com.vzome.core.commands.AttributeMap = new com.vzome.core.commands.AttributeMap();
            const kids: org.w3c.dom.NodeList = editElem.getChildNodes();
            for(let j: number = 0; j < kids.getLength(); j++) {{
                const node: org.w3c.dom.Node = kids.item(j);
                if (!(node != null && (node.constructor != null && node.constructor["__interfaces"] != null && node.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)))continue;
                let attrElem: org.w3c.dom.Element = <org.w3c.dom.Element><any>node;
                let elemName: string = attrElem.getLocalName();
                let attrName: string = attrElem.getAttribute("attrName");
                if (this.interim210format()){
                    attrName = attrElem.getAttribute("name");
                    const elemKid: org.w3c.dom.Element = com.vzome.xml.DomUtils.getFirstChildElement$org_w3c_dom_Element(attrElem);
                    if (elemKid != null){
                        attrElem = elemKid;
                        elemName = attrElem.getLocalName();
                    }
                }
                let value: any = this.parseAlgebraicObject(elemName, attrElem);
                if (value === XmlSaveFormat.NOT_AN_ATTRIBUTE_$LI$())if (this.rationalVectors()){
                    if (elemName === ("point"))value = this.parsePoint$org_w3c_dom_Element$java_lang_String$boolean(attrElem, "at", projectTo3d); else if (elemName === ("segment"))value = this.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String$boolean(attrElem, "start", "end", projectTo3d); else if (elemName === ("polygon"))value = this.parsePolygon$org_w3c_dom_Element$java_lang_String$boolean(attrElem, "vertex", projectTo3d); else throw new java.lang.IllegalStateException("unknown parameter construction: " + elemName);
                } else value = this.parseConstruction$java_lang_String$org_w3c_dom_Element$boolean(elemName, attrElem, projectTo3d);
                attrs.put(attrName, value);
            };}
            return attrs;
        }

        public loadCommandAttributes(editElem?: any, projectTo3d?: any): com.vzome.core.commands.AttributeMap {
            if (((editElem != null && (editElem.constructor != null && editElem.constructor["__interfaces"] != null && editElem.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || editElem === null) && ((typeof projectTo3d === 'boolean') || projectTo3d === null)) {
                return <any>this.loadCommandAttributes$org_w3c_dom_Element$boolean(editElem, projectTo3d);
            } else if (((editElem != null && (editElem.constructor != null && editElem.constructor["__interfaces"] != null && editElem.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || editElem === null) && projectTo3d === undefined) {
                return <any>this.loadCommandAttributes$org_w3c_dom_Element(editElem);
            } else throw new Error('invalid overload');
        }

        public getField(): com.vzome.core.algebra.AlgebraicField {
            return this.mField;
        }

        public getScale(): number {
            return this.mScale;
        }

        public static serializeNumber(xml: org.w3c.dom.Element, attrName: string, number: com.vzome.core.algebra.AlgebraicNumber) {
            com.vzome.xml.DomUtils.addAttribute(xml, attrName, number.toString(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT));
        }

        public static serializePoint(xml: org.w3c.dom.Element, attrName: string, point: com.vzome.core.construction.Point) {
            com.vzome.xml.DomUtils.addAttribute(xml, attrName, point.getLocation().getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT));
        }

        public static serializeSegment(xml: org.w3c.dom.Element, startAttrName: string, endAttrName: string, segment: com.vzome.core.construction.Segment) {
            com.vzome.xml.DomUtils.addAttribute(xml, startAttrName, segment.getStart().getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT));
            com.vzome.xml.DomUtils.addAttribute(xml, endAttrName, segment.getEnd().getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT));
        }

        public static serializePolygon(xml: org.w3c.dom.Element, vertexChildName: string, polygon: com.vzome.core.construction.Polygon) {
            polygon.getXml$org_w3c_dom_Element$java_lang_String(xml, vertexChildName);
        }

        public parseRationalVector(xml: org.w3c.dom.Element, attrName: string): com.vzome.core.algebra.AlgebraicVector {
            const nums: string = xml.getAttribute(attrName);
            if (nums == null || /* isEmpty */(nums.length === 0))return null;
            const loc: com.vzome.core.algebra.AlgebraicVector = this.mField.parseVector(nums);
            return loc;
        }

        public parseRationalNumber(xml: org.w3c.dom.Element, attrName: string): com.vzome.core.algebra.AlgebraicNumber {
            const nums: string = xml.getAttribute(attrName);
            if (nums == null || /* isEmpty */(nums.length === 0))return null;
            const loc: com.vzome.core.algebra.AlgebraicNumber = this.mField.parseNumber(nums);
            return loc;
        }

        public parsePoint$org_w3c_dom_Element$java_lang_String(xml: org.w3c.dom.Element, attrName: string): com.vzome.core.construction.Point {
            return this.parsePoint$org_w3c_dom_Element$java_lang_String$boolean(xml, attrName, false);
        }

        public parsePoint$org_w3c_dom_Element$java_lang_String$boolean(xml: org.w3c.dom.Element, attrName: string, projectTo3d: boolean): com.vzome.core.construction.Point {
            const nums: string = xml.getAttribute(attrName);
            if (nums == null || /* isEmpty */(nums.length === 0))return null;
            let loc: com.vzome.core.algebra.AlgebraicVector = this.mField.parseVector(nums);
            if (projectTo3d)loc = loc.projectTo3d(true);
            return new com.vzome.core.construction.FreePoint(loc);
        }

        public parsePoint(xml?: any, attrName?: any, projectTo3d?: any): com.vzome.core.construction.Point {
            if (((xml != null && (xml.constructor != null && xml.constructor["__interfaces"] != null && xml.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || xml === null) && ((typeof attrName === 'string') || attrName === null) && ((typeof projectTo3d === 'boolean') || projectTo3d === null)) {
                return <any>this.parsePoint$org_w3c_dom_Element$java_lang_String$boolean(xml, attrName, projectTo3d);
            } else if (((xml != null && (xml.constructor != null && xml.constructor["__interfaces"] != null && xml.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || xml === null) && ((typeof attrName === 'string') || attrName === null) && projectTo3d === undefined) {
                return <any>this.parsePoint$org_w3c_dom_Element$java_lang_String(xml, attrName);
            } else throw new Error('invalid overload');
        }

        public parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml: org.w3c.dom.Element, startAttrName: string, endAttrName: string): com.vzome.core.construction.Segment {
            return this.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String$boolean(xml, startAttrName, endAttrName, false);
        }

        public parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String$boolean(xml: org.w3c.dom.Element, startAttrName: string, endAttrName: string, projectTo3d: boolean): com.vzome.core.construction.Segment {
            let nums: string = xml.getAttribute(endAttrName);
            if (nums == null || /* isEmpty */(nums.length === 0))return null;
            let eloc: com.vzome.core.algebra.AlgebraicVector = this.mField.parseVector(nums);
            nums = xml.getAttribute(startAttrName);
            let sloc: com.vzome.core.algebra.AlgebraicVector = (nums == null || /* isEmpty */(nums.length === 0)) ? this.mField.origin(eloc.dimension()) : this.mField.parseVector(nums);
            if (projectTo3d){
                sloc = sloc.projectTo3d(true);
                eloc = eloc.projectTo3d(true);
            }
            return new com.vzome.core.construction.SegmentJoiningPoints(new com.vzome.core.construction.FreePoint(sloc), new com.vzome.core.construction.FreePoint(eloc));
        }

        public parseSegment(xml?: any, startAttrName?: any, endAttrName?: any, projectTo3d?: any): com.vzome.core.construction.Segment {
            if (((xml != null && (xml.constructor != null && xml.constructor["__interfaces"] != null && xml.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || xml === null) && ((typeof startAttrName === 'string') || startAttrName === null) && ((typeof endAttrName === 'string') || endAttrName === null) && ((typeof projectTo3d === 'boolean') || projectTo3d === null)) {
                return <any>this.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String$boolean(xml, startAttrName, endAttrName, projectTo3d);
            } else if (((xml != null && (xml.constructor != null && xml.constructor["__interfaces"] != null && xml.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || xml === null) && ((typeof startAttrName === 'string') || startAttrName === null) && ((typeof endAttrName === 'string') || endAttrName === null) && projectTo3d === undefined) {
                return <any>this.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, startAttrName, endAttrName);
            } else throw new Error('invalid overload');
        }

        public parsePolygon$org_w3c_dom_Element$java_lang_String(xml: org.w3c.dom.Element, vertexChildName: string): com.vzome.core.construction.Polygon {
            return this.parsePolygon$org_w3c_dom_Element$java_lang_String$boolean(xml, vertexChildName, false);
        }

        public parsePolygon$org_w3c_dom_Element$java_lang_String$boolean(xml: org.w3c.dom.Element, vertexChildName: string, projectTo3d: boolean): com.vzome.core.construction.Polygon {
            const kids: org.w3c.dom.NodeList = xml.getElementsByTagName(vertexChildName);
            const pts: com.vzome.core.construction.Point[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(kids.getLength());
            for(let k: number = 0; k < kids.getLength(); k++) {{
                const nums: string = (<org.w3c.dom.Element><any>kids.item(k)).getAttribute("at");
                let loc: com.vzome.core.algebra.AlgebraicVector = this.mField.parseVector(nums);
                if (projectTo3d)loc = loc.projectTo3d(true);
                pts[k] = new com.vzome.core.construction.FreePoint(loc);
            };}
            return new com.vzome.core.construction.PolygonFromVertices(pts);
        }

        public parsePolygon(xml?: any, vertexChildName?: any, projectTo3d?: any): com.vzome.core.construction.Polygon {
            if (((xml != null && (xml.constructor != null && xml.constructor["__interfaces"] != null && xml.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || xml === null) && ((typeof vertexChildName === 'string') || vertexChildName === null) && ((typeof projectTo3d === 'boolean') || projectTo3d === null)) {
                return <any>this.parsePolygon$org_w3c_dom_Element$java_lang_String$boolean(xml, vertexChildName, projectTo3d);
            } else if (((xml != null && (xml.constructor != null && xml.constructor["__interfaces"] != null && xml.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)) || xml === null) && ((typeof vertexChildName === 'string') || vertexChildName === null) && projectTo3d === undefined) {
                return <any>this.parsePolygon$org_w3c_dom_Element$java_lang_String(xml, vertexChildName);
            } else throw new Error('invalid overload');
        }

        public parsePolygonReversed(xml: org.w3c.dom.Element, vertexChildName: string): com.vzome.core.construction.Polygon {
            const kids: org.w3c.dom.NodeList = xml.getElementsByTagName(vertexChildName);
            const pts: com.vzome.core.construction.Point[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(kids.getLength());
            const kmax: number = kids.getLength() - 1;
            for(let k: number = 0; k < kids.getLength(); k++) {{
                const nums: string = (<org.w3c.dom.Element><any>kids.item(k)).getAttribute("at");
                const loc: com.vzome.core.algebra.AlgebraicVector = this.mField.parseVector(nums);
                pts[kmax - k] = new com.vzome.core.construction.FreePoint(loc);
            };}
            return new com.vzome.core.construction.PolygonFromVertices(pts);
        }

        public parseNumber(xml: org.w3c.dom.Element, attrName: string): com.vzome.core.algebra.AlgebraicNumber {
            const nums: string = xml.getAttribute(attrName);
            if (nums == null || /* isEmpty */(nums.length === 0))return null;
            return this.mField.parseNumber(nums);
        }

        public static escapeNewlines(input: string): string {
            const buf: java.lang.StringBuffer = new java.lang.StringBuffer();
            const br: java.io.BufferedReader = new java.io.BufferedReader(new java.io.StringReader(input));
            let line: string = null;
            try {
                while(((line = br.readLine()) != null)) {{
                    const comment: number = line.indexOf("//");
                    if (comment >= 0){
                        line = line.substring(0, comment);
                    }
                    buf.append(line + "\n");
                }};
            } catch(e) {
            }
            return buf.toString();
        }

        public loadToRender(): boolean {
            return !("true" === this.properties.getProperty("no.rendering"));
        }

        public getToolVersion(element: org.w3c.dom.Element): string {
            let fileEdition: string = element.getAttribute("edition");
            if (fileEdition == null || /* isEmpty */(fileEdition.length === 0))fileEdition = "vZome";
            return fileEdition + " " + this.writerVersion;
        }
    }
    XmlSaveFormat["__class"] = "com.vzome.core.commands.XmlSaveFormat";

}

