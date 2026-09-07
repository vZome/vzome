import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Color } from "../construction/Color.js";
import { OrbitSource } from "../editor/api/OrbitSource.js";
import { Shapes } from "../editor/api/Shapes.js";
import { Polyhedron } from "../math/Polyhedron.js";
import { RealVector } from "../math/RealVector.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { Embedding } from "../math/symmetry/Embedding.js";
import { OrbitSet } from "../math/symmetry/OrbitSet.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { Connector } from "../model/Connector.js";
import { HasRenderedObject } from "../model/HasRenderedObject.js";
import { Manifestation } from "../model/Manifestation.js";
import { ManifestationChanges } from "../model/ManifestationChanges.js";
import { Panel } from "../model/Panel.js";
import { Strut } from "../model/Strut.js";
import { RenderedManifestation } from "./RenderedManifestation.js";
import { RenderingChanges } from "./RenderingChanges.js";

export class RenderedModel implements ManifestationChanges, java.lang.Iterable<RenderedManifestation> {
    mListeners: java.util.List<RenderingChanges>;

    /*private*/ mPolyhedra: Shapes;

    /*private*/ mSelectionGlow: number;

    mRendered: java.util.HashSet<RenderedManifestation>;

    byID: java.util.HashMap<string, RenderedManifestation>;

    /*private*/ field: AlgebraicField;

    /*private*/ orbitSource: OrbitSource;

    /*private*/ oneSidedPanels: boolean;

    /*private*/ mainListener: RenderingChanges;

    /*private*/ enabled: boolean;

    /*private*/ colorPanels: boolean;

