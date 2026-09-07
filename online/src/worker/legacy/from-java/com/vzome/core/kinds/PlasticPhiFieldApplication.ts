import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { PlasticPhiField } from "../algebra/PlasticPhiField.js";
import { CommandUniformH4Polytope } from "../commands/CommandUniformH4Polytope.js";
import { SymmetryPerspective } from "../editor/SymmetryPerspective.js";
import { ToolsModel } from "../editor/ToolsModel.js";
import { DefaultFieldApplication } from "./DefaultFieldApplication.js";
import { IcosahedralSymmetryPerspective } from "./IcosahedralSymmetryPerspective.js";
import { IcosahedralSymmetry } from "../math/symmetry/IcosahedralSymmetry.js";
import { QuaternionicSymmetry } from "../math/symmetry/QuaternionicSymmetry.js";
import { WythoffConstruction } from "../math/symmetry/WythoffConstruction.js";
import { AxialStretchTool } from "../tools/AxialStretchTool.js";
import { IcosahedralToolFactory } from "../tools/IcosahedralToolFactory.js";

export class PlasticPhiFieldApplication extends DefaultFieldApplication {
    /*private*/ icosahedralPerspective: IcosahedralSymmetryPerspective;

    symmetryPerspectives: java.util.List<SymmetryPerspective>;

    /*private*/ H4: QuaternionicSymmetry;

    public constructor(field: PlasticPhiField) {
        super(field);
        if (this.icosahedralPerspective === undefined) { this.icosahedralPerspective = null; }
        this.symmetryPerspectives = <any>(new java.util.ArrayList<any>());
        if (this.H4 === undefined) { this.H4 = null; }
        this.h4Builder = null;
        this.icosahedralPerspective = new IcosahedralSymmetryPerspective(this.getField());
        this.symmetryPerspectives.add(this.icosahedralPerspective);
        this.symmetryPerspectives.add(super.getDefaultSymmetryPerspective());
        this.H4 = new QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", this.getField());
    }

    /**
     * 
     * @return {PlasticPhiField}
     */
    public getField(): PlasticPhiField {
        return <PlasticPhiField><any>super.getField();
    }

    /**
     * 
     * @return {*}
     */
    public getSymmetryPerspectives(): java.util.Collection<SymmetryPerspective> {
        return this.symmetryPerspectives;
    }

    /**
     * 
     * @return {*}
     */
    public getDefaultSymmetryPerspective(): SymmetryPerspective {
        return this.icosahedralPerspective;
    }

    /**
     * 
     * @param {string} symmName
     * @return {*}
     */
    public getSymmetryPerspective(symmName: string): SymmetryPerspective {
        for(let index=this.symmetryPerspectives.iterator();index.hasNext();) {
            let sp = index.next();
            {
                if (sp.getName() === symmName){
                    return sp;
                }
            }
        }
        return super.getSymmetryPerspective(symmName);
    }

    /**
     * 
     * @param {*} toolFactories
     * @param {ToolsModel} tools
     */
    public registerToolFactories(toolFactories: java.util.Map<string, Tool.Factory>, tools: ToolsModel) {
        super.registerToolFactories(toolFactories, tools);
        const symm: IcosahedralSymmetry = this.icosahedralPerspective.getSymmetry();
        toolFactories.put("AxialStretchTool", new AxialStretchTool.Factory(tools, symm, false, false, false));
        toolFactories.put("SymmetryTool", new IcosahedralToolFactory(tools, symm));
    }

    /**
     * 
     * @param {string} name
     * @return {QuaternionicSymmetry}
     */
    public getQuaternionSymmetry(name: string): QuaternionicSymmetry {
        switch((name)) {
        case "H_4":
            return this.H4;
        default:
            return null;
        }
    }

    /*private*/ h4Builder: CommandUniformH4Polytope;

    /**
     * 
     * @param {string} groupName
     * @param {number} index
     * @param {number} edgesToRender
     * @param {AlgebraicNumber[]} edgeScales
     * @param {*} listener
     */
    public constructPolytope(groupName: string, index: number, edgesToRender: number, edgeScales: AlgebraicNumber[], listener: WythoffConstruction.Listener) {
        switch((groupName)) {
        case "H4":
            if (this.h4Builder == null){
                const qsymm: QuaternionicSymmetry = new QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", this.getField());
                this.h4Builder = new CommandUniformH4Polytope(this.getField(), qsymm, 0);
            }
            this.h4Builder.generate(index, edgesToRender, edgeScales, listener);
            break;
        default:
            super.constructPolytope(groupName, index, edgesToRender, edgeScales, listener);
            break;
        }
    }
}
PlasticPhiFieldApplication["__class"] = "com.vzome.core.kinds.PlasticPhiFieldApplication";
PlasticPhiFieldApplication["__interfaces"] = ["com.vzome.core.math.symmetry.Symmetries4D","com.vzome.core.editor.FieldApplication"];
