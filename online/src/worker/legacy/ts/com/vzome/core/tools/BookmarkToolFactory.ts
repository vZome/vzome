/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.tools {
    export class BookmarkToolFactory extends com.vzome.core.editor.AbstractToolFactory {
        public constructor(tools: com.vzome.core.editor.ToolsModel) {
            super(tools, null, com.vzome.core.tools.BookmarkTool.ID, com.vzome.core.tools.BookmarkTool.LABEL, com.vzome.core.tools.BookmarkTool.TOOLTIP);
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
            return (total > 0);
        }

        /**
         * 
         * @param {string} id
         * @return {com.vzome.core.editor.Tool}
         */
        public createToolInternal(id: string): com.vzome.core.editor.Tool {
            return new com.vzome.core.tools.BookmarkTool(id, this.getToolsModel());
        }

        /**
         * 
         * @param {*} selection
         * @return {boolean}
         */
        bindParameters(selection: com.vzome.core.editor.api.Selection): boolean {
            return true;
        }
    }
    BookmarkToolFactory["__class"] = "com.vzome.core.tools.BookmarkToolFactory";
    BookmarkToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];


}

