/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class BookmarkTool extends com.vzome.core.editor.Tool {
        public static ID: string = "bookmark";

        static LABEL: string = "Create a selection bookmark";

        static TOOLTIP: string = "<p>A selection bookmark lets you re-create<br>any selection at a later time.</p>";

        /*private*/ bookmarkedConstructions: java.util.List<com.vzome.core.construction.Construction>;

        public constructor(id: string, tools: com.vzome.core.editor.ToolsModel) {
            super(id, tools);
            this.bookmarkedConstructions = <any>(new java.util.ArrayList<any>());
        }

        /**
         * 
         * @return {boolean}
         */
        public isSticky(): boolean {
            return true;
        }

        /**
         * 
         */
        public perform() {
            const duper: com.vzome.core.editor.Duplicator = new com.vzome.core.editor.Duplicator(null, null);
            if (this.mSelection.size() === 0)this.bookmarkedConstructions.add(new com.vzome.core.construction.FreePoint(this.mManifestations.getField().origin(3))); else for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    const result: com.vzome.core.construction.Construction = duper.duplicateConstruction(man);
                    this.bookmarkedConstructions.add(result);
                    this.addParameter(result);
                }
            }
            super.perform();
        }

        /**
         * 
         * @return {boolean}
         */
        public needsInput(): boolean {
            return false;
        }

        /**
         * 
         * @param {com.vzome.core.editor.api.ChangeManifestations} edit
         */
        public prepare(edit: com.vzome.core.editor.api.ChangeManifestations) {
            if (this.bookmarkedConstructions.isEmpty()){
                edit.manifestConstruction(new com.vzome.core.construction.FreePoint(this.mManifestations.getField().origin(3)));
            } else for(let index=this.bookmarkedConstructions.iterator();index.hasNext();) {
                let con = index.next();
                {
                    edit.manifestConstruction(con);
                }
            }
            edit.redo();
        }

        /**
         * 
         * @param {com.vzome.core.editor.api.ChangeManifestations} applyTool
         */
        public complete(applyTool: com.vzome.core.editor.api.ChangeManifestations) {
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} c
         * @param {com.vzome.core.editor.api.ChangeManifestations} applyTool
         */
        public performEdit(c: com.vzome.core.construction.Construction, applyTool: com.vzome.core.editor.api.ChangeManifestations) {
        }

        /**
         * 
         * @param {*} man
         * @param {com.vzome.core.editor.api.ChangeManifestations} applyTool
         */
        public performSelect(man: com.vzome.core.model.Manifestation, applyTool: com.vzome.core.editor.api.ChangeManifestations) {
        }

        /**
         * 
         */
        public redo() {
        }

        /**
         * 
         */
        public undo() {
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "BookmarkTool";
        }

        /**
         * 
         * @return {string}
         */
        public getCategory(): string {
            return BookmarkTool.ID;
        }

        /**
         * 
         * @param {boolean} prepareTool
         * @return {string}
         */
        checkSelection(prepareTool: boolean): string {
            return null;
        }
    }
    BookmarkTool["__class"] = "com.vzome.core.tools.BookmarkTool";
    BookmarkTool["__interfaces"] = ["com.vzome.api.Tool"];


}

