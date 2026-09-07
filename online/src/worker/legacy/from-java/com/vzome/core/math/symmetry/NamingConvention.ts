import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Axis } from "./Axis.js";
import { DirectionNaming } from "./DirectionNaming.js";

/**
 * @author Scott Vorthmann
 * @class
 */
export class NamingConvention {
    public static UNKNOWN_AXIS: string = "UNKNOWN AXIS";

    /*private*/ mNamings: java.util.Map<string, DirectionNaming>;

    public addDirectionNaming(naming: DirectionNaming) {
        this.mNamings.put(naming.getName$(), naming);
    }

    public getAxis(color: string, name: string): Axis {
        const naming: DirectionNaming = this.mNamings.get(color);
        if (naming == null)return null;
        return naming.getAxis(name);
    }

    public getName(axis: Axis): string {
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
