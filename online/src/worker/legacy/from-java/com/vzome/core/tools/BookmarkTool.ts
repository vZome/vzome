import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Construction } from "../construction/Construction.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Duplicator } from "../editor/Duplicator.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { Manifestation } from "../model/Manifestation.js";

export class BookmarkTool extends Tool {
    public static ID: string = "bookmark";

    static LABEL: string = "Create a selection bookmark";

    static TOOLTIP: string = "<p>A selection bookmark lets you re-create<br>any selection at a later time.</p>";

    /*private*/ bookmarkedConstructions: java.util.List<Construction>;

    public constructor(id: string, tools: ToolsModel) {
        super(id, tools);
        this.bookmarkedConstructions = <any>(new java.util.ArrayList<any>());
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
        const duper: Duplicator = new Duplicator(null, null);
        if (this.mSelection.size() === 0)this.bookmarkedConstructions.add(new FreePoint(this.mManifestations.getField().origin(3))); else for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                const result: Construction = duper.duplicateConstruction(man);
                this.bookmarkedConstructions.add(result);
                this.addParameter(result);
            }
        }
        super.perform();
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
     * @param {ChangeManifestations} edit
     */
    public prepare(edit: ChangeManifestations) {
        if (this.bookmarkedConstructions.isEmpty()){
            edit.manifestConstruction(new FreePoint(this.mManifestations.getField().origin(3)));
        } else for(let index=this.bookmarkedConstructions.iterator();index.hasNext();) {
            let con = index.next();
            {
                edit.manifestConstruction(con);
            }
        }
        edit.redo();
    }

    /**
     * 
     * @param {ChangeManifestations} applyTool
     */
    public complete(applyTool: ChangeManifestations) {
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
        return "BookmarkTool";
    }

    /**
     * 
     * @return {string}
     */
    public getCategory(): string {
        return BookmarkTool.ID;
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
BookmarkTool["__class"] = "com.vzome.core.tools.BookmarkTool";
BookmarkTool["__interfaces"] = ["com.vzome.api.Tool"];
