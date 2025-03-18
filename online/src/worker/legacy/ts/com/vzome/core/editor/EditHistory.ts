/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export class EditHistory implements java.lang.Iterable<com.vzome.core.editor.api.UndoableEdit> {
        /*private*/ mEdits: java.util.List<com.vzome.core.editor.api.UndoableEdit>;

        /*private*/ mEditNumber: number;

        /*private*/ breakpointHit: boolean;

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (EditHistory.logger == null) { EditHistory.logger = java.util.logging.Logger.getLogger("com.vzome.core.EditHistory"); }  return EditHistory.logger; }

        static breakpointLogger: java.util.logging.Logger; public static breakpointLogger_$LI$(): java.util.logging.Logger { if (EditHistory.breakpointLogger == null) { EditHistory.breakpointLogger = java.util.logging.Logger.getLogger("com.vzome.core.editor.Breakpoint"); }  return EditHistory.breakpointLogger; }

        public listener: EditHistory.Listener;

        public setListener(listener: EditHistory.Listener) {
            this.listener = listener;
        }

        /*private*/ serializer: EditHistory.XmlSerializer;

        public setSerializer(serializer: EditHistory.XmlSerializer) {
            this.serializer = serializer;
        }

        public addEdit(edit: com.vzome.core.editor.api.UndoableEdit, context: com.vzome.core.editor.api.Context) {
            if (!edit.isDestructive()){
                this.mEdits.add(this.mEditNumber, edit);
                ++this.mEditNumber;
                return;
            }
            if (this.mEditNumber < this.mEdits.size()){
                let makeBranch: boolean = false;
                let lastStickyEdit: number = this.mEditNumber - 1;
                let deadEditIndex: number = this.mEditNumber;
                for(const deadEdits: java.util.Iterator<com.vzome.core.editor.api.UndoableEdit> = this.mEdits.listIterator(this.mEditNumber); deadEdits.hasNext(); ) {{
                    const dead: com.vzome.core.editor.api.UndoableEdit = deadEdits.next();
                    if (dead.isSticky()){
                        makeBranch = true;
                        lastStickyEdit = deadEditIndex;
                    }
                    ++deadEditIndex;
                };}
                const branch: com.vzome.core.editor.Branch = makeBranch ? new com.vzome.core.editor.Branch(context) : null;
                deadEditIndex = this.mEditNumber;
                for(const deadEdits: java.util.Iterator<com.vzome.core.editor.api.UndoableEdit> = this.mEdits.listIterator(this.mEditNumber); deadEdits.hasNext(); ) {{
                    const removed: com.vzome.core.editor.api.UndoableEdit = deadEdits.next();
                    deadEdits.remove();
                    if (deadEditIndex <= lastStickyEdit){
                        branch.addEdit(removed);
                    }
                    ++deadEditIndex;
                };}
                if (makeBranch){
                    this.mEdits.add(branch);
                    ++this.mEditNumber;
                }
            }
            this.mEdits.add(edit);
            ++this.mEditNumber;
        }

        public undoAll(): com.vzome.core.editor.api.UndoableEdit {
            let last: com.vzome.core.editor.api.UndoableEdit = null;
            do {{
                const edit: com.vzome.core.editor.api.UndoableEdit = this.undo$();
                if (edit == null)break;
                last = edit;
            }} while((true));
            this.listener.publishChanges();
            return last;
        }

        public undoToManifestation(man: com.vzome.core.model.Manifestation): com.vzome.core.editor.api.UndoableEdit {
            let edit: com.vzome.core.editor.api.UndoableEdit = null;
            do {{
                edit = this.undo$();
                if (edit == null)break;
                if ((edit != null && edit instanceof <any>com.vzome.core.editor.api.ChangeManifestations) && (<com.vzome.core.editor.api.ChangeManifestations>edit).showsManifestation(man)){
                    break;
                }
            }} while((true));
            this.listener.publishChanges();
            return edit;
        }

        public redoToBreakpoint(): com.vzome.core.editor.api.UndoableEdit {
            let edit: com.vzome.core.editor.api.UndoableEdit = this.redo$();
            if (edit == null)return edit;
            do {{
                if (this.atBreakpoint())break;
                edit = this.redo$();
                if (edit == null)break;
            }} while((true));
            this.listener.publishChanges();
            return edit;
        }

        atBreakpoint(): boolean {
            if (this.mEditNumber === this.mEdits.size())return false;
            const edit: com.vzome.core.editor.api.UndoableEdit = this.mEdits.get(this.mEditNumber);
            return edit.hasBreakpoint();
        }

        public setBreakpoints(lineNumbers: number[]) {
            java.util.Arrays.sort(lineNumbers);
            let index: number = 0;
            let lineNumber: number = lineNumbers[index];
            for(let index1=this.mEdits.iterator();index1.hasNext();) {
                let edit = index1.next();
                {
                    const startLine: number = edit.getLineNumber();
                    if (startLine !== 0 && startLine >= lineNumber){
                        edit.setBreakpoint(true);
                        ++index;
                        if (index < lineNumbers.length)lineNumber = lineNumbers[index]; else lineNumber = javaemul.internal.IntegerHelper.MAX_VALUE;
                    } else {
                        edit.setBreakpoint(false);
                    }
                }
            }
        }

        public getBreakpoints(): java.util.List<number> {
            const result: java.util.List<number> = <any>(new java.util.ArrayList<any>());
            for(let index=this.mEdits.iterator();index.hasNext();) {
                let edit = index.next();
                {
                    if (edit.hasBreakpoint())result.add(edit.getLineNumber());
                }
            }
            return result;
        }

        public redoAll(breakpoint: number): com.vzome.core.editor.api.UndoableEdit {
            let last: com.vzome.core.editor.api.UndoableEdit = null;
            this.breakpointHit = false;
            do {{
                const edit: com.vzome.core.editor.api.UndoableEdit = this.redo$();
                if (edit == null)break;
                last = edit;
                if (this.breakpointHit){
                    this.breakpointHit = false;
                    break;
                }
            }} while((breakpoint === -1 || this.mEditNumber < breakpoint));
            this.listener.publishChanges();
            return last;
        }

        public goToEdit(editNum: number) {
            if (editNum === -1)editNum = this.mEdits.size();
            if (editNum === this.mEditNumber)return;
            while((this.mEditNumber < editNum)) {{
                if (this.mEditNumber === this.mEdits.size())break;
                const undoable: com.vzome.core.editor.api.UndoableEdit = this.mEdits.get(this.mEditNumber++);
                undoable.redo();
            }};
            while((this.mEditNumber > editNum)) {{
                if (this.mEditNumber === 0)break;
                const undoable: com.vzome.core.editor.api.UndoableEdit = this.mEdits.get(--this.mEditNumber);
                undoable.undo();
            }};
            this.listener.publishChanges();
        }

        public undo$(): com.vzome.core.editor.api.UndoableEdit {
            return this.undo$boolean(true);
        }

        public undo$boolean(useBlocks: boolean): com.vzome.core.editor.api.UndoableEdit {
            if (this.mEditNumber === 0)return null;
            const undoable: com.vzome.core.editor.api.UndoableEdit = this.mEdits.get(--this.mEditNumber);
            if (useBlocks && (undoable != null && undoable instanceof <any>com.vzome.core.editor.EndBlock))return this.undoBlock();
            undoable.undo();
            EditHistory.logger_$LI$().fine("undo: " + undoable.toString());
            if (undoable != null && undoable instanceof <any>com.vzome.core.editor.BeginBlock)return undoable;
            if (!undoable.isVisible())return this.undo$();
            this.listener.publishChanges();
            return undoable;
        }

        public undo(useBlocks?: any): com.vzome.core.editor.api.UndoableEdit {
            if (((typeof useBlocks === 'boolean') || useBlocks === null)) {
                return <any>this.undo$boolean(useBlocks);
            } else if (useBlocks === undefined) {
                return <any>this.undo$();
            } else throw new Error('invalid overload');
        }

        undoBlock(): com.vzome.core.editor.api.UndoableEdit {
            let undone: com.vzome.core.editor.api.UndoableEdit;
            do {{
                undone = this.undo$();
            }} while((!(undone != null && undone instanceof <any>com.vzome.core.editor.BeginBlock)));
            return undone;
        }

        public getNextLineNumber(): number {
            if (this.mEdits.isEmpty())return 3;
            let editNumber: number = this.mEditNumber;
            if (editNumber >= this.mEdits.size())editNumber = this.mEdits.size() - 1;
            const undoable: com.vzome.core.editor.api.UndoableEdit = this.mEdits.get(editNumber);
            if (undoable != null && undoable instanceof <any>com.vzome.core.editor.EditHistory.DeferredEdit)return (<EditHistory.DeferredEdit>undoable).getLineNumber(); else return 0;
        }

        public redo$(): com.vzome.core.editor.api.UndoableEdit {
            return this.redo$boolean(true);
        }

        public redo$boolean(useBlocks: boolean): com.vzome.core.editor.api.UndoableEdit {
            if (this.mEditNumber === this.mEdits.size())return null;
            const undoable: com.vzome.core.editor.api.UndoableEdit = this.mEdits.get(this.mEditNumber++);
            if (useBlocks && (undoable != null && undoable instanceof <any>com.vzome.core.editor.BeginBlock))return this.redoBlock();
            try {
                if (EditHistory.logger_$LI$().isLoggable(java.util.logging.Level.FINE))EditHistory.logger_$LI$().fine("redo: " + undoable.toString());
                undoable.redo();
            } catch(e) {
                if (EditHistory.logger_$LI$().isLoggable(java.util.logging.Level.WARNING))EditHistory.logger_$LI$().warning("edit number that failed is " + (this.mEditNumber - 1));
                throw e;
            }
            if (undoable != null && undoable instanceof <any>com.vzome.core.editor.EndBlock)return undoable;
            if (!undoable.isVisible())return this.redo$();
            this.listener.publishChanges();
            return undoable;
        }

        public redo(useBlocks?: any): com.vzome.core.editor.api.UndoableEdit {
            if (((typeof useBlocks === 'boolean') || useBlocks === null)) {
                return <any>this.redo$boolean(useBlocks);
            } else if (useBlocks === undefined) {
                return <any>this.redo$();
            } else throw new Error('invalid overload');
        }

        redoBlock(): com.vzome.core.editor.api.UndoableEdit {
            let lastSuccessfulRedo: string = "none";
            const startingEditNumber: number = this.mEditNumber;
            let redone: com.vzome.core.editor.api.UndoableEdit;
            do {{
                redone = this.redo$();
                if (redone == null){
                    throw new java.lang.IllegalStateException("All " + this.mEditNumber + " edits have been redone without reaching an EndBlock. Starting edit number was " + startingEditNumber + ". Last successful redo was " + lastSuccessfulRedo + ". ");
                }
                lastSuccessfulRedo = /* getSimpleName */(c => typeof c === 'string' ? (<any>c).substring((<any>c).lastIndexOf('.')+1) : c["__class"] ? c["__class"].substring(c["__class"].lastIndexOf('.')+1) : c["name"].substring(c["name"].lastIndexOf('.')+1))((<any>redone.constructor));
                if (EditHistory.logger_$LI$().isLoggable(java.util.logging.Level.FINE)){
                    const msg: string = "redoBlock is redoing edits from " + startingEditNumber + ". Current edit number is " + this.mEditNumber + ". Last redone was " + lastSuccessfulRedo;
                    EditHistory.logger_$LI$().fine(msg);
                }
            }} while((!(redone != null && redone instanceof <any>com.vzome.core.editor.EndBlock)));
            return redone;
        }

        public getDetailXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("EditHistoryDetails");
            com.vzome.xml.DomUtils.addAttribute(result, "editNumber", /* toString */(''+(this.mEditNumber)));
            let edits: number = 0;
            let lastStickyEdit: number = -1;
            for(let index=this.iterator();index.hasNext();) {
                let undoable = index.next();
                {
                    const edit: org.w3c.dom.Element = undoable.getDetailXml(doc);
                    ++edits;
                    com.vzome.xml.DomUtils.addAttribute(edit, "editNumber", /* toString */(''+(edits)));
                    if (EditHistory.logger_$LI$().isLoggable(java.util.logging.Level.FINEST))EditHistory.logger_$LI$().finest("side-effect: " + this.serializer.serialize(edit));
                    result.appendChild(edit);
                    if (undoable.isSticky())lastStickyEdit = edits;
                }
            }
            result.setAttribute("lastStickyEdit", /* toString */(''+(lastStickyEdit)));
            return result;
        }

        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("EditHistory");
            com.vzome.xml.DomUtils.addAttribute(result, "editNumber", /* toString */(''+(this.mEditNumber)));
            return result;
        }

        public mergeSelectionChanges() {
            let cursor: number = this.mEditNumber;
            if (cursor === 0)return;
            --cursor;
            const above: com.vzome.core.editor.api.UndoableEdit = this.mEdits.get(cursor);
            if (above != null && above instanceof <any>com.vzome.core.editor.api.ChangeManifestations)return;
            if (!(above != null && above instanceof <any>com.vzome.core.editor.api.ChangeSelection))return;
            if (cursor === 0)return;
            --cursor;
            const below: com.vzome.core.editor.api.UndoableEdit = this.mEdits.get(cursor);
            if (below != null && below instanceof <any>com.vzome.core.editor.api.ChangeManifestations)return;
            if (below != null && below instanceof <any>com.vzome.core.editor.api.ChangeSelection){
                let bracket: com.vzome.core.editor.api.UndoableEdit = new com.vzome.core.editor.BeginBlock(null);
                this.mEdits.add(cursor, bracket);
                bracket = new com.vzome.core.editor.EndBlock(null);
                this.mEdits.add(bracket);
                this.mEditNumber += 2;
            } else if (below != null && below instanceof <any>com.vzome.core.editor.EndBlock){
                let scan: number = cursor - 1;
                const done: boolean = false;
                while((!done)) {{
                    const next: com.vzome.core.editor.api.UndoableEdit = this.mEdits.get(scan);
                    if (next != null && next instanceof <any>com.vzome.core.editor.api.ChangeManifestations)return;
                    if (next != null && next instanceof <any>com.vzome.core.editor.api.ChangeSelection)--scan; else if (next != null && next instanceof <any>com.vzome.core.editor.BeginBlock){
                        this.mEdits.remove(above);
                        this.mEdits.add(cursor, above);
                        return;
                    } else return;
                }};
            }
        }

        public replaceEdit(oldEdit: com.vzome.core.editor.api.UndoableEdit, newEdit: com.vzome.core.editor.api.UndoableEdit) {
            this.mEdits.set(this.mEdits.indexOf(oldEdit), newEdit);
        }

        /**
         * This is used during DeferredEdit .redo(), possibly to migrate one UndoableEdit into several.
         * It must maintain the invariant that the next UndoableEdit is the next DeferredEdit to redo.
         * @param {com.vzome.core.editor.api.UndoableEdit} edit
         */
        public insert(edit: com.vzome.core.editor.api.UndoableEdit) {
            this.mEdits.add(this.mEditNumber++, edit);
        }

        /**
         * Redo to greater of lastStickyEdit and lastDoneEdit, undo back to lastDoneEdit.
         * If there are explicitSnapshots, this is a migration of an old Article, using edit
         * numbers, and we have to redo as far as the last one, inserting snapshots as we go.
         * Note that lastStickyEdit and explicitSnapshots are mutually exclusive, after and before
         * migration, respectively.
         * 
         * @param {number} lastDoneEdit
         * @param {number} lastStickyEdit
         * @param {com.vzome.core.editor.api.UndoableEdit[]} explicitSnapshots
         * @throws Failure
         */
        public synchronize(lastDoneEdit: number, lastStickyEdit: number, explicitSnapshots: com.vzome.core.editor.api.UndoableEdit[]) {
            let redoThreshold: number = Math.max(lastDoneEdit, lastStickyEdit);
            if (explicitSnapshots != null)redoThreshold = Math.max(redoThreshold, explicitSnapshots.length - 1);
            this.mEditNumber = 0;
            let targetEdit: number = 0;
            const toRedo: java.util.List<com.vzome.core.editor.api.UndoableEdit> = <any>(new java.util.ArrayList<any>());
            for(let i: number = 0; i < redoThreshold; i++) {if (i < this.mEdits.size())toRedo.add(this.mEdits.get(i)); else break;;}
            for(let oldIndex: number = 0; oldIndex < toRedo.size(); oldIndex++) {{
                const edit: EditHistory.DeferredEdit = <EditHistory.DeferredEdit>toRedo.get(oldIndex);
                try {
                    if (explicitSnapshots != null && explicitSnapshots.length > oldIndex && explicitSnapshots[oldIndex] != null){
                        const snapshot: com.vzome.core.editor.api.UndoableEdit = explicitSnapshots[oldIndex];
                        this.mEdits.add(this.mEditNumber, snapshot);
                        if (this.mEditNumber <= lastDoneEdit)++lastDoneEdit;
                        ++this.mEditNumber;
                        snapshot.perform();
                    }
                    ++this.mEditNumber;
                    edit.redo();
                    if (oldIndex + 1 === lastDoneEdit)targetEdit = this.mEditNumber;
                } catch(e) {
                    if (EditHistory.logger_$LI$().isLoggable(java.util.logging.Level.WARNING))EditHistory.logger_$LI$().warning("edit number that failed is " + (this.mEditNumber - 1));
                    const t: Error = (<Error>null);
                    if (t != null && t instanceof <any>com.vzome.core.commands.Command.Failure)throw <com.vzome.core.commands.Command.Failure>t; else throw e;
                }
            };}
            if (explicitSnapshots != null && explicitSnapshots.length > redoThreshold && explicitSnapshots[redoThreshold] != null){
                const snapshot: com.vzome.core.editor.api.UndoableEdit = explicitSnapshots[redoThreshold];
                this.mEdits.add(this.mEditNumber, snapshot);
                ++this.mEditNumber;
                snapshot.perform();
            }
            this.goToEdit(targetEdit);
        }

        public loadEdit(format: com.vzome.core.commands.XmlSaveFormat, editElem: org.w3c.dom.Element, context: com.vzome.core.editor.api.Context) {
            const edit: EditHistory.DeferredEdit = new EditHistory.DeferredEdit(this, format, editElem, context);
            this.addEdit(edit, context);
        }

        /**
         * 
         * @return {*}
         */
        public iterator(): java.util.Iterator<com.vzome.core.editor.api.UndoableEdit> {
            return this.mEdits.iterator();
        }

        public getEditNumber(): number {
            return this.mEditNumber;
        }

        constructor() {
            this.mEdits = <any>(new java.util.ArrayList<any>());
            this.mEditNumber = 0;
            this.breakpointHit = false;
            if (this.listener === undefined) { this.listener = null; }
            if (this.serializer === undefined) { this.serializer = null; }
        }
    }
    EditHistory["__class"] = "com.vzome.core.editor.EditHistory";
    EditHistory["__interfaces"] = ["java.lang.Iterable"];



    export namespace EditHistory {

        export interface Listener {
            showCommand(xml: org.w3c.dom.Element, editNumber: number);

            publishChanges();
        }

        export interface XmlSerializer {
            serialize(xmlElement: org.w3c.dom.Element): string;
        }

        export class Breakpoint extends com.vzome.core.editor.api.UndoableEdit {
            public __parent: any;
            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                return doc.createElement("Breakpoint");
            }

            /**
             * 
             * @return {boolean}
             */
            public isNoOp(): boolean {
                return false;
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
             * @param {*} xml
             * @param {com.vzome.core.commands.XmlSaveFormat} format
             * @param {*} context
             */
            public loadAndPerform(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat, context: com.vzome.core.editor.api.Context) {
                context.performAndRecord(this);
            }

            /**
             * 
             */
            public perform() {
                com.vzome.core.editor.EditHistory.breakpointLogger_$LI$().info("hit a Breakpoint at " + this.__parent.mEditNumber);
                this.__parent.breakpointHit = true;
            }

            /**
             * 
             */
            public redo() {
                this.perform();
            }

            /**
             * 
             */
            public undo() {
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
            public isSticky(): boolean {
                return false;
            }

            /**
             * 
             * @return {boolean}
             */
            public isDestructive(): boolean {
                return false;
            }

            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getDetailXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                return this.getXml(doc);
            }

            constructor(__parent: any) {
                super();
                this.__parent = __parent;
            }
        }
        Breakpoint["__class"] = "com.vzome.core.editor.EditHistory.Breakpoint";


        export class DeferredEdit extends com.vzome.core.editor.api.UndoableEdit {
            public __parent: any;
            format: com.vzome.core.commands.XmlSaveFormat;

            xml: org.w3c.dom.Element;

            context: com.vzome.core.editor.api.Context;

            isBreakpoint: boolean;

            public constructor(__parent: any, format: com.vzome.core.commands.XmlSaveFormat, editElem: org.w3c.dom.Element, context: com.vzome.core.editor.api.Context) {
                super();
                this.__parent = __parent;
                if (this.format === undefined) { this.format = null; }
                if (this.xml === undefined) { this.xml = null; }
                if (this.context === undefined) { this.context = null; }
                this.isBreakpoint = false;
                this.format = format;
                this.xml = editElem;
                this.context = context;
            }

            /**
             * 
             * @param {boolean} value
             */
            public setBreakpoint(value: boolean) {
                this.isBreakpoint = value;
            }

            /**
             * 
             * @return {boolean}
             */
            public hasBreakpoint(): boolean {
                return this.isBreakpoint;
            }

            /**
             * 
             * @return {boolean}
             */
            public isNoOp(): boolean {
                return false;
            }

            /**
             * 
             * @return {number}
             */
            public getLineNumber(): number {
                const locationData: com.vzome.xml.LocationData = <com.vzome.xml.LocationData>this.xml.getUserData(com.vzome.xml.LocationData.LOCATION_DATA_KEY);
                if (locationData != null)return locationData.getStartLine(); else return 0;
            }

            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                return (/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(doc,this.xml.getOwnerDocument()))) ? this.xml : <org.w3c.dom.Element><any>doc.importNode(this.xml, true);
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

            /**
             * 
             */
            public redo() {
                const num: number = this.getLineNumber();
                this.__parent.mEdits.remove(--this.__parent.mEditNumber);
                if (com.vzome.core.editor.EditHistory.logger_$LI$().isLoggable(java.util.logging.Level.FINE))com.vzome.core.editor.EditHistory.logger_$LI$().fine("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " + num + ": " + this.__parent.serializer.serialize(this.xml));
                let realized: com.vzome.core.editor.api.UndoableEdit = null;
                const cmdName: string = this.xml.getLocalName();
                if (cmdName === ("Breakpoint")){
                    realized = new EditHistory.Breakpoint(this.__parent);
                } else realized = this.context.createEdit(this.xml);
                realized.setLineNumber(num);
                try {
                    this.__parent.listener.showCommand(this.xml, num);
                    realized.loadAndPerform(this.xml, this.format, new DeferredEdit.DeferredEdit$0(this));
                } catch(e) {
                    com.vzome.core.editor.EditHistory.logger_$LI$().warning("failure during initial edit replay:\n" + this.__parent.serializer.serialize(this.xml));
                    throw e;
                }
            }

            /**
             * 
             * @param {*} xml
             * @param {com.vzome.core.commands.XmlSaveFormat} format
             * @param {*} context
             */
            public loadAndPerform(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat, context: com.vzome.core.editor.api.Context) {
                throw new java.lang.IllegalStateException("should never be called");
            }

            /**
             * 
             */
            public undo() {
            }

            /**
             * 
             * @param {*} props
             */
            public configure(props: java.util.Map<string, any>) {
            }

            /**
             * 
             */
            public perform() {
            }

            /**
             * 
             * @return {boolean}
             */
            public isSticky(): boolean {
                return false;
            }

            /**
             * 
             * @param {*} doc
             * @return {*}
             */
            public getDetailXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
                return doc.createElement("deferredEdit");
            }
        }
        DeferredEdit["__class"] = "com.vzome.core.editor.EditHistory.DeferredEdit";


        export namespace DeferredEdit {

            export class DeferredEdit$0 implements com.vzome.core.editor.api.Context {
                public __parent: any;
                /**
                 * 
                 * @param {com.vzome.core.editor.api.UndoableEdit} edit
                 */
                public performAndRecord(edit: com.vzome.core.editor.api.UndoableEdit) {
                    try {
                        edit.perform();
                        if (edit.isNoOp())return;
                        if (com.vzome.core.editor.EditHistory.logger_$LI$().isLoggable(java.util.logging.Level.FINEST)){
                            const details: org.w3c.dom.Element = edit.getDetailXml(this.__parent.xml.getOwnerDocument());
                            com.vzome.core.editor.EditHistory.logger_$LI$().finest("side-effect: " + this.__parent.__parent.serializer.serialize(details));
                        }
                    } catch(e) {
                        throw new java.lang.RuntimeException(e);
                    }
                    this.__parent.__parent.insert(edit);
                }

                /**
                 * 
                 * @param {*} xml
                 * @return {com.vzome.core.editor.api.UndoableEdit}
                 */
                public createEdit(xml: org.w3c.dom.Element): com.vzome.core.editor.api.UndoableEdit {
                    const edit: com.vzome.core.editor.api.UndoableEdit = this.__parent.context.createEdit(xml);
                    edit.setLineNumber(this.__parent.getLineNumber());
                    return edit;
                }

                /**
                 * 
                 * @param {string} cmdName
                 * @return {*}
                 */
                public createLegacyCommand(cmdName: string): com.vzome.core.commands.Command {
                    return this.__parent.context.createLegacyCommand(cmdName);
                }

                /**
                 * 
                 * @param {string} action
                 * @param {*} props
                 * @return {boolean}
                 */
                public doEdit(action: string, props: java.util.Map<string, any>): boolean {
                    return false;
                }

                constructor(__parent: any) {
                    this.__parent = __parent;
                }
            }
            DeferredEdit$0["__interfaces"] = ["com.vzome.core.editor.api.Context"];


        }

    }

}

