/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    export class OrbitSet {
        /*private*/ contents: java.util.Map<string, com.vzome.core.math.symmetry.Direction>;

        /*private*/ symmetry: com.vzome.core.math.symmetry.Symmetry;

        /*private*/ lastAdded: com.vzome.core.math.symmetry.Direction;

        public constructor(symmetry: com.vzome.core.math.symmetry.Symmetry) {
            this.contents = <any>(new java.util.HashMap<any, any>());
            if (this.symmetry === undefined) { this.symmetry = null; }
            this.lastAdded = null;
            this.symmetry = symmetry;
        }

        public getSymmetry(): com.vzome.core.math.symmetry.Symmetry {
            return this.symmetry;
        }

        public getAxis(vector: com.vzome.core.math.RealVector): com.vzome.core.math.symmetry.Axis {
            return this.symmetry['getAxis$com_vzome_core_math_RealVector$java_util_Collection'](vector, this.contents.values());
        }

        public getDirection(name: string): com.vzome.core.math.symmetry.Direction {
            for(let index=this.getDirections().iterator();index.hasNext();) {
                let dir = index.next();
                {
                    if (dir.getCanonicalName() === name)return dir;
                    if (dir.getName() === name)return dir;
                }
            }
            return null;
        }

        public getDirections(): java.lang.Iterable<com.vzome.core.math.symmetry.Direction> {
            return this.contents.values();
        }

        public remove(orbit: com.vzome.core.math.symmetry.Direction): boolean {
            const key: string = orbit.toString();
            const hadOne: boolean = this.contents.containsKey(key);
            this.contents.remove(orbit.toString());
            return hadOne;
        }

        public add(orbit: com.vzome.core.math.symmetry.Direction): boolean {
            const key: string = orbit.toString();
            const hadOne: boolean = this.contents.containsKey(key);
            this.contents.put(orbit.toString(), orbit);
            if (!hadOne)this.lastAdded = orbit;
            return !hadOne;
        }

        public contains(orbit: com.vzome.core.math.symmetry.Direction): boolean {
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

        public last(): com.vzome.core.math.symmetry.Direction {
            return this.lastAdded;
        }
    }
    OrbitSet["__class"] = "com.vzome.core.math.symmetry.OrbitSet";


    export namespace OrbitSet {

        export interface Field {
            getGroup(name: string): com.vzome.core.math.symmetry.OrbitSet;

            getQuaternionSet(name: string): com.vzome.core.math.symmetry.QuaternionicSymmetry;
        }

        export class OrbitComparator {
            public __parent: any;
            names: string[];

            /**
             * 
             * @param {com.vzome.core.math.symmetry.Direction} dir1
             * @param {com.vzome.core.math.symmetry.Direction} dir2
             * @return {number}
             */
            public compare(dir1: com.vzome.core.math.symmetry.Direction, dir2: com.vzome.core.math.symmetry.Direction): number {
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

}

