/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class IcosahedralToolFactory extends com.vzome.core.editor.AbstractToolFactory {
        static ID: string = "icosahedral";

        static LABEL: string = "Create an icosahedral symmetry tool";

        static TOOLTIP: string = "<p>Each tool produces up to 59 copies of the input<br>selection, using the rotation symmetries of an<br>icosahedron.  To create a tool, select a single<br>ball that defines the center of symmetry.<br><br>Combine with a point reflection tool to achieve<br>all 120 symmetries of the icosahedron, including<br>reflections.<br></p>";

        public constructor(tools: com.vzome.core.editor.ToolsModel, symmetry: com.vzome.core.math.symmetry.IcosahedralSymmetry) {
            super(tools, symmetry, IcosahedralToolFactory.ID, IcosahedralToolFactory.LABEL, IcosahedralToolFactory.TOOLTIP);
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
    IcosahedralToolFactory["__class"] = "com.vzome.core.tools.IcosahedralToolFactory";
    IcosahedralToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];


}

