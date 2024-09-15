/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.render {
    /**
     * @author Scott Vorthmann
     * @param {*} m
     * @param {*} orbitSource
     * @class
     */
    export class RenderedManifestation implements com.vzome.core.model.RenderedObject {
        /*private*/ mManifestation: com.vzome.core.model.Manifestation;

        /*private*/ mShape: com.vzome.core.math.Polyhedron;

        /*private*/ color: com.vzome.core.construction.Color;

        /*private*/ mOrientation: com.vzome.core.algebra.AlgebraicMatrix;

        /*private*/ mGlow: number;

        /*private*/ mTransparency: number;

        /*private*/ mGraphicsObject: any;

        /*private*/ mPickable: boolean;

        /*private*/ isOffset: boolean;

        /*private*/ location: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ fixedLocation: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ strutZone: number;

        /*private*/ strutLength: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ strutOrbit: com.vzome.core.math.symmetry.Direction;

        /*private*/ strutSense: number;

        /*private*/ guid: java.util.UUID;

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (RenderedManifestation.logger == null) { RenderedManifestation.logger = java.util.logging.Logger.getLogger("com.vzome.core.render.RenderedManifestation"); }  return RenderedManifestation.logger; }

        /*private*/ orbitSource: com.vzome.core.editor.api.OrbitSource;

        /*private*/ label: string;

        public constructor(m: com.vzome.core.model.Manifestation, orbitSource: com.vzome.core.editor.api.OrbitSource) {
            if (this.mManifestation === undefined) { this.mManifestation = null; }
            if (this.mShape === undefined) { this.mShape = null; }
            this.color = null;
            if (this.mOrientation === undefined) { this.mOrientation = null; }
            this.mGlow = 0.0;
            this.mTransparency = 0.0;
            if (this.mGraphicsObject === undefined) { this.mGraphicsObject = null; }
            this.mPickable = true;
            this.isOffset = false;
            if (this.location === undefined) { this.location = null; }
            if (this.fixedLocation === undefined) { this.fixedLocation = null; }
            this.strutZone = -1;
            this.strutLength = null;
            this.strutOrbit = null;
            if (this.strutSense === undefined) { this.strutSense = 0; }
            this.guid = java.util.UUID.randomUUID();
            if (this.orbitSource === undefined) { this.orbitSource = null; }
            if (this.label === undefined) { this.label = null; }
            this.mManifestation = m;
            this.orbitSource = orbitSource;
            if (m != null)this.location = m.getLocation();
            this.fixedLocation = this.location;
            this.mOrientation = null;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return this.mManifestation.toString();
        }

        public getGuid(): java.util.UUID {
            return this.guid;
        }

        public setGraphicsObject(go: any) {
            this.mGraphicsObject = go;
        }

        public getGraphicsObject(): any {
            return this.mGraphicsObject;
        }

        public setGlow(glow: number) {
            this.mGlow = glow;
        }

        public getGlow(): number {
            return this.mGlow;
        }

        public setTransparency(trans: number) {
            this.mTransparency = trans;
        }

        public getTransparency(): number {
            return this.mTransparency;
        }

        public getShapeId(): java.util.UUID {
            return this.mShape.getGuid();
        }

        public getShape(): com.vzome.core.math.Polyhedron {
            return this.mShape;
        }

        public setPickable(value: boolean) {
            this.mPickable = value;
        }

        public isPickable(): boolean {
            return this.mPickable;
        }

        public getManifestation(): com.vzome.core.model.Manifestation {
            return this.mManifestation;
        }

        public getColor(): com.vzome.core.construction.Color {
            return this.color;
        }

        public getColorWeb(): string {
            return this.color.toWebString();
        }

        public setColor(color: com.vzome.core.construction.Color) {
            this.color = color;
        }

        public setOrientation(m: com.vzome.core.algebra.AlgebraicMatrix) {
            this.mOrientation = m;
        }

        public getOrientation(): com.vzome.core.algebra.AlgebraicMatrix {
            return this.mOrientation;
        }

        public getLocation(): com.vzome.core.math.RealVector {
            if (this.location != null)return this.getEmbedding().embedInR3(this.location); else return new com.vzome.core.math.RealVector(0.0, 0.0, 0.0);
        }

        public getLocationAV(): com.vzome.core.algebra.AlgebraicVector {
            return this.location;
        }

        public getEmbedding(): com.vzome.core.math.symmetry.Embedding {
            return this.orbitSource.getSymmetry();
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            return /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.guid));
        }

        /**
         * 
         * @param {*} obj
         * @return {boolean}
         */
        public equals(obj: any): boolean {
            if (this === obj){
                return true;
            }
            if (obj == null){
                return false;
            }
            if ((<any>this.constructor) !== (<any>obj.constructor)){
                return false;
            }
            const other: RenderedManifestation = <RenderedManifestation>obj;
            if (this.fixedLocation == null){
                if (other.fixedLocation != null){
                    return false;
                }
            } else if (!this.fixedLocation.equals(other.fixedLocation)){
                return false;
            }
            if (this.isOffset !== other.isOffset){
                return false;
            }
            if (this.mOrientation == null){
                if (other.mOrientation != null){
                    return false;
                }
            } else if (!this.mOrientation.equals(other.mOrientation)){
                return false;
            }
            if (this.mShape == null){
                if (other.mShape != null){
                    return false;
                }
            } else if (!this.mShape.equals(other.mShape)){
                return false;
            }
            if (this.strutSense !== other.strutSense){
                return false;
            }
            return true;
        }

        public copy(): RenderedManifestation {
            const copy: RenderedManifestation = new RenderedManifestation(null, this.orbitSource);
            copy.location = this.location;
            copy.fixedLocation = this.fixedLocation;
            copy.color = this.color;
            copy.mGlow = this.mGlow;
            copy.mOrientation = this.mOrientation;
            copy.mShape = this.mShape;
            copy.mTransparency = this.mTransparency;
            copy.strutLength = this.strutLength;
            copy.strutZone = this.strutZone;
            copy.label = this.label;
            return copy;
        }

        public setStrut(orbit: com.vzome.core.math.symmetry.Direction, zone: number, sense: number, length: com.vzome.core.algebra.AlgebraicNumber) {
            this.strutOrbit = orbit;
            this.strutZone = zone;
            this.strutSense = sense;
            this.strutLength = length;
        }

        public getStrutZone(): number {
            return this.strutZone;
        }

        public getStrutSense(): number {
            return this.strutSense;
        }

        public getStrutLength(): com.vzome.core.algebra.AlgebraicNumber {
            return this.strutLength;
        }

        public getStrutOrbit(): com.vzome.core.math.symmetry.Direction {
            return this.strutOrbit;
        }

        offsetLocation() {
            if (this.mManifestation != null){
                const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>this.mManifestation;
                this.location = strut.getEnd();
                this.isOffset = true;
            }
        }

        resetLocation() {
            if (this.mManifestation != null){
                this.location = this.mManifestation.getLocation();
                this.isOffset = false;
            }
        }

        public getSymmetryShapes(): string {
            return this.orbitSource.getName() + ":" + this.orbitSource.getShapes().getName();
        }

        public resetAttributes(oneSidedPanels: boolean, colorPanels: boolean) {
            const label: string = this.mManifestation.getLabel();
            if (null != label){
                this.setLabel(label);
            }
            if (this.mManifestation != null && (this.mManifestation.constructor != null && this.mManifestation.constructor["__interfaces"] != null && this.mManifestation.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                this.resetPanelAttributes(oneSidedPanels, colorPanels);
            } else if (this.orbitSource.getShapes() == null){
                return;
            } else if (this.mManifestation != null && (this.mManifestation.constructor != null && this.mManifestation.constructor["__interfaces"] != null && this.mManifestation.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                this.resetConnectorAttributes(<com.vzome.core.model.Connector><any>this.mManifestation);
            } else if (this.mManifestation != null && (this.mManifestation.constructor != null && this.mManifestation.constructor["__interfaces"] != null && this.mManifestation.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>this.mManifestation;
                this.resetStrutAttributes(strut);
            } else throw new java.lang.UnsupportedOperationException("only strut, ball, and panel shapes currently supported");
        }

        /*private*/ resetPanelAttributes(oneSidedPanels: boolean, colorPanels: boolean) {
            const shapes: com.vzome.core.editor.api.Shapes = this.orbitSource.getShapes();
            const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>this.mManifestation;
            this.location = panel.getFirstVertex();
            const relativeVertices: java.util.List<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>());
            for(let index=panel.iterator();index.hasNext();) {
                let vertex = index.next();
                {
                    relativeVertices.add(vertex.minus(this.location));
                }
            }
            const normal: com.vzome.core.algebra.AlgebraicVector = panel['getNormal$']();
            if (normal.isOrigin())return;
            const zone: com.vzome.core.math.symmetry.Axis = this.orbitSource.getAxis(normal);
            const shape: com.vzome.core.math.Polyhedron = shapes.getPanelShape(panel.getVertexCount(), panel.getQuadrea(), zone, relativeVertices, oneSidedPanels);
            if (shape == null)return;
            this.mShape = shape;
            if (zone == null){
                this.setColor(com.vzome.core.construction.Color.WHITE_$LI$());
                return;
            }
            const orn: number = zone.getOrientation();
            const orientation: com.vzome.core.algebra.AlgebraicMatrix = shapes.getSymmetry().getMatrix(orn);
            this.setOrientation(orientation);
            this.strutZone = zone.getOrientation();
            if (!colorPanels)return;
            try {
                panel.setZoneVector(zone.normal());
                const orbit: com.vzome.core.math.symmetry.Direction = zone.getDirection();
                let color: com.vzome.core.construction.Color = this.mManifestation.getColor();
                if (color == null){
                    color = this.orbitSource.getColor(orbit);
                    if (color != null)color = color.getPastel();
                }
                this.setColor(color);
            } catch(e) {
                if (RenderedManifestation.logger_$LI$().isLoggable(java.util.logging.Level.WARNING))RenderedManifestation.logger_$LI$().warning("Unable to set color for panel, normal = " + normal.toString());
            }
        }

        resetStrutAttributes(strut: com.vzome.core.model.Strut) {
            const shapes: com.vzome.core.editor.api.Shapes = this.orbitSource.getShapes();
            const offset: com.vzome.core.algebra.AlgebraicVector = strut.getOffset();
            if (offset.isOrigin())return;
            const axis: com.vzome.core.math.symmetry.Axis = this.orbitSource.getAxis(offset);
            if (axis == null)return;
            strut.setZoneVector(axis.normal());
            const orbit: com.vzome.core.math.symmetry.Direction = axis.getDirection();
            const len: com.vzome.core.algebra.AlgebraicNumber = axis.getLength(offset);
            const prototypeLengthShape: com.vzome.core.math.Polyhedron = shapes.getStrutShape(orbit, len);
            this.mShape = prototypeLengthShape;
            const orn: number = axis.getOrientation();
            const orientation: com.vzome.core.algebra.AlgebraicMatrix = shapes.getSymmetry().getMatrix(orn);
            const reflection: com.vzome.core.algebra.AlgebraicMatrix = this.orbitSource.getSymmetry().getPrincipalReflection();
            if (reflection != null){
                if (RenderedManifestation.logger_$LI$().isLoggable(java.util.logging.Level.FINE)){
                    RenderedManifestation.logger_$LI$().fine("rendering " + offset + " as " + axis);
                }
                if (axis.getSense() === com.vzome.core.math.symmetry.Axis.MINUS){
                    if (RenderedManifestation.logger_$LI$().isLoggable(java.util.logging.Level.FINER)){
                        RenderedManifestation.logger_$LI$().finer("mirroring orientation " + orn);
                    }
                    this.mShape = prototypeLengthShape.getEvilTwin(reflection);
                }
                if (!axis.isOutbound()){
                    this.offsetLocation();
                } else this.resetLocation();
            } else {
                if (axis.getSense() === com.vzome.core.math.symmetry.Axis.MINUS){
                    this.offsetLocation();
                } else this.resetLocation();
            }
            this.setStrut(orbit, orn, axis.getSense(), len);
            this.setOrientation(orientation);
            let color: com.vzome.core.construction.Color = this.getManifestation().getColor();
            if (color == null)color = shapes.getColor(orbit);
            if (color == null)color = this.orbitSource.getColor(orbit);
            this.setColor(color);
        }

        resetConnectorAttributes(m: com.vzome.core.model.Connector) {
            const shapes: com.vzome.core.editor.api.Shapes = this.orbitSource.getShapes();
            this.mShape = shapes.getConnectorShape();
            let color: com.vzome.core.construction.Color = this.getManifestation().getColor();
            if (color == null)color = shapes.getColor(null);
            if (color == null)color = this.orbitSource.getColor(null);
            this.setColor(color);
            this.setOrientation(this.orbitSource.getSymmetry().getField().identityMatrix(3));
        }

        public setOrbitSource(orbitSource: com.vzome.core.editor.api.OrbitSource) {
            this.orbitSource = orbitSource;
        }

        public getOrbitSource(): com.vzome.core.editor.api.OrbitSource {
            return this.orbitSource;
        }

        public getLabel(): string {
            return this.label;
        }

        public setLabel(label: string) {
            this.label = label;
        }
    }
    RenderedManifestation["__class"] = "com.vzome.core.render.RenderedManifestation";
    RenderedManifestation["__interfaces"] = ["com.vzome.core.model.RenderedObject"];


}

