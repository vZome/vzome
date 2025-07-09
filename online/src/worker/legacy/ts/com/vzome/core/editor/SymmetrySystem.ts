/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export class SymmetrySystem implements com.vzome.core.editor.api.OrbitSource {
        /* Default method injected from com.vzome.core.editor.api.OrbitSource */
        getOrientations$(): number[][] {
            return this.getOrientations(false);
        }
        /* Default method injected from com.vzome.core.editor.api.OrbitSource */
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
        /* Default method injected from com.vzome.core.editor.api.OrbitSource */
        getZone(orbit: string, orientation: number): com.vzome.core.math.symmetry.Axis {
            return this.getSymmetry().getDirection(orbit).getAxis(com.vzome.core.math.symmetry.Symmetry.PLUS, orientation);
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
        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (SymmetrySystem.logger == null) { SymmetrySystem.logger = java.util.logging.Logger.getLogger("com.vzome.core.editor"); }  return SymmetrySystem.logger; }

        /*private*/ nextNewAxis: number;

        /*private*/ symmetry: com.vzome.core.math.symmetry.Symmetry;

        /*private*/ orbits: com.vzome.core.math.symmetry.OrbitSet;

        /*private*/ orbitColors: java.util.Map<string, com.vzome.core.construction.Color>;

        /*private*/ shapes: com.vzome.core.editor.api.Shapes;

        /*private*/ vectorToAxis: java.util.Map<string, com.vzome.core.math.symmetry.Axis>;

        /*private*/ noKnownDirections: boolean;

        /*private*/ symmetryPerspective: com.vzome.core.editor.SymmetryPerspective;

        /*private*/ toolFactoryLists: java.util.Map<com.vzome.api.Tool.Kind, java.util.List<com.vzome.api.Tool.Factory>>;

        /*private*/ toolLists: java.util.Map<com.vzome.api.Tool.Kind, java.util.List<com.vzome.api.Tool>>;

        /*private*/ context: com.vzome.core.editor.api.Context;

        /*private*/ editor: com.vzome.core.editor.api.EditorModel;

        /*private*/ colors: com.vzome.core.render.Colors;

        public constructor(symmXml: org.w3c.dom.Element, symmetryPerspective: com.vzome.core.editor.SymmetryPerspective, context: com.vzome.core.editor.api.Context, colors: com.vzome.core.render.Colors, allowNonstandard: boolean) {
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
            this.orbits = new com.vzome.core.math.symmetry.OrbitSet(this.symmetry);
            if (symmXml == null){
                for(let index=this.symmetry.getOrbitSet().getDirections().iterator();index.hasNext();) {
                    let orbit = index.next();
                    {
                        if (symmetryPerspective.orbitIsStandard(orbit) || allowNonstandard)this.orbits.add(orbit);
                        const color: com.vzome.core.construction.Color = colors.getColor(com.vzome.core.render.Colors.DIRECTION_$LI$() + orbit.getName());
                        this.orbitColors.put(orbit.getName(), color);
                    }
                }
            } else {
                styleName = symmXml.getAttribute("renderingStyle");
                const nodes: org.w3c.dom.NodeList = symmXml.getChildNodes();
                for(let i: number = 0; i < nodes.getLength(); i++) {{
                    const node: org.w3c.dom.Node = nodes.item(i);
                    if (node != null && (node.constructor != null && node.constructor["__interfaces"] != null && node.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                        const dirElem: org.w3c.dom.Element = <org.w3c.dom.Element><any>node;
                        const name: string = dirElem.getAttribute("name");
                        let orbit: com.vzome.core.math.symmetry.Direction = null;
                        const nums: string = dirElem.getAttribute("prototype");
                        if (nums != null && !/* isEmpty */(nums.length === 0)){
                            try {
                                const prototype: com.vzome.core.algebra.AlgebraicVector = this.symmetry.getField().parseVector(nums);
                                orbit = this.symmetry.createNewZoneOrbit(name, 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, prototype);
                            } catch(e) {
                                console.error("Integer overflow happened while creating orbit: " + name);
                                continue;
                            }
                            orbit.setAutomatic(true);
                            try {
                                const autoNum: number = javaemul.internal.IntegerHelper.parseInt(name);
                                this.nextNewAxis = Math.max(this.nextNewAxis, autoNum + 1);
                            } catch(e) {
                                console.error(e.message);
                            }
                        } else {
                            orbit = this.symmetry.getDirection(name);
                            if (orbit == null)continue;
                        }
                        this.orbits.add(orbit);
                        let color: com.vzome.core.construction.Color = colors.getColor(com.vzome.core.render.Colors.DIRECTION_$LI$() + orbit.getCanonicalName());
                        const str: string = dirElem.getAttribute("color");
                        if (str != null && !/* isEmpty */(str.length === 0) && !(str === ("255,255,255"))){
                            color = com.vzome.core.construction.Color.parseColor(str);
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
                        const color: com.vzome.core.construction.Color = colors.getColor(com.vzome.core.render.Colors.DIRECTION_$LI$() + orbit.getCanonicalName());
                        this.orbitColors.put(orbit.getName(), color);
                        this.orbitColors.put(orbit.getCanonicalName(), color);
                    }
                }
            }
            this.setStyle(styleName);
        }

        public setEditorModel(editor: com.vzome.core.editor.api.EditorModel) {
            this.editor = editor;
        }

        public createToolFactories(tools: com.vzome.core.editor.ToolsModel) {
            {
                let array = /* Enum.values */function() { let result: com.vzome.api.Tool.Kind[] = []; for(let val in com.vzome.api.Tool.Kind) { if (!isNaN(<any>val)) { result.push(parseInt(val,10)); } } return result; }();
                for(let index = 0; index < array.length; index++) {
                    let kind = array[index];
                    {
                        const list: java.util.List<com.vzome.api.Tool.Factory> = this.symmetryPerspective.createToolFactories(kind, tools);
                        this.toolFactoryLists.put(kind, list);
                        const toolList: java.util.List<com.vzome.api.Tool> = this.symmetryPerspective.predefineTools(kind, tools);
                        this.toolLists.put(kind, toolList);
                    }
                }
            }
        }

        public getName(): string {
            return this.symmetry.getName();
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} vector
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getAxis(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.symmetry.Axis {
            if (vector.isOrigin()){
                return null;
            }
            let line: com.vzome.core.math.symmetry.Axis = this.vectorToAxis.get(vector.toString());
            if (line != null)return line;
            if (!this.noKnownDirections){
                line = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_math_symmetry_OrbitSet'](vector, this.orbits);
                if (line != null){
                    this.vectorToAxis.put(vector.toString(), line);
                    return line;
                }
            }
            const dir: com.vzome.core.math.symmetry.Direction = this.createAnonymousOrbit(vector);
            line = dir.getAxis$com_vzome_core_algebra_AlgebraicVector(vector);
            this.vectorToAxis.put(vector.toString(), line);
            return line;
        }

        public createAnonymousOrbit(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.symmetry.Direction {
            const symm: com.vzome.core.math.symmetry.Symmetry = this.orbits.getSymmetry();
            const field: com.vzome.core.algebra.AlgebraicField = symm.getField();
            const longer: com.vzome.core.algebra.AlgebraicNumber = field['createPower$int'](1);
            const shorter: com.vzome.core.algebra.AlgebraicNumber = field['createPower$int'](-1);
            const rv: com.vzome.core.math.RealVector = vector.toRealVector();
            let longVector: com.vzome.core.algebra.AlgebraicVector = vector;
            let shortVector: com.vzome.core.algebra.AlgebraicVector = vector;
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
            const orbit: com.vzome.core.math.symmetry.Direction = symm.createNewZoneOrbit(colorName, 0, com.vzome.core.math.symmetry.Symmetry.NO_ROTATION, vector);
            orbit.setAutomatic(true);
            this.orbits.add(orbit);
            let color: com.vzome.core.construction.Color = this.colors.getColor(com.vzome.core.render.Colors.DIRECTION_$LI$() + orbit.getCanonicalName());
            if (color == null)color = com.vzome.core.construction.Color.WHITE_$LI$();
            this.orbitColors.put(orbit.getName(), color);
            this.orbitColors.put(orbit.getCanonicalName(), color);
            return orbit;
        }

        public getVectorColor(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.construction.Color {
            if (vector == null || vector.isOrigin()){
                return this.colors.getColor(com.vzome.core.render.Colors.CONNECTOR_$LI$());
            }
            let line: com.vzome.core.math.symmetry.Axis = this.vectorToAxis.get(vector.toString());
            if (line == null){
                line = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_math_symmetry_OrbitSet'](vector, this.orbits);
            }
            return (line == null) ? com.vzome.core.construction.Color.WHITE_$LI$() : this.getColor(line.getDirection());
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Direction} orbit
         * @return {com.vzome.core.construction.Color}
         */
        public getColor(orbit: com.vzome.core.math.symmetry.Direction): com.vzome.core.construction.Color {
            if (orbit == null)return this.colors.getColor(com.vzome.core.render.Colors.CONNECTOR_$LI$());
            let shapeColor: com.vzome.core.construction.Color = this.shapes.getColor(orbit);
            if (shapeColor == null)shapeColor = this.orbitColors.get(orbit.getName());
            if (shapeColor == null)return com.vzome.core.construction.Color.WHITE_$LI$();
            return shapeColor;
        }

        /**
         * 
         * @return {*}
         */
        public getSymmetry(): com.vzome.core.math.symmetry.Symmetry {
            return this.symmetry;
        }

        /**
         * 
         * @return {com.vzome.core.math.symmetry.OrbitSet}
         */
        public getOrbits(): com.vzome.core.math.symmetry.OrbitSet {
            return this.orbits;
        }

        public disableKnownDirection() {
            this.noKnownDirections = true;
        }

        public getRenderingStyle(): com.vzome.core.editor.api.Shapes {
            return this.shapes;
        }

        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("SymmetrySystem");
            com.vzome.xml.DomUtils.addAttribute(result, "name", this.getSymmetry().getName());
            com.vzome.xml.DomUtils.addAttribute(result, "renderingStyle", this.shapes.getName());
            for(let index=this.orbits.getDirections().iterator();index.hasNext();) {
                let dir = index.next();
                {
                    const dirElem: org.w3c.dom.Element = doc.createElement("Direction");
                    if (dir.isAutomatic())com.vzome.xml.DomUtils.addAttribute(dirElem, "prototype", dir.getPrototype().getVectorExpression$int(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT));
                    com.vzome.xml.DomUtils.addAttribute(dirElem, "name", dir.getName());
                    com.vzome.xml.DomUtils.addAttribute(dirElem, "orbit", dir.getCanonicalName());
                    {
                        const color: com.vzome.core.construction.Color = this.getColor(dir);
                        if (color != null)com.vzome.xml.DomUtils.addAttribute(dirElem, "color", color.toString());
                    };
                    result.appendChild(dirElem);
                }
            }
            return result;
        }

        public getStyle$java_lang_String(styleName: string): com.vzome.core.editor.api.Shapes {
            const found: java.util.Optional<com.vzome.core.editor.api.Shapes> = this.symmetryPerspective.getGeometries().stream().filter((e) => (styleName === e.getName()) || (styleName === e.getAlias()) || (styleName === e.getPackage())).findFirst();
            if (found.isPresent())return found.get(); else return null;
        }

        public getStyle(styleName?: any): com.vzome.core.editor.api.Shapes {
            if (((typeof styleName === 'string') || styleName === null)) {
                return <any>this.getStyle$java_lang_String(styleName);
            } else if (styleName === undefined) {
                return <any>this.getStyle$();
            } else throw new Error('invalid overload');
        }

        public setStyle(styleName: string) {
            const result: com.vzome.core.editor.api.Shapes = this.getStyle$java_lang_String(styleName);
            if (result != null)this.shapes = result; else {
                SymmetrySystem.logger_$LI$().warning("UNKNOWN STYLE NAME: " + styleName);
                this.shapes = this.symmetryPerspective.getDefaultGeometry();
            }
        }

        public getStyleNames(): string[] {
            return this.symmetryPerspective.getGeometries().stream().map<any>((e) => e.getName()).toArray<any>((arg0) => { return new Array<string>(arg0) });
        }

        public getStyle$(): com.vzome.core.editor.api.Shapes {
            return this.shapes;
        }

        /**
         * 
         * @return {*}
         */
        public getShapes(): com.vzome.core.editor.api.Shapes {
            return this.shapes;
        }

        public getShape$com_vzome_core_algebra_AlgebraicVector(offset: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.Polyhedron {
            return this.getShape$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_editor_api_Shapes(offset, this.shapes);
        }

        public getShape$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_editor_api_Shapes(offset: com.vzome.core.algebra.AlgebraicVector, shapes: com.vzome.core.editor.api.Shapes): com.vzome.core.math.Polyhedron {
            if (offset == null)return shapes.getConnectorShape(); else {
                if (offset.isOrigin())return null;
                const axis: com.vzome.core.math.symmetry.Axis = this.getAxis(offset);
                if (axis == null)return null;
                const orbit: com.vzome.core.math.symmetry.Direction = axis.getDirection();
                const len: com.vzome.core.algebra.AlgebraicNumber = axis.getLength(offset);
                return shapes.getStrutShape(orbit, len);
            }
        }

        public getShape(offset?: any, shapes?: any): com.vzome.core.math.Polyhedron {
            if (((offset != null && offset instanceof <any>com.vzome.core.algebra.AlgebraicVector) || offset === null) && ((shapes != null && (shapes.constructor != null && shapes.constructor["__interfaces"] != null && shapes.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.Shapes") >= 0)) || shapes === null)) {
                return <any>this.getShape$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_editor_api_Shapes(offset, shapes);
            } else if (((offset != null && offset instanceof <any>com.vzome.core.algebra.AlgebraicVector) || offset === null) && shapes === undefined) {
                return <any>this.getShape$com_vzome_core_algebra_AlgebraicVector(offset);
            } else throw new Error('invalid overload');
        }

        public getToolFactories(kind: com.vzome.api.Tool.Kind): java.util.List<com.vzome.api.Tool.Factory> {
            return this.toolFactoryLists.get(kind);
        }

        public getPredefinedTools(kind: com.vzome.api.Tool.Kind): java.util.List<com.vzome.api.Tool> {
            return this.toolLists.get(kind);
        }

        public doAction(action: string): boolean {
            const command: com.vzome.core.commands.Command = this.symmetryPerspective.getLegacyCommand(action);
            if (command != null){
                const edit: com.vzome.core.editor.CommandEdit = new com.vzome.core.editor.CommandEdit(<com.vzome.core.commands.AbstractCommand><any>command, this.editor);
                this.context.performAndRecord(edit);
                return true;
            }
            return false;
        }

        public getModelResourcePath(): string {
            return this.symmetryPerspective.getModelResourcePath();
        }

        public orbitIsStandard(orbit: com.vzome.core.math.symmetry.Direction): boolean {
            return this.symmetryPerspective.orbitIsStandard(orbit);
        }

        public orbitIsBuildDefault(orbit: com.vzome.core.math.symmetry.Direction): boolean {
            return this.symmetryPerspective.orbitIsBuildDefault(orbit);
        }

        public getOrbitUnitLength(orbit: com.vzome.core.math.symmetry.Direction): com.vzome.core.algebra.AlgebraicNumber {
            return this.symmetryPerspective.getOrbitUnitLength(orbit);
        }

        public resetColors() {
            for(let index=this.symmetry.getOrbitSet().getDirections().iterator();index.hasNext();) {
                let orbit = index.next();
                {
                    const color: com.vzome.core.construction.Color = this.colors.getColor(com.vzome.core.render.Colors.DIRECTION_$LI$() + orbit.getName());
                    this.orbitColors.put(orbit.getName(), color);
                }
            }
        }
    }
    SymmetrySystem["__class"] = "com.vzome.core.editor.SymmetrySystem";
    SymmetrySystem["__interfaces"] = ["com.vzome.core.editor.api.OrbitSource"];


}

