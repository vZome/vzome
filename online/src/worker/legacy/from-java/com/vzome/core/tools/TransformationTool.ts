import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Color } from "../construction/Color.js";
import { Construction } from "../construction/Construction.js";
import { Point } from "../construction/Point.js";
import { Transformation } from "../construction/Transformation.js";
import { Tool } from "../editor/Tool.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { Manifestation } from "../model/Manifestation.js";

export abstract class TransformationTool extends Tool {
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

    transforms: Transformation[];

    originPoint: Point;

    public constructor(id: string, tools: ToolsModel) {
        super(id, tools);
        if (this.transforms === undefined) { this.transforms = null; }
        if (this.originPoint === undefined) { this.originPoint = null; }
        this.originPoint = tools.getOriginPoint();
    }

    /**
     * 
     * @param {*} that
     * @return {boolean}
     */
    public equals(that: any): boolean {
        if (this === that){
            return true;
        }
        if (that == null){
            return false;
        }
        if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })((<any>that.constructor),(<any>this.constructor)))){
            return false;
        }
        const other: TransformationTool = <TransformationTool>that;
        if (this.originPoint == null){
            if (other.originPoint != null){
                return false;
            }
        } else if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.originPoint,other.originPoint))){
            return false;
        }
        if (!java.util.Arrays.equals(this.transforms, other.transforms)){
            return false;
        }
        return true;
    }

    /**
     * 
     * @param {Construction} c
     * @param {ChangeManifestations} applyTool
     */
    public performEdit(c: Construction, applyTool: ChangeManifestations) {
        for(let index = 0; index < this.transforms.length; index++) {
            let transform = this.transforms[index];
            {
                const result: Construction = transform.transform$com_vzome_core_construction_Construction(c);
                if (result == null)continue;
                const color: Color = c.getColor();
                result.setColor(color);
                const m: Manifestation = applyTool.manifestConstruction(result);
                if (m != null)if (color != null)applyTool.colorManifestation(m, c.getColor());
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

    public unselect$com_vzome_core_model_Manifestation$boolean(man: Manifestation, ignoreGroups: boolean) {
        const c: Construction = man.getFirstConstruction();
        this.addParameter(c);
        super.unselect$com_vzome_core_model_Manifestation$boolean(man, ignoreGroups);
    }

    /**
     * 
     * @param {*} man
     * @param {boolean} ignoreGroups
     */
    public unselect(man?: any, ignoreGroups?: any) {
        if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ((typeof ignoreGroups === 'boolean') || ignoreGroups === null)) {
            return <any>this.unselect$com_vzome_core_model_Manifestation$boolean(man, ignoreGroups);
        } else if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ignoreGroups === undefined) {
            return <any>this.unselect$com_vzome_core_model_Manifestation(man);
        } else throw new Error('invalid overload');
    }

    isAutomatic(): boolean {
        return /* contains */(this.getId().indexOf(".auto/") != -1);
    }
}
TransformationTool["__class"] = "com.vzome.core.tools.TransformationTool";
TransformationTool["__interfaces"] = ["com.vzome.api.Tool"];
