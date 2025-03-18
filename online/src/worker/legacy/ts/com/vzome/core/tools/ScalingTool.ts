/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class ScalingTool extends com.vzome.core.tools.SymmetryTool {
        static ID: string = "scaling";

        static LABEL: string = "Create a scaling tool";

        static TOOLTIP: string = "<p>Each tool enlarges or shrinks the selected objects,<br>relative to a central point.  To create a tool,<br>select a ball representing the central point, and<br>two struts from the same orbit (color) with different<br>sizes.<br><br>The selection order matters.  First select a strut<br>that you want to enlarge or shrink, then select a<br>strut that has the desired target size.<br></p>";

        /*private*/ scaleFactor: com.vzome.core.algebra.AlgebraicNumber;

        /**
         * 
         * @return {string}
         */
        public getCategory(): string {
            return ScalingTool.ID;
        }

        public constructor(id: string, symmetry: com.vzome.core.math.symmetry.Symmetry, tools: com.vzome.core.editor.ToolsModel) {
            super(id, symmetry, tools);
            if (this.scaleFactor === undefined) { this.scaleFactor = null; }
            this.scaleFactor = null;
            this.setInputBehaviors(false, true);
        }

        setScaleFactor(scaleFactor: com.vzome.core.algebra.AlgebraicNumber) {
            this.scaleFactor = scaleFactor;
        }

        /**
         * 
         * @param {boolean} prepareTool
         * @return {string}
         */
        checkSelection(prepareTool: boolean): string {
            if (this.scaleFactor != null){
                const field: com.vzome.core.algebra.AlgebraicField = this.scaleFactor.getField();
                this.transforms = [null];
                const column1: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X).scale(this.scaleFactor);
                const column2: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Y).scale(this.scaleFactor);
                const column3: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z).scale(this.scaleFactor);
                const p1: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X).scale(field['createPower$int'](4)));
                const p2: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(column2.scale(field['createPower$int'](4)));
                this.addParameter(this.originPoint);
                this.addParameter(new com.vzome.core.construction.SegmentJoiningPoints(this.originPoint, p1));
                this.addParameter(new com.vzome.core.construction.SegmentJoiningPoints(this.originPoint, p2));
                const transform: com.vzome.core.algebra.AlgebraicMatrix = new com.vzome.core.algebra.AlgebraicMatrix(column1, column2, column3);
                this.transforms[0] = new com.vzome.core.construction.MatrixTransformation(transform, this.originPoint.getLocation());
                return null;
            }
            let s1: com.vzome.core.construction.Segment = null;
            let s2: com.vzome.core.construction.Segment = null;
            let center: com.vzome.core.construction.Point = null;
            let correct: boolean = true;
            let hasPanels: boolean = false;
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
                        if (s2 != null){
                            correct = false;
                            break;
                        }
                        if (s1 == null)s1 = <com.vzome.core.construction.Segment>(<com.vzome.core.model.Strut><any>man).getFirstConstruction(); else s2 = <com.vzome.core.construction.Segment>(<com.vzome.core.model.Strut><any>man).getFirstConstruction();
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0))hasPanels = true;
                }
            }
            if (center == null){
                if (prepareTool)center = this.originPoint; else return "No symmetry center selected";
            }
            correct = correct && s2 != null;
            if (!prepareTool && hasPanels)correct = false;
            if (!correct)return "scaling tool requires before and after struts, and a single center";
            const zone1: com.vzome.core.math.symmetry.Axis = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](s1.getOffset());
            const zone2: com.vzome.core.math.symmetry.Axis = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](s2.getOffset());
            if (zone1 == null || zone2 == null)return "struts cannot be automatic";
            const orbit: com.vzome.core.math.symmetry.Direction = zone1.getDirection();
            if (orbit !== zone2.getDirection())return "before and after struts must be from the same orbit";
            if (prepareTool){
                this.transforms = [null];
                this.transforms[0] = new com.vzome.core.construction.Scaling(s1, s2, center, this.symmetry);
            }
            return null;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "ScalingTool";
        }

        /**
         * 
         * @param {*} element
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(element: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const symmName: string = element.getAttribute("symmetry");
            if (symmName == null || /* isEmpty */(symmName.length === 0)){
                element.setAttribute("symmetry", "icosahedral");
                com.vzome.core.editor.api.SideEffects.logBugAccommodation("scaling tool serialized with no symmetry; assuming icosahedral");
            }
            super.setXmlAttributes(element, format);
        }
    }
    ScalingTool["__class"] = "com.vzome.core.tools.ScalingTool";
    ScalingTool["__interfaces"] = ["com.vzome.api.Tool"];


}

