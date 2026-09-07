import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Bivector3d } from "../algebra/Bivector3d.js";
import { Vector3d } from "../algebra/Vector3d.js";
import { Command } from "../commands/Command.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Construction } from "../construction/Construction.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { Connector } from "../model/Connector.js";
import { Manifestation } from "../model/Manifestation.js";
import { Strut } from "../model/Strut.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class PlaneSelectionTool extends Tool {
    /*private*/ plane: Bivector3d;

    /*private*/ anchor: AlgebraicVector;

    /*private*/ halfSpace: boolean;

    /*private*/ boundaryOpen: boolean;

    /*private*/ above: boolean;

    /*private*/ includeBalls: boolean;

    /*private*/ includeStruts: boolean;

    /*private*/ includePanels: boolean;

    /*private*/ includePartials: boolean;

    public constructor(id: string, tools: ToolsModel) {
        super(id, tools);
        if (this.plane === undefined) { this.plane = null; }
        if (this.anchor === undefined) { this.anchor = null; }
        this.halfSpace = true;
        this.boundaryOpen = false;
        this.above = true;
        this.includeBalls = true;
        this.includeStruts = true;
        this.includePanels = true;
        this.includePartials = false;
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
     */
    public perform() {
        let p1: AlgebraicVector = null;
        let p2: AlgebraicVector = null;
        let p3: AlgebraicVector = null;
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    if (p1 == null){
                        p1 = man.getLocation();
                        continue;
                    }
                    if (p2 == null){
                        p2 = man.getLocation();
                        continue;
                    }
                    if (p3 == null){
                        p3 = man.getLocation();
                        continue;
                    } else {
                        throw new Command.Failure("half-space selection tool requires exactly three balls");
                    }
                }
            }
        }
        if (p3 == null)throw new Command.Failure("half-space selection tool requires exactly three balls");
        const v1: Vector3d = new Vector3d(p2.minus(p1));
        const v2: Vector3d = new Vector3d(p3.minus(p1));
        this.plane = v1.outer(v2);
        this.anchor = p1;
        super.perform();
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
        return false;
    }

    /**
     * 
     * @param {Construction} c
     * @param {ChangeManifestations} applyTool
     */
    public performEdit(c: Construction, applyTool: ChangeManifestations) {
    }

    /**
     * 
     * @param {*} man
     * @param {ChangeManifestations} applyTool
     */
    public performSelect(man: Manifestation, applyTool: ChangeManifestations) {
        if (man.isHidden())return;
        if (!man.isRendered())return;
        if (this.includePanels && (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)))return;
        if (this.includeBalls && (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0))){
            const c: Connector = <Connector><any>man;
            const orientation: number = this.orient(c.getLocation());
            if (!this.boundaryOpen && orientation === 0)applyTool.select$com_vzome_core_model_Manifestation(man); else if (this.halfSpace && this.above && orientation > 0)applyTool.select$com_vzome_core_model_Manifestation(man); else if (this.halfSpace && !this.above && orientation < 0)applyTool.select$com_vzome_core_model_Manifestation(man);
        } else if (this.includeStruts && (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0))){
            const o1: number = this.orient((<Strut><any>man).getLocation());
            const o2: number = this.orient((<Strut><any>man).getEnd());
            if (this.includePartials){
                if (!this.boundaryOpen && (o1 === 0 || o2 === 0))applyTool.select$com_vzome_core_model_Manifestation(man); else if (this.halfSpace){
                    if (this.above && (o1 > 0 || o2 > 0))applyTool.select$com_vzome_core_model_Manifestation(man); else if (!this.above && (o1 < 0 || o2 < 0))applyTool.select$com_vzome_core_model_Manifestation(man);
                }
            } else {
                if (!this.halfSpace && o1 === 0 && o2 === 0)applyTool.select$com_vzome_core_model_Manifestation(man); else if (this.halfSpace){
                    if (this.boundaryOpen){
                        if (this.above && (o1 > 0 && o2 > 0))applyTool.select$com_vzome_core_model_Manifestation(man); else if (!this.above && (o1 < 0 && o2 < 0))applyTool.select$com_vzome_core_model_Manifestation(man);
                    } else {
                        if (this.above && (o1 >= 0 && o2 >= 0))applyTool.select$com_vzome_core_model_Manifestation(man); else if (!this.above && (o1 <= 0 && o2 <= 0))applyTool.select$com_vzome_core_model_Manifestation(man);
                    }
                }
            }
        }
        applyTool.redo();
    }

    /*private*/ orient(point: AlgebraicVector): number {
        const diff: AlgebraicVector = point.minus(this.anchor);
        const v: Vector3d = new Vector3d(diff);
        const volume: AlgebraicNumber = this.plane.outer(v);
        if (volume.isZero())return 0; else {
            const volD: number = volume.evaluate();
            return (volD > 0.0) ? 1 : -1;
        }
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
        return "PlaneSelectionTool";
    }

    /**
     * 
     * @param {*} element
     */
    getXmlAttributes(element: Element) {
        element.setAttribute("name", this.getId());
    }

    /**
     * 
     * @param {*} element
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(element: Element, format: XmlSaveFormat) {
        super.setXmlAttributes(element, format);
        this.includeBalls = !("false" === element.getAttribute("balls"));
        this.includeStruts = !("false" === element.getAttribute("struts"));
        this.includePanels = "true" === element.getAttribute("panels");
        this.includePartials = "any" === element.getAttribute("vertices");
        this.boundaryOpen = "true" === element.getAttribute("open");
        const halfSpace: string = element.getAttribute("halfSpace");
        if ("above" === halfSpace){
            this.halfSpace = true;
            this.above = true;
        } else if ("below" === halfSpace){
            this.halfSpace = true;
            this.above = false;
        } else {
            this.halfSpace = false;
            this.boundaryOpen = false;
        }
    }

    /**
     * 
     * @return {string}
     */
    public getCategory(): string {
        return "plane";
    }

    public getDefaultName(): string {
        return "plane";
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
PlaneSelectionTool["__class"] = "com.vzome.core.tools.PlaneSelectionTool";
PlaneSelectionTool["__interfaces"] = ["com.vzome.api.Tool"];