    public constructor(field?: any, orbitSource?: any) {
        if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.algebra.AlgebraicField") >= 0)) || field === null) && ((orbitSource != null && (orbitSource.constructor != null && orbitSource.constructor["__interfaces"] != null && orbitSource.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.OrbitSource") >= 0)) || orbitSource === null)) {
            let __args = arguments;
            if (this.mPolyhedra === undefined) { this.mPolyhedra = null; } 
            if (this.field === undefined) { this.field = null; } 
            if (this.orbitSource === undefined) { this.orbitSource = null; } 
            if (this.mainListener === undefined) { this.mainListener = null; } 
            this.mListeners = <any>(new java.util.ArrayList<any>());
            this.mSelectionGlow = 0.8;
            this.mRendered = <any>(new java.util.HashSet<any>());
            this.byID = <any>(new java.util.HashMap<any, any>());
            this.oneSidedPanels = false;
            this.enabled = true;
            this.colorPanels = true;
            this.field = field;
            this.orbitSource = orbitSource;
            this.mPolyhedra = (orbitSource == null) ? null : orbitSource.getShapes();
        } else if (((field != null && (field.constructor != null && field.constructor["__interfaces"] != null && field.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Symmetry") >= 0)) || field === null) && orbitSource === undefined) {
            let __args = arguments;
            let symmetry: any = __args[0];
            {
                let __args = arguments;
                let field: any = symmetry.getField();
                let orbitSource: any = new RenderedModel.SymmetryOrbitSource(symmetry);
                if (this.mPolyhedra === undefined) { this.mPolyhedra = null; } 
                if (this.field === undefined) { this.field = null; } 
                if (this.orbitSource === undefined) { this.orbitSource = null; } 
                if (this.mainListener === undefined) { this.mainListener = null; } 
                this.mListeners = <any>(new java.util.ArrayList<any>());
                this.mSelectionGlow = 0.8;
                this.mRendered = <any>(new java.util.HashSet<any>());
                this.byID = <any>(new java.util.HashMap<any, any>());
                this.oneSidedPanels = false;
                this.enabled = true;
                this.colorPanels = true;
                this.field = field;
                this.orbitSource = orbitSource;
                this.mPolyhedra = (orbitSource == null) ? null : orbitSource.getShapes();
            }
            (() => {
                this.enabled = false;
            })();
        } else throw new Error('invalid overload');
    }

    public withColorPanels(setting: boolean): RenderedModel {
        this.colorPanels = setting;
        return this;
    }

    public getField(): AlgebraicField {
        return this.field;
    }

    public addListener(listener: RenderingChanges) {
        if (this.mainListener == null)this.mainListener = listener; else this.mListeners.add(listener);
    }

    public removeListener(listener: RenderingChanges) {
        if (this.mainListener === listener)this.mainListener = null; else this.mListeners.remove(listener);
    }

    public render(manifestation: Manifestation): RenderedManifestation {
        const rm: RenderedManifestation = new RenderedManifestation(manifestation, this.orbitSource);
        rm.resetAttributes(this.oneSidedPanels, this.colorPanels);
        return rm;
    }

    /**
     * 
     * @param {*} m
     */
    public manifestationAdded(m: Manifestation) {
        if (!this.enabled){
            m.setRenderedObject(new RenderedManifestation(m, this.orbitSource));
            return;
        }
        const rm: RenderedManifestation = this.render(m);
        const poly: Polyhedron = rm.getShape();
        if (poly == null)return;
        m.setRenderedObject(rm);
        this.mRendered.add(rm);
        this.byID.put(rm.getGuid().toString(), rm);
        if (this.mainListener != null)this.mainListener.manifestationAdded(rm);
        for(let index=this.mListeners.iterator();index.hasNext();) {
            let listener = index.next();
            {
                listener.manifestationAdded(rm);
            }
        }
    }

    /**
     * 
     * @param {*} m
     */
    public manifestationRemoved(m: Manifestation) {
        if (!this.enabled){
            m.setRenderedObject(null);
            return;
        }
        const rendered: RenderedManifestation = <RenderedManifestation><any>(<HasRenderedObject><any>m).getRenderedObject();
        if (rendered == null)return;
        for(let index=this.mListeners.iterator();index.hasNext();) {
            let listener = index.next();
            {
                listener.manifestationRemoved(rendered);
            }
        }
        if (this.mainListener != null)this.mainListener.manifestationRemoved(rendered);
        if (!this.mRendered.remove(rendered))throw new java.lang.IllegalStateException("unable to remove RenderedManifestation");
        this.byID.remove(rendered.getGuid().toString());
        m.setRenderedObject(null);
    }

    public getRenderedManifestation(guid: string): RenderedManifestation {
        return this.byID.get(guid);
    }

    public setManifestationGlow(m: Manifestation, on: boolean) {
        const rendered: RenderedManifestation = <RenderedManifestation><any>(<HasRenderedObject><any>m).getRenderedObject();
        if (rendered == null)return;
        rendered.setGlow(on ? this.mSelectionGlow : 0.0);
        if (this.mainListener != null)this.mainListener.glowChanged(rendered);
        for(let index=this.mListeners.iterator();index.hasNext();) {
            let listener = index.next();
            {
                listener.glowChanged(rendered);
            }
        }
    }

    public setManifestationColor(m: Manifestation, color: Color) {
        const rendered: RenderedManifestation = <RenderedManifestation><any>(<HasRenderedObject><any>m).getRenderedObject();
        if (rendered == null)return;
        rendered.setColor(color);
        if (this.mainListener != null)this.mainListener.colorChanged(rendered);
        for(let index=this.mListeners.iterator();index.hasNext();) {
            let listener = index.next();
            {
                listener.colorChanged(rendered);
            }
        }
    }

    public setManifestationLabel(m: Manifestation, label: string) {
        const rendered: RenderedManifestation = <RenderedManifestation><any>(<HasRenderedObject><any>m).getRenderedObject();
        if (rendered == null)return;
        rendered.setLabel(label);
        if (this.mainListener != null)this.mainListener.labelChanged(rendered);
        for(let index=this.mListeners.iterator();index.hasNext();) {
            let listener = index.next();
            {
                listener.labelChanged(rendered);
            }
        }
    }

    public setManifestationTransparency(m: Manifestation, on: boolean) {
        const rendered: RenderedManifestation = <RenderedManifestation><any>(<HasRenderedObject><any>m).getRenderedObject();
        if (rendered == null)return;
        rendered.setTransparency(on ? this.mSelectionGlow : 0.0);
        if (this.mainListener != null)this.mainListener.colorChanged(rendered);
        for(let index=this.mListeners.iterator();index.hasNext();) {
            let listener = index.next();
            {
                listener.colorChanged(rendered);
            }
        }
    }

    /**
     * 
     * @return {*}
     */
    public iterator(): java.util.Iterator<RenderedManifestation> {
        return this.mRendered.iterator();
    }

    public getOrbitSource(): OrbitSource {
        return this.orbitSource;
    }

    public setShapes(shapes: Shapes) {
        const supported: boolean = this.mainListener.shapesChanged(shapes);
        if (!supported)this.setOrbitSource(this.orbitSource);
    }

    public setOrbitSource(orbitSource: OrbitSource) {
        this.orbitSource = orbitSource;
        this.enabled = true;
        this.mPolyhedra = orbitSource.getShapes();
        if (this.mPolyhedra == null)return;
        {
            const newSet: java.util.HashSet<RenderedManifestation> = <any>(new java.util.HashSet<any>());
            for(const rms: java.util.Iterator<RenderedManifestation> = this.mRendered.iterator(); rms.hasNext(); ) {{
                const rendered: RenderedManifestation = rms.next();
                rms.remove();
                const m: Manifestation = rendered.getManifestation();
                if (m.isHidden())continue;
                if (rendered.getShape() != null){
                    if (this.mainListener != null){
                        this.mainListener.manifestationRemoved(rendered);
                    }
                    for(let index=this.mListeners.iterator();index.hasNext();) {
                        let listener = index.next();
                        {
                            listener.manifestationRemoved(rendered);
                        }
                    }
                }
                rendered.setOrbitSource(this.orbitSource);
                rendered.resetAttributes(this.oneSidedPanels, this.colorPanels);
                newSet.add(rendered);
                const glow: number = rendered.getGlow();
                if (rendered.getShape() != null){
                    if (this.mainListener != null){
                        this.mainListener.manifestationAdded(rendered);
                        if (glow !== 0.0)this.mainListener.glowChanged(rendered);
                    }
                    for(let index=this.mListeners.iterator();index.hasNext();) {
                        let listener = index.next();
                        {
                            listener.manifestationAdded(rendered);
                            if (glow !== 0.0)listener.glowChanged(rendered);
                        }
                    }
                }
            };}
            this.mRendered.addAll(newSet);
            for(let index=newSet.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    this.byID.put(rm.getGuid().toString(), rm);
                }
            }
        };
    }

    /**
     * 
     * @param {*} m
     * @param {Color} color
     */
    public manifestationColored(m: Manifestation, color: Color) {
        if (this.enabled)this.setManifestationColor(m, color);
    }

    /**
     * 
     * @param {*} m
     * @param {string} label
     */
    public manifestationLabeled(m: Manifestation, label: string) {
        if (this.enabled)this.setManifestationLabel(m, label);
    }

    public snapshot(): RenderedModel {
        const snapshot: RenderedModel = new RenderedModel(this.orbitSource.getSymmetry());
        for(let index=this.mRendered.iterator();index.hasNext();) {
            let rm = index.next();
            {
                const copy: RenderedManifestation = rm.copy();
                snapshot.mRendered.add(copy);
            }
        }
        return snapshot;
    }

    /**
     * Switch a scene graph (changes) from rendering one RenderedModel to another one.
     * For RenderedManifestations that show the same object in both, just update the
     * attributes.
     * When "from" is empty, this is the initial rendering of the "to" RenderedModel.
     * @param {RenderedModel} from is an empty RenderedModel in some cases
     * @param {RenderedModel} to
     * @param {*} changes is a scene graph
     */
    public static renderChange(from: RenderedModel, to: RenderedModel, changes: RenderingChanges) {
        const toRemove: java.util.HashSet<RenderedManifestation> = <any>(new java.util.HashSet<any>(from.mRendered));
        toRemove.removeAll(to.mRendered);
        for(let index=toRemove.iterator();index.hasNext();) {
            let rm = index.next();
            {
                changes.manifestationRemoved(rm);
            }
        }
        const toAdd: java.util.HashSet<RenderedManifestation> = <any>(new java.util.HashSet<any>(to.mRendered));
        toAdd.removeAll(from.mRendered);
        for(let index=toAdd.iterator();index.hasNext();) {
            let rm = index.next();
            {
                changes.manifestationAdded(rm);
            }
        }
        for(let index=from.mRendered.iterator();index.hasNext();) {
            let fromRm = index.next();
            {
                for(let index=to.mRendered.iterator();index.hasNext();) {
                    let toRm = index.next();
                    {
                        if (fromRm.equals(toRm)){
                            changes.manifestationSwitched(fromRm, toRm);
                            if (javaemul.internal.FloatHelper.floatToIntBits(fromRm.getGlow()) !== javaemul.internal.FloatHelper.floatToIntBits(toRm.getGlow()))changes.glowChanged(toRm);
                            const fromColor: Color = fromRm.getColor();
                            const toColor: Color = toRm.getColor();
                            if (fromColor == null && toColor == null)continue;
                            if ((fromColor == null && toColor != null) || (fromColor != null && toColor == null) || !fromColor.equals(toColor))changes.colorChanged(toRm);
                        }
                    }
                }
            }
        }
    }

    public renderVector(av: AlgebraicVector): RealVector {
        if (av != null)return this.getEmbedding().embedInR3(av); else return new RealVector(0.0, 0.0, 0.0);
    }

    public renderVectorDouble(av: AlgebraicVector): number[] {
        if (av != null)return this.getEmbedding().embedInR3Double(av); else return [0.0, 0.0, 0.0];
    }

    public getEmbedding(): Embedding {
        return this.orbitSource.getSymmetry();
    }

    public measureDistanceCm(c1: Connector, c2: Connector): number {
        return this.measureLengthCm$com_vzome_core_math_RealVector(this.renderVector(c1.getLocation().minus(c2.getLocation())));
    }

    public getCmScaling(): number {
        return this.mPolyhedra.getCmScaling();
    }

    public measureLengthCm$com_vzome_core_math_RealVector(rv: RealVector): number {
        return rv.length() * this.mPolyhedra.getCmScaling();
    }

    public measureLengthCm(rv?: any): number {
        if (((rv != null && rv instanceof <any>RealVector) || rv === null)) {
            return <any>this.measureLengthCm$com_vzome_core_math_RealVector(rv);
        } else if (((rv != null && (rv.constructor != null && rv.constructor["__interfaces"] != null && rv.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)) || rv === null)) {
            return <any>this.measureLengthCm$com_vzome_core_model_Strut(rv);
        } else throw new Error('invalid overload');
    }

    public measureLengthCm$com_vzome_core_model_Strut(strut: Strut): number {
        return this.measureLengthCm$com_vzome_core_math_RealVector(this.renderVector(strut.getOffset()));
    }

    public measureDihedralAngle(p1: Panel, p2: Panel): number {
        const v1: RealVector = p1['getNormal$com_vzome_core_math_symmetry_Embedding'](this.getEmbedding());
        const v2: RealVector = p2['getNormal$com_vzome_core_math_symmetry_Embedding'](this.getEmbedding());
        return RenderedModel.safeAcos(v1, v2);
    }

    public measureAngle(s1: Strut, s2: Strut): number {
        const v1: RealVector = this.renderVector(s1.getOffset());
        const v2: RealVector = this.renderVector(s2.getOffset());
        return RenderedModel.safeAcos(v1, v2);
    }

    public static safeAcos(v1: RealVector, v2: RealVector): number {
        let cosine: number = v1.dot(v2) / (v1.length() * v2.length());
        cosine = Math.min(1.0, cosine);
        cosine = Math.max(-1.0, cosine);
        return Math.acos(cosine);
    }

    public getNearbyBall(location: RealVector, tolerance: number): RenderedManifestation {
        for(let index=this.mRendered.iterator();index.hasNext();) {
            let rm = index.next();
            {
                if (rm.getManifestation() != null && (rm.getManifestation().constructor != null && rm.getManifestation().constructor["__interfaces"] != null && rm.getManifestation().constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    const ballLoc: RealVector = rm.getLocation();
                    const distance: number = ballLoc.minus(location).length();
                    if (distance < tolerance)return rm;
                }
            }
        }
        return null;
    }

    public getManifestations(): java.lang.Iterable<Manifestation> {
        return <any>(this.mRendered.stream().map<any>((rm) => rm.getManifestation()).collect<any, any>(java.util.stream.Collectors.toList<any>()));
    }
}
RenderedModel["__class"] = "com.vzome.core.render.RenderedModel";
RenderedModel["__interfaces"] = ["com.vzome.core.model.ManifestationChanges","java.lang.Iterable"];



export namespace RenderedModel {

    export class SymmetryOrbitSource implements OrbitSource {
        /* Default method injected from OrbitSource */
        public getOrientations(rowMajor?: any): number[][] {
            if (((typeof rowMajor === 'boolean') || rowMajor === null)) {
                let __args = arguments;
                if (this.symmetry === undefined) { this.symmetry = null; } 
                if (this.orbits === undefined) { this.orbits = null; } 
                return <any>(() => {
                    const symmetry: Symmetry = this.getSymmetry();
                    const field: AlgebraicField = symmetry.getField();
                    const order: number = symmetry.getChiralOrder();
                    const orientations: number[][] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order);
                    for(let orientation: number = 0; orientation < order; orientation++) {{
                        if (rowMajor){
                            orientations[orientation] = symmetry.getMatrix(orientation).getRowMajorRealElements();
                            continue;
                        }
                        const asFloats: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(16);
                        const transform: AlgebraicMatrix = symmetry.getMatrix(orientation);
                        for(let i: number = 0; i < 3; i++) {{
                            const columnSelect: AlgebraicVector = field.basisVector(3, i);
                            const columnI: AlgebraicVector = transform.timesColumn(columnSelect);
                            const colRV: RealVector = columnI.toRealVector();
                            asFloats[i * 4 + 0] = colRV.x;
                            asFloats[i * 4 + 1] = colRV.y;
                            asFloats[i * 4 + 2] = colRV.z;
                            asFloats[i * 4 + 3] = 0.0;
                        };}
                        asFloats[12] = 0.0;
                        asFloats[13] = 0.0;
                        asFloats[14] = 0.0;
                        asFloats[15] = 1.0;
                        orientations[orientation] = asFloats;
                    };}
                    return orientations;
                })();
            } else if (rowMajor === undefined) {
                return <any>this.getOrientations$();
            } else throw new Error('invalid overload');
        }
        /* Default method injected from OrbitSource */
        getOrientations$(): number[][] {
            return this.getOrientations(false);
        }
        /* Default method injected from OrbitSource */
        getZone(orbit: string, orientation: number): Axis {
            return this.getSymmetry().getDirection(orbit).getAxis(Symmetry.PLUS, orientation);
        }
        /* Default method injected from OrbitSource */
        getEmbedding(): number[] {
            const symmetry: Symmetry = this.getSymmetry();
            const field: AlgebraicField = symmetry.getField();
            const embedding: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(16);
            for(let i: number = 0; i < 3; i++) {{
                const columnSelect: AlgebraicVector = field.basisVector(3, i);
                const colRV: RealVector = symmetry.embedInR3(columnSelect);
                embedding[i * 4 + 0] = colRV.x;
                embedding[i * 4 + 1] = colRV.y;
                embedding[i * 4 + 2] = colRV.z;
                embedding[i * 4 + 3] = 0.0;
            };}
            embedding[12] = 0.0;
            embedding[13] = 0.0;
            embedding[14] = 0.0;
            embedding[15] = 1.0;
            return embedding;
        }
        symmetry: Symmetry;

        orbits: OrbitSet;

        constructor(symmetry: Symmetry) {
            if (this.symmetry === undefined) { this.symmetry = null; }
            if (this.orbits === undefined) { this.orbits = null; }
            this.symmetry = symmetry;
            this.orbits = new OrbitSet(symmetry);
        }

        /**
         * 
         * @param {Direction} orbit
         * @return {Color}
         */
        public getColor(orbit: Direction): Color {
            return new Color(128, 123, 128);
        }

        /**
         * 
         * @param {AlgebraicVector} vector
         * @return {Axis}
         */
        public getAxis(vector: AlgebraicVector): Axis {
            return this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](vector);
        }

        /**
         * 
         * @return {OrbitSet}
         */
        public getOrbits(): OrbitSet {
            return this.orbits;
        }

        /**
         * 
         * @return {*}
         */
        public getShapes(): Shapes {
            return null;
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
         * @param {AlgebraicVector} vector
         * @return {Color}
         */
        public getVectorColor(vector: AlgebraicVector): Color {
            return null;
        }

        /**
         * 
         * @return {string}
         */
        public getName(): string {
            return null;
        }
    }
    SymmetryOrbitSource["__class"] = "com.vzome.core.render.RenderedModel.SymmetryOrbitSource";
    SymmetryOrbitSource["__interfaces"] = ["com.vzome.core.editor.api.OrbitSource"];


}
