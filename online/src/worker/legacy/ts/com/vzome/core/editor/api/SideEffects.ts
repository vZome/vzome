/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export abstract class SideEffects extends com.vzome.core.editor.api.UndoableEdit {
        /**
         * 
         * @param {*} doc
         * @return {*}
         */
        public getDetailXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = this.getXml(doc);
            const effects: org.w3c.dom.Element = doc.createElement("effects");
            for(let index=this.mItems.iterator();index.hasNext();) {
                let se = index.next();
                {
                    if (se != null){
                        const effect: org.w3c.dom.Element = se.getXml(doc);
                        if (effect != null){
                            effects.appendChild(effect);
                        }
                    }
                }
            }
            result.appendChild(effects);
            return result;
        }

        /**
         * 
         * @return {boolean}
         */
        public isSticky(): boolean {
            return false;
        }

        /*private*/ mItems: java.util.List<com.vzome.core.editor.api.SideEffect>;

        /**
         * This lets us use this pattern:
         * plan plan plan plan
         * redo
         * plan plan plan
         * redo
         * 
         * ... so we can sync the state with a redo
         * 
         */
        /*private*/ redone: number;

        static BUG_ACCOMMODATION_LOGGER: java.util.logging.Logger; public static BUG_ACCOMMODATION_LOGGER_$LI$(): java.util.logging.Logger { if (SideEffects.BUG_ACCOMMODATION_LOGGER == null) { SideEffects.BUG_ACCOMMODATION_LOGGER = java.util.logging.Logger.getLogger("com.vzome.core.bug.accommodations"); }  return SideEffects.BUG_ACCOMMODATION_LOGGER; }

        public static logBugAccommodation(accommodation: string) {
            if (SideEffects.BUG_ACCOMMODATION_LOGGER_$LI$().isLoggable(java.util.logging.Level.WARNING))SideEffects.BUG_ACCOMMODATION_LOGGER_$LI$().warning("ACCOMMODATION: " + accommodation);
        }

        /**
         * 
         * @return {boolean}
         */
        public isVisible(): boolean {
            return true;
        }

        /**
         * 
         * @return {boolean}
         */
        public isDestructive(): boolean {
            return true;
        }

        plan(se: com.vzome.core.editor.api.SideEffect) {
            this.mItems.add(se);
        }

        /**
         * 
         */
        public perform() {
            this.redo();
        }

        fail(message: string) {
            this.undo();
            throw new com.vzome.core.commands.Command.Failure(message);
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
        }

        /**
         * 
         * @return {boolean}
         */
        public isNoOp(): boolean {
            return this.mItems.size() === 0;
        }

        /**
         * 
         */
        public redo() {
            for(let i: number = this.redone; i < this.mItems.size(); i++) {{
                const se: com.vzome.core.editor.api.SideEffect = this.mItems.get(i);
                if (se != null)se.redo();
            };}
            this.redone = this.mItems.size();
        }

        /**
         * 
         */
        public undo() {
            for(let i: number = this.mItems.size(); i > 0; i--) {{
                const se: com.vzome.core.editor.api.SideEffect = this.mItems.get(i - 1);
                if (se != null)se.undo();
            };}
            this.redone = 0;
        }

        getEffects(): java.util.Iterator<com.vzome.core.editor.api.SideEffect> {
            return this.mItems.iterator();
        }

        constructor() {
            super();
            this.mItems = <any>(new java.util.ArrayList<any>());
            this.redone = 0;
        }
    }
    SideEffects["__class"] = "com.vzome.core.editor.api.SideEffects";

}

