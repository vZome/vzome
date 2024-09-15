/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class ModuleTool extends com.vzome.core.editor.Tool {
        static ID: string = "module";

        static LABEL: string = "Create a module tool";

        static TOOLTIP: string = "<p>Each tool duplicates the original module.<br></p>";

        /*private*/ name: string;

        /*private*/ bookmarkedSelection: java.util.List<com.vzome.core.model.Manifestation>;

        public constructor(id: string, tools: com.vzome.core.editor.ToolsModel) {
            super(id, tools);
            if (this.name === undefined) { this.name = null; }
            this.bookmarkedSelection = <any>(new java.util.ArrayList<any>());
            this.mSelection.copy(this.bookmarkedSelection);
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
         * @param {com.vzome.core.editor.api.ChangeManifestations} applyTool
         */
        public prepare(applyTool: com.vzome.core.editor.api.ChangeManifestations) {
        }

        /**
         * 
         * @param {com.vzome.core.editor.api.ChangeManifestations} applyTool
         */
        public complete(applyTool: com.vzome.core.editor.api.ChangeManifestations) {
        }

        /**
         * 
         * @return {boolean}
         */
        public needsInput(): boolean {
            return true;
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} c
         * @param {com.vzome.core.editor.api.ChangeManifestations} applyTool
         */
        public performEdit(c: com.vzome.core.construction.Construction, applyTool: com.vzome.core.editor.api.ChangeManifestations) {
            if (!(c != null && c instanceof <any>com.vzome.core.construction.Point))return;
            const p: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>c;
            const loc: com.vzome.core.algebra.AlgebraicVector = p.getLocation();
            const duper: com.vzome.core.editor.Duplicator = new com.vzome.core.editor.Duplicator(applyTool, loc);
            for(let index=this.bookmarkedSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    duper.duplicateManifestation(man);
                }
            }
            applyTool.redo();
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
            return "ModuleTool";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            element.setAttribute("name", this.name);
        }

        /**
         * 
         * @param {*} element
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(element: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.name = element.getAttribute("name");
        }

        /**
         * 
         * @return {string}
         */
        public getCategory(): string {
            return ModuleTool.ID;
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
    ModuleTool["__class"] = "com.vzome.core.tools.ModuleTool";
    ModuleTool["__interfaces"] = ["com.vzome.api.Tool"];


}

