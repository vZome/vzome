/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class SelectAll extends com.vzome.core.editor.api.ChangeSelection {
        /*private*/ realizedModel: com.vzome.core.model.RealizedModel;

        /*private*/ originLast: boolean;

        /**
         * 
         */
        public perform() {
            if (this.originLast){
                let originBall: com.vzome.core.model.Connector = null;
                const ignoreGroups: boolean = true;
                for(let index=this.realizedModel.iterator();index.hasNext();) {
                    let m = index.next();
                    {
                        if (m.isRendered()){
                            if (originBall == null && (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) && m.getLocation().isOrigin()){
                                originBall = <com.vzome.core.model.Connector><any>m;
                            } else if (!this.mSelection.manifestationSelected(m)){
                                this.select$com_vzome_core_model_Manifestation$boolean(m, ignoreGroups);
                            }
                        }
                    }
                }
                if (originBall != null){
                    if (this.mSelection.manifestationSelected(originBall)){
                        this.unselect$com_vzome_core_model_Manifestation$boolean(originBall, ignoreGroups);
                        this.redo();
                    }
                    this.select$com_vzome_core_model_Manifestation$boolean(originBall, ignoreGroups);
                }
            } else {
                for(let index=this.realizedModel.iterator();index.hasNext();) {
                    let m = index.next();
                    {
                        if (m.isRendered()){
                            if (!this.mSelection.manifestationSelected(m))this.select$com_vzome_core_model_Manifestation$boolean(m, true);
                        }
                    }
                }
            }
            super.perform();
        }

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor.getSelection());
            if (this.realizedModel === undefined) { this.realizedModel = null; }
            this.originLast = true;
            this.realizedModel = editor.getRealizedModel();
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const mode: string = xml.getAttribute("originLast");
            this.originLast = "true" === mode;
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            if (this.originLast)element.setAttribute("originLast", "true");
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
            return "SelectAll";
        }
    }
    SelectAll["__class"] = "com.vzome.core.edits.SelectAll";

}

