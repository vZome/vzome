/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class DeselectAll extends com.vzome.core.editor.api.ChangeSelection {
        /**
         * 
         */
        public perform() {
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation$boolean(man, true);
                }
            }
            super.perform();
        }

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor.getSelection());
        }

        /**
         * 
         * @return {boolean}
         */
        groupingAware(): boolean {
            return true;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "DeselectAll";
        }
    }
    DeselectAll["__class"] = "com.vzome.core.edits.DeselectAll";

}

