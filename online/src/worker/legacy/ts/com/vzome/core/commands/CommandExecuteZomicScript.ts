/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.math.symmetry.IcosahedralSymmetry} symmetry
     * @class
     * @extends com.vzome.core.commands.AbstractCommand
     */
    export class CommandExecuteZomicScript extends com.vzome.core.commands.AbstractCommand {
        /**
         * 
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setFixedAttributes(attributes: com.vzome.core.commands.AttributeMap, format: com.vzome.core.commands.XmlSaveFormat) {
            super.setFixedAttributes(attributes, format);
            this.symmetry = <com.vzome.core.math.symmetry.IcosahedralSymmetry>attributes.get(com.vzome.core.commands.CommandTransform.SYMMETRY_GROUP_ATTR_NAME);
            if (this.symmetry == null)this.symmetry = <com.vzome.core.math.symmetry.IcosahedralSymmetry><any>(<com.vzome.core.commands.XmlSymmetryFormat>format).parseSymmetry("icosahedral");
        }

        public constructor(symmetry?: any) {
            if (((symmetry != null && symmetry instanceof <any>com.vzome.core.math.symmetry.IcosahedralSymmetry) || symmetry === null)) {
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

        /*private*/ symmetry: com.vzome.core.math.symmetry.IcosahedralSymmetry;

        public static SCRIPT_ATTR: string = "script";

        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandExecuteZomicScript.PARAM_SIGNATURE == null) { CommandExecuteZomicScript.PARAM_SIGNATURE = [["start", com.vzome.core.construction.Point]]; }  return CommandExecuteZomicScript.PARAM_SIGNATURE; }

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandExecuteZomicScript.ATTR_SIGNATURE == null) { CommandExecuteZomicScript.ATTR_SIGNATURE = [[CommandExecuteZomicScript.SCRIPT_ATTR, com.vzome.core.zomic.program.ZomicStatement]]; }  return CommandExecuteZomicScript.ATTR_SIGNATURE; }

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
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attrs
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attrs: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const script: string = <string>attrs.get(CommandExecuteZomicScript.SCRIPT_ATTR);
            const result: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            if (parameters.size() !== 1)throw new Command.Failure("start parameter must be a single connector");
            const c: com.vzome.core.construction.Construction = parameters.get(0);
            if (!(c != null && c instanceof <any>com.vzome.core.construction.Point))throw new Command.Failure("start parameter must be a connector");
            const pt1: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>c;
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


}

