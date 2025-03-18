/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class ProjectionTool extends com.vzome.core.tools.TransformationTool {
        static ID: string = "projection";

        static LABEL: string = "Create a plane projection tool";

        static TOOLTIP: string = "<p>Created tools project selected objects to a 2D plane.<br><br>To create a tool, define the projection plane<br> by selecting either a single panel<br> or strut that is normal to the projection plane<br> and a ball on the plane.<br>When the projection plane is defined by selecting a panel,<br>  an optional strut may be selected to define the line of projection.<br>The default line of projection is orthogonal to the projection plane.<br></p>";

        public constructor(id: string, tools: com.vzome.core.editor.ToolsModel) {
            super(id, tools);
            this.setInputBehaviors(false, true);
        }

        /**
         * 
         */
        public perform() {
            let plane: com.vzome.core.construction.Plane = null;
            let line: com.vzome.core.construction.Line = null;
            let point: com.vzome.core.algebra.AlgebraicVector = null;
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
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        if (line == null){
                            const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>man;
                            const segment: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>strut.toConstruction();
                            line = new com.vzome.core.construction.LineExtensionOfSegment(segment);
                        } else {
                            throw new com.vzome.core.commands.Command.Failure("Projection tool allows only a single selected strut");
                        }
                    }
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (point == null){
                            point = man.getLocation();
                            continue;
                        } else {
                            throw new com.vzome.core.commands.Command.Failure("Projection tool allows only a single selected ball");
                        }
                    }
                }
            }
            if (point != null && line != null){
                plane = new com.vzome.core.construction.PlaneFromPointAndNormal(point, line.getDirection());
            }
            if (plane == null){
                throw new com.vzome.core.commands.Command.Failure("Projection tool requires a selected panel or else a selected ball and strut.");
            }
            this.transforms = [null];
            this.transforms[0] = new com.vzome.core.construction.PlaneProjection(plane, line);
            if (line != null){
                const test: com.vzome.core.algebra.AlgebraicVector = this.transforms[0].transform$com_vzome_core_algebra_AlgebraicVector(line.getDirection());
                if (test == null)throw new com.vzome.core.commands.Command.Failure("Selected strut and plane must not be parallel");
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


}

