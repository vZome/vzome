import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Construction } from "./Construction.js";

/**
 * A selection in the model.
 * @class
 * @extends java.util.ArrayList
 */
export class ConstructionList extends java.util.ArrayList<Construction> {
    public addConstruction(ball: Construction): ConstructionList {
        this.add(ball);
        return this;
    }

    public removeConstruction(ball: Construction): ConstructionList {
        this.remove(ball);
        return this;
    }

    public getConstructions(): Construction[] {
        return this.toArray<any>((s => { let a=[]; while(s-->0) a.push(null); return a; })(this.size()));
    }

    constructor() {
        super();
    }
}
ConstructionList["__class"] = "com.vzome.core.construction.ConstructionList";
ConstructionList["__interfaces"] = ["java.util.RandomAccess","java.util.List","java.lang.Cloneable","java.util.Collection","java.lang.Iterable","java.io.Serializable"];
