import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { GeometryExporter } from "./GeometryExporter.js";
import { RealVector } from "../math/RealVector.js";
import { Manifestation } from "../model/Manifestation.js";
import { Strut } from "../model/Strut.js";
import { File } from "../../../../java/io/File.js";
import { PrintWriter } from "../../../../java/io/PrintWriter.js";

export class PdbExporter extends GeometryExporter {
    /**
     * 
     * @param {File} directory
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public doExport(directory: File, writer: java.io.Writer, height: number, width: number) {
        const atoms: java.util.Map<AlgebraicVector, PdbExporter.Atom> = <any>(new java.util.HashMap<any, any>());
        const atomsList: java.util.List<PdbExporter.Atom> = <any>(new java.util.ArrayList<any>());
        let indices: number = 0;
        for(let index=this.mModel.iterator();index.hasNext();) {
            let rm = index.next();
            {
                const man: Manifestation = rm.getManifestation();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    const startLoc: AlgebraicVector = (<Strut><any>man).getLocation();
                    const endLoc: AlgebraicVector = (<Strut><any>man).getEnd();
                    let startAtom: PdbExporter.Atom = atoms.get(startLoc);
                    if (startAtom == null){
                        startAtom = new PdbExporter.Atom(this, startLoc, ++indices);
                        atoms.put(startLoc, startAtom);
                        atomsList.add(startAtom);
                    }
                    let endAtom: PdbExporter.Atom = atoms.get(endLoc);
                    if (endAtom == null){
                        endAtom = new PdbExporter.Atom(this, endLoc, ++indices);
                        atoms.put(endLoc, endAtom);
                        atomsList.add(endAtom);
                    }
                    startAtom.neighbors.add(endAtom);
                    endAtom.neighbors.add(startAtom);
                }
            }
        }
        const field: AlgebraicField = this.mModel.getField();
        const scale: AlgebraicNumber = field['createAlgebraicNumber$int$int$int$int'](4, 6, 1, 0);
        const scaleFactor: number = 5.0 / scale.evaluate();
        const locations: java.lang.StringBuilder = new java.lang.StringBuilder();
        const neighbors: java.lang.StringBuilder = new java.lang.StringBuilder();
        for(let index=atomsList.iterator();index.hasNext();) {
            let atom = index.next();
            {
                const rv: RealVector = this.mModel.renderVector(atom.location);
                console.info(atom.location.toString());
                locations.append(javaemul.internal.StringHelper.format("HETATM%5d He   UNK  0001     %7.3f %7.3f %7.3f\n", atom.index, rv.x * scaleFactor, rv.y * scaleFactor, rv.z * scaleFactor));
                neighbors.append(javaemul.internal.StringHelper.format("CONECT%5d", atom.index));
                for(let index=atom.neighbors.iterator();index.hasNext();) {
                    let neighbor = index.next();
                    {
                        neighbors.append(javaemul.internal.StringHelper.format("%5d", neighbor.index));
                    }
                }
                neighbors.append("\n");
            }
        }
        this.output = new PrintWriter(writer);
        this.output.println$java_lang_Object("HEADER");
        this.output.println$java_lang_Object("REMARK vZome");
        this.output.print(locations);
        this.output.print(neighbors);
        this.output.println$java_lang_Object("END");
        this.output.flush();
    }

    /**
     * 
     * @return {string}
     */
    public getFileExtension(): string {
        return "pdb";
    }
}
PdbExporter["__class"] = "com.vzome.core.exporters.PdbExporter";


export namespace PdbExporter {

    export class Atom {
        public __parent: any;
        public constructor(__parent: any, location: AlgebraicVector, i: number) {
            this.__parent = __parent;
            if (this.location === undefined) { this.location = null; }
            if (this.index === undefined) { this.index = 0; }
            this.neighbors = <any>(new java.util.HashSet<any>());
            this.location = location;
            this.index = i;
        }

        location: AlgebraicVector;

        index: number;

        neighbors: java.util.Set<PdbExporter.Atom>;
    }
    Atom["__class"] = "com.vzome.core.exporters.PdbExporter.Atom";

}
