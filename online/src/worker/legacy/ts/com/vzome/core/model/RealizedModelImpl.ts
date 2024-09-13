/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    /**
     * @author Scott Vorthmann
     * @param {*} field
     * @param {*} projection
     * @class
     */
    export class RealizedModelImpl implements com.vzome.core.model.RealizedModel {
        /*private*/ mListeners: java.util.List<com.vzome.core.model.ManifestationChanges>;

        /*private*/ mManifestations: java.util.HashMap<string, com.vzome.core.model.Manifestation>;

        /*private*/ mProjection: com.vzome.core.math.Projection;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        public constructor(field: com.vzome.core.algebra.AlgebraicField, projection: com.vzome.core.math.Projection) {
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

        public moreVisibleThan(other: RealizedModelImpl): java.util.Set<com.vzome.core.model.Manifestation> {
            const result: java.util.Set<com.vzome.core.model.Manifestation> = <any>(new java.util.HashSet<any>());
            for(let index=this.mManifestations.values().iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man.isHidden())continue;
                    const doppel: com.vzome.core.model.Manifestation = other.mManifestations.get(man.toConstruction().getSignature());
                    if (doppel == null || doppel.isHidden())result.add(man);
                }
            }
            return result;
        }

        public addListener(l: com.vzome.core.model.ManifestationChanges) {
            this.mListeners.add(l);
        }

        public removeListener(l: com.vzome.core.model.ManifestationChanges) {
            this.mListeners.remove(l);
        }

        /**
         * 
         * @return {*}
         */
        public iterator(): java.util.Iterator<com.vzome.core.model.Manifestation> {
            return this.mManifestations.values().iterator();
        }

        public manifest(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation {
            let m: com.vzome.core.model.Manifestation = null;
            if (c != null && c instanceof <any>com.vzome.core.construction.Point){
                const p: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>c;
                m = new com.vzome.core.model.ConnectorImpl(this.mProjection.projectImage(p.getLocation(), true));
            } else if (c != null && c instanceof <any>com.vzome.core.construction.Segment){
                const s: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>c;
                const start: com.vzome.core.algebra.AlgebraicVector = this.mProjection.projectImage(s.getStart(), true);
                const end: com.vzome.core.algebra.AlgebraicVector = this.mProjection.projectImage(s.getEnd(), true);
                if (!start.equals(end)){
                    m = new com.vzome.core.model.StrutImpl(start, end);
                }
            } else if (c != null && c instanceof <any>com.vzome.core.construction.Polygon){
                const p: com.vzome.core.construction.Polygon = <com.vzome.core.construction.Polygon>c;
                const vertices: java.util.List<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>());
                for(let i: number = 0; i < p.getVertexCount(); i++) {{
                    vertices.add(this.mProjection.projectImage(p.getVertex(i), true));
                };}
                m = new com.vzome.core.model.PanelImpl(vertices);
            }
            return m;
        }

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (RealizedModelImpl.logger == null) { RealizedModelImpl.logger = java.util.logging.Logger.getLogger("com.vzome.core.model"); }  return RealizedModelImpl.logger; }

        /**
         * 
         * @param {*} m
         */
        public add(m: com.vzome.core.model.Manifestation) {
            const key: string = m.toConstruction().getSignature();
            this.mManifestations.put(key, m);
            if (RealizedModelImpl.logger_$LI$().isLoggable(java.util.logging.Level.FINER))RealizedModelImpl.logger_$LI$().finer("add manifestation: " + m.toString());
        }

        /**
         * 
         * @param {*} m
         */
        public remove(m: com.vzome.core.model.Manifestation) {
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
        public show(m: com.vzome.core.model.Manifestation) {
            if (this.doingBatch){
                if (this.removals.contains(m))this.removals.remove(m); else this.additions.add(m);
            } else this.privateShow(m);
        }

        /*private*/ privateShow(m: com.vzome.core.model.Manifestation) {
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
        public hide(m: com.vzome.core.model.Manifestation) {
            if (this.doingBatch){
                if (this.additions.contains(m))this.additions.remove(m); else this.removals.add(m);
            } else this.privateHide(m);
        }

        /*private*/ privateHide(m: com.vzome.core.model.Manifestation) {
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
         * @param {com.vzome.core.construction.Color} color
         */
        public setColor(m: com.vzome.core.model.Manifestation, color: com.vzome.core.construction.Color) {
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
        public setLabel(m: com.vzome.core.model.Manifestation, label: string) {
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
         * @param {com.vzome.core.construction.Construction} c
         * @return {*}
         */
        public findConstruction(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation {
            let actualMan: com.vzome.core.model.Manifestation = this.mManifestations.get(c.getSignature());
            if (actualMan == null)actualMan = this.manifest(c);
            return actualMan;
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} c
         * @return {*}
         */
        public removeConstruction(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation {
            const actualMan: com.vzome.core.model.Manifestation = this.mManifestations.get(c.getSignature());
            if (actualMan == null)return null;
            return this.manifest(c);
        }

        /**
         * @param {com.vzome.core.construction.Construction} c
         * @return
         * @return {*}
         */
        public getManifestation(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation {
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
            if (!(object != null && object instanceof <any>com.vzome.core.model.RealizedModelImpl))return false;
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

        /*private*/ additions: java.util.Set<com.vzome.core.model.Manifestation>;

        /*private*/ removals: java.util.Set<com.vzome.core.model.Manifestation>;

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
        public getField(): com.vzome.core.algebra.AlgebraicField {
            return this.field;
        }

        /**
         * This records the NEW manifestations produced by manifestConstruction for this edit,
         * to avoid creating colliding manifestations.
         */
        /*private*/ mManifestedNow: java.util.Map<string, com.vzome.core.model.Manifestation>;

        /**
         * 
         * @param {string} signature
         * @return {*}
         */
        public findPerEditManifestation(signature: string): com.vzome.core.model.Manifestation {
            return this.mManifestedNow.get(signature);
        }

        /**
         * 
         * @param {string} signature
         * @param {*} m
         */
        public addPerEditManifestation(signature: string, m: com.vzome.core.model.Manifestation) {
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


}

