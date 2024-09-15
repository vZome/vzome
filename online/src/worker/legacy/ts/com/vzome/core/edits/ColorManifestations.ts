/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class ColorManifestations extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ color: com.vzome.core.construction.Color;

        public constructor(editorModel: com.vzome.core.editor.api.EditorModel) {
            super(editorModel);
            if (this.color === undefined) { this.color = null; }
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            const mode: string = <string>props.get("mode");
            if (mode != null)this.initialize(new com.vzome.core.construction.Color(mode));
        }

        /*private*/ initialize(color: com.vzome.core.construction.Color) {
            this.color = color;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let m = index.next();
                {
                    if (m.isRendered())this.colorManifestation(m, color);
                    this.unselect$com_vzome_core_model_Manifestation$boolean(m, true);
                }
            }
        }

        /**
         * 
         * @param {*} result
         */
        public getXmlAttributes(result: org.w3c.dom.Element) {
            result.setAttribute("red", "" + this.color.getRed());
            result.setAttribute("green", "" + this.color.getGreen());
            result.setAttribute("blue", "" + this.color.getBlue());
            const alpha: number = this.color.getAlpha();
            if (alpha < 255)result.setAttribute("alpha", "" + alpha);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            if (format.loadToRender()){
                const red: string = xml.getAttribute("red");
                const green: string = xml.getAttribute("green");
                const blue: string = xml.getAttribute("blue");
                const alphaStr: string = xml.getAttribute("alpha");
                const alpha: number = (alphaStr == null || /* isEmpty */(alphaStr.length === 0)) ? 255 : javaemul.internal.IntegerHelper.parseInt(alphaStr);
                this.initialize(new com.vzome.core.construction.Color(javaemul.internal.IntegerHelper.parseInt(red), javaemul.internal.IntegerHelper.parseInt(green), javaemul.internal.IntegerHelper.parseInt(blue), alpha));
            } else this.initialize(null);
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "setItemColor";
        }
    }
    ColorManifestations["__class"] = "com.vzome.core.edits.ColorManifestations";

}

