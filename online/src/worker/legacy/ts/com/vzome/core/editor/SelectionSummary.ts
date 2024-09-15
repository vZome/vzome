/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export class SelectionSummary implements com.vzome.core.model.ManifestationChanges {
        static LOGGER: java.util.logging.Logger; public static LOGGER_$LI$(): java.util.logging.Logger { if (SelectionSummary.LOGGER == null) { SelectionSummary.LOGGER = java.util.logging.Logger.getLogger("com.vzome.core.editor.SelectionSummary"); }  return SelectionSummary.LOGGER; }

        /*private*/ balls: number;

        /*private*/ struts: number;

        /*private*/ panels: number;

        /*private*/ listeners: java.util.Collection<SelectionSummary.Listener>;

        /*private*/ selection: com.vzome.core.editor.api.Selection;

        public constructor(selection: com.vzome.core.editor.api.Selection) {
            this.balls = 0;
            this.struts = 0;
            this.panels = 0;
            this.listeners = <any>(new java.util.ArrayList<SelectionSummary.Listener>());
            if (this.selection === undefined) { this.selection = null; }
            this.selection = selection;
        }

        public notifyListeners() {
            for(let index=this.listeners.iterator();index.hasNext();) {
                let listener = index.next();
                {
                    listener.selectionChanged(this.selection.size(), this.balls, this.struts, this.panels);
                }
            }
        }

        /**
         * 
         * @param {*} m
         */
        public manifestationAdded(m: com.vzome.core.model.Manifestation) {
            if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0))++this.balls; else if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0))++this.struts; else if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0))++this.panels;
            this.verifyCounts();
        }

        /**
         * 
         * @param {*} m
         */
        public manifestationRemoved(m: com.vzome.core.model.Manifestation) {
            if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0))--this.balls; else if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0))--this.struts; else if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0))--this.panels;
            this.verifyCounts();
        }

        verifyCounts() {
            if (this.balls + this.struts + this.panels !== this.selection.size()){
                if (SelectionSummary.LOGGER_$LI$().isLoggable(java.util.logging.Level.WARNING)){
                    SelectionSummary.LOGGER_$LI$().warning("Incorrect total for balls, struts and panels: " + this.balls + " + " + this.struts + " + " + this.panels + " != " + this.selection.size());
                }
                this.balls = this.struts = this.panels = 0;
                for(let index=this.selection.iterator();index.hasNext();) {
                    let m = index.next();
                    {
                        if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0))++this.balls; else if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0))++this.struts; else if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0))++this.panels;
                    }
                }
                if (SelectionSummary.LOGGER_$LI$().isLoggable(java.util.logging.Level.WARNING)){
                    SelectionSummary.LOGGER_$LI$().warning("SelectionSummary resynced on thread: " + java.lang.Thread.currentThread() + ". " + this.balls + " + " + this.struts + " + " + this.panels + " = " + this.selection.size());
                }
            }
        }

        /**
         * 
         * @param {*} m
         * @param {com.vzome.core.construction.Color} color
         */
        public manifestationColored(m: com.vzome.core.model.Manifestation, color: com.vzome.core.construction.Color) {
        }

        /**
         * 
         * @param {*} m
         * @param {string} label
         */
        public manifestationLabeled(m: com.vzome.core.model.Manifestation, label: string) {
        }

        public addListener(listener: SelectionSummary.Listener) {
            this.listeners.add(listener);
        }
    }
    SelectionSummary["__class"] = "com.vzome.core.editor.SelectionSummary";
    SelectionSummary["__interfaces"] = ["com.vzome.core.model.ManifestationChanges"];



    export namespace SelectionSummary {

        export interface Listener {
            selectionChanged(total: number, balls: number, struts: number, panels: number);
        }
    }

}

