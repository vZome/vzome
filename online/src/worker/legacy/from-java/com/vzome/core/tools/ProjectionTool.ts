import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Command } from "../commands/Command.js";
import { Line } from "../construction/Line.js";
import { LineExtensionOfSegment } from "../construction/LineExtensionOfSegment.js";
import { Plane } from "../construction/Plane.js";
import { PlaneExtensionOfPolygon } from "../construction/PlaneExtensionOfPolygon.js";
import { PlaneFromPointAndNormal } from "../construction/PlaneFromPointAndNormal.js";
import { PlaneProjection } from "../construction/PlaneProjection.js";
import { Polygon } from "../construction/Polygon.js";
import { Segment } from "../construction/Segment.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Panel } from "../model/Panel.js";
import { Strut } from "../model/Strut.js";
import { TransformationTool } from "./TransformationTool.js";

export class ProjectionTool extends TransformationTool {
    static ID: string = "projection";

    static LABEL: string = "Create a plane projection tool";

    static TOOLTIP: string = "<p>Created tools project selected objects to a 2D plane.<br><br>To create a tool, define the projection plane<br> by selecting either a single panel<br> or strut that is normal to the projection plane<br> and a ball on the plane.<br>When the projection plane is defined by selecting a panel,<br>  an optional strut may be selected to define the line of projection.<br>The default line of projection is orthogonal to the projection plane.<br></p>";

    public constructor(id: string, tools: ToolsModel) {
        super(id, tools);
        this.setInputBehaviors(false, true);
    }

    /**
     * 
     */
    public perform() {
        let plane: Plane = null;
        let line: Line = null;
        let point: AlgebraicVector = null;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    if (plane == null){
                        const panel: Panel = <Panel><any>man;
                        const polygon: Polygon = <Polygon>panel.toConstruction();
                        plane = new PlaneExtensionOfPolygon(polygon);
                    } else {
                        throw new Command.Failure("Projection tool allows only a single selected panel");
                    }
                }
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    if (line == null){
                        const strut: Strut = <Strut><any>man;
                        const segment: Segment = <Segment>strut.toConstruction();
                        line = new LineExtensionOfSegment(segment);
                    } else {
                        throw new Command.Failure("Projection tool allows only a single selected strut");
                    }
                }
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    if (point == null){
                        point = man.getLocation();
                        continue;
                    } else {
                        throw new Command.Failure("Projection tool allows only a single selected ball");
                    }
                }
            }
        }
        if (point != null && line != null){
            plane = new PlaneFromPointAndNormal(point, line.getDirection());
        }
        if (plane == null){
            throw new Command.Failure("Projection tool requires a selected panel or else a selected ball and strut.");
        }
        this.transforms = [null];
        this.transforms[0] = new PlaneProjection(plane, line);
        if (line != null){
            const test: AlgebraicVector = this.transforms[0].transform$com_vzome_core_algebra_AlgebraicVector(line.getDirection());
            if (test == null)throw new Command.Failure("Selected strut and plane must not be parallel");
        }
        super.perform();
    }

    /**
     * 
     * @param {boolean} prepareTool
     * @return {string}
     */
    checkSelection(prepareTool: boolean): string {
        return null;
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "ProjectionTool";
    }

    /**
     * 
     * @return {string}
     */
    public getCategory(): string {
        return ProjectionTool.ID;
    }
}
ProjectionTool["__class"] = "com.vzome.core.tools.ProjectionTool";
ProjectionTool["__interfaces"] = ["com.vzome.api.Tool"];
