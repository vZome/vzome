/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.math.symmetry.QuaternionicSymmetry} left
     * @param {com.vzome.core.math.symmetry.QuaternionicSymmetry} right
     * @class
     * @extends com.vzome.core.commands.CommandTransform
     */
    export class CommandQuaternionSymmetry extends com.vzome.core.commands.CommandTransform {
        /**
         * 
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setFixedAttributes(attributes: com.vzome.core.commands.AttributeMap, format: com.vzome.core.commands.XmlSaveFormat) {
            super.setFixedAttributes(attributes, format);
            if (!attributes.containsKey(CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$())){
                this.mLeft = (<com.vzome.core.commands.XmlSymmetryFormat>format).getQuaternionicSymmetry("H_4");
                attributes.put(CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$(), this.mLeft);
            }
            if (!attributes.containsKey(CommandQuaternionSymmetry.RIGHT_SYMMETRY_GROUP_ATTR_NAME)){
                this.mRight = (<com.vzome.core.commands.XmlSymmetryFormat>format).getQuaternionicSymmetry("H_4");
                attributes.put(CommandQuaternionSymmetry.RIGHT_SYMMETRY_GROUP_ATTR_NAME, this.mRight);
            }
        }

        public static LEFT_SYMMETRY_GROUP_ATTR_NAME: string; public static LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$(): string { if (CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME == null) { CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME = com.vzome.core.commands.CommandTransform.SYMMETRY_GROUP_ATTR_NAME; }  return CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME; }

        public static RIGHT_SYMMETRY_GROUP_ATTR_NAME: string = "right.symmetry.group";

        /*private*/ mLeft: com.vzome.core.math.symmetry.QuaternionicSymmetry;

        /*private*/ mRight: com.vzome.core.math.symmetry.QuaternionicSymmetry;

        public constructor(left?: any, right?: any) {
            if (((left != null && left instanceof <any>com.vzome.core.math.symmetry.QuaternionicSymmetry) || left === null) && ((right != null && right instanceof <any>com.vzome.core.math.symmetry.QuaternionicSymmetry) || right === null)) {
                let __args = arguments;
                super();
                if (this.mLeft === undefined) { this.mLeft = null; } 
                if (this.mRight === undefined) { this.mRight = null; } 
                this.mLeft = left;
                this.mRight = right;
            } else if (left === undefined && right === undefined) {
                let __args = arguments;
                super();
                if (this.mLeft === undefined) { this.mLeft = null; } 
                if (this.mRight === undefined) { this.mRight = null; } 
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return com.vzome.core.commands.CommandTransform.GROUP_ATTR_SIGNATURE_$LI$();
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            if (this.mLeft == null)this.mLeft = <com.vzome.core.math.symmetry.QuaternionicSymmetry>attributes.get(CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$()); else if (!attributes.containsKey(CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$()))attributes.put(CommandQuaternionSymmetry.LEFT_SYMMETRY_GROUP_ATTR_NAME_$LI$(), this.mLeft);
            if (this.mRight == null)this.mRight = <com.vzome.core.math.symmetry.QuaternionicSymmetry>attributes.get(CommandQuaternionSymmetry.RIGHT_SYMMETRY_GROUP_ATTR_NAME); else if (!attributes.containsKey(CommandQuaternionSymmetry.RIGHT_SYMMETRY_GROUP_ATTR_NAME))attributes.put(CommandQuaternionSymmetry.RIGHT_SYMMETRY_GROUP_ATTR_NAME, this.mRight);
            const leftRoots: com.vzome.core.algebra.Quaternion[] = this.mLeft.getRoots();
            const rightRoots: com.vzome.core.algebra.Quaternion[] = this.mRight.getRoots();
            const params: com.vzome.core.construction.Construction[] = parameters.getConstructions();
            const output: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            for(let index = 0; index < params.length; index++) {
                let param = params[index];
                {
                    output.addConstruction(param);
                }
            }
            for(let index = 0; index < leftRoots.length; index++) {
                let leftRoot = leftRoots[index];
                {
                    for(let index1 = 0; index1 < rightRoots.length; index1++) {
                        let rightRoot = rightRoots[index1];
                        {
                            for(let index2 = 0; index2 < params.length; index2++) {
                                let param = params[index2];
                                {
                                    let result: com.vzome.core.construction.Construction = null;
                                    if (param != null && param instanceof <any>com.vzome.core.construction.Point){
                                        result = new com.vzome.core.construction.PointRotated4D(leftRoot, rightRoot, <com.vzome.core.construction.Point>param);
                                    } else if (param != null && param instanceof <any>com.vzome.core.construction.Segment){
                                        result = new com.vzome.core.construction.SegmentRotated4D(leftRoot, rightRoot, <com.vzome.core.construction.Segment>param);
                                    } else if (param != null && param instanceof <any>com.vzome.core.construction.Polygon){
                                        result = new com.vzome.core.construction.PolygonRotated4D(leftRoot, rightRoot, <com.vzome.core.construction.Polygon>param);
                                    } else {
                                    }
                                    if (result == null)continue;
                                    effects['constructionAdded$com_vzome_core_construction_Construction'](result);
                                    output.addConstruction(result);
                                }
                            }
                        }
                    }
                }
            }
            return output;
        }
    }
    CommandQuaternionSymmetry["__class"] = "com.vzome.core.commands.CommandQuaternionSymmetry";
    CommandQuaternionSymmetry["__interfaces"] = ["com.vzome.core.commands.Command"];


}

