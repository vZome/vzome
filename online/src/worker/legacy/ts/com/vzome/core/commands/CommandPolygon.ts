/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    export class CommandPolygon extends com.vzome.core.commands.AbstractCommand {
        static PARAM_SIGNATURE: any[][]; public static PARAM_SIGNATURE_$LI$(): any[][] { if (CommandPolygon.PARAM_SIGNATURE == null) { CommandPolygon.PARAM_SIGNATURE = [[com.vzome.core.commands.Command.GENERIC_PARAM_NAME, com.vzome.core.construction.Point]]; }  return CommandPolygon.PARAM_SIGNATURE; }

        static ATTR_SIGNATURE: any[][]; public static ATTR_SIGNATURE_$LI$(): any[][] { if (CommandPolygon.ATTR_SIGNATURE == null) { CommandPolygon.ATTR_SIGNATURE = []; }  return CommandPolygon.ATTR_SIGNATURE; }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getParameterSignature(): any[][] {
            return CommandPolygon.PARAM_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {java.lang.Object[][]}
         */
        public getAttributeSignature(): any[][] {
            return CommandPolygon.ATTR_SIGNATURE_$LI$();
        }

        /**
         * 
         * @return {boolean}
         */
        public ordersSelection(): boolean {
            return true;
        }

        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attrs
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attrs: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            const points: java.util.List<com.vzome.core.construction.Point> = <any>(new java.util.ArrayList<any>());
            {
                let array = parameters.getConstructions();
                for(let index = 0; index < array.length; index++) {
                    let param = array[index];
                    {
                        if (param != null && param instanceof <any>com.vzome.core.construction.Point){
                            points.add(<com.vzome.core.construction.Point>param);
                        }
                    }
                }
            }
            let errorMsg: string = null;
            if (points.size() < 3){
                errorMsg = "A polygon requires at least three vertices.";
            } else if (points.get(0).is3d() && points.get(1).is3d() && points.get(1).is3d()){
                const normal: com.vzome.core.algebra.AlgebraicVector = com.vzome.core.algebra.AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(points.get(0).getLocation(), points.get(1).getLocation(), points.get(2).getLocation());
                if (normal.isOrigin()){
                    errorMsg = "First 3 points cannot be collinear.";
                } else {
                    let base: com.vzome.core.algebra.AlgebraicVector = null;
                    for(let index=points.iterator();index.hasNext();) {
                        let point = index.next();
                        {
                            if (base == null){
                                base = point.getLocation();
                            } else {
                                if (!point.getLocation().minus(base).dot(normal).isZero()){
                                    errorMsg = "Points are not coplanar.";
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (errorMsg != null && attrs.get(com.vzome.core.commands.Command.LOADING_FROM_FILE) == null){
                throw new Command.Failure(errorMsg);
            }
            const poly: com.vzome.core.construction.PolygonFromVertices = new com.vzome.core.construction.PolygonFromVertices(points);
            if (errorMsg != null){
                poly.setFailed();
            } else {
                effects['constructionAdded$com_vzome_core_construction_Construction'](poly);
            }
            const result: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
            result.addConstruction(poly);
            return result;
        }

        constructor() {
            super();
        }
    }
    CommandPolygon["__class"] = "com.vzome.core.commands.CommandPolygon";
    CommandPolygon["__interfaces"] = ["com.vzome.core.commands.Command"];


}

