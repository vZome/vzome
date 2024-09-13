/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class InversionTool extends com.vzome.core.tools.TransformationTool {
        static ID: string = "point reflection";

        static LABEL: string = "Create a point reflection tool";

        static TOOLTIP: string = "<p>Each tool duplicates the selection by reflecting<br>each point through the defined center.  To create a<br>tool, select a single ball that defines that center.<br></p>";

        public constructor(toolName: string, tools: com.vzome.core.editor.ToolsModel) {
            super(toolName, tools);
        }

        /**
         * 
         * @param {boolean} prepareTool
         * @return {string}
         */
        checkSelection(prepareTool: boolean): string {
            let center: com.vzome.core.construction.Point = null;
            if (!this.isAutomatic())for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (prepareTool)this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (center != null)return "more than one center selected";
                        center = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                    } else if (!prepareTool)return "panel or strut selected";
                }
            }
            if (center == null){
                if (prepareTool){
                    center = this.originPoint;
                    this.addParameter(center);
                } else return "No symmetry center selected";
            }
            if (prepareTool){
                this.transforms = [null];
                this.transforms[0] = new com.vzome.core.construction.PointReflection(center);
            }
            return null;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "InversionTool";
        }

        /**
         * 
         * @return {string}
         */
        public getCategory(): string {
            return InversionTool.ID;
        }
    }
    InversionTool["__class"] = "com.vzome.core.tools.InversionTool";
    InversionTool["__interfaces"] = ["com.vzome.api.Tool"];


}

