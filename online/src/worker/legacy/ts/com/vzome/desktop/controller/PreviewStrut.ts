/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class PreviewStrut implements java.beans.PropertyChangeListener {
        /*private*/ model: com.vzome.core.model.RealizedModelImpl;

        /*private*/ editorModel: com.vzome.core.editor.api.EditorModel;

        /*private*/ rendering: com.vzome.core.render.RenderedModel;

        /*private*/ zoneBall: com.vzome.desktop.controller.ZoneVectorBall;

        /*private*/ context: com.vzome.core.editor.api.Context;

        /*private*/ point: com.vzome.core.construction.Point;

        /*private*/ zone: com.vzome.core.math.symmetry.Axis;

        /*private*/ symmetryController: com.vzome.desktop.controller.SymmetryController;

        /*private*/ length: com.vzome.desktop.controller.LengthController;

        /*private*/ strut: com.vzome.core.edits.StrutCreation;

        /*private*/ workingPlaneDual: number[];

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (PreviewStrut.logger == null) { PreviewStrut.logger = java.util.logging.Logger.getLogger("org.vorthmann.zome.app.impl.PreviewStrut"); }  return PreviewStrut.logger; }

        public constructor(field: com.vzome.core.algebra.AlgebraicField, mainScene: com.vzome.core.render.RenderingChanges, context: com.vzome.core.editor.api.Context) {
            if (this.model === undefined) { this.model = null; }
            if (this.editorModel === undefined) { this.editorModel = null; }
            if (this.rendering === undefined) { this.rendering = null; }
            if (this.zoneBall === undefined) { this.zoneBall = null; }
            if (this.context === undefined) { this.context = null; }
            if (this.point === undefined) { this.point = null; }
            if (this.zone === undefined) { this.zone = null; }
            if (this.symmetryController === undefined) { this.symmetryController = null; }
            if (this.length === undefined) { this.length = null; }
            if (this.strut === undefined) { this.strut = null; }
            this.workingPlaneDual = null;
            this.context = context;
            this.rendering = new com.vzome.core.render.RenderedModel(field, null);
            const transp: com.vzome.core.render.TransparentRendering = new com.vzome.core.render.TransparentRendering(mainScene);
            this.rendering.addListener(transp);
            this.model = new com.vzome.core.model.RealizedModelImpl(field, new com.vzome.core.math.Projection.Default(field));
            this.model.addListener(this.rendering);
            this.editorModel = new PreviewStrut.PreviewStrut$0(this);
            this.zoneBall = new PreviewStrut.PreviewStrut$1(this);
        }

        /**
         * 
         * @param {*} obj
         * @return {boolean}
         */
        public equals(obj: any): boolean {
            if (this === obj)return true;
            if (obj == null)return false;
            if ((<any>this.constructor) !== (<any>obj.constructor))return false;
            return false;
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            return /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this));
        }

        /*private*/ setZone(zone: com.vzome.core.math.symmetry.Axis) {
            this.zone = zone;
        }

        public startRendering(point: com.vzome.core.construction.Point, workingPlaneAxis: com.vzome.core.algebra.AlgebraicVector, worldEye: com.vzome.core.math.RealVector) {
            this.point = point;
            let orbits: com.vzome.core.math.symmetry.OrbitSet = this.symmetryController.getBuildOrbits();
            if (workingPlaneAxis != null){
                orbits = new com.vzome.core.math.symmetry.PlaneOrbitSet(orbits, workingPlaneAxis);
                const normal: com.vzome.core.math.RealVector = workingPlaneAxis.toRealVector();
                let other: com.vzome.core.math.RealVector = new com.vzome.core.math.RealVector(1.0, 0.0, 0.0);
                let v1: com.vzome.core.math.RealVector = normal.cross(other);
                const len: number = v1.length();
                if (len < 1.0E-4){
                    other = new com.vzome.core.math.RealVector(0.0, 1.0, 0.0);
                    v1 = normal.cross(other);
                }
                const v2: com.vzome.core.math.RealVector = normal.cross(v1);
                const p: com.vzome.core.math.RealVector = this.point.getLocation().toRealVector();
                const q: com.vzome.core.math.RealVector = p.plus(v1);
                const r: com.vzome.core.math.RealVector = p.plus(v2);
                const e12: number = (<any>Math).fround((<any>Math).fround(p.x * q.y) - (<any>Math).fround(q.x * p.y));
                const e23: number = (<any>Math).fround((<any>Math).fround(p.y * q.z) - (<any>Math).fround(q.y * p.z));
                const e31: number = (<any>Math).fround((<any>Math).fround(p.z * q.x) - (<any>Math).fround(q.z * p.x));
                const e10: number = (<any>Math).fround(p.x - q.x);
                const e20: number = (<any>Math).fround(p.y - q.y);
                const e30: number = (<any>Math).fround(p.z - q.z);
                const P_e123: number = e12 * r.z + e23 * r.x + e31 * r.y;
                const P_e310: number = e10 * r.z + e31 * 1.0 - e30 * r.x;
                const P_e320: number = e20 * r.z - e30 * r.y - e23 * 1.0;
                const P_e120: number = e12 * 1.0 + e20 * r.x - e10 * r.y;
                this.workingPlaneDual = [0, 0, 0, 0];
                this.workingPlaneDual[0] = -P_e123;
                this.workingPlaneDual[1] = -P_e320;
                this.workingPlaneDual[2] = P_e310;
                this.workingPlaneDual[3] = P_e120;
            }
            this.zone = this.zoneBall.initializeZone(orbits, worldEye);
            if (this.zone == null){
                this.length = null;
                return;
            }
            this.length = <com.vzome.desktop.controller.LengthController><any>this.symmetryController.orbitLengths.get(this.zone.getDirection());
            this.adjustStrut();
            this.length.addPropertyListener(this);
        }

        public finishPreview() {
            if (this.length == null)return;
            this.length.removePropertyListener(this);
            this.strut.undo();
            this.strut = null;
            if (PreviewStrut.logger_$LI$().isLoggable(java.util.logging.Level.FINE))PreviewStrut.logger_$LI$().fine("preview finished at  " + this.zone);
            const params: java.util.Map<string, any> = <any>(new java.util.HashMap<any, any>());
            params.put("anchor", this.point);
            params.put("zone", this.zone);
            params.put("length", this.length.getValue());
            this.context.doEdit("StrutCreation", params);
            this.point = null;
            this.zone = null;
            this.length = null;
            this.workingPlaneDual = null;
        }

        /*private*/ adjustStrut() {
            if (this.strut != null)this.strut.undo();
            if (this.length == null)return;
            if (PreviewStrut.logger_$LI$().isLoggable(java.util.logging.Level.FINER))PreviewStrut.logger_$LI$().finer("preview now " + this.zone);
            this.strut = new com.vzome.core.edits.StrutCreation(this.point, this.zone, this.length.getValue(), this.editorModel);
            this.strut.perform();
        }

        /**
         * 
         * @param {java.beans.PropertyChangeEvent} evt
         */
        public propertyChange(evt: java.beans.PropertyChangeEvent) {
            if ("length" === evt.getPropertyName())this.adjustStrut();
        }

        public setSymmetryController(symmetryController: com.vzome.desktop.controller.SymmetryController) {
            this.symmetryController = symmetryController;
            this.rendering.setOrbitSource(symmetryController.getOrbitSource());
        }

        /*private*/ usingWorkingPlane(): boolean {
            return this.workingPlaneDual != null;
        }

        public trackballRolled(rowMajor: com.vzome.core.math.RealVector[]) {
            if (this.point != null && !this.usingWorkingPlane())this.zoneBall.trackballRolled(rowMajor);
        }

        public workingPlaneDrag(ray: com.vzome.core.math.Line) {
            if (this.usingWorkingPlane()){
                if (this.point == null && PreviewStrut.logger_$LI$().isLoggable(java.util.logging.Level.SEVERE)){
                    PreviewStrut.logger_$LI$().severe("No point during workingPlaneDrag!");
                    return;
                }
                const planeIntersection: com.vzome.core.math.RealVector = this.intersectWorkingPlane(ray);
                const vectorInPlane: com.vzome.core.math.RealVector = planeIntersection.minus(this.point.getLocation().toRealVector());
                const almostPlanarVector: com.vzome.core.math.RealVector = new com.vzome.core.math.RealVector(vectorInPlane.x, vectorInPlane.y, vectorInPlane.z);
                this.zoneBall.setVector(almostPlanarVector);
            }
        }

        /*private*/ intersectWorkingPlane(ray: com.vzome.core.math.Line): com.vzome.core.math.RealVector {
            const s: com.vzome.core.math.RealVector = ray.getOrigin();
            const t: com.vzome.core.math.RealVector = s.plus(ray.getDirection());
            const e12: number = (<any>Math).fround((<any>Math).fround(s.x * t.y) - (<any>Math).fround(t.x * s.y));
            const e23: number = (<any>Math).fround((<any>Math).fround(s.y * t.z) - (<any>Math).fround(t.y * s.z));
            const e31: number = (<any>Math).fround((<any>Math).fround(s.z * t.x) - (<any>Math).fround(t.z * s.x));
            const e10: number = (<any>Math).fround(s.x - t.x);
            const e20: number = (<any>Math).fround(s.y - t.y);
            const e30: number = (<any>Math).fround(s.z - t.z);
            const x_e1: number = -this.workingPlaneDual[2] * e12 - this.workingPlaneDual[0] * e10 + this.workingPlaneDual[3] * e31;
            const x_e2: number = -this.workingPlaneDual[3] * e23 - this.workingPlaneDual[0] * e20 + this.workingPlaneDual[1] * e12;
            const x_e3: number = -this.workingPlaneDual[1] * e31 - this.workingPlaneDual[0] * e30 + this.workingPlaneDual[2] * e23;
            const x_e0: number = this.workingPlaneDual[1] * e10 + this.workingPlaneDual[2] * e20 + this.workingPlaneDual[3] * e30;
            return new com.vzome.core.math.RealVector(x_e1 / x_e0, x_e2 / x_e0, x_e3 / x_e0);
        }

        public getLengthController(): com.vzome.desktop.controller.LengthController {
            return this.length;
        }
    }
    PreviewStrut["__class"] = "com.vzome.desktop.controller.PreviewStrut";
    PreviewStrut["__interfaces"] = ["java.util.EventListener","java.beans.PropertyChangeListener"];



    export namespace PreviewStrut {

        export class PreviewStrut$0 implements com.vzome.core.editor.api.EditorModel {
            public __parent: any;
            /**
             * 
             * @return {*}
             */
            public getRealizedModel(): com.vzome.core.model.RealizedModel {
                return this.__parent.model;
            }

            /**
             * 
             * @return {*}
             */
            public getSelection(): com.vzome.core.editor.api.Selection {
                return null;
            }

            public getSymmetrySystem$(): com.vzome.core.editor.api.OrbitSource {
                return null;
            }

            public getSymmetrySystem$java_lang_String(name: string): com.vzome.core.editor.api.OrbitSource {
                return null;
            }

            /**
             * 
             * @param {string} name
             * @return {*}
             */
            public getSymmetrySystem(name?: any): com.vzome.core.editor.api.OrbitSource {
                if (((typeof name === 'string') || name === null)) {
                    return <any>this.getSymmetrySystem$java_lang_String(name);
                } else if (name === undefined) {
                    return <any>this.getSymmetrySystem$();
                } else throw new Error('invalid overload');
            }

            /**
             * 
             * @return {*}
             */
            public get4dSymmetries(): com.vzome.core.math.symmetry.Symmetries4D {
                return null;
            }

            /**
             * 
             * @param {*} listener
             */
            public addSelectionSummaryListener(listener: com.vzome.core.editor.SelectionSummary.Listener) {
            }

            constructor(__parent: any) {
                this.__parent = __parent;
            }
        }
        PreviewStrut$0["__interfaces"] = ["com.vzome.core.editor.api.EditorModel","com.vzome.core.editor.api.SymmetryAware"];



        export class PreviewStrut$1 extends com.vzome.desktop.controller.ZoneVectorBall {
            public __parent: any;
            /**
             * 
             * @param {com.vzome.core.math.symmetry.Axis} oldZone
             * @param {com.vzome.core.math.symmetry.Axis} newZone
             */
            zoneChanged(oldZone: com.vzome.core.math.symmetry.Axis, newZone: com.vzome.core.math.symmetry.Axis) {
                if (this.__parent.length != null)this.__parent.length.removePropertyListener(this.__parent);
                this.__parent.setZone(newZone);
                if (newZone == null)return;
                this.__parent.length = <com.vzome.desktop.controller.LengthController><any>this.__parent.symmetryController.orbitLengths.get(newZone.getDirection());
                this.__parent.adjustStrut();
                this.__parent.length.addPropertyListener(this.__parent);
            }

            constructor(__parent: any) {
                super();
                this.__parent = __parent;
            }
        }

    }

}

