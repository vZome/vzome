/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export class ToolsModel extends java.util.TreeMap<string, com.vzome.core.editor.Tool> implements com.vzome.core.editor.Tool.Source {
        /*private*/ editor: com.vzome.core.editor.api.EditorModel;

        /*private*/ lastId: number;

        /*private*/ pcs: java.beans.PropertyChangeSupport;

        /*private*/ context: com.vzome.core.editor.api.Context;

        /*private*/ originPoint: com.vzome.core.construction.Point;

        /*private*/ toolLabels: java.util.Map<string, string>;

        /*private*/ toolDeleteInputs: java.util.Map<string, boolean>;

        /*private*/ toolSelectInputs: java.util.Map<string, boolean>;

        /*private*/ toolCopyColors: java.util.Map<string, boolean>;

        /*private*/ hiddenTools: java.util.Set<string>;

        /*private*/ customTools: java.util.List<string>;

        /*private*/ customBookmarks: java.util.List<string>;

        public constructor(context: com.vzome.core.editor.api.Context, originPoint: com.vzome.core.construction.Point) {
            super();
            if (this.editor === undefined) { this.editor = null; }
            this.lastId = 0;
            this.pcs = new java.beans.PropertyChangeSupport(this);
            if (this.context === undefined) { this.context = null; }
            if (this.originPoint === undefined) { this.originPoint = null; }
            this.toolLabels = <any>(new java.util.HashMap<any, any>());
            this.toolDeleteInputs = <any>(new java.util.HashMap<any, any>());
            this.toolSelectInputs = <any>(new java.util.HashMap<any, any>());
            this.toolCopyColors = <any>(new java.util.HashMap<any, any>());
            this.hiddenTools = <any>(new java.util.HashSet<any>());
            this.customTools = <any>(new java.util.ArrayList<any>());
            this.customBookmarks = <any>(new java.util.ArrayList<any>());
            this.context = context;
            this.originPoint = originPoint;
        }

        public reserveId(): number {
            return this.lastId++;
        }

        /**
         * Only called during load of a document, before any new tool creations with reserveId.
         * @param {number} id
         */
        public setMaxId(id: number) {
            if (id >= this.lastId)this.lastId = id + 1;
        }

        /**
         * 
         * @param {string} key
         * @param {com.vzome.core.editor.Tool} tool
         * @return {com.vzome.core.editor.Tool}
         */
        public put(key: string, tool: com.vzome.core.editor.Tool): com.vzome.core.editor.Tool {
            const result: com.vzome.core.editor.Tool = super.put(key, tool);
            this.pcs.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("tool.instances", null, tool);
            if (!tool.isPredefined() && !tool.isHidden()){
                if (tool.getCategory() === com.vzome.core.tools.BookmarkTool.ID){
                    this.customBookmarks.add(key);
                    this.pcs.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("customBookmarks", null, this.getToolIDs(true));
                } else {
                    this.customTools.add(key);
                    this.pcs.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("customTools", null, this.getToolIDs(false));
                }
            }
            return result;
        }

        public getToolIDs(bookmarks: boolean): string[] {
            return bookmarks ? this.customBookmarks.toArray<any>([]) : this.customTools.toArray<any>([]);
        }

        public createEdit(className: string): com.vzome.core.editor.api.UndoableEdit {
            switch((className)) {
            case "ToolApplied":
                return new com.vzome.core.editor.ApplyTool(this, null, false, false, false, false, false, true);
            case "ApplyTool":
                return new com.vzome.core.editor.ApplyTool(this, null, false, false, false, false, true, true);
            case "SelectToolParameters":
                return new com.vzome.core.editor.SelectToolParameters(this, null);
            default:
                return null;
            }
        }

        public applyTool(tool: com.vzome.core.editor.Tool, selectInputs: boolean, deleteInputs: boolean, createOutputs: boolean, selectOutputs: boolean, copyColors: boolean) {
            const edit: com.vzome.core.editor.api.UndoableEdit = new com.vzome.core.editor.ApplyTool(this, tool, selectInputs, deleteInputs, createOutputs, selectOutputs, true, copyColors);
            this.getContext().performAndRecord(edit);
        }

        public selectToolParameters(tool: com.vzome.core.editor.Tool) {
            const edit: com.vzome.core.editor.api.UndoableEdit = new com.vzome.core.editor.SelectToolParameters(this, tool);
            this.getContext().performAndRecord(edit);
        }

        public addPropertyListener(listener: java.beans.PropertyChangeListener) {
            this.pcs.addPropertyChangeListener$java_beans_PropertyChangeListener(listener);
        }

        public removePropertyListener(listener: java.beans.PropertyChangeListener) {
            this.pcs.removePropertyChangeListener$java_beans_PropertyChangeListener(listener);
        }

        public setEditorModel(editor: com.vzome.core.editor.api.EditorModel) {
            this.editor = editor;
        }

        public getEditorModel(): com.vzome.core.editor.api.EditorModel {
            return this.editor;
        }

        /**
         * 
         * @param {string} id
         * @return {com.vzome.core.editor.Tool}
         */
        public getPredefinedTool(id: string): com.vzome.core.editor.Tool {
            return this.get(id);
        }

        public getContext(): com.vzome.core.editor.api.Context {
            return this.context;
        }

        public getOriginPoint(): com.vzome.core.construction.Point {
            return this.originPoint;
        }

        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("Tools");
            for(let index=this.values().iterator();index.hasNext();) {
                let tool = index.next();
                if (!tool.isPredefined()){
                    const toolElem: org.w3c.dom.Element = doc.createElement("Tool");
                    com.vzome.xml.DomUtils.addAttribute(toolElem, "id", tool.getId());
                    com.vzome.xml.DomUtils.addAttribute(toolElem, "label", tool.getLabel());
                    if (tool.isHidden())com.vzome.xml.DomUtils.addAttribute(toolElem, "hidden", "true");
                    toolElem.setAttribute("selectInputs", javaemul.internal.BooleanHelper.toString(tool.isSelectInputs()));
                    toolElem.setAttribute("deleteInputs", javaemul.internal.BooleanHelper.toString(tool.isDeleteInputs()));
                    toolElem.setAttribute("copyColors", javaemul.internal.BooleanHelper.toString(tool.isCopyColors()));
                    result.appendChild(toolElem);
                }
            }
            return result;
        }

        loadFromXml(xml: org.w3c.dom.Element) {
            const nodes: org.w3c.dom.NodeList = xml.getChildNodes();
            for(let i: number = 0; i < nodes.getLength(); i++) {{
                const node: org.w3c.dom.Node = nodes.item(i);
                if (node != null && (node.constructor != null && node.constructor["__interfaces"] != null && node.constructor["__interfaces"].indexOf("org.w3c.dom.Element") >= 0)){
                    const toolElem: org.w3c.dom.Element = <org.w3c.dom.Element><any>node;
                    const id: string = toolElem.getAttribute("id");
                    const label: string = toolElem.getAttribute("label");
                    this.toolLabels.put(id, label);
                    let value: string = toolElem.getAttribute("selectInputs");
                    if (value != null && !(value === ("")))this.toolSelectInputs.put(id, javaemul.internal.BooleanHelper.parseBoolean(value));
                    value = toolElem.getAttribute("deleteInputs");
                    if (value != null && !(value === ("")))this.toolDeleteInputs.put(id, javaemul.internal.BooleanHelper.parseBoolean(value));
                    value = toolElem.getAttribute("copyColors");
                    if (value != null && !(value === ("")))this.toolCopyColors.put(id, javaemul.internal.BooleanHelper.parseBoolean(value));
                    const hiddenStr: string = toolElem.getAttribute("hidden");
                    if (hiddenStr != null && (hiddenStr === ("true")))this.hiddenTools.add(id);
                }
            };}
        }

        public setConfiguration(tool: com.vzome.core.editor.Tool) {
            const id: string = tool.getId();
            const label: string = this.toolLabels.get(id);
            if (label != null)tool.setLabel(label);
            if (this.toolDeleteInputs.containsKey(id) || this.toolSelectInputs.containsKey(id)){
                const deleteInputs: boolean = this.toolDeleteInputs.containsKey(id) ? this.toolDeleteInputs.get(id) : true;
                const selectInputs: boolean = this.toolSelectInputs.containsKey(id) ? this.toolSelectInputs.get(id) : false;
                tool.setInputBehaviors(selectInputs, deleteInputs);
            }
            if (this.toolCopyColors.containsKey(id)){
                tool.setCopyColors(this.toolCopyColors.get(id));
            }
            tool.setHidden(this.hiddenTools.contains(id));
        }

        public hideTool(tool: com.vzome.core.editor.Tool) {
            this.pcs.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("tool.instances", tool, null);
            if (tool.getCategory() === com.vzome.core.tools.BookmarkTool.ID){
                this.customBookmarks.remove(tool.getId());
                this.pcs.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("customBookmarks", null, this.getToolIDs(true));
            } else {
                this.customTools.remove(tool.getId());
                this.pcs.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("customTools", null, this.getToolIDs(false));
            }
        }
    }
    ToolsModel["__class"] = "com.vzome.core.editor.ToolsModel";
    ToolsModel["__interfaces"] = ["java.lang.Cloneable","com.vzome.api.Tool.Source","java.util.Map","java.util.NavigableMap","java.util.SortedMap","java.io.Serializable"];


}

