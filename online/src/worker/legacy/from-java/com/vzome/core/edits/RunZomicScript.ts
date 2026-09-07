import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Command } from "../commands/Command.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { XmlSymmetryFormat } from "../commands/XmlSymmetryFormat.js";
import { Point } from "../construction/Point.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { ImplicitSymmetryParameters } from "../editor/api/ImplicitSymmetryParameters.js";
import { ManifestConstructions } from "../editor/api/ManifestConstructions.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { IcosahedralSymmetry } from "../math/symmetry/IcosahedralSymmetry.js";
import { Connector } from "../model/Connector.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class RunZomicScript extends ChangeManifestations {
    /*private*/ programText: string;

    /*private*/ origin: Point;

    /*private*/ symm: IcosahedralSymmetry;

    public constructor(editor: EditorModel) {
        super(editor);
        if (this.programText === undefined) { this.programText = null; }
        if (this.origin === undefined) { this.origin = null; }
        if (this.symm === undefined) { this.symm = null; }
        this.origin = (<ImplicitSymmetryParameters><any>editor).getCenterPoint();
        this.symm = <IcosahedralSymmetry><any>(<SymmetryAware><any>editor)['getSymmetrySystem$']().getSymmetry();
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        this.programText = <string>props.get("script");
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "RunZomicScript";
    }

    getScriptDialect(): string {
        return "zomic";
    }

    /**
     * 
     * @param {*} element
     */
    getXmlAttributes(element: Element) {
        element.setTextContent(XmlSaveFormat.escapeNewlines(this.programText));
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        this.programText = xml.getTextContent();
        this.symm = <IcosahedralSymmetry><any>(<XmlSymmetryFormat>format).parseSymmetry("icosahedral");
    }

    /**
     * 
     */
    public perform() {
        let offset: Point = null;
        let pointFound: boolean = false;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    const nextPoint: Point = <Point>(<Connector><any>man).getFirstConstruction();
                    if (!pointFound){
                        pointFound = true;
                        offset = nextPoint;
                    } else {
                        offset = null;
                    }
                }
            }
        }
        if (offset == null)offset = this.origin;
        try {
            this.symm.interpretScript(this.programText, this.getScriptDialect(), offset, this.symm, new ManifestConstructions(this));
        } catch(e) {
            throw new Command.Failure(e.message, e);
        }
        this.redo();
    }
}
RunZomicScript["__class"] = "com.vzome.core.edits.RunZomicScript";
