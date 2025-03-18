/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class VectorController extends com.vzome.desktop.controller.DefaultController {
        /*private*/ coordinates: com.vzome.desktop.controller.NumberController[];

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        public constructor(initial: com.vzome.core.algebra.AlgebraicVector) {
            super();
            if (this.coordinates === undefined) { this.coordinates = null; }
            if (this.field === undefined) { this.field = null; }
            this.field = initial.getField();
            this.coordinates = (s => { let a=[]; while(s-->0) a.push(null); return a; })(initial.dimension());
            for(let i: number = 0; i < this.coordinates.length; i++) {{
                this.coordinates[i] = new com.vzome.desktop.controller.NumberController(initial.getField());
                this.coordinates[i].setValue(initial.getComponent(i));
            };}
        }

        /**
         * 
         * @param {string} name
         * @return {*}
         */
        public getSubController(name: string): com.vzome.desktop.api.Controller {
            switch((name)) {
            case "w":
                return this.coordinates[0];
            case "x":
                return this.coordinates[1];
            case "y":
                return this.coordinates[2];
            case "z":
                return this.coordinates[3];
            default:
                return super.getSubController(name);
            }
        }

        public setVector(vector: com.vzome.core.algebra.AlgebraicVector) {
            for(let i: number = 0; i < this.coordinates.length; i++) {{
                const numberController: com.vzome.desktop.controller.NumberController = this.coordinates[i];
                const coord: com.vzome.core.algebra.AlgebraicNumber = vector.getComponent(i);
                numberController.setValue(coord);
            };}
        }

        public getVector(): com.vzome.core.algebra.AlgebraicVector {
            const result: com.vzome.core.algebra.AlgebraicVector = this.field.basisVector(this.coordinates.length, 0);
            for(let i: number = 0; i < this.coordinates.length; i++) {{
                const numberController: com.vzome.desktop.controller.NumberController = this.coordinates[i];
                const coord: com.vzome.core.algebra.AlgebraicNumber = numberController.getValue();
                result.setComponent(i, coord);
            };}
            return result;
        }
    }
    VectorController["__class"] = "com.vzome.desktop.controller.VectorController";
    VectorController["__interfaces"] = ["com.vzome.desktop.api.Controller"];


}

