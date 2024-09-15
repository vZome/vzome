/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * @author David Hall
     * This class is designed to be a generalized replacement for the legacy DeselectByClass
     * It allows balls, struts and/or panels to be selected, deselected or ignored by class
     * It can be used in place of DeselectByClass including the ability to parse the legacy XML.
     * DeselectByClass has been renamed as AdjustSelectionByClass and modified with the additional functionality.
     * @param {*} editor
     * @class
     * @extends com.vzome.core.editor.api.ChangeSelection
     */
    export class AdjustSelectionByClass extends com.vzome.core.editor.api.ChangeSelection {
        /*private*/ ballAction: com.vzome.core.editor.api.ActionEnum;

        /*private*/ strutAction: com.vzome.core.editor.api.ActionEnum;

        /*private*/ panelAction: com.vzome.core.editor.api.ActionEnum;

        /*private*/ editor: com.vzome.core.editor.api.EditorModel;

        /*private*/ originLast: boolean;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor.getSelection());
            this.ballAction = com.vzome.core.editor.api.ActionEnum.IGNORE;
            this.strutAction = com.vzome.core.editor.api.ActionEnum.IGNORE;
            this.panelAction = com.vzome.core.editor.api.ActionEnum.IGNORE;
            if (this.editor === undefined) { this.editor = null; }
            this.originLast = true;
            this.editor = editor;
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            const mode: string = <string>props.get("mode");
            if (mode != null)switch((mode)) {
            case "selectBalls":
                this.ballAction = com.vzome.core.editor.api.ActionEnum.SELECT;
                break;
            case "selectStruts":
                this.strutAction = com.vzome.core.editor.api.ActionEnum.SELECT;
                break;
            case "selectPanels":
                this.panelAction = com.vzome.core.editor.api.ActionEnum.SELECT;
                break;
            case "deselectBalls":
            case "unselectBalls":
                this.ballAction = com.vzome.core.editor.api.ActionEnum.DESELECT;
                break;
            case "deselectStruts":
                this.strutAction = com.vzome.core.editor.api.ActionEnum.DESELECT;
                break;
            case "deselectPanels":
                this.panelAction = com.vzome.core.editor.api.ActionEnum.DESELECT;
                break;
            case "unselectStruts":
            case "unselectStrutsAndPanels":
                this.strutAction = com.vzome.core.editor.api.ActionEnum.DESELECT;
                this.panelAction = com.vzome.core.editor.api.ActionEnum.DESELECT;
                break;
            }
        }

        /**
         * 
         */
        public perform() {
            const whichManifestationSet: java.lang.Iterable<com.vzome.core.model.Manifestation> = (this.ballAction === com.vzome.core.editor.api.ActionEnum.SELECT || this.strutAction === com.vzome.core.editor.api.ActionEnum.SELECT || this.panelAction === com.vzome.core.editor.api.ActionEnum.SELECT) ? this.editor.getRealizedModel() : this.mSelection;
            let originBall: com.vzome.core.model.Connector = null;
            for(let index=whichManifestationSet.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man.isRendered()){
                        if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                            if (this.originLast && originBall == null && this.ballAction === com.vzome.core.editor.api.ActionEnum.SELECT && man.getLocation().isOrigin()){
                                originBall = <com.vzome.core.model.Connector><any>man;
                            } else {
                                this.adjustSelection(man, this.ballAction);
                            }
                        } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                            this.adjustSelection(man, this.strutAction);
                        } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                            this.adjustSelection(man, this.panelAction);
                        }
                    }
                }
            }
            if (originBall != null){
                const ignoreGroups: boolean = true;
                if (this.mSelection.manifestationSelected(originBall)){
                    this.unselect$com_vzome_core_model_Manifestation$boolean(originBall, ignoreGroups);
                    this.redo();
                }
                this.select$com_vzome_core_model_Manifestation$boolean(originBall, ignoreGroups);
            }
            this.redo();
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            element.setAttribute("balls", /* Enum.name */com.vzome.core.editor.api.ActionEnum[this.ballAction]);
            element.setAttribute("struts", /* Enum.name */com.vzome.core.editor.api.ActionEnum[this.strutAction]);
            element.setAttribute("panels", /* Enum.name */com.vzome.core.editor.api.ActionEnum[this.panelAction]);
            if (this.originLast){
                element.setAttribute("originLast", "true");
            }
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            if (xml.getLocalName() === ("DeselectByClass")){
                if (xml.getAttribute("class") === ("balls")){
                    this.ballAction = com.vzome.core.editor.api.ActionEnum.DESELECT;
                    this.strutAction = com.vzome.core.editor.api.ActionEnum.IGNORE;
                    this.panelAction = com.vzome.core.editor.api.ActionEnum.IGNORE;
                } else {
                    this.ballAction = com.vzome.core.editor.api.ActionEnum.IGNORE;
                    this.strutAction = com.vzome.core.editor.api.ActionEnum.DESELECT;
                    this.panelAction = com.vzome.core.editor.api.ActionEnum.DESELECT;
                }
            } else {
                this.ballAction = /* Enum.valueOf */<any>com.vzome.core.editor.api.ActionEnum[xml.getAttribute("balls")];
                this.strutAction = /* Enum.valueOf */<any>com.vzome.core.editor.api.ActionEnum[xml.getAttribute("struts")];
                this.panelAction = /* Enum.valueOf */<any>com.vzome.core.editor.api.ActionEnum[xml.getAttribute("panels")];
            }
            const mode: string = xml.getAttribute("originLast");
            this.originLast = "true" === mode;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "AdjustSelectionByClass";
        }
    }
    AdjustSelectionByClass["__class"] = "com.vzome.core.edits.AdjustSelectionByClass";

}

