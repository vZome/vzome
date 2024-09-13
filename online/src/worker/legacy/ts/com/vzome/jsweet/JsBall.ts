/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.jsweet {
    export class JsBall extends com.vzome.jsweet.JsManifestation implements com.vzome.core.model.Connector {
        public constructor(field: com.vzome.core.algebra.AlgebraicField, adapter: Object, coords: number[][][]) {
            super(field, adapter, coords);
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getLocation(): com.vzome.core.algebra.AlgebraicVector {
            return (<com.vzome.jsweet.JsAlgebraicField><any>this.field).createVectorFromTDs(this.vectors[0]);
        }

        /**
         * 
         * @return {com.vzome.core.construction.Construction}
         */
        public toConstruction(): com.vzome.core.construction.Construction {
            return new com.vzome.core.construction.FreePoint(this.getLocation());
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
    }
    JsBall["__class"] = "com.vzome.jsweet.JsBall";
    JsBall["__interfaces"] = ["com.vzome.core.model.GroupElement","com.vzome.core.model.Connector","java.lang.Comparable","com.vzome.core.model.Manifestation"];


}

