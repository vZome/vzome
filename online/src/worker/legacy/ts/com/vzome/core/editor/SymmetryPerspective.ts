/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export interface SymmetryPerspective {
        getGeometries(): java.util.List<com.vzome.core.editor.api.Shapes>;

        getDefaultGeometry(): com.vzome.core.editor.api.Shapes;

        getName(): string;

        getSymmetry(): com.vzome.core.math.symmetry.Symmetry;

        createToolFactories(kind: com.vzome.api.Tool.Kind, model: com.vzome.core.editor.ToolsModel): java.util.List<com.vzome.api.Tool.Factory>;

        predefineTools(kind: com.vzome.api.Tool.Kind, model: com.vzome.core.editor.ToolsModel): java.util.List<com.vzome.api.Tool>;

        /**
         * These commands should all be symmetry-DEPENDANT.
         * Contrast with {@code FieldApplication.getLegacyCommand(action) }.
         * @param {string} action
         * @return
         * @return {*}
         */
        getLegacyCommand(action: string): com.vzome.core.commands.Command;

        getModelResourcePath(): string;

        orbitIsStandard(orbit: com.vzome.core.math.symmetry.Direction): boolean;

        orbitIsBuildDefault(orbit: com.vzome.core.math.symmetry.Direction): boolean;

        getOrbitUnitLength(orbit: com.vzome.core.math.symmetry.Direction): com.vzome.core.algebra.AlgebraicNumber;

        getLabel(): string;
    }
}

