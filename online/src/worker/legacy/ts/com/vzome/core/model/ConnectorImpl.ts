/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.algebra.AlgebraicVector} loc
     * @class
     * @extends com.vzome.core.model.ManifestationImpl
     */
    export class ConnectorImpl extends com.vzome.core.model.ManifestationImpl implements com.vzome.core.model.Connector {
        public constructor(loc: com.vzome.core.algebra.AlgebraicVector) {
            super();
            if (this.m_center === undefined) { this.m_center = null; }
            if (this.label === undefined) { this.label = null; }
            this.m_center = loc;
        }

        /*private*/ m_center: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ label: string;

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getLocation(): com.vzome.core.algebra.AlgebraicVector {
            return this.m_center;
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getCentroid(): com.vzome.core.algebra.AlgebraicVector {
            return this.m_center;
        }

        /**
         * 
         * @return {com.vzome.core.construction.Construction}
         */
        public toConstruction(): com.vzome.core.construction.Construction {
            const first: com.vzome.core.construction.Construction = this.getFirstConstruction();
            if (first != null && first.is3d())return first;
            const field: com.vzome.core.algebra.AlgebraicField = this.m_center.getField();
            return new com.vzome.core.construction.FreePoint(field.projectTo3d(this.m_center, true));
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            return /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.m_center));
        }

        /**
         * 
         * @param {*} other
         * @return {boolean}
         */
        public equals(other: any): boolean {
            if (other == null)return false;
            if (other === this)return true;
            if (!(other != null && other instanceof <any>com.vzome.core.model.ConnectorImpl))return false;
            const conn: ConnectorImpl = <ConnectorImpl>other;
            return this.getLocation().equals(conn.getLocation());
        }

        /**
         * 
         * @param {*} other
         * @return {number}
         */
        public compareTo(other: com.vzome.core.model.Connector): number {
            if (this === other){
                return 0;
            }
            if (/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(other,this))){
                return 0;
            }
            return this.getLocation().compareTo(other.getLocation());
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return "connector at " + this.m_center.toString();
        }

        /**
         * 
         * @param {string} label
         */
        public setLabel(label: string) {
            this.label = label;
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return this.label;
        }
    }
    ConnectorImpl["__class"] = "com.vzome.core.model.ConnectorImpl";
    ConnectorImpl["__interfaces"] = ["com.vzome.core.model.HasRenderedObject","com.vzome.core.model.GroupElement","com.vzome.core.model.Connector","java.lang.Comparable","com.vzome.core.model.Manifestation"];


}

