/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class StrutBuilderController extends com.vzome.desktop.controller.DefaultController {
        /*private*/ useGraphicalViews: boolean;

        /*private*/ showStrutScales: boolean;

        /*private*/ useWorkingPlane: boolean;

        /*private*/ workingPlaneAxis: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ previewStrut: com.vzome.desktop.controller.PreviewStrut;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ context: com.vzome.core.editor.api.Context;

        public constructor(context: com.vzome.core.editor.api.Context, field: com.vzome.core.algebra.AlgebraicField) {
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

        public setWorkingPlaneAxis(axis: com.vzome.core.algebra.AlgebraicVector) {
            this.workingPlaneAxis = axis;
            this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("workingPlaneDefined", false, true);
        }

        public setMainScene(mainScene: com.vzome.core.render.RenderingChanges) {
            this.previewStrut = new com.vzome.desktop.controller.PreviewStrut(this.field, mainScene, this.context);
        }

        public getPreviewStrut(): com.vzome.desktop.controller.PreviewStrut {
            return this.previewStrut;
        }

        public startRendering(point: com.vzome.core.construction.Point, worldEye: com.vzome.core.math.RealVector) {
            const axis: com.vzome.core.algebra.AlgebraicVector = this.useWorkingPlane ? this.workingPlaneAxis : null;
            this.previewStrut.startRendering(point, axis, worldEye);
        }

        public setSymmetryController(symmetryController: com.vzome.desktop.controller.SymmetryController) {
            if (this.previewStrut != null)this.previewStrut.setSymmetryController(symmetryController);
        }
    }
    StrutBuilderController["__class"] = "com.vzome.desktop.controller.StrutBuilderController";
    StrutBuilderController["__interfaces"] = ["com.vzome.desktop.api.Controller"];


}

