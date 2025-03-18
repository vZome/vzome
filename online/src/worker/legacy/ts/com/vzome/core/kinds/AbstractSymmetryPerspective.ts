/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.kinds {
    export abstract class AbstractSymmetryPerspective implements com.vzome.core.editor.SymmetryPerspective {
        symmetry: com.vzome.core.math.symmetry.Symmetry;

        /*private*/ geometries: java.util.List<com.vzome.core.editor.api.Shapes>;

        /*private*/ defaultShapes: com.vzome.core.editor.api.Shapes;

        public constructor(symmetry: com.vzome.core.math.symmetry.Symmetry) {
            if (this.symmetry === undefined) { this.symmetry = null; }
            this.geometries = <any>(new java.util.ArrayList<any>());
            this.defaultShapes = null;
            this.symmetry = symmetry;
        }

        /**
         * 
         * @return {*}
         */
        public getSymmetry(): com.vzome.core.math.symmetry.Symmetry {
            return this.symmetry;
        }

        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return this.getSymmetry().getName();
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return this.getSymmetry().getName();
        }

        addShapes(shapes: com.vzome.core.editor.api.Shapes) {
            const old: com.vzome.core.editor.api.Shapes = this.getGeometry(shapes.getName());
            if (old != null){
                this.geometries.remove(old);
            }
            this.geometries.add(shapes);
        }

        clearShapes() {
            this.geometries.clear();
            this.defaultShapes = null;
        }

        /**
         * 
         * @return {*}
         */
        public getGeometries(): java.util.List<com.vzome.core.editor.api.Shapes> {
            return this.geometries;
        }

        /*private*/ getGeometry(name: string): com.vzome.core.editor.api.Shapes {
            for(let index=this.geometries.iterator();index.hasNext();) {
                let shapes = index.next();
                {
                    if (shapes.getName() === name){
                        return shapes;
                    }
                }
            }
            return null;
        }

        public setDefaultGeometry(shapes: com.vzome.core.editor.api.Shapes) {
            this.defaultShapes = shapes;
            this.addShapes(shapes);
        }

        /**
         * 
         * @return {*}
         */
        public getDefaultGeometry(): com.vzome.core.editor.api.Shapes {
            return this.defaultShapes;
        }

        /**
         * 
         * @param {string} action
         * @return {*}
         */
        public getLegacyCommand(action: string): com.vzome.core.commands.Command {
            switch((action)) {
            case "octasymm":
                {
                    let octaSymm: com.vzome.core.math.symmetry.Symmetry = this.getSymmetry();
                    if (!(octaSymm != null && octaSymm instanceof <any>com.vzome.core.math.symmetry.OctahedralSymmetry)){
                        octaSymm = new com.vzome.core.math.symmetry.OctahedralSymmetry(octaSymm.getField());
                    }
                    return new com.vzome.core.commands.CommandSymmetry(octaSymm);
                };
            case "tetrasymm":
                {
                    const symmetry: com.vzome.core.math.symmetry.Symmetry = this.getSymmetry();
                    const closure: number[] = symmetry.subgroup(com.vzome.core.math.symmetry.Symmetry.TETRAHEDRAL);
                    return new com.vzome.core.commands.CommandTetrahedralSymmetry(symmetry);
                };
            default:
                return null;
            }
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Direction} orbit
         * @return {boolean}
         */
        public orbitIsStandard(orbit: com.vzome.core.math.symmetry.Direction): boolean {
            return orbit.isStandard();
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Direction} orbit
         * @return {boolean}
         */
        public orbitIsBuildDefault(orbit: com.vzome.core.math.symmetry.Direction): boolean {
            const zone0: com.vzome.core.math.symmetry.Axis = orbit.getAxis$int$int(0, 0);
            return zone0.getRotationPermutation() != null;
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Direction} orbit
         * @return {*}
         */
        public getOrbitUnitLength(orbit: com.vzome.core.math.symmetry.Direction): com.vzome.core.algebra.AlgebraicNumber {
            return orbit.getUnitLength();
        }

        public abstract createToolFactories(kind?: any, model?: any): any;
        public abstract getModelResourcePath(): any;
        public abstract predefineTools(kind?: any, model?: any): any;    }
    AbstractSymmetryPerspective["__class"] = "com.vzome.core.kinds.AbstractSymmetryPerspective";
    AbstractSymmetryPerspective["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];


}

