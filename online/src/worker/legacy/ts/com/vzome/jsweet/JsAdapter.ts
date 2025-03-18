/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.jsweet {
    export class JsAdapter {
        public static mapVectorToJava(vector: number[][], field: com.vzome.jsweet.JsAlgebraicField): com.vzome.core.algebra.AlgebraicVector {
            const ans: com.vzome.core.algebra.AlgebraicNumber[] = <com.vzome.core.algebra.AlgebraicNumber[]>java.util.stream.Stream.of<any>(vector).map<any>((ints) => new com.vzome.jsweet.JsAlgebraicNumber(field, ints)).toArray();
            return new com.vzome.core.algebra.AlgebraicVector(ans);
        }

        public static mapVectorToJavascript(vector: com.vzome.core.algebra.AlgebraicVector): number[][] {
            return <number[][]>java.util.stream.Stream.of<any>(vector.getComponents()).map<any>((an) => an.toTrailingDivisor()).toArray();
        }

        public static getZoneGrid(orbits: com.vzome.core.editor.api.OrbitSource, planeNormal: number[][]): Object {
            const field: com.vzome.jsweet.JsAlgebraicField = <com.vzome.jsweet.JsAlgebraicField><any>orbits.getSymmetry().getField();
            const normal: com.vzome.core.algebra.AlgebraicVector = JsAdapter.mapVectorToJava(planeNormal, field);
            const planeColor: string = orbits.getVectorColor(normal).toWebString();
            const planeName: string = orbits.getSymmetry()['getAxis$com_vzome_core_algebra_AlgebraicVector'](normal).getOrbit().getName();
            const zonesList: java.util.ArrayList<Object> = <any>(new java.util.ArrayList<any>());
            const planeOrbits: com.vzome.core.math.symmetry.PlaneOrbitSet = new com.vzome.core.math.symmetry.PlaneOrbitSet(orbits.getOrbits(), normal);
            for(const iterator: java.util.Iterator<com.vzome.core.math.symmetry.Axis> = planeOrbits.zones(); iterator.hasNext(); ) {{
                const zone: com.vzome.core.math.symmetry.Axis = <com.vzome.core.math.symmetry.Axis>iterator.next();
                const orbit: com.vzome.core.math.symmetry.Direction = zone.getDirection();
                if (!orbit.isStandard())continue;
                const gridPoints: java.util.ArrayList<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>());
                const zoneNormal: com.vzome.core.algebra.AlgebraicVector = zone.normal();
                const zoneColor: string = orbits.getVectorColor(zoneNormal).toWebString();
                let scale: com.vzome.core.algebra.AlgebraicNumber = orbit.getUnitLength();
                for(let i: number = 0; i < 5; i++) {{
                    scale = scale['times$com_vzome_core_algebra_AlgebraicNumber'](field.createPower$int(1));
                    const gridPoint: com.vzome.core.algebra.AlgebraicVector = zoneNormal.scale(scale);
                    gridPoints.add(gridPoint);
                };}
                const vectors: com.vzome.core.algebra.AlgebraicVector[] = gridPoints.stream().toArray<any>((size) => (s => { let a=[]; while(s-->0) a.push(null); return a; })(size));
                const zoneObj: Object = ((target:Object) => {
                    target["color"] = zoneColor;
                    target["vectors"] = vectors;
                    return target;

                })(new Object());
                zonesList.add(zoneObj);
            };}
            const zones: Object[] = zonesList.stream().toArray<any>((size) => (s => { let a=[]; while(s-->0) a.push(null); return a; })(size));
            return ((target:Object) => {
                target["color"] = planeColor;
                target["zones"] = zones;
                return target;

            })(new Object());
        }
    }
    JsAdapter["__class"] = "com.vzome.jsweet.JsAdapter";

}

