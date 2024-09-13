/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    /**
     * @author Scott Vorthmann
     * @class
     */
    export class NamingConvention {
        public static UNKNOWN_AXIS: string = "UNKNOWN AXIS";

        /*private*/ mNamings: java.util.Map<string, com.vzome.core.math.symmetry.DirectionNaming>;

        public addDirectionNaming(naming: com.vzome.core.math.symmetry.DirectionNaming) {
            this.mNamings.put(naming.getName$(), naming);
        }

        public getAxis(color: string, name: string): com.vzome.core.math.symmetry.Axis {
            const naming: com.vzome.core.math.symmetry.DirectionNaming = this.mNamings.get(color);
            if (naming == null)return null;
            return naming.getAxis(name);
        }

        public getName(axis: com.vzome.core.math.symmetry.Axis): string {
            for(let index=this.mNamings.values().iterator();index.hasNext();) {
                let naming = index.next();
                {
                    if (naming.getDirection().equals(axis.getDirection()))return naming.getName$com_vzome_core_math_symmetry_Axis(axis);
                }
            }
            return NamingConvention.UNKNOWN_AXIS;
        }

        constructor() {
            this.mNamings = <any>(new java.util.HashMap<any, any>());
        }
    }
    NamingConvention["__class"] = "com.vzome.core.math.symmetry.NamingConvention";

}

