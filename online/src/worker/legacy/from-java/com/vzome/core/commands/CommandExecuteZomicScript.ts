import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AbstractCommand } from "./AbstractCommand.js";
import { AttributeMap } from "./AttributeMap.js";
import { Command } from "./Command.js";
import { CommandTransform } from "./CommandTransform.js";
import { XmlSaveFormat } from "./XmlSaveFormat.js";
import { XmlSymmetryFormat } from "./XmlSymmetryFormat.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { ConstructionList } from "../construction/ConstructionList.js";
import { Point } from "../construction/Point.js";
import { IcosahedralSymmetry } from "../math/symmetry/IcosahedralSymmetry.js";
import { ZomicStatement } from "../zomic/program/ZomicStatement.js";

/**
 * @author Scott Vorthmann
 * @param {IcosahedralSymmetry} symmetry
 * @class
 * @extends AbstractCommand
 */
export class CommandExecuteZomicScript extends AbstractCommand {
    /**
     * 
     * @param {AttributeMap} attributes
     * @param {XmlSaveFormat} format
     */
    public setFixedAttributes(attributes: AttributeMap, format: XmlSaveFormat) {
        super.setFixedAttributes(attributes, format);
        this.symmetry = <IcosahedralSymmetry>attributes.get(CommandTransform.SYMMETRY_GROUP_ATTR_NAME);
        if (this.symmetry == null)this.symmetry = <IcosahedralSymmetry><any>(<XmlSymmetryFormat>format).parseSymmetry("icosahedral");
    }

    public constructor(symmetry?: any) {
        if (((symmetry != null && symmetry instanceof <any>IcosahedralSymmetry) || symmetry === null)) {
            let __args = arguments;
            super();
            if (this.symmetry === undefined) { this.symmetry = null; } 
            this.symmetry = symmetry;
        } else if (symmetry === undefined) {
            let __args = arguments;
            super();
            if (this.symmetry === undefined) { this.symmetry = null; } 
            this.symmetry = null;
        } else throw new Error('invalid overload');
    }

    /*private*/ symmetry: IcosahedralSymmetry;

    public static SCRIPT_ATTR: string = "script";

    static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandExecuteZomicScript.PARAM_SIGNATURE == null) { CommandExecuteZomicScript.PARAM_SIGNATURE = [["start", Point]]; }  return CommandExecuteZomicScript.PARAM_SIGNATURE; }

    static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandExecuteZomicScript.ATTR_SIGNATURE == null) { CommandExecuteZomicScript.ATTR_SIGNATURE = [[CommandExecuteZomicScript.SCRIPT_ATTR, ZomicStatement]]; }  return CommandExecuteZomicScript.ATTR_SIGNATURE; }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getParameterSignature(): any[][] {
        return CommandExecuteZomicScript.PARAM_SIGNATURE_$LI$();
    }

    /**
     * 
     * @return {java.lang.Object[][]}
     */
    public getAttributeSignature(): any[][] {
        return CommandExecuteZomicScript.ATTR_SIGNATURE_$LI$();
    }

    /**
     * 
     * @param {ConstructionList} parameters
     * @param {AttributeMap} attrs
     * @param {*} effects
     * @return {ConstructionList}
     */
    public apply(parameters: ConstructionList, attrs: AttributeMap, effects: ConstructionChanges): ConstructionList {
        const script: string = <string>attrs.get(CommandExecuteZomicScript.SCRIPT_ATTR);
        const result: ConstructionList = new ConstructionList();
        if (parameters.size() !== 1)throw new Command.Failure("start parameter must be a single connector");
        const c: Construction = parameters.get(0);
        if (!(c != null && c instanceof <any>Point))throw new Command.Failure("start parameter must be a connector");
        const pt1: Point = <Point>c;
        try {
            this.symmetry.interpretScript(script, "zomic", pt1, this.symmetry, effects);
        } catch(e) {
            throw new Command.Failure(e.message, e);
        }
        return result;
    }
}
CommandExecuteZomicScript["__class"] = "com.vzome.core.commands.CommandExecuteZomicScript";
CommandExecuteZomicScript["__interfaces"] = ["com.vzome.core.commands.Command"];
