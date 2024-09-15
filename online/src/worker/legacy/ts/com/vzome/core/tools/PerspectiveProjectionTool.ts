/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class PerspectiveProjectionTool extends com.vzome.core.tools.TransformationTool {
        static ID: string = "perspective";

        static LABEL: string = "Create a perspective projection tool";

        static TOOLTIP: string = "<p>Created tools project selected objects to a 2D plane.<br><br>To create a tool, define the projection<br> by selecting a single panel<br> and a ball not in the plane of the panel.</p>";

        public constructor(id: string, tools: com.vzome.core.editor.ToolsModel) {
            super(id, tools);
            this.setInputBehaviors(false, true);
        }

        /**
         * 
         */
        public perform() {
            let plane: com.vzome.core.construction.Plane = null;
            let point: com.vzome.core.construction.Point = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        if (plane == null){
                            const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                            const polygon: com.vzome.core.construction.Polygon = <com.vzome.core.construction.Polygon>panel.toConstruction();
                            plane = new com.vzome.core.construction.PlaneExtensionOfPolygon(polygon);
                        } else {
                            throw new com.vzome.core.commands.Command.Failure("Projection tool allows only a single selected panel");
                        }
                    }
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (point == null){
                            const ball: com.vzome.core.model.Connector = <com.vzome.core.model.Connector><any>man;
                            point = <com.vzome.core.construction.Point>ball.toConstruction();
                            continue;
                        } else {
                            throw new com.vzome.core.commands.Command.Failure("Projection tool allows only a single selected ball");
                        }
                    }
                }
            }
            if (plane == null || point == null){
                throw new com.vzome.core.commands.Command.Failure("Projection tool requires a selected panel and a selected ball.");
            }
            this.transforms = [null];
            this.transforms[0] = new com.vzome.core.construction.PerspectiveProjection(plane, point);
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


}

