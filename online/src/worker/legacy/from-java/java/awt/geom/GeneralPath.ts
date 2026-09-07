import { java, javaemul } from "../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { PathIterator } from "./PathIterator.js";

export class GeneralPath {
    /*private*/ xs: java.util.ArrayList<any>;

    /*private*/ ys: java.util.ArrayList<any>;

    /*private*/ actions: java.util.ArrayList<any>;

    public moveTo(x: number, y: number) {
        this.xs.add(x);
        this.ys.add(y);
        this.actions.add(PathIterator.SEG_MOVETO);
    }

    public lineTo(x: number, y: number) {
        this.xs.add(x);
        this.ys.add(y);
        this.actions.add(PathIterator.SEG_LINETO);
    }

    public closePath() {
        this.xs.add(0);
        this.ys.add(0);
        this.actions.add(PathIterator.SEG_CLOSE);
    }

    public getPathIterator(at: any): PathIterator {
        return new GeneralPath.GeneralPath$0(this);
    }

    constructor() {
        this.xs = <any>(new java.util.ArrayList());
        this.ys = <any>(new java.util.ArrayList());
        this.actions = <any>(new java.util.ArrayList());
    }
}
GeneralPath["__class"] = "java.awt.geom.GeneralPath";


export namespace GeneralPath {

    export class GeneralPath$0 implements PathIterator {
        public __parent: any;
        index: number;

        public isDone(): boolean {
            return this.index === this.__parent.actions.size();
        }

        public currentSegment(coords: number[]): number {
            coords[0] = <number>this.__parent.xs.get(this.index);
            coords[1] = <number>this.__parent.ys.get(this.index);
            return (<number>this.__parent.actions.get(this.index)|0);
        }

        public next() {
            ++this.index;
        }

        constructor(__parent: any) {
            this.__parent = __parent;
            this.index = 0;
        }
    }
    GeneralPath$0["__interfaces"] = ["java.awt.geom.PathIterator"];


}
