/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class LinearMapTool extends com.vzome.core.tools.TransformationTool {
        static CATEGORY: string = "linear map";

        static LABEL: string = "Create a linear map tool";

        static TOOLTIP: string = "<p><b>For experts and Linear Algebra students...</b><br><br>Each tool applies a linear transformation to the selected<br>objects, possibly rotating, stretching, and compressing.  To<br>create a tool, select a ball as the center of the mapping,<br>three struts (in order) to define the input basis, and three<br>more struts to define the output basis.<br><br>You can omit the input basis if it would consist of three<br>identical blue struts at right angles; the three struts you<br>select will be interpreted as the output basis.<br><br>By default, the input selection will be removed, and replaced<br>with the transformed equivalent.  If you want to keep the inputs,<br>you can right-click after creating the tool, to configure it.<br></p>";

        /*private*/ originalScaling: boolean;

        public constructor(name: string, tools: com.vzome.core.editor.ToolsModel, originalScaling: boolean) {
            super(name, tools);
            if (this.originalScaling === undefined) { this.originalScaling = false; }
            this.originalScaling = originalScaling;
            this.setInputBehaviors(false, true);
        }

        checkSelection(prepareTool: boolean): string {
            const oldBasis: com.vzome.core.construction.Segment[] = [null, null, null];
            const newBasis: com.vzome.core.construction.Segment[] = [null, null, null];
            let index: number = 0;
            let correct: boolean = true;
            let center: com.vzome.core.construction.Point = null;
            for(let index1=this.mSelection.iterator();index1.hasNext();) {
                let man = index1.next();
                {
                    if (prepareTool)this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (center != null){
                            correct = false;
                            break;
                        }
                        center = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (index >= 6){
                            correct = false;
                            break;
                        }
                        if ((index / 3|0) === 0){
                            oldBasis[index % 3] = <com.vzome.core.construction.Segment>man.getFirstConstruction();
                        } else {
                            newBasis[index % 3] = <com.vzome.core.construction.Segment>man.getFirstConstruction();
                        }
                        ++index;
                    }
                }
            }
            correct = correct && ((index === 3) || (index === 6));
            if (!correct)return "linear map tool requires three adjacent, non-parallel struts (or two sets of three) and a single (optional) center ball";
            if (prepareTool){
                if (center == null)center = this.originPoint;
                this.transforms = [null];
                if (index === 6)this.transforms[0] = new com.vzome.core.construction.ChangeOfBasis(oldBasis, newBasis, center); else this.transforms[0] = new com.vzome.core.construction.ChangeOfBasis(oldBasis[0], oldBasis[1], oldBasis[2], center, this.originalScaling);
            }
            return null;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "LinearTransformTool";
        }

        /**
         * 
         * @return {string}
         */
        public getCategory(): string {
            return LinearMapTool.CATEGORY;
        }
    }
    LinearMapTool["__class"] = "com.vzome.core.tools.LinearMapTool";
    LinearMapTool["__interfaces"] = ["com.vzome.api.Tool"];


}

