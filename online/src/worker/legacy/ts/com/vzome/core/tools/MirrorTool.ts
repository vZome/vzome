/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class MirrorTool extends com.vzome.core.tools.TransformationTool {
        static ID: string = "mirror";

        static LABEL: string = "Create a mirror reflection tool";

        static TOOLTIP: string = "<p>Each tool duplicates the selection by reflecting<br>each object in a mirror plane.  To create a<br>tool, define the mirror plane by selecting a single<br>panel, or by selecting a strut orthogonal to the<br>plane and a ball lying in the plane.<br></p>";

        symmSys: com.vzome.core.editor.api.OrbitSource;

        public constructor(id: string, tools: com.vzome.core.editor.ToolsModel) {
            super(id, tools);
            if (this.symmSys === undefined) { this.symmSys = null; }
            this.symmSys = (<com.vzome.core.editor.api.SymmetryAware><any>tools.getEditorModel())['getSymmetrySystem$']();
        }

        /**
         * 
         * @param {boolean} prepareTool
         * @return {string}
         */
        checkSelection(prepareTool: boolean): string {
            let center: com.vzome.core.construction.Point = null;
            let axis: com.vzome.core.construction.Segment = null;
            let mirrorPanel: com.vzome.core.construction.Polygon = null;
            if (this.getId() === ("mirror.builtin/reflection through XY plane")){
                center = this.originPoint;
                this.addParameter(center);
                const field: com.vzome.core.algebra.AlgebraicField = this.originPoint.getField();
                const zAxis: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Z).scale(field['createPower$int'](com.vzome.core.math.symmetry.Direction.USER_SCALE));
                const p2: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(zAxis);
                axis = new com.vzome.core.construction.SegmentJoiningPoints(center, p2);
                this.addParameter(axis);
            } else if (this.getId() === ("mirror.builtin/reflection through X=Y green plane")){
                center = this.originPoint;
                this.addParameter(center);
                const field: com.vzome.core.algebra.AlgebraicField = this.originPoint.getField();
                const greenAxis: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X).plus(field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.Y)).scale(field['createPower$int'](com.vzome.core.math.symmetry.Direction.USER_SCALE));
                const p2: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(greenAxis);
                axis = new com.vzome.core.construction.SegmentJoiningPoints(center, p2);
                this.addParameter(axis);
            } else if (this.getId() === ("mirror.builtin/reflection through red plane")){
                center = this.originPoint;
                this.addParameter(center);
                const redAxis: com.vzome.core.algebra.AlgebraicVector = this.symmSys.getSymmetry().getSpecialOrbit(com.vzome.core.math.symmetry.SpecialOrbit.RED).getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, 0).normal();
                const p2: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(redAxis);
                axis = new com.vzome.core.construction.SegmentJoiningPoints(center, p2);
                this.addParameter(axis);
            } else if (this.isAutomatic()){
                center = this.originPoint;
                const field: com.vzome.core.algebra.AlgebraicField = this.originPoint.getField();
                const xAxis: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X);
                const p2: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(xAxis);
                axis = new com.vzome.core.construction.SegmentJoiningPoints(center, p2);
            } else for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (prepareTool)this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (center != null){
                            if (prepareTool)break; else return "Only one center ball may be selected";
                        }
                        center = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (axis != null){
                            if (prepareTool)break; else return "Only one mirror axis strut may be selected";
                        }
                        axis = <com.vzome.core.construction.Segment>(<com.vzome.core.model.Strut><any>man).getFirstConstruction();
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        if (mirrorPanel != null){
                            if (prepareTool)break; else return "Only one mirror panel may be selected";
                        }
                        mirrorPanel = <com.vzome.core.construction.Polygon>(<com.vzome.core.model.Panel><any>man).getFirstConstruction();
                    }
                }
            }
            if (center == null){
                if (prepareTool)center = this.originPoint; else if (mirrorPanel == null)return "No symmetry center selected";
            }
            let mirrorPlane: com.vzome.core.construction.Plane = null;
            if (axis != null && center != null && mirrorPanel == null){
                if (prepareTool)mirrorPlane = new com.vzome.core.construction.PlaneFromNormalSegment(center, axis);
            } else if (axis == null && mirrorPanel != null){
                if (prepareTool)mirrorPlane = new com.vzome.core.construction.PlaneExtensionOfPolygon(mirrorPanel); else if (center != null)return "mirror tool requires a single panel,\nor a single strut and a single center ball";
            } else {
                const msg: string = "mirror tool requires a single panel,\nor a single strut and a single center ball";
                if (prepareTool){
                    throw new java.lang.IllegalStateException("Failed to prepare tool: " + msg);
                } else {
                    return msg;
                }
            }
            if (prepareTool){
                this.transforms = [null];
                this.transforms[0] = new com.vzome.core.construction.PlaneReflection(mirrorPlane);
            }
            return null;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "MirrorTool";
        }

        /**
         * 
         * @return {string}
         */
        public getCategory(): string {
            return MirrorTool.ID;
        }
    }
    MirrorTool["__class"] = "com.vzome.core.tools.MirrorTool";
    MirrorTool["__interfaces"] = ["com.vzome.api.Tool"];


}

