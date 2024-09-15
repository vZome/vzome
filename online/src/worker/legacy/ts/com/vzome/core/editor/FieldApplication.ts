/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export interface FieldApplication extends com.vzome.core.math.symmetry.Symmetries4D {
        getField(): com.vzome.core.algebra.AlgebraicField;

        getSymmetryPerspectives(): java.util.Collection<com.vzome.core.editor.SymmetryPerspective>;

        getDefaultSymmetryPerspective(): com.vzome.core.editor.SymmetryPerspective;

        getSymmetryPerspective(name: string): com.vzome.core.editor.SymmetryPerspective;

        getName(): string;

        getLabel(): string;

        registerToolFactories(toolFactories: java.util.Map<string, com.vzome.api.Tool.Factory>, tools: com.vzome.core.editor.ToolsModel);

        /**
         * These commands should all be symmetry-INDEPENDANT.
         * Contrast with {@code FieldApplication.SymmetryPerspective.getLegacyCommand(action) }.
         * @param {string} action
         * @return
         * @return {*}
         */
        getLegacyCommand(action: string): com.vzome.core.commands.Command;
    }
}

