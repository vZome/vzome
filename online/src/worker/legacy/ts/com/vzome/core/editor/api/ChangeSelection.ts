/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export abstract class ChangeSelection extends com.vzome.core.editor.api.SideEffects {
        mSelection: com.vzome.core.editor.api.Selection;

        /*private*/ groupingDoneInSelection: boolean;

        /*private*/ orderedSelection: boolean;

        /*private*/ selectionEffects: java.util.Deque<com.vzome.core.editor.api.SideEffect>;

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (ChangeSelection.logger == null) { ChangeSelection.logger = java.util.logging.Logger.getLogger("com.vzome.core.editor.ChangeSelection"); }  return ChangeSelection.logger; }

        public constructor(selection: com.vzome.core.editor.api.Selection) {
            super();
            if (this.mSelection === undefined) { this.mSelection = null; }
            if (this.groupingDoneInSelection === undefined) { this.groupingDoneInSelection = false; }
            this.orderedSelection = false;
            this.selectionEffects = null;
            this.mSelection = selection;
            this.groupingDoneInSelection = false;
        }

        public setOrderedSelection(orderedSelection: boolean) {
            this.orderedSelection = orderedSelection;
        }

        /**
         * 
         */
        public undo() {
            if (this.orderedSelection){
                const stack: java.util.Deque<com.vzome.core.editor.api.SideEffect> = <any>(new java.util.ArrayDeque<com.vzome.core.editor.api.SideEffect>());
                this.selectionEffects = stack;
                super.undo();
                this.selectionEffects = null;
                while((!stack.isEmpty())) {{
                    const se: com.vzome.core.editor.api.SideEffect = stack.pop();
                    se.undo();
                }};
            } else super.undo();
        }

        getXmlAttributes(element: org.w3c.dom.Element) {
        }

        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
        }

        abstract getXmlElementName(): string;

        /**
         * 
         * @param {*} doc
         * @return {*}
         */
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement(this.getXmlElementName());
            if (this.groupingDoneInSelection)com.vzome.xml.DomUtils.addAttribute(result, "grouping", "2.1.1");
            this.getXmlAttributes(result);
            return result;
        }

        adjustSelection(man: com.vzome.core.model.Manifestation, action: com.vzome.core.editor.api.ActionEnum) {
            switch((action)) {
            case com.vzome.core.editor.api.ActionEnum.SELECT:
                this.select$com_vzome_core_model_Manifestation(man);
                break;
            case com.vzome.core.editor.api.ActionEnum.DESELECT:
                this.unselect$com_vzome_core_model_Manifestation(man);
                break;
            case com.vzome.core.editor.api.ActionEnum.IGNORE:
                break;
            default:
                ChangeSelection.logger_$LI$().warning("unexpected action: " + /* Enum.name */com.vzome.core.editor.api.ActionEnum[action]);
                break;
            }
        }

        /**
         * Any subclass can override to alter loading, or migrate (insert other edits), etc.
         * ALWAYS DO SOME INSERT, or all trace of the command will disappear!
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         * @param {*} context
         */
        public loadAndPerform(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat, context: com.vzome.core.editor.api.Context) {
            const grouping: string = xml.getAttribute("grouping");
            if (this.groupingAware() && (format.groupingDoneInSelection() || ("2.1.1" === grouping)))this.groupingDoneInSelection = true;
            this.setXmlAttributes(xml, format);
            context.performAndRecord(this);
        }

        groupingAware(): boolean {
            return false;
        }

        public unselect$com_vzome_core_model_Manifestation(man: com.vzome.core.model.Manifestation) {
            this.unselect$com_vzome_core_model_Manifestation$boolean(man, false);
        }

        public unselect$com_vzome_core_model_Manifestation$boolean(man: com.vzome.core.model.Manifestation, ignoreGroups: boolean) {
            if (this.groupingDoneInSelection){
                this.plan(new ChangeSelection.SelectManifestation(this, man, false));
                return;
            }
            if (man == null){
                SideEffects.logBugAccommodation("null manifestation");
                return;
            }
            if (!this.mSelection.manifestationSelected(man))return;
            const group: com.vzome.core.model.Group = ignoreGroups ? null : com.vzome.core.editor.api.Selection.biggestGroup(man);
            if (group == null)this.plan(new ChangeSelection.SelectManifestation(this, man, false)); else this.unselectGroup(group);
        }

        public unselect(man?: any, ignoreGroups?: any) {
            if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ((typeof ignoreGroups === 'boolean') || ignoreGroups === null)) {
                return <any>this.unselect$com_vzome_core_model_Manifestation$boolean(man, ignoreGroups);
            } else if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ignoreGroups === undefined) {
                return <any>this.unselect$com_vzome_core_model_Manifestation(man);
            } else throw new Error('invalid overload');
        }

        public select$com_vzome_core_model_Manifestation(man: com.vzome.core.model.Manifestation) {
            this.select$com_vzome_core_model_Manifestation$boolean(man, false);
        }

        public recordSelected(man: com.vzome.core.model.Manifestation) {
            if (!this.mSelection.manifestationSelected(man))return;
            this.plan(new ChangeSelection.RecordSelectedManifestation(this, man));
        }

        public select$com_vzome_core_model_Manifestation$boolean(man: com.vzome.core.model.Manifestation, ignoreGroups: boolean) {
            if (this.groupingDoneInSelection){
                this.plan(new ChangeSelection.SelectManifestation(this, man, true));
                return;
            }
            if (man == null){
                SideEffects.logBugAccommodation("null manifestation");
                return;
            }
            if (this.mSelection.manifestationSelected(man))return;
            const group: com.vzome.core.model.Group = ignoreGroups ? null : com.vzome.core.editor.api.Selection.biggestGroup(man);
            if (group == null)this.plan(new ChangeSelection.SelectManifestation(this, man, true)); else this.selectGroup(group);
        }

        public select(man?: any, ignoreGroups?: any) {
            if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ((typeof ignoreGroups === 'boolean') || ignoreGroups === null)) {
                return <any>this.select$com_vzome_core_model_Manifestation$boolean(man, ignoreGroups);
            } else if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ignoreGroups === undefined) {
                return <any>this.select$com_vzome_core_model_Manifestation(man);
            } else throw new Error('invalid overload');
        }

        selectGroup(group: com.vzome.core.model.Group) {
            for(let index=group.iterator();index.hasNext();) {
                let next = index.next();
                {
                    if (next != null && next instanceof <any>com.vzome.core.model.Group)this.selectGroup(<com.vzome.core.model.Group><any>next); else this.plan(new ChangeSelection.SelectManifestation(this, <com.vzome.core.model.Manifestation><any>next, true));
                }
            }
        }

        unselectGroup(group: com.vzome.core.model.Group) {
            for(let index=group.iterator();index.hasNext();) {
                let next = index.next();
                {
                    if (next != null && next instanceof <any>com.vzome.core.model.Group)this.unselectGroup(<com.vzome.core.model.Group><any>next); else this.plan(new ChangeSelection.SelectManifestation(this, <com.vzome.core.model.Manifestation><any>next, false));
                }
            }
        }

        getSelectedConnectors(): com.vzome.core.editor.api.Manifestations.ConnectorIterator {
            return com.vzome.core.editor.api.Manifestations.getConnectors$java_lang_Iterable(this.mSelection);
        }

        getSelectedStruts(): com.vzome.core.editor.api.Manifestations.StrutIterator {
            return com.vzome.core.editor.api.Manifestations.getStruts$java_lang_Iterable(this.mSelection);
        }

        getSelectedPanels(): com.vzome.core.editor.api.Manifestations.PanelIterator {
            return com.vzome.core.editor.api.Manifestations.getPanels$java_lang_Iterable(this.mSelection);
        }

        public getLastSelectedManifestation(): com.vzome.core.model.Manifestation {
            let last: com.vzome.core.model.Manifestation = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    last = man;
                }
            }
            return last;
        }

        public getLastSelectedConnector(): com.vzome.core.model.Connector {
            let last: com.vzome.core.model.Connector = null;
            for(let index=this.getSelectedConnectors().iterator();index.hasNext();) {
                let connector = index.next();
                {
                    last = connector;
                }
            }
            return last;
        }

        public getLastSelectedStrut(): com.vzome.core.model.Strut {
            let last: com.vzome.core.model.Strut = null;
            for(let index=this.getSelectedStruts().iterator();index.hasNext();) {
                let strut = index.next();
                {
                    last = strut;
                }
            }
            return last;
        }

        public getLastSelectedPanel(): com.vzome.core.model.Panel {
            let last: com.vzome.core.model.Panel = null;
            for(let index=this.getSelectedPanels().iterator();index.hasNext();) {
                let panel = index.next();
                {
                    last = panel;
                }
            }
            return last;
        }

        public unselectAll(): boolean {
            let anySelected: boolean = false;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    anySelected = true;
                    this.unselect$com_vzome_core_model_Manifestation(man);
                }
            }
            if (anySelected){
                this.redo();
            }
            return anySelected;
        }

        public unselectConnectors(): boolean {
            let anySelected: boolean = false;
            for(let index=this.getSelectedConnectors().iterator();index.hasNext();) {
                let connector = index.next();
                {
                    anySelected = true;
                    this.unselect$com_vzome_core_model_Manifestation(connector);
                }
            }
            if (anySelected){
                this.redo();
            }
            return anySelected;
        }

        public unselectStruts(): boolean {
            let anySelected: boolean = false;
            for(let index=this.getSelectedStruts().iterator();index.hasNext();) {
                let strut = index.next();
                {
                    anySelected = true;
                    this.unselect$com_vzome_core_model_Manifestation(strut);
                }
            }
            if (anySelected){
                this.redo();
            }
            return anySelected;
        }

        public unselectPanels(): boolean {
            let anySelected: boolean = false;
            for(let index=this.getSelectedPanels().iterator();index.hasNext();) {
                let panel = index.next();
                {
                    anySelected = true;
                    this.unselect$com_vzome_core_model_Manifestation(panel);
                }
            }
            if (anySelected){
                this.redo();
            }
            return anySelected;
        }
    }
    ChangeSelection["__class"] = "com.vzome.core.editor.api.ChangeSelection";


    export namespace ChangeSelection {

        export class SelectManifestation implements com.vzome.core.editor.api.SideEffect {
            public __parent: any;
            mMan: com.vzome.core.model.Manifestation;

            mOn: boolean;

            public constructor(__parent: any, man: com.vzome.core.model.Manifestation, value: boolean) {
                this.__parent = __parent;
                if (this.mMan === undefined) { this.mMan = null; }
                if (this.mOn === undefined) { this.mOn = false; }
                this.mMan = man;
                this.mOn = value;
                com.vzome.core.editor.api.ChangeSelection.logger_$LI$().finest("constructing SelectManifestation");
            }

            /**
             * 
             */
            public redo() {
                if (this.__parent.groupingDoneInSelection){
                    if (this.mOn)this.__parent.mSelection.selectWithGrouping(this.mMan); else this.__parent.mSelection.unselectWithGrouping(this.mMan);
                } else if (this.mOn)this.__parent.mSelection.select(this.mMan); else this.__parent.mSelection.unselect(this.mMan);
            }

            /**
             * 
             */
            public undo() {
                if (this.__parent.groupingDoneInSelection){
                    if (this.mOn)this.__parent.mSelection.unselectWithGrouping(this.mMan); else this.__parent.mSelection.selectWithGrouping(this.mMan);
                } else if (this.mOn)this.__parent.mSelection.unselect(this.mMan); else if (this.__parent.selectionEffects != null){
                    this.__parent.selectionEffects.push(this);
                } else this.__parent.mSelection.select(this.mMan);
            }

            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                const result: org.w3c.dom.Element = this.mOn ? doc.createElement("select") : doc.createElement("deselect");
                if (this.mMan != null){
                    const man: org.w3c.dom.Element = this.mMan.getXml(doc);
                    result.appendChild(man);
                }
                return result;
            }
        }
        SelectManifestation["__class"] = "com.vzome.core.editor.api.ChangeSelection.SelectManifestation";
        SelectManifestation["__interfaces"] = ["com.vzome.core.editor.api.SideEffect"];



        export class RecordSelectedManifestation implements com.vzome.core.editor.api.SideEffect {
            public __parent: any;
            mMan: com.vzome.core.model.Manifestation;

            public constructor(__parent: any, man: com.vzome.core.model.Manifestation) {
                this.__parent = __parent;
                if (this.mMan === undefined) { this.mMan = null; }
                this.mMan = man;
                com.vzome.core.editor.api.ChangeSelection.logger_$LI$().finest("constructing RecordSelectedManifestation");
            }

            /**
             * 
             */
            public redo() {
                com.vzome.core.editor.api.ChangeSelection.logger_$LI$().finest("redoing RecordSelectedManifestation");
            }

            /**
             * 
             */
            public undo() {
                com.vzome.core.editor.api.ChangeSelection.logger_$LI$().finest("undoing RecordSelectedManifestation");
                if (this.__parent.selectionEffects == null)this.__parent.mSelection.select(this.mMan); else this.__parent.selectionEffects.push(this);
            }

            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                return doc.createElement("recordSelected");
            }
        }
        RecordSelectedManifestation["__class"] = "com.vzome.core.editor.api.ChangeSelection.RecordSelectedManifestation";
        RecordSelectedManifestation["__interfaces"] = ["com.vzome.core.editor.api.SideEffect"];


    }

}

