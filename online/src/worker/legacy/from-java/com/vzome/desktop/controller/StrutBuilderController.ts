import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../../core/algebra/AlgebraicField.js";
import { AlgebraicVector } from "../../core/algebra/AlgebraicVector.js";
import { Point } from "../../core/construction/Point.js";
import { Context } from "../../core/editor/api/Context.js";
import { RealVector } from "../../core/math/RealVector.js";
import { RenderingChanges } from "../../core/render/RenderingChanges.js";
import { DefaultController } from "./DefaultController.js";
import { PreviewStrut } from "./PreviewStrut.js";
import { SymmetryController } from "./SymmetryController.js";

export class StrutBuilderController extends DefaultController {
    /*private*/ useGraphicalViews: boolean;

    /*private*/ showStrutScales: boolean;

    /*private*/ useWorkingPlane: boolean;

    /*private*/ workingPlaneAxis: AlgebraicVector;

    /*private*/ previewStrut: PreviewStrut;

    /*private*/ field: AlgebraicField;

    /*private*/ context: Context;

    public constructor(context: Context, field: AlgebraicField) {
        super();
        this.useGraphicalViews = false;
        this.showStrutScales = false;
        this.useWorkingPlane = false;
        this.workingPlaneAxis = null;
        if (this.previewStrut === undefined) { this.previewStrut = null; }
        if (this.field === undefined) { this.field = null; }
        if (this.context === undefined) { this.context = null; }
        this.context = context;
        this.field = field;
    }

    public withGraphicalViews(value: boolean): StrutBuilderController {
        this.useGraphicalViews = value;
        return this;
    }

    public withShowStrutScales(value: boolean): StrutBuilderController {
        this.showStrutScales = value;
        return this;
    }

    /**
     * 
     * @param {string} propName
     * @return {string}
     */
    public getProperty(propName: string): string {
        switch((propName)) {
        case "useGraphicalViews":
            return javaemul.internal.BooleanHelper.toString(this.useGraphicalViews);
        case "useWorkingPlane":
            return javaemul.internal.BooleanHelper.toString(this.useWorkingPlane);
        case "workingPlaneDefined":
            return javaemul.internal.BooleanHelper.toString(this.workingPlaneAxis != null);
        case "showStrutScales":
            return javaemul.internal.BooleanHelper.toString(this.showStrutScales);
        default:
            return super.getProperty(propName);
        }
    }

    /**
     * 
     * @param {string} name
     * @param {*} value
     */
    public setModelProperty(name: string, value: any) {
        switch((name)) {
        case "useGraphicalViews":
            {
                const old: boolean = this.useGraphicalViews;
                this.useGraphicalViews = /* equals */(<any>((o1: any, o2: any) => o1 && o1.equals ? o1.equals(o2) : o1 === o2)("true",value));
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(name, old, this.useGraphicalViews);
                break;
            };
        case "showStrutScales":
            {
                const old: boolean = this.showStrutScales;
                this.showStrutScales = /* equals */(<any>((o1: any, o2: any) => o1 && o1.equals ? o1.equals(o2) : o1 === o2)("true",value));
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(name, old, this.showStrutScales);
                break;
            };
        default:
            super.setModelProperty(name, value);
        }
    }

    /**
     * 
     * @param {string} action
     */
    public doAction(action: string) {
        switch((action)) {
        case "toggleWorkingPlane":
            this.useWorkingPlane = !this.useWorkingPlane;
            break;
        case "toggleOrbitViews":
            {
                const old: boolean = this.useGraphicalViews;
                this.useGraphicalViews = !old;
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("useGraphicalViews", old, this.useGraphicalViews);
                break;
            };
        case "toggleStrutScales":
            {
                const old: boolean = this.showStrutScales;
                this.showStrutScales = !old;
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("showStrutScales", old, this.showStrutScales);
                break;
            };
        default:
            super.doAction(action);
        }
    }

    public setWorkingPlaneAxis(axis: AlgebraicVector) {
        this.workingPlaneAxis = axis;
        this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("workingPlaneDefined", false, true);
    }

    public setMainScene(mainScene: RenderingChanges) {
        this.previewStrut = new PreviewStrut(this.field, mainScene, this.context);
    }

    public getPreviewStrut(): PreviewStrut {
        return this.previewStrut;
    }

    public startRendering(point: Point, worldEye: RealVector) {
        const axis: AlgebraicVector = this.useWorkingPlane ? this.workingPlaneAxis : null;
        this.previewStrut.startRendering(point, axis, worldEye);
    }

    public setSymmetryController(symmetryController: SymmetryController) {
        if (this.previewStrut != null)this.previewStrut.setSymmetryController(symmetryController);
    }
}
StrutBuilderController["__class"] = "com.vzome.desktop.controller.StrutBuilderController";
StrutBuilderController["__interfaces"] = ["com.vzome.desktop.api.Controller"];
