/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export interface EditorModel extends com.vzome.core.editor.api.SymmetryAware {
        getRealizedModel(): com.vzome.core.model.RealizedModel;

        getSelection(): com.vzome.core.editor.api.Selection;

        addSelectionSummaryListener(listener: com.vzome.core.editor.SelectionSummary.Listener);
    }
}

