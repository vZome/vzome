/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class SymmetryToolFactory extends com.vzome.core.editor.AbstractToolFactory {
        public constructor(tools: com.vzome.core.editor.ToolsModel, symmetry: com.vzome.core.math.symmetry.Symmetry) {
            super(tools, symmetry, com.vzome.core.tools.SymmetryTool.ID, com.vzome.core.tools.SymmetryTool.LABEL, com.vzome.core.tools.SymmetryTool.TOOLTIP);
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
            return (total === 1 && balls === 1);
        }

        /**
         * 
         * @param {string} id
         * @return {com.vzome.core.editor.Tool}
         */
        public createToolInternal(id: string): com.vzome.core.editor.Tool {
            return new com.vzome.core.tools.SymmetryTool(id, this.getSymmetry(), this.getToolsModel());
        }

        /**
         * 
         * @param {*} selection
         * @return {boolean}
         */
        bindParameters(selection: com.vzome.core.editor.api.Selection): boolean {
            return selection.size() === 1 && (selection.iterator().next() != null && (selection.iterator().next().constructor != null && selection.iterator().next().constructor["__interfaces"] != null && selection.iterator().next().constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0));
        }
    }
    SymmetryToolFactory["__class"] = "com.vzome.core.tools.SymmetryToolFactory";
    SymmetryToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];


}

