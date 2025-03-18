/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class ValidateSelection extends com.vzome.core.editor.api.ChangeSelection {
        /**
         * 
         */
        public perform() {
            if (this.mSelection.size() === 0)throw new com.vzome.core.commands.Command.Failure("selection is empty");
        }

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor.getSelection());
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "ValidateSelection";
        }
    }
    ValidateSelection["__class"] = "com.vzome.core.edits.ValidateSelection";

}

