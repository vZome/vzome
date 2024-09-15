/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class ToolController extends com.vzome.desktop.controller.DefaultController {
        /*private*/ tool: com.vzome.api.Tool;

        /*private*/ selectOutputs: boolean;

        /*private*/ justSelect: boolean;

        public constructor(tool: com.vzome.api.Tool) {
            super();
            if (this.tool === undefined) { this.tool = null; }
            if (this.selectOutputs === undefined) { this.selectOutputs = false; }
            if (this.justSelect === undefined) { this.justSelect = false; }
            this.tool = tool;
            this.selectOutputs = true;
            this.justSelect = false;
        }

        /**
         * 
         * @param {string} action
         */
        public doAction(action: string) {
            const selectInputs: boolean = this.tool.isSelectInputs();
            let deleteInputs: boolean = this.tool.isDeleteInputs();
            let copyColors: boolean = this.tool.isCopyColors();
            switch((action)) {
            case "apply":
                const createOutputs: boolean = !this.justSelect;
                this.tool.apply(selectInputs, deleteInputs, createOutputs, this.selectOutputs, copyColors);
                break;
            case "hideTool":
                this.tool.setHidden(true);
                break;
            case "selectParams":
                this.tool.selectParameters();
                break;
            case "selectInputs":
                this.tool.setInputBehaviors(!selectInputs, deleteInputs);
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("selectInputs", null, javaemul.internal.BooleanHelper.toString(this.tool.isSelectInputs()));
                break;
            case "deleteInputs":
                deleteInputs = !deleteInputs;
                this.tool.setInputBehaviors(selectInputs && !deleteInputs, deleteInputs);
                if (deleteInputs){
                    this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("selectInputs", null, "false");
                }
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("deleteInputs", null, javaemul.internal.BooleanHelper.toString(deleteInputs));
                break;
            case "copyColors":
                copyColors = !copyColors;
                this.tool.setCopyColors(copyColors);
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("copyColors", null, javaemul.internal.BooleanHelper.toString(copyColors));
                break;
            case "selectOutputs":
                this.selectOutputs = !this.selectOutputs;
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("selectOutputs", null, javaemul.internal.BooleanHelper.toString(this.selectOutputs));
                break;
            case "createOutputs":
                this.justSelect = !this.justSelect;
                if (this.justSelect){
                    this.selectOutputs = true;
                    this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("selectOutputs", null, "true");
                }
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("createOutputs", null, javaemul.internal.BooleanHelper.toString(!this.justSelect));
                break;
            default:
                super.doAction(action);
            }
        }

        /**
         * 
         * @param {string} name
         * @return {string}
         */
        public getProperty(name: string): string {
            switch((name)) {
            case "id":
                return this.tool.getId();
            case "label":
                return this.tool.getLabel();
            case "kind":
                return this.tool.getCategory();
            case "predefined":
                return javaemul.internal.BooleanHelper.toString(this.tool.isPredefined());
            case "selectInputs":
                return javaemul.internal.BooleanHelper.toString(this.tool.isSelectInputs());
            case "deleteInputs":
                return javaemul.internal.BooleanHelper.toString(this.tool.isDeleteInputs());
            case "copyColors":
                return javaemul.internal.BooleanHelper.toString(this.tool.isCopyColors());
            case "selectOutputs":
                return javaemul.internal.BooleanHelper.toString(this.selectOutputs);
            case "createOutputs":
                return javaemul.internal.BooleanHelper.toString(!this.justSelect);
            default:
                return super.getProperty(name);
            }
        }

        /**
         * 
         * @param {string} name
         * @param {*} value
         */
        public setModelProperty(name: string, value: any) {
            switch((name)) {
            case "label":
                this.tool.setLabel(<string>value);
                this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("label", null, <string>value);
                return;
            default:
                super.setModelProperty(name, value);
            }
        }
    }
    ToolController["__class"] = "com.vzome.desktop.controller.ToolController";
    ToolController["__interfaces"] = ["com.vzome.desktop.api.Controller"];


}

