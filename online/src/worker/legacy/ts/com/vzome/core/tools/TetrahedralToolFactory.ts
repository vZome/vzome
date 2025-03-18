/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class TetrahedralToolFactory extends com.vzome.core.tools.OctahedralToolFactory {
        static __com_vzome_core_tools_TetrahedralToolFactory_ID: string = "tetrahedral";

        static __com_vzome_core_tools_TetrahedralToolFactory_LABEL: string = "Create a tetrahedral symmetry tool";

        static TOOLTIP1: string = "<p>Each tool produces up to 11 copies of the input<br>selection, using the rotation symmetries of a<br>tetrahedron.  To create a tool, select a ball<br>that defines the center of symmetry, and a single<br>blue or green strut, defining one of five<br>possible orientations for the symmetry.<br><br>Combine with a point reflection tool to achieve<br>all 24 symmetries of the tetrahedron, including<br>reflections.<br></p>";

        static __com_vzome_core_tools_TetrahedralToolFactory_TOOLTIP2: string = "<p>Each tool produces up to 11 copies of the input<br>selection, using the rotation symmetries of a<br>tetrahedron.  To create a tool, select a ball<br>that defines the center of symmetry.<br><br>Combine with a point reflection tool to achieve<br>all 24 symmetries of the tetrahedron, including<br>reflections.<br></p>";

        public constructor(tools: com.vzome.core.editor.ToolsModel, symmetry: com.vzome.core.math.symmetry.Symmetry) {
            super(tools, symmetry, TetrahedralToolFactory.__com_vzome_core_tools_TetrahedralToolFactory_ID, TetrahedralToolFactory.__com_vzome_core_tools_TetrahedralToolFactory_LABEL, "icosahedral" === symmetry.getName() ? TetrahedralToolFactory.TOOLTIP1 : TetrahedralToolFactory.__com_vzome_core_tools_TetrahedralToolFactory_TOOLTIP2);
        }

        /**
         * 
         * @param {string} id
         * @return {com.vzome.core.editor.Tool}
         */
        public createToolInternal(id: string): com.vzome.core.editor.Tool {
            if (!/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(id, "tetrahedral"))id = "tetrahedral." + id;
            return new com.vzome.core.tools.SymmetryTool(id, this.getSymmetry(), this.getToolsModel());
        }
    }
    TetrahedralToolFactory["__class"] = "com.vzome.core.tools.TetrahedralToolFactory";
    TetrahedralToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];


}

