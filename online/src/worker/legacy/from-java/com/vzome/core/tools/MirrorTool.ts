import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Plane } from "../construction/Plane.js";
import { PlaneExtensionOfPolygon } from "../construction/PlaneExtensionOfPolygon.js";
import { PlaneFromNormalSegment } from "../construction/PlaneFromNormalSegment.js";
import { PlaneReflection } from "../construction/PlaneReflection.js";
import { Point } from "../construction/Point.js";
import { Polygon } from "../construction/Polygon.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { OrbitSource } from "../editor/api/OrbitSource.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { Direction } from "../math/symmetry/Direction.js";
import { SpecialOrbit } from "../math/symmetry/SpecialOrbit.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { Connector } from "../model/Connector.js";
import { Panel } from "../model/Panel.js";
import { Strut } from "../model/Strut.js";
import { TransformationTool } from "./TransformationTool.js";

export class MirrorTool extends TransformationTool {
    static ID: string = "mirror";

    static LABEL: string = "Create a mirror reflection tool";

    static TOOLTIP: string = "<p>Each tool duplicates the selection by reflecting<br>each object in a mirror plane.  To create a<br>tool, define the mirror plane by selecting a single<br>panel, or by selecting a strut orthogonal to the<br>plane and a ball lying in the plane.<br></p>";

    symmSys: OrbitSource;

    public constructor(id: string, tools: ToolsModel) {
        super(id, tools);
        if (this.symmSys === undefined) { this.symmSys = null; }
        this.symmSys = (<SymmetryAware><any>tools.getEditorModel())['getSymmetrySystem$']();
    }

    /**
     * 
     * @param {boolean} prepareTool
     * @return {string}
     */
    checkSelection(prepareTool: boolean): string {
        let center: Point = null;
        let axis: Segment = null;
        let mirrorPanel: Polygon = null;
        if (this.getId() === ("mirror.builtin/reflection through XY plane")){
            center = this.originPoint;
            this.addParameter(center);
            const field: AlgebraicField = this.originPoint.getField();
            const zAxis: AlgebraicVector = field.basisVector(3, AlgebraicVector.Z).scale(field['createPower$int'](Direction.USER_SCALE));
            const p2: Point = new FreePoint(zAxis);
            axis = new SegmentJoiningPoints(center, p2);
            this.addParameter(axis);
        } else if (this.getId() === ("mirror.builtin/reflection through X=Y green plane")){
            center = this.originPoint;
            this.addParameter(center);
            const field: AlgebraicField = this.originPoint.getField();
            const greenAxis: AlgebraicVector = field.basisVector(3, AlgebraicVector.X).plus(field.basisVector(3, AlgebraicVector.Y)).scale(field['createPower$int'](Direction.USER_SCALE));
            const p2: Point = new FreePoint(greenAxis);
            axis = new SegmentJoiningPoints(center, p2);
            this.addParameter(axis);
        } else if (this.getId() === ("mirror.builtin/reflection through red plane")){
            center = this.originPoint;
            this.addParameter(center);
            const redAxis: AlgebraicVector = this.symmSys.getSymmetry().getSpecialOrbit(SpecialOrbit.RED).getAxis$int$int(Symmetry.PLUS, 0).normal();
            const p2: Point = new FreePoint(redAxis);
            axis = new SegmentJoiningPoints(center, p2);
            this.addParameter(axis);
        } else if (this.isAutomatic()){
            center = this.originPoint;
            const field: AlgebraicField = this.originPoint.getField();
            const xAxis: AlgebraicVector = field.basisVector(3, AlgebraicVector.X);
            const p2: Point = new FreePoint(xAxis);
            axis = new SegmentJoiningPoints(center, p2);
        } else for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (prepareTool)this.unselect$com_vzome_core_model_Manifestation(man);
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    if (center != null){
                        if (prepareTool)break; else return "Only one center ball may be selected";
                    }
                    center = <Point>(<Connector><any>man).getFirstConstruction();
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    if (axis != null){
                        if (prepareTool)break; else return "Only one mirror axis strut may be selected";
                    }
                    axis = <Segment>(<Strut><any>man).getFirstConstruction();
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    if (mirrorPanel != null){
                        if (prepareTool)break; else return "Only one mirror panel may be selected";
                    }
                    mirrorPanel = <Polygon>(<Panel><any>man).getFirstConstruction();
                }
            }
        }
        if (center == null){
            if (prepareTool)center = this.originPoint; else if (mirrorPanel == null)return "No symmetry center selected";
        }
        let mirrorPlane: Plane = null;
        if (axis != null && center != null && mirrorPanel == null){
            if (prepareTool)mirrorPlane = new PlaneFromNormalSegment(center, axis);
        } else if (axis == null && mirrorPanel != null){
            if (prepareTool)mirrorPlane = new PlaneExtensionOfPolygon(mirrorPanel); else if (center != null)return "mirror tool requires a single panel,\nor a single strut and a single center ball";
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
            this.transforms[0] = new PlaneReflection(mirrorPlane);
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
