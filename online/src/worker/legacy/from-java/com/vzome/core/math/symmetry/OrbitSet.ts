import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { RealVector } from "../RealVector.js";
import { Axis } from "./Axis.js";
import { Direction } from "./Direction.js";
import { QuaternionicSymmetry } from "./QuaternionicSymmetry.js";
import { Symmetry } from "./Symmetry.js";

export class OrbitSet {
    /*private*/ contents: java.util.Map<string, Direction>;

    /*private*/ symmetry: Symmetry;

    /*private*/ lastAdded: Direction;

    public constructor(symmetry: Symmetry) {
        this.contents = <any>(new java.util.HashMap<any, any>());
        if (this.symmetry === undefined) { this.symmetry = null; }
        this.lastAdded = null;
        this.symmetry = symmetry;
    }

    public getSymmetry(): Symmetry {
        return this.symmetry;
    }

    public getAxis(vector: RealVector): Axis {
        return this.symmetry['getAxis$com_vzome_core_math_RealVector$java_util_Collection'](vector, this.contents.values());
    }

    public getDirection(name: string): Direction {
        for(let index=this.getDirections().iterator();index.hasNext();) {
            let dir = index.next();
            {
                if (dir.getCanonicalName() === name)return dir;
                if (dir.getName() === name)return dir;
            }
        }
        return null;
    }

    public getDirections(): java.lang.Iterable<Direction> {
        return this.contents.values();
    }

    public remove(orbit: Direction): boolean {
        const key: string = orbit.toString();
        const hadOne: boolean = this.contents.containsKey(key);
        this.contents.remove(orbit.toString());
        return hadOne;
    }

    public add(orbit: Direction): boolean {
        const key: string = orbit.toString();
        const hadOne: boolean = this.contents.containsKey(key);
        this.contents.put(orbit.toString(), orbit);
        if (!hadOne)this.lastAdded = orbit;
        return !hadOne;
    }

    public contains(orbit: Direction): boolean {
        return this.contents.containsKey(orbit.toString());
    }

    public size(): number {
        return this.contents.size();
    }

    public clear() {
        this.contents.clear();
    }

    public addAll(orbits: OrbitSet) {
        this.contents.putAll(orbits.contents);
    }

    public retainAll(allOrbits: OrbitSet) {
        const badKeys: java.util.List<string> = <any>(new java.util.ArrayList<string>());
        for(let index=this.contents.keySet().iterator();index.hasNext();) {
            let key = index.next();
            {
                if (!allOrbits.contents.containsKey(key))badKeys.add(key);
            }
        }
        for(let index=badKeys.iterator();index.hasNext();) {
            let key = index.next();
            {
                this.contents.remove(key);
            }
        }
    }

    public isEmpty(): boolean {
        return this.contents.isEmpty();
    }

    public last(): Direction {
        return this.lastAdded;
    }
}
OrbitSet["__class"] = "com.vzome.core.math.symmetry.OrbitSet";


export namespace OrbitSet {

    export interface Field {
        getGroup(name: string): OrbitSet;

        getQuaternionSet(name: string): QuaternionicSymmetry;
    }

    export class OrbitComparator {
        public __parent: any;
        names: string[];

        /**
         * 
         * @param {Direction} dir1
         * @param {Direction} dir2
         * @return {number}
         */
        public compare(dir1: Direction, dir2: Direction): number {
            const name1: string = dir1.getName();
            const name2: string = dir2.getName();
            let i1: number = -1;
            let i2: number = -1;
            for(let i: number = 0; i < this.names.length; i++) {{
                if (name1 === (this.names[i]))i1 = i; else if (name2 === (this.names[i]))i2 = i;
            };}
            return i2 - i1;
        }

        constructor(__parent: any) {
            this.__parent = __parent;
            this.names = this.__parent.getSymmetry().getDirectionNames();
        }
    }
    OrbitComparator["__class"] = "com.vzome.core.math.symmetry.OrbitSet.OrbitComparator";
    OrbitComparator["__interfaces"] = ["java.util.Comparator"];


}
