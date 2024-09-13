/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    export class Marker extends com.vzome.core.construction.Construction {
        /*private*/ mTarget: com.vzome.core.construction.Construction;

        public constructor(target: com.vzome.core.construction.Construction) {
            super(target.field);
            if (this.mTarget === undefined) { this.mTarget = null; }
            this.mTarget = target;
        }

        public getTarget(): com.vzome.core.construction.Construction {
            return this.mTarget;
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            return false;
        }

        /**
         * 
         * @param {*} doc
         * @return {*}
         */
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("marker");
            return result;
        }

        /**
         * 
         * @return {boolean}
         */
        public is3d(): boolean {
            return this.mTarget.is3d();
        }
    }
    Marker["__class"] = "com.vzome.core.construction.Marker";

}

