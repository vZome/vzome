import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AbstractCommand } from "../commands/AbstractCommand.js";
import { AttributeMap } from "../commands/AttributeMap.js";
import { Command } from "../commands/Command.js";
import { CommandHide } from "../commands/CommandHide.js";
import { CommandObliquePentagon } from "../commands/CommandObliquePentagon.js";
import { CommandTransform } from "../commands/CommandTransform.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Color } from "../construction/Color.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { BeginBlock } from "./BeginBlock.js";
import { EndBlock } from "./EndBlock.js";
import { ChangeManifestations } from "./api/ChangeManifestations.js";
import { ChangeSelection } from "./api/ChangeSelection.js";
import { Context } from "./api/Context.js";
import { EditorModel } from "./api/EditorModel.js";
import { LegacyEditorModel } from "./api/LegacyEditorModel.js";
import { UndoableEdit } from "./api/UndoableEdit.js";
import { AffinePentagon } from "../edits/AffinePentagon.js";
import { DeselectAll } from "../edits/DeselectAll.js";
import { SelectAll } from "../edits/SelectAll.js";
import { SelectManifestation } from "../edits/SelectManifestation.js";
import { SymmetryAxisChange } from "../edits/SymmetryAxisChange.js";
import { SymmetryCenterChange } from "../edits/SymmetryCenterChange.js";
import { Manifestation } from "../model/Manifestation.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";
import { Node } from "../../../../org/w3c/dom/Node.js";
import { NodeList } from "../../../../org/w3c/dom/NodeList.js";

/**
 * Just a mechanism to incorporate the legacy edit mechanism into the new undo/redo.
 * 
 * @author Scott Vorthmann 2006
 * @param {AbstractCommand} cmd
 * @param {*} editor
 * @class
 * @extends ChangeManifestations
 */
export class CommandEdit extends ChangeManifestations {
    /*private*/ mEditorModel: EditorModel;

    /*private*/ mCommand: AbstractCommand;

    /*private*/ mAttrs: AttributeMap;

