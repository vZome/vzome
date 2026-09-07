import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Color } from "../construction/Color.js";
import { Construction } from "../construction/Construction.js";
import { Point } from "../construction/Point.js";
import { Polygon } from "../construction/Polygon.js";
import { Segment } from "../construction/Segment.js";
import { Projection } from "../math/Projection.js";
import { ConnectorImpl } from "./ConnectorImpl.js";
import { Manifestation } from "./Manifestation.js";
import { ManifestationChanges } from "./ManifestationChanges.js";
import { PanelImpl } from "./PanelImpl.js";
import { RealizedModel } from "./RealizedModel.js";
import { StrutImpl } from "./StrutImpl.js";

/**
 * @author Scott Vorthmann
 * @param {*} field
 * @param {*} projection
 * @class
 */
export class RealizedModelImpl implements RealizedModel {
    /*private*/ mListeners: java.util.List<ManifestationChanges>;

    /*private*/ mManifestations: java.util.HashMap<string, Manifestation>;

    /*private*/ mProjection: Projection;

    /*private*/ field: AlgebraicField;

    public constructor(field: AlgebraicField, projection: Projection) {
        this.mListeners = <any>(new java.util.ArrayList<any>(1));
        this.mManifestations = <any>(new java.util.LinkedHashMap<any, any>(1000));
        if (this.mProjection === undefined) { this.mProjection = null; }
        if (this.field === undefined) { this.field = null; }
        this.doingBatch = false;
        this.additions = <any>(new java.util.HashSet<any>());
        this.removals = <any>(new java.util.HashSet<any>());
        if (this.mManifestedNow === undefined) { this.mManifestedNow = null; }
        this.field = field;
        this.mProjection = projection;
    }

