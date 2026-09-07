import { AlgebraicField } from "../../core/algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../../core/algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../core/algebra/AlgebraicVector.js";
import { Controller } from "../api/Controller.js";
import { DefaultController } from "./DefaultController.js";
import { NumberController } from "./NumberController.js";

export class VectorController extends DefaultController {
    /*private*/ coordinates: NumberController[];

    /*private*/ field: AlgebraicField;

    public constructor(initial: AlgebraicVector) {
        super();
        if (this.coordinates === undefined) { this.coordinates = null; }
        if (this.field === undefined) { this.field = null; }
        this.field = initial.getField();
        this.coordinates = (s => { let a=[]; while(s-->0) a.push(null); return a; })(initial.dimension());
        for(let i: number = 0; i < this.coordinates.length; i++) {{
            this.coordinates[i] = new NumberController(initial.getField());
            this.coordinates[i].setValue(initial.getComponent(i));
        };}
    }

    /**
     * 
     * @param {string} name
     * @return {*}
     */
    public getSubController(name: string): Controller {
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

    public setVector(vector: AlgebraicVector) {
        for(let i: number = 0; i < this.coordinates.length; i++) {{
            const numberController: NumberController = this.coordinates[i];
            const coord: AlgebraicNumber = vector.getComponent(i);
            numberController.setValue(coord);
        };}
    }

    public getVector(): AlgebraicVector {
        const result: AlgebraicVector = this.field.basisVector(this.coordinates.length, 0);
        for(let i: number = 0; i < this.coordinates.length; i++) {{
            const numberController: NumberController = this.coordinates[i];
            const coord: AlgebraicNumber = numberController.getValue();
            result.setComponent(i, coord);
        };}
        return result;
    }
}
VectorController["__class"] = "com.vzome.desktop.controller.VectorController";
VectorController["__interfaces"] = ["com.vzome.desktop.api.Controller"];
