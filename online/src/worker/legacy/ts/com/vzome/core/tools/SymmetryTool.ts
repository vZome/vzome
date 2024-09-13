/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class SymmetryTool extends com.vzome.core.tools.TransformationTool {
        static ID: string = "symmetry";

        static LABEL: string = "Create a general symmetry tool";

        static TOOLTIP: string = "<p>General symmetry tool.<br></p>";

        symmetry: com.vzome.core.math.symmetry.Symmetry;

        public constructor(id: string, symmetry: com.vzome.core.math.symmetry.Symmetry, tools: com.vzome.core.editor.ToolsModel) {
            super(id, tools);
            if (this.symmetry === undefined) { this.symmetry = null; }
            this.symmetry = symmetry;
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            const prime: number = 31;
            let result: number = 1;
            result = prime * result + ((this.symmetry == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.symmetry)));
            return result;
        }

        /**
         * 
         * @param {*} that
         * @return {boolean}
         */
        public equals(that: any): boolean {
            if (this === that){
                return true;
            }
            if (!super.equals(that)){
                return false;
            }
            if ((<any>this.constructor) !== (<any>that.constructor)){
                return false;
            }
            const other: SymmetryTool = <SymmetryTool>that;
            if (this.symmetry == null){
                if (other.symmetry != null){
                    return false;
                }
            } else if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.symmetry,other.symmetry))){
                return false;
            }
            return true;
        }

        /**
         * 
         * @param {boolean} prepareTool
         * @return {string}
         */
        checkSelection(prepareTool: boolean): string {
            let center: com.vzome.core.construction.Point = null;
            let axis: com.vzome.core.model.Strut = null;
            let correct: boolean = true;
            let hasPanels: boolean = false;
            if (!this.isAutomatic())for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (prepareTool)this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (center != null)return "No unique symmetry center selected";
                        center = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (axis != null)correct = false; else axis = <com.vzome.core.model.Strut><any>man;
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        hasPanels = true;
                    }
                }
            }
            if (center == null){
                if (prepareTool){
                    center = this.originPoint;
                    this.addParameter(center);
                } else return "No symmetry center selected";
            }
            if (hasPanels){
                if (!prepareTool)return "panels are selected";
            }
            let closure: number[] = this.symmetry.subgroup(com.vzome.core.math.symmetry.Symmetry.TETRAHEDRAL);
            switch((this.symmetry.getName())) {
            case "icosahedral":
                if (!prepareTool && (axis != null) && (this.getCategory() === ("icosahedral")))return "No struts needed for icosahedral symmetry.";
                switch((this.getCategory())) {
                case "tetrahedral":
                    if (!correct)return "no unique alignment strut selected.";
                    if (axis == null){
                        if (!prepareTool)return "no aligment strut selected.";
                    } else {
                        const icosa: com.vzome.core.math.symmetry.IcosahedralSymmetry = <com.vzome.core.math.symmetry.IcosahedralSymmetry><any>this.symmetry;
                        const zone: com.vzome.core.math.symmetry.Axis = icosa.getAxis$com_vzome_core_algebra_AlgebraicVector(axis.getOffset());
                        if (zone == null)return "selected alignment strut is not a tetrahedral axis.";
                        const allowYellow: boolean = prepareTool;
                        closure = icosa.subgroup$java_lang_String$com_vzome_core_math_symmetry_Axis$boolean(com.vzome.core.math.symmetry.Symmetry.TETRAHEDRAL, zone, allowYellow);
                        if (closure == null)return "selected alignment strut is not a tetrahedral axis.";
                    }
                    if (prepareTool){
                        const order: number = closure.length;
                        this.transforms = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order - 1);
                        for(let i: number = 0; i < order - 1; i++) {this.transforms[i] = new com.vzome.core.construction.SymmetryTransformation(this.symmetry, closure[i + 1], center);}
                    }
                    break;
                case "octahedral":
                    let orientation: com.vzome.core.algebra.AlgebraicMatrix = null;
                    if (!correct)return "no unique alignment strut selected.";
                    if (axis == null){
                        if (!prepareTool)return "no aligment strut selected.";
                    } else {
                        const icosa: com.vzome.core.math.symmetry.IcosahedralSymmetry = <com.vzome.core.math.symmetry.IcosahedralSymmetry><any>this.symmetry;
                        const zone: com.vzome.core.math.symmetry.Axis = icosa.getAxis$com_vzome_core_algebra_AlgebraicVector(axis.getOffset());
                        if (zone == null)return "selected alignment strut is not an octahedral axis.";
                        let blueIndex: number = 0;
                        switch((zone.getDirection().getName())) {
                        case "green":
                            blueIndex = icosa.blueTetrahedralFromGreen(zone.getOrientation());
                            break;
                        case "blue":
                            blueIndex = zone.getOrientation();
                            break;
                        default:
                            return "selected alignment strut is not an octahedral axis.";
                        }
                        orientation = this.symmetry.getMatrix(blueIndex);
                    }
                    if (prepareTool){
                        const inverse: com.vzome.core.algebra.AlgebraicMatrix = orientation.inverse();
                        const octa: com.vzome.core.math.symmetry.OctahedralSymmetry = (this.symmetry != null && this.symmetry instanceof <any>com.vzome.core.math.symmetry.OctahedralSymmetry) ? <com.vzome.core.math.symmetry.OctahedralSymmetry><any>this.symmetry : new com.vzome.core.math.symmetry.OctahedralSymmetry(this.symmetry.getField());
                        const order: number = octa.getChiralOrder();
                        this.transforms = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order - 1);
                        for(let i: number = 0; i < order - 1; i++) {{
                            let matrix: com.vzome.core.algebra.AlgebraicMatrix = octa.getMatrix(i + 1);
                            matrix = orientation.times(matrix.times(inverse));
                            this.transforms[i] = new com.vzome.core.construction.MatrixTransformation(matrix, center.getLocation());
                        };}
                    }
                    break;
                default:
                    if (prepareTool)this.prepareFullSymmetry(center);
                    break;
                }
                break;
            case "synestructics":
            case "octahedral":
                if (prepareTool){
                    if (this.getCategory() === ("tetrahedral")){
                        const order: number = closure.length;
                        this.transforms = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order - 1);
                        for(let i: number = 0; i < order - 1; i++) {this.transforms[i] = new com.vzome.core.construction.SymmetryTransformation(this.symmetry, closure[i + 1], center);}
                    } else {
                        this.prepareFullSymmetry(center);
                    }
                } else {
                    if (axis != null)return "No struts needed for symmetry";
                }
                break;
            default:
                if (prepareTool)this.prepareFullSymmetry(center);
                break;
            }
            return null;
        }

        /*private*/ prepareFullSymmetry(center: com.vzome.core.construction.Point) {
            const order: number = this.symmetry.getChiralOrder();
            this.transforms = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order - 1);
            for(let i: number = 0; i < order - 1; i++) {this.transforms[i] = new com.vzome.core.construction.SymmetryTransformation(this.symmetry, i + 1, center);}
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "SymmetryTool";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            element.setAttribute("symmetry", this.symmetry.getName());
            super.getXmlAttributes(element);
        }

        /**
         * 
         * @param {*} element
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(element: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const symmName: string = element.getAttribute("symmetry");
            this.symmetry = (<com.vzome.core.commands.XmlSymmetryFormat>format).parseSymmetry(symmName);
            super.setXmlAttributes(element, format);
        }
    }
    SymmetryTool["__class"] = "com.vzome.core.tools.SymmetryTool";
    SymmetryTool["__interfaces"] = ["com.vzome.api.Tool"];


}

