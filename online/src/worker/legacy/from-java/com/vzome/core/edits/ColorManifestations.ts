import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Color } from "../construction/Color.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class ColorManifestations extends ChangeManifestations {
    /*private*/ color: Color;

    public constructor(editorModel: EditorModel) {
        super(editorModel);
        if (this.color === undefined) { this.color = null; }
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        const mode: string = <string>props.get("mode");
        if (mode != null)this.initialize(new Color(mode));
    }

    /*private*/ initialize(color: Color) {
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
    public getXmlAttributes(result: Element) {
        result.setAttribute("red", "" + this.color.getRed());
        result.setAttribute("green", "" + this.color.getGreen());
        result.setAttribute("blue", "" + this.color.getBlue());
        const alpha: number = this.color.getAlpha();
        if (alpha < 255)result.setAttribute("alpha", "" + alpha);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    public setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        if (format.loadToRender()){
            const red: string = xml.getAttribute("red");
            const green: string = xml.getAttribute("green");
            const blue: string = xml.getAttribute("blue");
            const alphaStr: string = xml.getAttribute("alpha");
            const alpha: number = (alphaStr == null || /* isEmpty */(alphaStr.length === 0)) ? 255 : javaemul.internal.IntegerHelper.parseInt(alphaStr);
            this.initialize(new Color(javaemul.internal.IntegerHelper.parseInt(red), javaemul.internal.IntegerHelper.parseInt(green), javaemul.internal.IntegerHelper.parseInt(blue), alpha));
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
