/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @param {*} symmetry
     * @class
     * @extends com.vzome.core.commands.CommandTransform
     */
    export class CommandSymmetry extends com.vzome.core.commands.CommandTransform {
        mSymmetry: com.vzome.core.math.symmetry.Symmetry;

        public constructor(symmetry?: any) {
            if (((symmetry != null && (symmetry.constructor != null && symmetry.constructor["__interfaces"] != null && symmetry.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || symmetry === null)) {
                let __args = arguments;
                super();
                if (this.mSymmetry === undefined) { this.mSymmetry = null; } 
                this.mSymmetry = symmetry;
            } else if (symmetry === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let symmetry: any = null;
                    super();
                    if (this.mSymmetry === undefined) { this.mSymmetry = null; } 
                    this.mSymmetry = symmetry;
                }
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return com.vzome.core.commands.CommandTransform.GROUP_ATTR_SIGNATURE_$LI$();
        }

        setSymmetry(attributes: com.vzome.core.commands.AttributeMap): com.vzome.core.construction.Point {
            if (this.mSymmetry == null)this.mSymmetry = <com.vzome.core.math.symmetry.Symmetry><any>attributes.get(com.vzome.core.commands.CommandTransform.SYMMETRY_GROUP_ATTR_NAME); else if (!attributes.containsKey(com.vzome.core.commands.CommandTransform.SYMMETRY_GROUP_ATTR_NAME))attributes.put(com.vzome.core.commands.CommandTransform.SYMMETRY_GROUP_ATTR_NAME, this.mSymmetry);
            if (this.mSymmetry == null)throw new java.lang.IllegalStateException("null symmetry no longer supported");
            const center: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>attributes.get(com.vzome.core.commands.CommandTransform.SYMMETRY_CENTER_ATTR_NAME);
            return center;
        }

        /**
         * 
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setFixedAttributes(attributes: com.vzome.core.commands.AttributeMap, format: com.vzome.core.commands.XmlSaveFormat) {
            if (!attributes.containsKey(com.vzome.core.commands.CommandTransform.SYMMETRY_GROUP_ATTR_NAME)){
                const icosahedralSymmetry: com.vzome.core.math.symmetry.Symmetry = (<com.vzome.core.commands.XmlSymmetryFormat>format).parseSymmetry("icosahedral");
                attributes.put(com.vzome.core.commands.CommandTransform.SYMMETRY_GROUP_ATTR_NAME, icosahedralSymmetry);
            }
            super.setFixedAttributes(attributes, format);
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const center: com.vzome.core.construction.Point = this.setSymmetry(attributes);
            const params: com.vzome.core.construction.Construction[] = parameters.getConstructions();
            const output: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            for(let index = 0; index < params.length; index++) {
                let param = params[index];
                {
                    output.addConstruction(param);
                }
            }
            for(let i: number = 1; i < this.mSymmetry.getChiralOrder(); i++) {{
                const transform: com.vzome.core.construction.Transformation = new com.vzome.core.construction.SymmetryTransformation(this.mSymmetry, i, center);
                output.addAll(this.transform(params, transform, effects));
            };}
            return output;
        }
    }
    CommandSymmetry["__class"] = "com.vzome.core.commands.CommandSymmetry";
    CommandSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];


}

