import { Command } from "../commands/Command.js";
import { PerspectiveProjection } from "../construction/PerspectiveProjection.js";
import { Plane } from "../construction/Plane.js";
import { PlaneExtensionOfPolygon } from "../construction/PlaneExtensionOfPolygon.js";
import { Point } from "../construction/Point.js";
import { Polygon } from "../construction/Polygon.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { Connector } from "../model/Connector.js";
import { Panel } from "../model/Panel.js";
import { TransformationTool } from "./TransformationTool.js";

export class PerspectiveProjectionTool extends TransformationTool {
    static ID: string = "perspective";

    static LABEL: string = "Create a perspective projection tool";

    static TOOLTIP: string = "<p>Created tools project selected objects to a 2D plane.<br><br>To create a tool, define the projection<br> by selecting a single panel<br> and a ball not in the plane of the panel.</p>";

    public constructor(id: string, tools: ToolsModel) {
        super(id, tools);
        this.setInputBehaviors(false, true);
    }

    /**
     * 
     */
    public perform() {
        let plane: Plane = null;
        let point: Point = null;
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
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    if (point == null){
                        const ball: Connector = <Connector><any>man;
                        point = <Point>ball.toConstruction();
                        continue;
                    } else {
                        throw new Command.Failure("Projection tool allows only a single selected ball");
                    }
                }
            }
        }
        if (plane == null || point == null){
            throw new Command.Failure("Projection tool requires a selected panel and a selected ball.");
        }
        this.transforms = [null];
        this.transforms[0] = new PerspectiveProjection(plane, point);
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
        return "PerspectiveProjectionTool";
    }

    /**
     * 
     * @return {string}
     */
    public getCategory(): string {
        return PerspectiveProjectionTool.ID;
    }
}
PerspectiveProjectionTool["__class"] = "com.vzome.core.tools.PerspectiveProjectionTool";
PerspectiveProjectionTool["__interfaces"] = ["com.vzome.api.Tool"];
