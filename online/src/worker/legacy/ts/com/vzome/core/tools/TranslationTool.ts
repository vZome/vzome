/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class TranslationTool extends com.vzome.core.tools.TransformationTool {
        public constructor(id: string, tools: com.vzome.core.editor.ToolsModel) {
            super(id, tools);
            this.setInputBehaviors(false, true);
        }

        static ID: string = "translation";

        static LABEL: string = "Create a translation tool";

        static TOOLTIP: string = "<p>Each tool moves the selected objects to a new location.<br>To create a tool, select two balls that are separated by<br>your desired translation offset.  Order of selection<br>matters: the first ball selected is the \"from\" location,<br>and the second is the \"to\" location.<br><br>By default, the input selection will be moved to the new<br>location.  If you want to copy rather than move, you can<br>right-click after creating the tool, to configure it.<br></p>";

        /**
         * 
         * @param {boolean} prepareTool
         * @return {string}
         */
        checkSelection(prepareTool: boolean): string {
            let p1: com.vzome.core.construction.Point = null;
            let p2: com.vzome.core.construction.Point = null;
            let correct: boolean = true;
            if (!this.isAutomatic())for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (prepareTool)this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (p2 != null){
                            correct = false;
                            break;
                        }
                        if (p1 == null)p1 = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction(); else p2 = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                    } else if (!prepareTool){
                        return "Only balls can be selected for this tool.";
                    }
                }
            }
            if (p1 == null){
                if (this.isAutomatic() || this.isPredefined()){
                    p1 = this.originPoint;
                    this.addParameter(p1);
                    const field: com.vzome.core.algebra.AlgebraicField = this.originPoint.getField();
                    let xAxis: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, com.vzome.core.algebra.AlgebraicVector.X);
                    let scale: com.vzome.core.algebra.AlgebraicNumber = field['createPower$int'](3);
                    scale = scale['times$com_vzome_core_algebra_AlgebraicNumber'](field['createRational$long'](2));
                    xAxis = xAxis.scale(scale);
                    p2 = new com.vzome.core.construction.FreePoint(xAxis);
                    this.addParameter(p2);
                } else {
                    correct = false;
                }
            } else if (p2 == null)if (prepareTool){
                p2 = p1;
                p1 = this.originPoint;
            } else correct = false;
            if (!correct)return "translation tool requires start and end points, or just an end point";
            if (prepareTool){
                this.transforms = [null];
                this.transforms[0] = new com.vzome.core.construction.PointToPointTranslation(p1, p2);
            }
            return null;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "TranslationTool";
        }

        /**
         * 
         * @return {string}
         */
        public getCategory(): string {
            return TranslationTool.ID;
        }
    }
    TranslationTool["__class"] = "com.vzome.core.tools.TranslationTool";
    TranslationTool["__interfaces"] = ["com.vzome.api.Tool"];


}

