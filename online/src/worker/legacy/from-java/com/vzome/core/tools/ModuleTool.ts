import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Construction } from "../construction/Construction.js";
import { Point } from "../construction/Point.js";
import { Duplicator } from "../editor/Duplicator.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { Manifestation } from "../model/Manifestation.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class ModuleTool extends Tool {
    static ID: string = "module";

    static LABEL: string = "Create a module tool";

    static TOOLTIP: string = "<p>Each tool duplicates the original module.<br></p>";

    /*private*/ name: string;

    /*private*/ bookmarkedSelection: java.util.List<Manifestation>;

    public constructor(id: string, tools: ToolsModel) {
        super(id, tools);
        if (this.name === undefined) { this.name = null; }
        this.bookmarkedSelection = <any>(new java.util.ArrayList<any>());
        this.mSelection.copy(this.bookmarkedSelection);
    }

    /**
     * 
     * @return {boolean}
     */
    public isSticky(): boolean {
        return true;
    }

    /**
     * 
     * @param {ChangeManifestations} applyTool
     */
    public prepare(applyTool: ChangeManifestations) {
    }

    /**
     * 
     * @param {ChangeManifestations} applyTool
     */
    public complete(applyTool: ChangeManifestations) {
    }

    /**
     * 
     * @return {boolean}
     */
    public needsInput(): boolean {
        return true;
    }

    /**
     * 
     * @param {Construction} c
     * @param {ChangeManifestations} applyTool
     */
    public performEdit(c: Construction, applyTool: ChangeManifestations) {
        if (!(c != null && c instanceof <any>Point))return;
        const p: Point = <Point>c;
        const loc: AlgebraicVector = p.getLocation();
        const duper: Duplicator = new Duplicator(applyTool, loc);
        for(let index=this.bookmarkedSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                duper.duplicateManifestation(man);
            }
        }
        applyTool.redo();
    }

    /**
     * 
     * @param {*} man
     * @param {ChangeManifestations} applyTool
     */
    public performSelect(man: Manifestation, applyTool: ChangeManifestations) {
    }

    /**
     * 
     */
    public redo() {
    }

    /**
     * 
     */
    public undo() {
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "ModuleTool";
    }

    /**
     * 
     * @param {*} element
     */
    getXmlAttributes(element: Element) {
        element.setAttribute("name", this.name);
    }

    /**
     * 
     * @param {*} element
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(element: Element, format: XmlSaveFormat) {
        this.name = element.getAttribute("name");
    }

    /**
     * 
     * @return {string}
     */
    public getCategory(): string {
        return ModuleTool.ID;
    }

    /**
     * 
     * @param {boolean} prepareTool
     * @return {string}
     */
    checkSelection(prepareTool: boolean): string {
        return null;
    }
}
ModuleTool["__class"] = "com.vzome.core.tools.ModuleTool";
ModuleTool["__interfaces"] = ["com.vzome.api.Tool"];