    public moreVisibleThan(other: RealizedModelImpl): java.util.Set<Manifestation> {
        const result: java.util.Set<Manifestation> = <any>(new java.util.HashSet<any>());
        for(let index=this.mManifestations.values().iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man.isHidden())continue;
                const doppel: Manifestation = other.mManifestations.get(man.toConstruction().getSignature());
                if (doppel == null || doppel.isHidden())result.add(man);
            }
        }
        return result;
    }

    public addListener(l: ManifestationChanges) {
        this.mListeners.add(l);
    }

    public removeListener(l: ManifestationChanges) {
        this.mListeners.remove(l);
    }

    /**
     * 
     * @return {*}
     */
    public iterator(): java.util.Iterator<Manifestation> {
        return this.mManifestations.values().iterator();
    }

    public manifest(c: Construction): Manifestation {
        let m: Manifestation = null;
        if (c != null && c instanceof <any>Point){
            const p: Point = <Point>c;
            m = new ConnectorImpl(this.mProjection.projectImage(p.getLocation(), true));
        } else if (c != null && c instanceof <any>Segment){
            const s: Segment = <Segment>c;
            const start: AlgebraicVector = this.mProjection.projectImage(s.getStart(), true);
            const end: AlgebraicVector = this.mProjection.projectImage(s.getEnd(), true);
            if (!start.equals(end)){
                m = new StrutImpl(start, end);
            }
        } else if (c != null && c instanceof <any>Polygon){
            const p: Polygon = <Polygon>c;
            const vertices: java.util.List<AlgebraicVector> = <any>(new java.util.ArrayList<any>());
            for(let i: number = 0; i < p.getVertexCount(); i++) {{
                vertices.add(this.mProjection.projectImage(p.getVertex(i), true));
            };}
            m = new PanelImpl(vertices);
        }
        return m;
    }

    static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (RealizedModelImpl.logger == null) { RealizedModelImpl.logger = java.util.logging.Logger.getLogger("com.vzome.core.model"); }  return RealizedModelImpl.logger; }

    /**
     * 
     * @param {*} m
     */
    public add(m: Manifestation) {
        const key: string = m.toConstruction().getSignature();
        this.mManifestations.put(key, m);
        if (RealizedModelImpl.logger_$LI$().isLoggable(java.util.logging.Level.FINER))RealizedModelImpl.logger_$LI$().finer("add manifestation: " + m.toString());
    }

    /**
     * 
     * @param {*} m
     */
    public remove(m: Manifestation) {
        const key: string = m.toConstruction().getSignature();
        this.mManifestations.remove(key);
        if (RealizedModelImpl.logger_$LI$().isLoggable(java.util.logging.Level.FINER))RealizedModelImpl.logger_$LI$().finer("remove manifestation: " + m.toString());
    }

    public refresh(on: boolean, unused: RealizedModelImpl) {
        for(let index=this.mManifestations.values().iterator();index.hasNext();) {
            let man = index.next();
            {
                if (!man.isHidden()){
                    if (on)this.show(man); else this.hide(man);
                }
            }
        }
    }

    /**
     * 
     * @param {*} m
     */
    public show(m: Manifestation) {
        if (this.doingBatch){
            if (this.removals.contains(m))this.removals.remove(m); else this.additions.add(m);
        } else this.privateShow(m);
    }

    /*private*/ privateShow(m: Manifestation) {
        if (!m.isRendered()){
            for(let index=this.mListeners.iterator();index.hasNext();) {
                let next = index.next();
                {
                    next.manifestationAdded(m);
                }
            }
        }
    }

    /**
     * 
     * @param {*} m
     */
    public hide(m: Manifestation) {
        if (this.doingBatch){
            if (this.additions.contains(m))this.additions.remove(m); else this.removals.add(m);
        } else this.privateHide(m);
    }

    /*private*/ privateHide(m: Manifestation) {
        if (m.isRendered()){
            for(let index=this.mListeners.iterator();index.hasNext();) {
                let next = index.next();
                {
                    next.manifestationRemoved(m);
                }
            }
        }
    }

    /**
     * 
     * @param {*} m
     * @param {Color} color
     */
    public setColor(m: Manifestation, color: Color) {
        m.setColor(color);
        if (m.isRendered()){
            for(let index=this.mListeners.iterator();index.hasNext();) {
                let next = index.next();
                {
                    next.manifestationColored(m, color);
                }
            }
        }
    }

    /**
     * 
     * @param {*} m
     * @param {string} label
     */
    public setLabel(m: Manifestation, label: string) {
        m.setLabel(label);
        if (m.isRendered()){
            for(let index=this.mListeners.iterator();index.hasNext();) {
                let next = index.next();
                {
                    next.manifestationLabeled(m, label);
                }
            }
        }
    }

    /**
     * 
     * @param {Construction} c
     * @return {*}
     */
    public findConstruction(c: Construction): Manifestation {
        let actualMan: Manifestation = this.mManifestations.get(c.getSignature());
        if (actualMan == null)actualMan = this.manifest(c);
        return actualMan;
    }

    /**
     * 
     * @param {Construction} c
     * @return {*}
     */
    public removeConstruction(c: Construction): Manifestation {
        const actualMan: Manifestation = this.mManifestations.get(c.getSignature());
        if (actualMan == null)return null;
        return this.manifest(c);
    }

    /**
     * @param {Construction} c
     * @return
     * @return {*}
     */
    public getManifestation(c: Construction): Manifestation {
        return this.mManifestations.get(c.getSignature());
    }

    /**
     * 
     * @return {number}
     */
    public size(): number {
        return this.mManifestations.size();
    }

    /**
     * 
     * @param {*} object
     * @return {boolean}
     */
    public equals(object: any): boolean {
        if (object == null){
            return false;
        }
        if (object === this){
            return true;
        }
        if (!(object != null && object instanceof <any>RealizedModelImpl))return false;
        const that: RealizedModelImpl = <RealizedModelImpl>object;
        if (this.size() !== that.size())return false;
        for(let index=this.mManifestations.values().iterator();index.hasNext();) {
            let man = index.next();
            {
                if (!that.mManifestations.values().contains(man)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 
     * @return {number}
     */
    public hashCode(): number {
        return this.size();
    }

    /*private*/ doingBatch: boolean;

    /*private*/ additions: java.util.Set<Manifestation>;

    /*private*/ removals: java.util.Set<Manifestation>;

    public startBatch() {
        this.additions.clear();
        this.removals.clear();
        this.doingBatch = true;
    }

    public endBatch() {
        for(let index=this.removals.iterator();index.hasNext();) {
            let m = index.next();
            {
                this.privateHide(m);
            }
        }
        for(let index=this.additions.iterator();index.hasNext();) {
            let m = index.next();
            {
                this.privateShow(m);
            }
        }
        this.additions.clear();
        this.removals.clear();
        this.doingBatch = false;
    }

    /**
     * 
     * @return {*}
     */
    public getField(): AlgebraicField {
        return this.field;
    }

    /**
     * This records the NEW manifestations produced by manifestConstruction for this edit,
     * to avoid creating colliding manifestations.
     */
    /*private*/ mManifestedNow: java.util.Map<string, Manifestation>;

    /**
     * 
     * @param {string} signature
     * @return {*}
     */
    public findPerEditManifestation(signature: string): Manifestation {
        return this.mManifestedNow.get(signature);
    }

    /**
     * 
     * @param {string} signature
     * @param {*} m
     */
    public addPerEditManifestation(signature: string, m: Manifestation) {
        this.mManifestedNow.put(signature, m);
    }

    /**
     * 
     */
    public clearPerEditManifestations() {
        this.mManifestedNow = <any>(new java.util.HashMap<any, any>());
    }
}
RealizedModelImpl["__class"] = "com.vzome.core.model.RealizedModelImpl";
RealizedModelImpl["__interfaces"] = ["com.vzome.core.model.RealizedModel","java.lang.Iterable"];
