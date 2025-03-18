/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class RotationTool extends com.vzome.core.tools.SymmetryTool {
        /*private*/ fullRotation: boolean;

        /*private*/ corrected: boolean;

        /**
         * 
         * @return {string}
         */
        public getCategory(): string {
            if (this.fullRotation)return "axial symmetry"; else return com.vzome.core.tools.RotationToolFactory.ID;
        }

        public constructor(id?: any, symmetry?: any, tools?: any, full?: any) {
            if (((typeof id === 'string') || id === null) && ((symmetry != null && (symmetry.constructor != null && symmetry.constructor["__interfaces"] != null && symmetry.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || symmetry === null) && ((tools != null && tools instanceof <any>com.vzome.core.editor.ToolsModel) || tools === null) && ((typeof full === 'boolean') || full === null)) {
                let __args = arguments;
                super(id, symmetry, tools);
                if (this.fullRotation === undefined) { this.fullRotation = false; } 
                if (this.corrected === undefined) { this.corrected = false; } 
                this.fullRotation = full;
                this.corrected = true;
                if (full)this.setInputBehaviors(true, false); else this.setInputBehaviors(false, true);
            } else if (((typeof id === 'string') || id === null) && ((symmetry != null && (symmetry.constructor != null && symmetry.constructor["__interfaces"] != null && symmetry.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || symmetry === null) && ((tools != null && tools instanceof <any>com.vzome.core.editor.ToolsModel) || tools === null) && full === undefined) {
                let __args = arguments;
                let editor: any = __args[2];
                {
                    let __args = arguments;
                    let tools: any = editor;
                    let full: any = false;
                    super(id, symmetry, tools);
                    if (this.fullRotation === undefined) { this.fullRotation = false; } 
                    if (this.corrected === undefined) { this.corrected = false; } 
                    this.fullRotation = full;
                    this.corrected = true;
                    if (full)this.setInputBehaviors(true, false); else this.setInputBehaviors(false, true);
                }
                (() => {
                    this.corrected = false;
                })();
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @param {boolean} prepareTool
         * @return {string}
         */
        checkSelection(prepareTool: boolean): string {
            let center: com.vzome.core.construction.Point = null;
            let axisStrut: com.vzome.core.construction.Segment = null;
            let correct: boolean = true;
            let rotationZone: com.vzome.core.math.symmetry.Axis = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (prepareTool)this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (center != null){
                            correct = false;
                            break;
                        }
                        center = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (axisStrut != null){
                            correct = false;
                            break;
                        }
                        axisStrut = <com.vzome.core.construction.Segment>(<com.vzome.core.model.Strut><any>man).getFirstConstruction();
                        let vector: com.vzome.core.algebra.AlgebraicVector = axisStrut.getOffset();
                        vector = axisStrut.getField().projectTo3d(vector, true);
                        rotationZone = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](vector);
                    }
                }
            }
            if (axisStrut == null){
                rotationZone = this.symmetry.getPreferredAxis();
                if (rotationZone != null){
                    const field: com.vzome.core.algebra.AlgebraicField = this.symmetry.getField();
                    center = this.originPoint;
                    this.addParameter(center);
                    axisStrut = new com.vzome.core.construction.AnchoredSegment(rotationZone, field.one(), center);
                    this.addParameter(axisStrut);
                } else if (this.isPredefined()){
                    center = this.originPoint;
                    this.addParameter(center);
                    const redOrbit: com.vzome.core.math.symmetry.Direction = this.symmetry.getSpecialOrbit(com.vzome.core.math.symmetry.SpecialOrbit.RED);
                    rotationZone = redOrbit.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 1);
                    const field: com.vzome.core.algebra.AlgebraicField = this.symmetry.getField();
                    const redScale: com.vzome.core.algebra.AlgebraicNumber = redOrbit.getUnitLength()['times$com_vzome_core_algebra_AlgebraicNumber'](field['createPower$int'](com.vzome.core.math.symmetry.Direction.USER_SCALE));
                    axisStrut = new com.vzome.core.construction.AnchoredSegment(rotationZone, redScale, center);
                    this.addParameter(axisStrut);
                } else if (this.isAutomatic()){
                    center = this.originPoint;
                    this.addParameter(center);
                    const field: com.vzome.core.algebra.AlgebraicField = this.symmetry.getField();
                    const zAxis: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z);
                    const len: com.vzome.core.algebra.AlgebraicNumber = field['createPower$int'](2);
                    rotationZone = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](zAxis);
                    axisStrut = new com.vzome.core.construction.AnchoredSegment(rotationZone, len, center);
                    this.addParameter(axisStrut);
                } else correct = false;
            } else if (center == null)center = new com.vzome.core.construction.SegmentEndPoint(axisStrut);
            if (!correct)return "rotation tool requires a single axis strut,\nand optionally a separate center point";
            if (rotationZone == null)return "selected strut is not an axis of rotation";
            const perm: com.vzome.core.math.symmetry.Permutation = rotationZone.getRotationPermutation();
            if (perm == null)return "selected strut is not an axis of rotation";
            let rotation: number = this.corrected ? perm.mapIndex(0) : rotationZone.getRotation();
            if (prepareTool){
                if (this.fullRotation){
                    const order: number = perm.getOrder();
                    this.transforms = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order - 1);
                    for(let i: number = 0; i < this.transforms.length; i++) {{
                        this.transforms[i] = new com.vzome.core.construction.SymmetryTransformation(this.symmetry, rotation, center);
                        rotation = perm.mapIndex(rotation);
                    };}
                } else {
                    this.transforms = [null];
                    this.transforms[0] = new com.vzome.core.construction.SymmetryTransformation(this.symmetry, rotation, center);
                }
            }
            return null;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "RotationTool";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            if (this.fullRotation)element.setAttribute("full", "true");
            if (this.corrected)element.setAttribute("corrected", "true");
            super.getXmlAttributes(element);
        }

        /**
         * 
         * @param {*} element
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(element: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            let value: string = element.getAttribute("full");
            this.fullRotation = (value != null) && ("true" === value);
            value = element.getAttribute("corrected");
            this.corrected = (value != null) && ("true" === value);
            super.setXmlAttributes(element, format);
        }
    }
    RotationTool["__class"] = "com.vzome.core.tools.RotationTool";
    RotationTool["__interfaces"] = ["com.vzome.api.Tool"];


}

