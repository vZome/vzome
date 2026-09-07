import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { Command } from "../commands/Command.js";
import { CommandSymmetry } from "../commands/CommandSymmetry.js";
import { CommandTetrahedralSymmetry } from "../commands/CommandTetrahedralSymmetry.js";
import { SymmetryPerspective } from "../editor/SymmetryPerspective.js";
import { Shapes } from "../editor/api/Shapes.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { OctahedralSymmetry } from "../math/symmetry/OctahedralSymmetry.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";

export abstract class AbstractSymmetryPerspective implements SymmetryPerspective {
    symmetry: Symmetry;

    /*private*/ geometries: java.util.List<Shapes>;

    /*private*/ defaultShapes: Shapes;

    public constructor(symmetry: Symmetry) {
        if (this.symmetry === undefined) { this.symmetry = null; }
        this.geometries = <any>(new java.util.ArrayList<any>());
        this.defaultShapes = null;
        this.symmetry = symmetry;
    }

    /**
     * 
     * @return {*}
     */
    public getSymmetry(): Symmetry {
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

    addShapes(shapes: Shapes) {
        const old: Shapes = this.getGeometry(shapes.getName());
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
    public getGeometries(): java.util.List<Shapes> {
        return this.geometries;
    }

    /*private*/ getGeometry(name: string): Shapes {
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

    public setDefaultGeometry(shapes: Shapes) {
        this.defaultShapes = shapes;
        this.addShapes(shapes);
    }

    /**
     * 
     * @return {*}
     */
    public getDefaultGeometry(): Shapes {
        return this.defaultShapes;
    }

    /**
     * 
     * @param {string} action
     * @return {*}
     */
    public getLegacyCommand(action: string): Command {
        switch((action)) {
        case "octasymm":
            {
                let octaSymm: Symmetry = this.getSymmetry();
                if (!(octaSymm != null && octaSymm instanceof <any>OctahedralSymmetry)){
                    octaSymm = new OctahedralSymmetry(octaSymm.getField());
                }
                return new CommandSymmetry(octaSymm);
            };
        case "tetrasymm":
            {
                const symmetry: Symmetry = this.getSymmetry();
                return new CommandTetrahedralSymmetry(symmetry);
            };
        default:
            return null;
        }
    }

    /**
     * 
     * @param {Direction} orbit
     * @return {boolean}
     */
    public orbitIsStandard(orbit: Direction): boolean {
        return orbit.isStandard();
    }

    /**
     * 
     * @param {Direction} orbit
     * @return {boolean}
     */
    public orbitIsBuildDefault(orbit: Direction): boolean {
        const zone0: Axis = orbit.getAxis$int$int(0, 0);
        return zone0.getRotationPermutation() != null;
    }

    /**
     * 
     * @param {Direction} orbit
     * @return {*}
     */
    public getOrbitUnitLength(orbit: Direction): AlgebraicNumber {
        return orbit.getUnitLength();
    }

    public abstract createToolFactories(kind?: any, model?: any): any;
    public abstract getModelResourcePath(): any;
    public abstract predefineTools(kind?: any, model?: any): any;    }
AbstractSymmetryPerspective["__class"] = "com.vzome.core.kinds.AbstractSymmetryPerspective";
AbstractSymmetryPerspective["__interfaces"] = ["com.vzome.core.editor.SymmetryPerspective"];
