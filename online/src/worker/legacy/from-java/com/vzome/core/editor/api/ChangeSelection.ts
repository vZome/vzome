import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { XmlSaveFormat } from "../../commands/XmlSaveFormat.js";
import { ActionEnum } from "./ActionEnum.js";
import { Context } from "./Context.js";
import { Manifestations } from "./Manifestations.js";
import { Selection } from "./Selection.js";
import { SideEffect } from "./SideEffect.js";
import { SideEffects } from "./SideEffects.js";
import { Connector } from "../../model/Connector.js";
import { Group } from "../../model/Group.js";
import { Manifestation } from "../../model/Manifestation.js";
import { Panel } from "../../model/Panel.js";
import { Strut } from "../../model/Strut.js";
import { DomUtils } from "../../../xml/DomUtils.js";
import { Document } from "../../../../../org/w3c/dom/Document.js";
import { Element } from "../../../../../org/w3c/dom/Element.js";

export abstract class ChangeSelection extends SideEffects {
    mSelection: Selection;

    /*private*/ groupingDoneInSelection: boolean;

    /*private*/ orderedSelection: boolean;

    /*private*/ selectionEffects: java.util.Deque<SideEffect>;

    static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (ChangeSelection.logger == null) { ChangeSelection.logger = java.util.logging.Logger.getLogger("com.vzome.core.editor.ChangeSelection"); }  return ChangeSelection.logger; }

    public constructor(selection: Selection) {
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
            const stack: java.util.Deque<SideEffect> = <any>(new java.util.ArrayDeque<SideEffect>());
            this.selectionEffects = stack;
            super.undo();
            this.selectionEffects = null;
            while((!stack.isEmpty())) {{
                const se: SideEffect = stack.pop();
                se.undo();
            }};
        } else super.undo();
    }

    getXmlAttributes(element: Element) {
    }

    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
    }

    abstract getXmlElementName(): string;

    /**
     * 
     * @param {*} doc
     * @return {*}
     */
    public getXml(doc: Document): Element {
        const result: Element = doc.createElement(this.getXmlElementName());
        if (this.groupingDoneInSelection)DomUtils.addAttribute(result, "grouping", "2.1.1");
        this.getXmlAttributes(result);
        return result;
    }

    adjustSelection(man: Manifestation, action: ActionEnum) {
        switch((action)) {
        case ActionEnum.SELECT:
            this.select$com_vzome_core_model_Manifestation(man);
            break;
        case ActionEnum.DESELECT:
            this.unselect$com_vzome_core_model_Manifestation(man);
            break;
        case ActionEnum.IGNORE:
            break;
        default:
            ChangeSelection.logger_$LI$().warning("unexpected action: " + /* Enum.name */ActionEnum[action]);
            break;
        }
    }

    /**
     * Any subclass can override to alter loading, or migrate (insert other edits), etc.
     * ALWAYS DO SOME INSERT, or all trace of the command will disappear!
     * @param {*} xml
     * @param {XmlSaveFormat} format
     * @param {*} context
     */
    public loadAndPerform(xml: Element, format: XmlSaveFormat, context: Context) {
        const grouping: string = xml.getAttribute("grouping");
        if (this.groupingAware() && (format.groupingDoneInSelection() || ("2.1.1" === grouping)))this.groupingDoneInSelection = true;
        this.setXmlAttributes(xml, format);
        context.performAndRecord(this);
    }

    groupingAware(): boolean {
        return false;
    }

    public unselect$com_vzome_core_model_Manifestation(man: Manifestation) {
        this.unselect$com_vzome_core_model_Manifestation$boolean(man, false);
    }

    public unselect$com_vzome_core_model_Manifestation$boolean(man: Manifestation, ignoreGroups: boolean) {
        if (this.groupingDoneInSelection){
            this.plan(new ChangeSelection.SelectManifestation(this, man, false));
            return;
        }
        if (man == null){
            SideEffects.logBugAccommodation("null manifestation");
            return;
        }
        if (!this.mSelection.manifestationSelected(man))return;
        const group: Group = ignoreGroups ? null : Selection.biggestGroup(man);
        if (group == null)this.plan(new ChangeSelection.SelectManifestation(this, man, false)); else this.unselectGroup(group);
    }

    public unselect(man?: any, ignoreGroups?: any) {
        if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ((typeof ignoreGroups === 'boolean') || ignoreGroups === null)) {
            return <any>this.unselect$com_vzome_core_model_Manifestation$boolean(man, ignoreGroups);
        } else if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ignoreGroups === undefined) {
            return <any>this.unselect$com_vzome_core_model_Manifestation(man);
        } else throw new Error('invalid overload');
    }

    public select$com_vzome_core_model_Manifestation(man: Manifestation) {
        this.select$com_vzome_core_model_Manifestation$boolean(man, false);
    }

    public recordSelected(man: Manifestation) {
        if (!this.mSelection.manifestationSelected(man))return;
        this.plan(new ChangeSelection.RecordSelectedManifestation(this, man));
    }

    public select$com_vzome_core_model_Manifestation$boolean(man: Manifestation, ignoreGroups: boolean) {
        if (this.groupingDoneInSelection){
            this.plan(new ChangeSelection.SelectManifestation(this, man, true));
            return;
        }
        if (man == null){
            SideEffects.logBugAccommodation("null manifestation");
            return;
        }
        if (this.mSelection.manifestationSelected(man))return;
        const group: Group = ignoreGroups ? null : Selection.biggestGroup(man);
        if (group == null)this.plan(new ChangeSelection.SelectManifestation(this, man, true)); else this.selectGroup(group);
    }

    public select(man?: any, ignoreGroups?: any) {
        if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ((typeof ignoreGroups === 'boolean') || ignoreGroups === null)) {
            return <any>this.select$com_vzome_core_model_Manifestation$boolean(man, ignoreGroups);
        } else if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ignoreGroups === undefined) {
            return <any>this.select$com_vzome_core_model_Manifestation(man);
        } else throw new Error('invalid overload');
    }

    selectGroup(group: Group) {
        for(let index=group.iterator();index.hasNext();) {
            let next = index.next();
            {
                if (next != null && next instanceof <any>Group)this.selectGroup(<Group><any>next); else this.plan(new ChangeSelection.SelectManifestation(this, <Manifestation><any>next, true));
            }
        }
    }

    unselectGroup(group: Group) {
        for(let index=group.iterator();index.hasNext();) {
            let next = index.next();
            {
                if (next != null && next instanceof <any>Group)this.unselectGroup(<Group><any>next); else this.plan(new ChangeSelection.SelectManifestation(this, <Manifestation><any>next, false));
            }
        }
    }

    getSelectedConnectors(): Manifestations.ConnectorIterator {
        return Manifestations.getConnectors$java_lang_Iterable(this.mSelection);
    }

    getSelectedStruts(): Manifestations.StrutIterator {
        return Manifestations.getStruts$java_lang_Iterable(this.mSelection);
    }

    getSelectedPanels(): Manifestations.PanelIterator {
        return Manifestations.getPanels$java_lang_Iterable(this.mSelection);
    }

    public getLastSelectedManifestation(): Manifestation {
        let last: Manifestation = null;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                last = man;
            }
        }
        return last;
    }

    public getLastSelectedConnector(): Connector {
        let last: Connector = null;
        for(let index=this.getSelectedConnectors().iterator();index.hasNext();) {
            let connector = index.next();
            {
                last = connector;
            }
        }
        return last;
    }

    public getLastSelectedStrut(): Strut {
        let last: Strut = null;
        for(let index=this.getSelectedStruts().iterator();index.hasNext();) {
            let strut = index.next();
            {
                last = strut;
            }
        }
        return last;
    }

    public getLastSelectedPanel(): Panel {
        let last: Panel = null;
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

    export class SelectManifestation implements SideEffect {
        public __parent: any;
        mMan: Manifestation;

        mOn: boolean;

        public constructor(__parent: any, man: Manifestation, value: boolean) {
            this.__parent = __parent;
            if (this.mMan === undefined) { this.mMan = null; }
            if (this.mOn === undefined) { this.mOn = false; }
            this.mMan = man;
            this.mOn = value;
            ChangeSelection.logger_$LI$().finest("constructing SelectManifestation");
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
        public getXml(doc: Document): Element {
            const result: Element = this.mOn ? doc.createElement("select") : doc.createElement("deselect");
            if (this.mMan != null){
                const man: Element = this.mMan.getXml(doc);
                result.appendChild(man);
            }
            return result;
        }
    }
    SelectManifestation["__class"] = "com.vzome.core.editor.api.ChangeSelection.SelectManifestation";
    SelectManifestation["__interfaces"] = ["com.vzome.core.editor.api.SideEffect"];



    export class RecordSelectedManifestation implements SideEffect {
        public __parent: any;
        mMan: Manifestation;

        public constructor(__parent: any, man: Manifestation) {
            this.__parent = __parent;
            if (this.mMan === undefined) { this.mMan = null; }
            this.mMan = man;
            ChangeSelection.logger_$LI$().finest("constructing RecordSelectedManifestation");
        }

        /**
         * 
         */
        public redo() {
            ChangeSelection.logger_$LI$().finest("redoing RecordSelectedManifestation");
        }

        /**
         * 
         */
        public undo() {
            ChangeSelection.logger_$LI$().finest("undoing RecordSelectedManifestation");
            if (this.__parent.selectionEffects == null)this.__parent.mSelection.select(this.mMan); else this.__parent.selectionEffects.push(this);
        }

        /**
         * 
         * @param {*} doc
         * @return {*}
         */
        public getXml(doc: Document): Element {
            return doc.createElement("recordSelected");
        }
    }
    RecordSelectedManifestation["__class"] = "com.vzome.core.editor.api.ChangeSelection.RecordSelectedManifestation";
    RecordSelectedManifestation["__interfaces"] = ["com.vzome.core.editor.api.SideEffect"];


}
