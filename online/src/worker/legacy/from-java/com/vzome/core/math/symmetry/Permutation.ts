import { Axis } from "./Axis.js";
import { Symmetry } from "./Symmetry.js";

export class Permutation {
    /**
     * Since the MINUS side always mirrors the PLUS side,
     * the map only needs to indicate one.
     */
    /*private*/ m_map: number[];

    /*private*/ m_order: number;

    /*private*/ mSymmetryGroup: Symmetry;

    public constructor(group: Symmetry, map: number[]) {
        if (this.m_map === undefined) { this.m_map = null; }
        this.m_order = 1;
        if (this.mSymmetryGroup === undefined) { this.mSymmetryGroup = null; }
        this.mSymmetryGroup = group;
        if (map == null){
            const numUnits: number = group.getChiralOrder();
            map = (s => { let a=[]; while(s-->0) a.push(0); return a; })(numUnits);
            for(let unit: number = 0; unit < numUnits; unit++) {map[unit] = unit;}
        }
        this.m_map = map;
        let unit: number = 0;
        for(let i: number = 0; i < this.m_map.length; i++) {{
            unit = this.m_map[unit];
            if (unit === 0){
                this.m_order = i + 1;
                break;
            }
        };}
    }

    public getJsonValue(): number[] {
        return this.m_map;
    }

    /**
     * 
     * @return {string}
     */
    public toString(): string {
        return "permutation #" + this.mapIndex(0);
    }

    public getOrder(): number {
        return this.m_order;
    }

    /**
     * Composition, where p1.compose( p2 ) .permute(axis)  == p1.permute( p2.permute( axis ) )
     * @param {Permutation} other
     * @return {Permutation}
     */
    public compose(other: Permutation): Permutation {
        return this.mSymmetryGroup.getPermutation(this.m_map[other.m_map[0]]);
    }

    public inverse(): Permutation {
        for(let i: number = 0; i < this.m_map.length; i++) {if (this.mapIndex(i) === 0)return this.mSymmetryGroup.getPermutation(i);;}
        return null;
    }

    public power(power: number): Permutation {
        if (power === 0)return this.mSymmetryGroup.getPermutation(0);
        let base: Permutation = this;
        if (power < 0){
            base = this.inverse();
            power *= -1;
        }
        if (power === 1)return base;
        return base.compose(base.power(power - 1));
    }

    public mapIndex(i: number): number {
        if ((i < 0) || (i >= this.m_map.length))return Symmetry.NO_ROTATION;
        return this.m_map[i];
    }

    public permute(axis: Axis, sense: number): Axis {
        let orn: number = axis.getOrientation();
        orn = this.mapIndex(orn);
        return axis.getDirection().getAxis$int$int((sense + axis.getSense()) % 2, orn);
    }
}
Permutation["__class"] = "com.vzome.core.math.symmetry.Permutation";
