/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    /**
     * Just a mechanism to incorporate the legacy edit mechanism into the new undo/redo.
     * 
     * @author Scott Vorthmann 2006
     * @param {com.vzome.core.commands.AbstractCommand} cmd
     * @param {*} editor
     * @class
     * @extends com.vzome.core.editor.api.ChangeManifestations
     */
    export class CommandEdit extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ mEditorModel: com.vzome.core.editor.api.EditorModel;

        /*private*/ mCommand: com.vzome.core.commands.AbstractCommand;

        /*private*/ mAttrs: com.vzome.core.commands.AttributeMap;

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (CommandEdit.logger == null) { CommandEdit.logger = java.util.logging.Logger.getLogger("com.vzome.core.editor.CommandEdit"); }  return CommandEdit.logger; }

        static loadAndPerformLgger: java.util.logging.Logger; public static loadAndPerformLgger_$LI$(): java.util.logging.Logger { if (CommandEdit.loadAndPerformLgger == null) { CommandEdit.loadAndPerformLgger = java.util.logging.Logger.getLogger("com.vzome.core.editor.CommandEdit.loadAndPerform"); }  return CommandEdit.loadAndPerformLgger; }

        public constructor(cmd: com.vzome.core.commands.AbstractCommand, editor: com.vzome.core.editor.api.EditorModel) {
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
        public getXmlAttributes(result: org.w3c.dom.Element) {
            this.mCommand.getXml(result, this.mAttrs);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.mAttrs = this.mCommand.setXml(xml, format);
            this.mAttrs.put(com.vzome.core.commands.Command.LOADING_FROM_FILE, javaemul.internal.BooleanHelper.TRUE);
        }

        /**
         * 
         */
        public perform() {
            const isHide: boolean = (this.mCommand != null && this.mCommand instanceof <any>com.vzome.core.commands.CommandHide);
            if (CommandEdit.logger_$LI$().isLoggable(java.util.logging.Level.FINER)){
                CommandEdit.logger_$LI$().finer("------------------- CommandEdit");
            }
            if (this.mCommand.ordersSelection())this.setOrderedSelection(true);
            const constrsBefore: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (CommandEdit.logger_$LI$().isLoggable(java.util.logging.Level.FINER)){
                        CommandEdit.logger_$LI$().finer("----------- manifestation: " + man.toString());
                        for(const iterator: java.util.Iterator<com.vzome.core.construction.Construction> = man.getConstructions(); iterator.hasNext(); ) {{
                            const c: com.vzome.core.construction.Construction = iterator.next();
                            CommandEdit.logger_$LI$().finer("   " + c.toString());
                        };}
                    }
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    if (isHide)this.hideManifestation(man); else {
                        const construction: com.vzome.core.construction.Construction = man.getFirstConstruction();
                        constrsBefore.add(construction);
                    }
                }
            }
            this.redo();
            if (isHide)return;
            if (this.mAttrs == null)this.mAttrs = new com.vzome.core.commands.AttributeMap();
            const symmAxis: com.vzome.core.construction.Segment = (<com.vzome.core.editor.api.LegacyEditorModel><any>this.mEditorModel).getSymmetrySegment();
            if (symmAxis != null)this.mAttrs.put(com.vzome.core.commands.CommandTransform.SYMMETRY_AXIS_ATTR_NAME, symmAxis);
            this.mAttrs.put(com.vzome.core.commands.CommandTransform.SYMMETRY_CENTER_ATTR_NAME, (<com.vzome.core.editor.api.LegacyEditorModel><any>this.mEditorModel).getCenterPoint());
            this.mAttrs.put(com.vzome.core.commands.Command.FIELD_ATTR_NAME, this.mManifestations.getField());
            const news: CommandEdit.NewConstructions = new CommandEdit.NewConstructions();
            let selectionAfter: com.vzome.core.construction.ConstructionList = null;
            const signature: any[][] = this.mCommand.getParameterSignature();
            const actualsLen: number = constrsBefore.size();
            if ((signature.length === actualsLen) || (signature.length === 1 && /* equals */(<any>((o1: any, o2: any) => o1 && o1.equals ? o1.equals(o2) : o1 === o2)(signature[0][0],com.vzome.core.commands.Command.GENERIC_PARAM_NAME)))){
                try {
                    selectionAfter = this.mCommand.apply(constrsBefore, this.mAttrs, news);
                } catch(f) {
                    this.undo();
                    throw f;
                }
            } else if (signature.length > actualsLen){
                this.fail("Too few objects in the selection.");
            } else if (signature.length === 1){
                let partial: com.vzome.core.construction.ConstructionList;
                selectionAfter = new com.vzome.core.construction.ConstructionList();
                for(let i: number = 0; i < actualsLen; i++) {{
                    const param: com.vzome.core.construction.Construction = constrsBefore.get(i);
                    const formalClass: any = (<any>signature[0][1]);
                    if ((formalClass === com.vzome.core.construction.Point && (param != null && param instanceof <any>com.vzome.core.construction.Point)) || (formalClass === com.vzome.core.construction.Segment && (param != null && param instanceof <any>com.vzome.core.construction.Segment))){
                        const single: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
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
                        (<com.vzome.core.editor.api.LegacyEditorModel><any>this.mEditorModel).addFailedConstruction(cons);
                        continue;
                    }
                    const man: com.vzome.core.model.Manifestation = this.manifestConstruction(cons);
                    if (man != null)this.select$com_vzome_core_model_Manifestation(man);
                }
            }
            this.redo();
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         * @param {*} context
         */
        public loadAndPerform(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat, context: com.vzome.core.editor.api.Context) {
            let cmdName: string = null;
            if (format.selectionsNotSaved())cmdName = xml.getLocalName(); else if (format.commandEditsCompacted())cmdName = "Command" + xml.getLocalName(); else cmdName = xml.getAttribute("command");
            if (cmdName === ("CommandIcosahedralSymmetry"))cmdName = "CommandSymmetry";
            this.mCommand = <com.vzome.core.commands.AbstractCommand><any>context.createLegacyCommand(cmdName);
            if (format.selectionsNotSaved()){
                const selectedBefore: java.util.Set<com.vzome.core.model.Manifestation> = <any>(new java.util.LinkedHashSet<any>());
                context.performAndRecord(new com.vzome.core.editor.BeginBlock(null));
                this.mAttrs = new com.vzome.core.commands.AttributeMap();
                const nodes: org.w3c.dom.NodeList = xml.getChildNodes();
                for(let j: number = 0; j < nodes.getLength(); j++) {{
                    const kid2: org.w3c.dom.Node = nodes.item(j);
                    if (kid2 != null && (kid2.constructor != null && kid2.constructor["__interfaces"] != null && kid2.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                        const attrOrParam: org.w3c.dom.Element = <org.w3c.dom.Element><any>kid2;
                        const apName: string = attrOrParam.getLocalName();
                        if (apName === ("attr")){
                            let attrName: string = attrOrParam.getAttribute("name");
                            if (/* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(attrName, ".symmetry.center"))attrName = com.vzome.core.commands.CommandTransform.SYMMETRY_CENTER_ATTR_NAME; else if (attrName === ("reflection.mirror.normal.segment"))attrName = com.vzome.core.commands.CommandTransform.SYMMETRY_AXIS_ATTR_NAME;
                            const val: org.w3c.dom.Element = com.vzome.xml.DomUtils.getFirstChildElement$org_w3c_dom_Element(attrOrParam);
                            let valName: string = val.getLocalName();
                            if (valName === ("FreePoint"))valName = "point";
                            let value: any = format.parseAlgebraicObject(valName, val);
                            if (value === com.vzome.core.commands.XmlSaveFormat.NOT_AN_ATTRIBUTE_$LI$())value = format.parseConstruction$java_lang_String$org_w3c_dom_Element(valName, val);
                            if (attrName === com.vzome.core.commands.CommandTransform.SYMMETRY_CENTER_ATTR_NAME){
                                const c: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint((<com.vzome.core.construction.Point>value).getLocation().projectTo3d(true));
                                context.performAndRecord(new com.vzome.core.edits.SymmetryCenterChange(<com.vzome.core.editor.api.LegacyEditorModel><any>this.mEditorModel, c));
                            } else if (attrName === com.vzome.core.commands.CommandTransform.SYMMETRY_AXIS_ATTR_NAME){
                                context.performAndRecord(new com.vzome.core.edits.SymmetryAxisChange(<com.vzome.core.editor.api.LegacyEditorModel><any>this.mEditorModel, <com.vzome.core.construction.Segment>value));
                                if (!this.mCommand.attributeIs3D(attrName)){
                                    const vector: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.core.construction.Segment>value).getOffset();
                                    this.mCommand.setQuaternion(vector);
                                }
                            } else this.mAttrs.put(attrName, value);
                        } else {
                            const c: com.vzome.core.construction.Construction = format.parseConstruction$java_lang_String$org_w3c_dom_Element(apName, attrOrParam);
                            if (c != null){
                                if ((<com.vzome.core.editor.api.LegacyEditorModel><any>this.mEditorModel).hasFailedConstruction(c)){
                                    CommandEdit.logBugAccommodation("skip selecting a failed construction");
                                    continue;
                                }
                                const m: com.vzome.core.model.Manifestation = this.getManifestation(c);
                                if (m == null || m.isUnnecessary()){
                                    CommandEdit.loadAndPerformLgger_$LI$().severe("CommandEdit parameter: " + attrOrParam.toString());
                                    throw new com.vzome.core.commands.Command.Failure("no manifestation to be selected.");
                                }
                                if (!selectedBefore.contains(m))selectedBefore.add(m);
                            }
                        }
                    }
                };}
                if (selectedBefore.size() > (this.mManifestations.size() / 2|0)){
                    const toUnselect: java.util.Collection<com.vzome.core.model.Manifestation> = <any>(new java.util.ArrayList<any>());
                    for(let index=this.mManifestations.iterator();index.hasNext();) {
                        let m = index.next();
                        {
                            if (!selectedBefore.contains(m))toUnselect.add(m);
                        }
                    }
                    let edit: com.vzome.core.editor.api.ChangeSelection = new com.vzome.core.edits.SelectAll(this.mEditorModel);
                    context.performAndRecord(edit);
                    for(let index=toUnselect.iterator();index.hasNext();) {
                        let m = index.next();
                        {
                            edit = new com.vzome.core.edits.SelectManifestation(this.mEditorModel, m);
                            context.performAndRecord(edit);
                        }
                    }
                } else {
                    let edit: com.vzome.core.editor.api.ChangeSelection = new com.vzome.core.edits.DeselectAll(this.mEditorModel);
                    context.performAndRecord(edit);
                    for(let index=selectedBefore.iterator();index.hasNext();) {
                        let m = index.next();
                        {
                            edit = new com.vzome.core.edits.SelectManifestation(this.mEditorModel, m);
                            context.performAndRecord(edit);
                        }
                    }
                }
                context.performAndRecord(new com.vzome.core.editor.EndBlock(null));
                this.redo();
                if (this.mCommand != null && this.mCommand instanceof <any>com.vzome.core.commands.CommandObliquePentagon){
                    const edit: com.vzome.core.editor.api.UndoableEdit = new com.vzome.core.edits.AffinePentagon(this.mEditorModel);
                    context.performAndRecord(edit);
                    return;
                }
                this.mCommand.setFixedAttributes(this.mAttrs, format);
                this.mAttrs.put(com.vzome.core.commands.Command.LOADING_FROM_FILE, javaemul.internal.BooleanHelper.TRUE);
                context.performAndRecord(this);
            } else super.loadAndPerform(xml, format, context);
        }
    }
    CommandEdit["__class"] = "com.vzome.core.editor.CommandEdit";


    export namespace CommandEdit {

        export class NewConstructions extends java.util.ArrayList<com.vzome.core.construction.Construction> implements com.vzome.core.construction.ConstructionChanges {
            public constructionAdded$com_vzome_core_construction_Construction(c: com.vzome.core.construction.Construction) {
                this.add(c);
            }

            public constructionAdded$com_vzome_core_construction_Construction$com_vzome_core_construction_Color(c: com.vzome.core.construction.Construction, color: com.vzome.core.construction.Color) {
                this.add(c);
            }

            /**
             * 
             * @param {com.vzome.core.construction.Construction} c
             * @param {com.vzome.core.construction.Color} color
             */
            public constructionAdded(c?: any, color?: any) {
                if (((c != null && c instanceof <any>com.vzome.core.construction.Construction) || c === null) && ((color != null && color instanceof <any>com.vzome.core.construction.Color) || color === null)) {
                    return <any>this.constructionAdded$com_vzome_core_construction_Construction$com_vzome_core_construction_Color(c, color);
                } else if (((c != null && c instanceof <any>com.vzome.core.construction.Construction) || c === null) && color === undefined) {
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

}

