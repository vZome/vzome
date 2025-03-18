/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class LineReflectionToolFactory extends com.vzome.core.editor.AbstractToolFactory {
        static ID: string = "line reflection";

        static LABEL: string = "Create a line reflection tool";

        static TOOLTIP: string = "<p>Each tool duplicates the selection by reflecting<br>each object in a line.  To create a tool,<br>define the mirror line by selecting a strut.<br></p>";

        public constructor(tools: com.vzome.core.editor.ToolsModel) {
            super(tools, null, LineReflectionToolFactory.ID, LineReflectionToolFactory.LABEL, LineReflectionToolFactory.TOOLTIP);
        }

        /**
         * 
         * @param {string} id
         * @return {com.vzome.core.editor.Tool}
         */
        public createToolInternal(id: string): com.vzome.core.editor.Tool {
            return new com.vzome.core.tools.LineReflectionTool(id, this.getToolsModel());
        }

        /**
         * 
         * @param {number} total
         * @param {number} balls
         * @param {number} struts
         * @param {number} panels
         * @return {boolean}
         */
        countsAreValid(total: number, balls: number, struts: number, panels: number): boolean {
            return (total === 1 && struts === 1);
        }

        /**
         * 
         * @param {*} selection
         * @return {boolean}
         */
        bindParameters(selection: com.vzome.core.editor.api.Selection): boolean {
            return true;
        }
    }
    LineReflectionToolFactory["__class"] = "com.vzome.core.tools.LineReflectionToolFactory";
    LineReflectionToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];


}