    static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (CommandEdit.logger == null) { CommandEdit.logger = java.util.logging.Logger.getLogger("com.vzome.core.editor.CommandEdit"); }  return CommandEdit.logger; }

    static loadAndPerformLgger: java.util.logging.Logger; public static loadAndPerformLgger_$LI$(): java.util.logging.Logger { if (CommandEdit.loadAndPerformLgger == null) { CommandEdit.loadAndPerformLgger = java.util.logging.Logger.getLogger("com.vzome.core.editor.CommandEdit.loadAndPerform"); }  return CommandEdit.loadAndPerformLgger; }

    public constructor(cmd: AbstractCommand, editor: EditorModel) {
        super(editor);
        if (this.mEditorModel === undefined) { this.mEditorModel = null; }
        if (this.mCommand === undefined) { this.mCommand = null; }
        if (this.mAttrs === undefined) { this.mAttrs = null; }
        this.mEditorModel = editor;
        this.mCommand = cmd;
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
        const cmdName: string = /* getName */(c => typeof c === 'string' ? c : c["__class"] ? c["__class"] : c["name"])((<any>this.mCommand.constructor));
        const lastDot: number = cmdName.lastIndexOf('.');
        return cmdName.substring(lastDot + 1 + "Command".length);
    }

    /**
     * 
     * @param {*} result
     */
    public getXmlAttributes(result: Element) {
        this.mCommand.getXml(result, this.mAttrs);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    public setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        this.mAttrs = this.mCommand.setXml(xml, format);
        this.mAttrs.put(Command.LOADING_FROM_FILE, javaemul.internal.BooleanHelper.TRUE);
    }

    /**
     * 
     */
    public perform() {
        const isHide: boolean = (this.mCommand != null && this.mCommand instanceof <any>CommandHide);
        if (CommandEdit.logger_$LI$().isLoggable(java.util.logging.Level.FINER)){
            CommandEdit.logger_$LI$().finer("------------------- CommandEdit");
        }
        if (this.mCommand.ordersSelection())this.setOrderedSelection(true);
        const constrsBefore: ConstructionList = new ConstructionList();
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (CommandEdit.logger_$LI$().isLoggable(java.util.logging.Level.FINER)){
                    CommandEdit.logger_$LI$().finer("----------- manifestation: " + man.toString());
                    for(const iterator: java.util.Iterator<Construction> = man.getConstructions(); iterator.hasNext(); ) {{
                        const c: Construction = iterator.next();
                        CommandEdit.logger_$LI$().finer("   " + c.toString());
                    };}
                }
                this.unselect$com_vzome_core_model_Manifestation(man);
                if (isHide)this.hideManifestation(man); else {
                    const construction: Construction = man.getFirstConstruction();
                    constrsBefore.add(construction);
                }
            }
        }
        this.redo();
        if (isHide)return;
        if (this.mAttrs == null)this.mAttrs = new AttributeMap();
        const symmAxis: Segment = (<LegacyEditorModel><any>this.mEditorModel).getSymmetrySegment();
        if (symmAxis != null)this.mAttrs.put(CommandTransform.SYMMETRY_AXIS_ATTR_NAME, symmAxis);
        this.mAttrs.put(CommandTransform.SYMMETRY_CENTER_ATTR_NAME, (<LegacyEditorModel><any>this.mEditorModel).getCenterPoint());
        this.mAttrs.put(Command.FIELD_ATTR_NAME, this.mManifestations.getField());
        const news: CommandEdit.NewConstructions = new CommandEdit.NewConstructions();
        let selectionAfter: ConstructionList = null;
        const signature: any[][] = this.mCommand.getParameterSignature();
        const actualsLen: number = constrsBefore.size();
        if ((signature.length === actualsLen) || (signature.length === 1 && /* equals */(<any>((o1: any, o2: any) => o1 && o1.equals ? o1.equals(o2) : o1 === o2)(signature[0][0],Command.GENERIC_PARAM_NAME)))){
            try {
                selectionAfter = this.mCommand.apply(constrsBefore, this.mAttrs, news);
            } catch(f) {
                this.undo();
                throw f;
            }
        } else if (signature.length > actualsLen){
            this.fail("Too few objects in the selection.");
        } else if (signature.length === 1){
            let partial: ConstructionList;
            selectionAfter = new ConstructionList();
            for(let i: number = 0; i < actualsLen; i++) {{
                const param: Construction = constrsBefore.get(i);
                const formalClass: any = (<any>signature[0][1]);
                if ((formalClass === Point && (param != null && param instanceof <any>Point)) || (formalClass === Segment && (param != null && param instanceof <any>Segment))){
                    const single: ConstructionList = new ConstructionList();
                    single.addConstruction(param);
                    partial = this.mCommand.apply(single, this.mAttrs, news);
                    selectionAfter.addAll(partial);
                } else selectionAfter.add(param);
            };}
        } else this.fail("Too many objects in the selection.");
        for(let index=news.iterator();index.hasNext();) {
            let c = index.next();
            {
                this.manifestConstruction(c);
            }
        }
        for(let index=selectionAfter.iterator();index.hasNext();) {
            let cons = index.next();
            {
                if (cons.failed()){
                    CommandEdit.logBugAccommodation("failed construction");
                    (<LegacyEditorModel><any>this.mEditorModel).addFailedConstruction(cons);
                    continue;
                }
                const man: Manifestation = this.manifestConstruction(cons);
                if (man != null)this.select$com_vzome_core_model_Manifestation(man);
            }
        }
        this.redo();
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     * @param {*} context
     */
    public loadAndPerform(xml: Element, format: XmlSaveFormat, context: Context) {
        let cmdName: string = null;
        if (format.selectionsNotSaved())cmdName = xml.getLocalName(); else if (format.commandEditsCompacted())cmdName = "Command" + xml.getLocalName(); else cmdName = xml.getAttribute("command");
        if (cmdName === ("CommandIcosahedralSymmetry"))cmdName = "CommandSymmetry";
        this.mCommand = <AbstractCommand><any>context.createLegacyCommand(cmdName);
        if (format.selectionsNotSaved()){
            const selectedBefore: java.util.Set<Manifestation> = <any>(new java.util.LinkedHashSet<any>());
            context.performAndRecord(new BeginBlock(null));
            this.mAttrs = new AttributeMap();
            const nodes: NodeList = xml.getChildNodes();
            for(let j: number = 0; j < nodes.getLength(); j++) {{
                const kid2: Node = nodes.item(j);
                if (kid2 != null && (kid2.constructor != null && kid2.constructor["__interfaces"] != null && kid2.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                    const attrOrParam: Element = <Element><any>kid2;
                    const apName: string = attrOrParam.getLocalName();
                    if (apName === ("attr")){
                        let attrName: string = attrOrParam.getAttribute("name");
                        if (/* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(attrName, ".symmetry.center"))attrName = CommandTransform.SYMMETRY_CENTER_ATTR_NAME; else if (attrName === ("reflection.mirror.normal.segment"))attrName = CommandTransform.SYMMETRY_AXIS_ATTR_NAME;
                        const val: Element = DomUtils.getFirstChildElement$org_w3c_dom_Element(attrOrParam);
                        let valName: string = val.getLocalName();
                        if (valName === ("FreePoint"))valName = "point";
                        let value: any = format.parseAlgebraicObject(valName, val);
                        if (value === XmlSaveFormat.NOT_AN_ATTRIBUTE_$LI$())value = format.parseConstruction$java_lang_String$org_w3c_dom_Element(valName, val);
                        if (attrName === CommandTransform.SYMMETRY_CENTER_ATTR_NAME){
                            const c: Point = new FreePoint((<Point>value).getLocation().projectTo3d(true));
                            context.performAndRecord(new SymmetryCenterChange(<LegacyEditorModel><any>this.mEditorModel, c));
                        } else if (attrName === CommandTransform.SYMMETRY_AXIS_ATTR_NAME){
                            context.performAndRecord(new SymmetryAxisChange(<LegacyEditorModel><any>this.mEditorModel, <Segment>value));
                            if (!this.mCommand.attributeIs3D(attrName)){
                                const vector: AlgebraicVector = (<Segment>value).getOffset();
                                this.mCommand.setQuaternion(vector);
                            }
                        } else this.mAttrs.put(attrName, value);
                    } else {
                        const c: Construction = format.parseConstruction$java_lang_String$org_w3c_dom_Element(apName, attrOrParam);
                        if (c != null){
                            if ((<LegacyEditorModel><any>this.mEditorModel).hasFailedConstruction(c)){
                                CommandEdit.logBugAccommodation("skip selecting a failed construction");
                                continue;
                            }
                            const m: Manifestation = this.getManifestation(c);
                            if (m == null || m.isUnnecessary()){
                                CommandEdit.loadAndPerformLgger_$LI$().severe("CommandEdit parameter: " + attrOrParam.toString());
                                throw new Command.Failure("no manifestation to be selected.");
                            }
                            if (!selectedBefore.contains(m))selectedBefore.add(m);
                        }
                    }
                }
            };}
            if (selectedBefore.size() > (this.mManifestations.size() / 2|0)){
                const toUnselect: java.util.Collection<Manifestation> = <any>(new java.util.ArrayList<any>());
                for(let index=this.mManifestations.iterator();index.hasNext();) {
                    let m = index.next();
                    {
                        if (!selectedBefore.contains(m))toUnselect.add(m);
                    }
                }
                let edit: ChangeSelection = new SelectAll(this.mEditorModel);
                context.performAndRecord(edit);
                for(let index=toUnselect.iterator();index.hasNext();) {
                    let m = index.next();
                    {
                        edit = new SelectManifestation(this.mEditorModel, m);
                        context.performAndRecord(edit);
                    }
                }
            } else {
                let edit: ChangeSelection = new DeselectAll(this.mEditorModel);
                context.performAndRecord(edit);
                for(let index=selectedBefore.iterator();index.hasNext();) {
                    let m = index.next();
                    {
                        edit = new SelectManifestation(this.mEditorModel, m);
                        context.performAndRecord(edit);
                    }
                }
            }
            context.performAndRecord(new EndBlock(null));
            this.redo();
            if (this.mCommand != null && this.mCommand instanceof <any>CommandObliquePentagon){
                const edit: UndoableEdit = new AffinePentagon(this.mEditorModel);
                context.performAndRecord(edit);
                return;
            }
            this.mCommand.setFixedAttributes(this.mAttrs, format);
            this.mAttrs.put(Command.LOADING_FROM_FILE, javaemul.internal.BooleanHelper.TRUE);
            context.performAndRecord(this);
        } else super.loadAndPerform(xml, format, context);
    }
}
CommandEdit["__class"] = "com.vzome.core.editor.CommandEdit";


export namespace CommandEdit {

    export class NewConstructions extends java.util.ArrayList<Construction> implements ConstructionChanges {
        public constructionAdded$com_vzome_core_construction_Construction(c: Construction) {
            this.add(c);
        }

        public constructionAdded$com_vzome_core_construction_Construction$com_vzome_core_construction_Color(c: Construction, color: Color) {
            this.add(c);
        }

        /**
         * 
         * @param {Construction} c
         * @param {Color} color
         */
        public constructionAdded(c?: any, color?: any) {
            if (((c != null && c instanceof <any>Construction) || c === null) && ((color != null && color instanceof <any>Color) || color === null)) {
                return <any>this.constructionAdded$com_vzome_core_construction_Construction$com_vzome_core_construction_Color(c, color);
            } else if (((c != null && c instanceof <any>Construction) || c === null) && color === undefined) {
                return <any>this.constructionAdded$com_vzome_core_construction_Construction(c);
            } else throw new Error('invalid overload');
        }

        constructor() {
            super();
        }
    }
    NewConstructions["__class"] = "com.vzome.core.editor.CommandEdit.NewConstructions";
    NewConstructions["__interfaces"] = ["java.util.RandomAccess","java.util.List","java.lang.Cloneable","com.vzome.core.construction.ConstructionChanges","java.util.Collection","java.lang.Iterable","java.io.Serializable"];


}
