/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class LineReflectionTool extends com.vzome.core.tools.TransformationTool {
        public constructor(id: string, tools: com.vzome.core.editor.ToolsModel) {
            super(id, tools);
            this.setCopyColors(false);
        }

        /**
         * 
         * @param {boolean} prepareTool
         * @return {string}
         */
        checkSelection(prepareTool: boolean): string {
            let axis: com.vzome.core.construction.Segment = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (prepareTool){
                        this.unselect$com_vzome_core_model_Manifestation(man);
                    }
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (axis != null){
                            if (prepareTool){
                                break;
                            } else {
                                return "Only one mirror axis strut may be selected";
                            }
                        }
                        axis = <com.vzome.core.construction.Segment>(<com.vzome.core.model.Strut><any>man).getFirstConstruction();
                    }
                }
            }
            if (axis == null){
                return "line reflection tool requires a single strut";
            }
            if (prepareTool){
                this.transforms = [null];
                this.transforms[0] = new com.vzome.core.construction.LineReflection(axis);
            }
            return null;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "LineReflectionTool";
        }
    }
    LineReflectionTool["__class"] = "com.vzome.core.tools.LineReflectionTool";
    LineReflectionTool["__interfaces"] = ["com.vzome.api.Tool"];


}

