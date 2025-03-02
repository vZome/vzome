/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.render {
    export class RenderedModel implements com.vzome.core.model.ManifestationChanges, java.lang.Iterable<com.vzome.core.render.RenderedManifestation> {
        mListeners: java.util.List<com.vzome.core.render.RenderingChanges>;

        /*private*/ mPolyhedra: com.vzome.core.editor.api.Shapes;

        /*private*/ mSelectionGlow: number;

        mRendered: java.util.HashSet<com.vzome.core.render.RenderedManifestation>;

        byID: java.util.HashMap<string, com.vzome.core.render.RenderedManifestation>;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ orbitSource: com.vzome.core.editor.api.OrbitSource;

        /*private*/ oneSidedPanels: boolean;

        /*private*/ mainListener: com.vzome.core.render.RenderingChanges;

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

        public getField(): com.vzome.core.algebra.AlgebraicField {
            return this.field;
        }

        public addListener(listener: com.vzome.core.render.RenderingChanges) {
            if (this.mainListener == null)this.mainListener = listener; else this.mListeners.add(listener);
        }

        public removeListener(listener: com.vzome.core.render.RenderingChanges) {
            if (this.mainListener === listener)this.mainListener = null; else this.mListeners.remove(listener);
        }

        public render(manifestation: com.vzome.core.model.Manifestation): com.vzome.core.render.RenderedManifestation {
            const rm: com.vzome.core.render.RenderedManifestation = new com.vzome.core.render.RenderedManifestation(manifestation, this.orbitSource);
            rm.resetAttributes(this.oneSidedPanels, this.colorPanels);
            return rm;
        }

        /**
         * 
         * @param {*} m
         */
        public manifestationAdded(m: com.vzome.core.model.Manifestation) {
            if (!this.enabled){
                m.setRenderedObject(new com.vzome.core.render.RenderedManifestation(m, this.orbitSource));
                return;
            }
            const rm: com.vzome.core.render.RenderedManifestation = this.render(m);
            const poly: com.vzome.core.math.Polyhedron = rm.getShape();
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
        public manifestationRemoved(m: com.vzome.core.model.Manifestation) {
            if (!this.enabled){
                m.setRenderedObject(null);
                return;
            }
            const rendered: com.vzome.core.render.RenderedManifestation = <com.vzome.core.render.RenderedManifestation><any>(<com.vzome.core.model.HasRenderedObject><any>m).getRenderedObject();
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

        public getRenderedManifestation(guid: string): com.vzome.core.render.RenderedManifestation {
            return this.byID.get(guid);
        }

        public setManifestationGlow(m: com.vzome.core.model.Manifestation, on: boolean) {
            const rendered: com.vzome.core.render.RenderedManifestation = <com.vzome.core.render.RenderedManifestation><any>(<com.vzome.core.model.HasRenderedObject><any>m).getRenderedObject();
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

        public setManifestationColor(m: com.vzome.core.model.Manifestation, color: com.vzome.core.construction.Color) {
            const rendered: com.vzome.core.render.RenderedManifestation = <com.vzome.core.render.RenderedManifestation><any>(<com.vzome.core.model.HasRenderedObject><any>m).getRenderedObject();
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

        public setManifestationLabel(m: com.vzome.core.model.Manifestation, label: string) {
            const rendered: com.vzome.core.render.RenderedManifestation = <com.vzome.core.render.RenderedManifestation><any>(<com.vzome.core.model.HasRenderedObject><any>m).getRenderedObject();
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

        public setManifestationTransparency(m: com.vzome.core.model.Manifestation, on: boolean) {
            const rendered: com.vzome.core.render.RenderedManifestation = <com.vzome.core.render.RenderedManifestation><any>(<com.vzome.core.model.HasRenderedObject><any>m).getRenderedObject();
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
        public iterator(): java.util.Iterator<com.vzome.core.render.RenderedManifestation> {
            return this.mRendered.iterator();
        }

        public getOrbitSource(): com.vzome.core.editor.api.OrbitSource {
            return this.orbitSource;
        }

        public setShapes(shapes: com.vzome.core.editor.api.Shapes) {
            const supported: boolean = this.mainListener.shapesChanged(shapes);
            if (!supported)this.setOrbitSource(this.orbitSource);
        }

        public setOrbitSource(orbitSource: com.vzome.core.editor.api.OrbitSource) {
            this.orbitSource = orbitSource;
            this.enabled = true;
            this.mPolyhedra = orbitSource.getShapes();
            if (this.mPolyhedra == null)return;
            {
                const newSet: java.util.HashSet<com.vzome.core.render.RenderedManifestation> = <any>(new java.util.HashSet<any>());
                for(const rms: java.util.Iterator<com.vzome.core.render.RenderedManifestation> = this.mRendered.iterator(); rms.hasNext(); ) {{
                    const rendered: com.vzome.core.render.RenderedManifestation = rms.next();
                    rms.remove();
                    const m: com.vzome.core.model.Manifestation = rendered.getManifestation();
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
         * @param {com.vzome.core.construction.Color} color
         */
        public manifestationColored(m: com.vzome.core.model.Manifestation, color: com.vzome.core.construction.Color) {
            if (this.enabled)this.setManifestationColor(m, color);
        }

        /**
         * 
         * @param {*} m
         * @param {string} label
         */
        public manifestationLabeled(m: com.vzome.core.model.Manifestation, label: string) {
            if (this.enabled)this.setManifestationLabel(m, label);
        }

        public snapshot(): RenderedModel {
            const snapshot: RenderedModel = new RenderedModel(this.orbitSource.getSymmetry());
            for(let index=this.mRendered.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const copy: com.vzome.core.render.RenderedManifestation = rm.copy();
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
         * @param {com.vzome.core.render.RenderedModel} from is an empty RenderedModel in some cases
         * @param {com.vzome.core.render.RenderedModel} to
         * @param {*} changes is a scene graph
         */
        public static renderChange(from: RenderedModel, to: RenderedModel, changes: com.vzome.core.render.RenderingChanges) {
            const toRemove: java.util.HashSet<com.vzome.core.render.RenderedManifestation> = <any>(new java.util.HashSet<any>(from.mRendered));
            toRemove.removeAll(to.mRendered);
            for(let index=toRemove.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    changes.manifestationRemoved(rm);
                }
            }
            const toAdd: java.util.HashSet<com.vzome.core.render.RenderedManifestation> = <any>(new java.util.HashSet<any>(to.mRendered));
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
                                const fromColor: com.vzome.core.construction.Color = fromRm.getColor();
                                const toColor: com.vzome.core.construction.Color = toRm.getColor();
                                if (fromColor == null && toColor == null)continue;
                                if ((fromColor == null && toColor != null) || (fromColor != null && toColor == null) || !fromColor.equals(toColor))changes.colorChanged(toRm);
                            }
                        }
                    }
                }
            }
        }

        public renderVector(av: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.RealVector {
            if (av != null)return this.getEmbedding().embedInR3(av); else return new com.vzome.core.math.RealVector(0.0, 0.0, 0.0);
        }

        public renderVectorDouble(av: com.vzome.core.algebra.AlgebraicVector): number[] {
            if (av != null)return this.getEmbedding().embedInR3Double(av); else return [0.0, 0.0, 0.0];
        }

        public getEmbedding(): com.vzome.core.math.symmetry.Embedding {
            return this.orbitSource.getSymmetry();
        }

        public measureDistanceCm(c1: com.vzome.core.model.Connector, c2: com.vzome.core.model.Connector): number {
            return RenderedModel.measureLengthCm(this.renderVector(c1.getLocation().minus(c2.getLocation())));
        }

        public static measureLengthCm(rv: com.vzome.core.math.RealVector): number {
            return rv.length() * com.vzome.core.render.RealZomeScaling.RZOME_CM_SCALING;
        }

        public measureLengthCm(strut: com.vzome.core.model.Strut): number {
            return RenderedModel.measureLengthCm(this.renderVector(strut.getOffset()));
        }

        public measureDihedralAngle(p1: com.vzome.core.model.Panel, p2: com.vzome.core.model.Panel): number {
            const v1: com.vzome.core.math.RealVector = p1['getNormal$com_vzome_core_math_symmetry_Embedding'](this.getEmbedding());
            const v2: com.vzome.core.math.RealVector = p2['getNormal$com_vzome_core_math_symmetry_Embedding'](this.getEmbedding());
            return RenderedModel.safeAcos(v1, v2);
        }

        public measureAngle(s1: com.vzome.core.model.Strut, s2: com.vzome.core.model.Strut): number {
            const v1: com.vzome.core.math.RealVector = this.renderVector(s1.getOffset());
            const v2: com.vzome.core.math.RealVector = this.renderVector(s2.getOffset());
            return RenderedModel.safeAcos(v1, v2);
        }

        public static safeAcos(v1: com.vzome.core.math.RealVector, v2: com.vzome.core.math.RealVector): number {
            let cosine: number = v1.dot(v2) / (v1.length() * v2.length());
            cosine = Math.min(1.0, cosine);
            cosine = Math.max(-1.0, cosine);
            return Math.acos(cosine);
        }

        public getNearbyBall(location: com.vzome.core.math.RealVector, tolerance: number): com.vzome.core.render.RenderedManifestation {
            for(let index=this.mRendered.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    if (rm.getManifestation() != null && (rm.getManifestation().constructor != null && rm.getManifestation().constructor["__interfaces"] != null && rm.getManifestation().constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        const ballLoc: com.vzome.core.math.RealVector = rm.getLocation();
                        const distance: number = ballLoc.minus(location).length();
                        if (distance < tolerance)return rm;
                    }
                }
            }
            return null;
        }

        public getManifestations(): java.lang.Iterable<com.vzome.core.model.Manifestation> {
            return <any>(this.mRendered.stream().map<any>((rm) => rm.getManifestation()).collect<any, any>(java.util.stream.Collectors.toList<any>()));
        }
    }
    RenderedModel["__class"] = "com.vzome.core.render.RenderedModel";
    RenderedModel["__interfaces"] = ["com.vzome.core.model.ManifestationChanges","java.lang.Iterable"];



    export namespace RenderedModel {

        export class SymmetryOrbitSource implements com.vzome.core.editor.api.OrbitSource {
            /* Default method injected from com.vzome.core.editor.api.OrbitSource */
            getZone(orbit: string, orientation: number): com.vzome.core.math.symmetry.Axis {
                return this.getSymmetry().getDirection(orbit).getAxis(com.vzome.core.math.symmetry.Symmetry.PLUS, orientation);
            }
            /* Default method injected from com.vzome.core.editor.api.OrbitSource */
            getOrientations$(): number[][] {
                return this.getOrientations(false);
            }
            /* Default method injected from com.vzome.core.editor.api.OrbitSource */
            public getOrientations(rowMajor?: any): number[][] {
                if (((typeof rowMajor === 'boolean') || rowMajor === null)) {
                    let __args = arguments;
                    if (this.symmetry === undefined) { this.symmetry = null; } 
                    if (this.orbits === undefined) { this.orbits = null; } 
                    return <any>(() => {
                        const symmetry: com.vzome.core.math.symmetry.Symmetry = this.getSymmetry();
                        const field: com.vzome.core.algebra.AlgebraicField = symmetry.getField();
                        const order: number = symmetry.getChiralOrder();
                        const orientations: number[][] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(order);
                        for(let orientation: number = 0; orientation < order; orientation++) {{
                            if (rowMajor){
                                orientations[orientation] = symmetry.getMatrix(orientation).getRowMajorRealElements();
                                continue;
                            }
                            const asFloats: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(16);
                            const transform: com.vzome.core.algebra.AlgebraicMatrix = symmetry.getMatrix(orientation);
                            for(let i: number = 0; i < 3; i++) {{
                                const columnSelect: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, i);
                                const columnI: com.vzome.core.algebra.AlgebraicVector = transform.timesColumn(columnSelect);
                                const colRV: com.vzome.core.math.RealVector = columnI.toRealVector();
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
            /* Default method injected from com.vzome.core.editor.api.OrbitSource */
            getEmbedding(): number[] {
                const symmetry: com.vzome.core.math.symmetry.Symmetry = this.getSymmetry();
                const field: com.vzome.core.algebra.AlgebraicField = symmetry.getField();
                const embedding: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(16);
                for(let i: number = 0; i < 3; i++) {{
                    const columnSelect: com.vzome.core.algebra.AlgebraicVector = field.basisVector(3, i);
                    const colRV: com.vzome.core.math.RealVector = symmetry.embedInR3(columnSelect);
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
            symmetry: com.vzome.core.math.symmetry.Symmetry;

            orbits: com.vzome.core.math.symmetry.OrbitSet;

            constructor(symmetry: com.vzome.core.math.symmetry.Symmetry) {
                if (this.symmetry === undefined) { this.symmetry = null; }
                if (this.orbits === undefined) { this.orbits = null; }
                this.symmetry = symmetry;
                this.orbits = new com.vzome.core.math.symmetry.OrbitSet(symmetry);
            }

            /**
             * 
             * @param {com.vzome.core.math.symmetry.Direction} orbit
             * @return {com.vzome.core.construction.Color}
             */
            public getColor(orbit: com.vzome.core.math.symmetry.Direction): com.vzome.core.construction.Color {
                return new com.vzome.core.construction.Color(128, 123, 128);
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} vector
             * @return {com.vzome.core.math.symmetry.Axis}
             */
            public getAxis(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.symmetry.Axis {
                return this.symmetry['getAxis$com_vzome_core_algebra_AlgebraicVector'](vector);
            }

            /**
             * 
             * @return {com.vzome.core.math.symmetry.OrbitSet}
             */
            public getOrbits(): com.vzome.core.math.symmetry.OrbitSet {
                return this.orbits;
            }

            /**
             * 
             * @return {*}
             */
            public getShapes(): com.vzome.core.editor.api.Shapes {
                return null;
            }

            /**
             * 
             * @return {*}
             */
            public getSymmetry(): com.vzome.core.math.symmetry.Symmetry {
                return this.symmetry;
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} vector
             * @return {com.vzome.core.construction.Color}
             */
            public getVectorColor(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.construction.Color {
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

}

