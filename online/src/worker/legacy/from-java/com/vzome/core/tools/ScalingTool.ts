import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { FreePoint } from "../construction/FreePoint.js";
import { MatrixTransformation } from "../construction/MatrixTransformation.js";
import { Point } from "../construction/Point.js";
import { Scaling } from "../construction/Scaling.js";
import { Segment } from "../construction/Segment.js";
import { SegmentJoiningPoints } from "../construction/SegmentJoiningPoints.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { SideEffects } from "../editor/api/SideEffects.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { Connector } from "../model/Connector.js";
import { Strut } from "../model/Strut.js";
import { SymmetryTool } from "./SymmetryTool.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class ScalingTool extends SymmetryTool {
    static ID: string = "scaling";

    static LABEL: string = "Create a scaling tool";

    static TOOLTIP: string = "<p>Each tool enlarges or shrinks the selected objects,<br>relative to a central point.  To create a tool,<br>select a ball representing the central point, and<br>two struts from the same orbit (color) with different<br>sizes.<br><br>The selection order matters.  First select a strut<br>that you want to enlarge or shrink, then select a<br>strut that has the desired target size.<br></p>";

    /*private*/ scaleFactor: AlgebraicNumber;

    /**
     * 
     * @return {string}
     */
    public getCategory(): string {
        return ScalingTool.ID;
    }

    public constructor(id: string, symmetry: Symmetry, tools: ToolsModel) {
        super(id, symmetry, tools);
        if (this.scaleFactor === undefined) { this.scaleFactor = null; }
        this.scaleFactor = null;
        this.setInputBehaviors(false, true);
    }

    setScaleFactor(scaleFactor: AlgebraicNumber) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * 
     * @param {boolean} prepareTool
     * @return {string}
     */
    checkSelection(prepareTool: boolean): string {
        if (this.scaleFactor != null){
            const field: AlgebraicField = this.scaleFactor.getField();
            this.transforms = [null];
            const column1: AlgebraicVector = field.basisVector(3, AlgebraicVector.X).scale(this.scaleFactor);
            const column2: AlgebraicVector = field.basisVector(3, AlgebraicVector.Y).scale(this.scaleFactor);
            const column3: AlgebraicVector = field.basisVector(3, AlgebraicVector.Z).scale(this.scaleFactor);
            const p1: Point = new FreePoint(field.basisVector(3, AlgebraicVector.X).scale(field['createPower$int'](4)));
            const p2: Point = new FreePoint(column2.scale(field['createPower$int'](4)));
            this.addParameter(this.originPoint);
            this.addParameter(new SegmentJoiningPoints(this.originPoint, p1));
            this.addParameter(new SegmentJoiningPoints(this.originPoint, p2));
            const transform: AlgebraicMatrix = new AlgebraicMatrix(column1, column2, column3);
            this.transforms[0] = new MatrixTransformation(transform, this.originPoint.getLocation());
            return null;
        }
        let s1: Segment = null;
        let s2: Segment = null;
        let center: Point = null;
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
                    center = <Point>(<Connector><any>man).getFirstConstruction();
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    if (s2 != null){
                        correct = false;
                        break;
                    }
                    if (s1 == null)s1 = <Segment>(<Strut><any>man).getFirstConstruction(); else s2 = <Segment>(<Strut><any>man).getFirstConstruction();
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0))hasPanels = true;
            }
        }
        if (center == null){
            if (prepareTool)center = this.originPoint; else return "No symmetry center selected";
        }
        correct = correct && s2 != null;
        if (!prepareTool && hasPanels)correct = false;
        if (!correct)return "scaling tool requires before and after struts, and a single center";
        const zone1: Axis = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](s1.getOffset());
        const zone2: Axis = this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](s2.getOffset());
        if (zone1 == null || zone2 == null)return "struts cannot be automatic";
        const orbit: Direction = zone1.getDirection();
        if (orbit !== zone2.getDirection())return "before and after struts must be from the same orbit";
        if (prepareTool){
            this.transforms = [null];
            this.transforms[0] = new Scaling(s1, s2, center, this.symmetry);
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
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(element: Element, format: XmlSaveFormat) {
        const symmName: string = element.getAttribute("symmetry");
        if (symmName == null || /* isEmpty */(symmName.length === 0)){
            element.setAttribute("symmetry", "icosahedral");
            SideEffects.logBugAccommodation("scaling tool serialized with no symmetry; assuming icosahedral");
        }
        super.setXmlAttributes(element, format);
    }
}
ScalingTool["__class"] = "com.vzome.core.tools.ScalingTool";
ScalingTool["__interfaces"] = ["com.vzome.api.Tool"];
