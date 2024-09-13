/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    /**
     * Used for the throwaway factories used to create predefined tools.
     * Using the symmetry name in the factory ID prevents collisions across
     * symmetries for common tools.
     * @param {com.vzome.core.editor.ToolsModel} tools
     * @param {*} symmetry
     * @param {boolean} useSymmetryName
     * @class
     * @extends com.vzome.core.tools.RotationToolFactory
     */
    export class AxialSymmetryToolFactory extends com.vzome.core.tools.RotationToolFactory {
        static ID: string = "axial symmetry";

        static __com_vzome_core_tools_AxialSymmetryToolFactory_LABEL: string = "Create a rotational symmetry tool";

        static __com_vzome_core_tools_AxialSymmetryToolFactory_TOOLTIP: string = "<p>Each tool creates enough copies of the selected objects to<br>create rotational symmetry around an axis.  To create a tool,<br>select a strut that defines that axis,  You can also define<br>the direction and center independently, by selecting a ball<br>for the center and a strut for the axis.  Note: not all struts<br>correspond to rotational symmetries!<br><br>Combine with a point reflection or mirror reflection tool to<br>achieve more symmetries.<br></p>";

        public constructor(tools: com.vzome.core.editor.ToolsModel, symmetry: com.vzome.core.math.symmetry.Symmetry, useSymmetryName: boolean = false) {
            super(tools, symmetry, (useSymmetryName ? symmetry.getName() + ' ' : "") + AxialSymmetryToolFactory.ID, AxialSymmetryToolFactory.__com_vzome_core_tools_AxialSymmetryToolFactory_LABEL, AxialSymmetryToolFactory.__com_vzome_core_tools_AxialSymmetryToolFactory_TOOLTIP);
        }

        /**
         * 
         * @param {string} id
         * @return {com.vzome.core.editor.Tool}
         */
        public createToolInternal(id: string): com.vzome.core.editor.Tool {
            return new com.vzome.core.tools.RotationTool(id, this.getSymmetry(), this.getToolsModel(), true);
        }
    }
    AxialSymmetryToolFactory["__class"] = "com.vzome.core.tools.AxialSymmetryToolFactory";
    AxialSymmetryToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];


}

