/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace java.awt.geom {
    export abstract class Rectangle2D {
        public abstract getWidth(): number;

        public abstract getHeight(): number;
    }
    Rectangle2D["__class"] = "java.awt.geom.Rectangle2D";


    export namespace Rectangle2D {

        export class Float extends java.awt.geom.Rectangle2D {
            public width: number;

            public height: number;

            public constructor(x: number, y: number, w: number, h: number) {
                super();
                if (this.width === undefined) { this.width = 0; }
                if (this.height === undefined) { this.height = 0; }
                this.width = w;
                this.height = h;
            }

            public getWidth(): number {
                return <number>this.width;
            }

            public getHeight(): number {
                return <number>this.height;
            }
        }
        Float["__class"] = "java.awt.geom.Rectangle2D.Float";

    }

}

