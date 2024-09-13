/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * A selection in the model.
     * @class
     * @extends java.util.ArrayList
     */
    export class ConstructionList extends java.util.ArrayList<com.vzome.core.construction.Construction> {
        public addConstruction(ball: com.vzome.core.construction.Construction): ConstructionList {
            this.add(ball);
            return this;
        }

        public removeConstruction(ball: com.vzome.core.construction.Construction): ConstructionList {
            this.remove(ball);
            return this;
        }

        public getConstructions(): com.vzome.core.construction.Construction[] {
            return this.toArray<any>((s => { let a=[]; while(s-->0) a.push(null); return a; })(this.size()));
        }

        constructor() {
            super();
        }
    }
    ConstructionList["__class"] = "com.vzome.core.construction.ConstructionList";
    ConstructionList["__interfaces"] = ["java.util.RandomAccess","java.util.List","java.lang.Cloneable","java.util.Collection","java.lang.Iterable","java.io.Serializable"];


}

