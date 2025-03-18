/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    export class Group extends java.util.ArrayList<com.vzome.core.model.GroupElement> implements com.vzome.core.model.GroupElement {
        /*private*/ mContainer: Group;

        public getContainer(): Group {
            return this.mContainer;
        }

        /**
         * 
         * @param {com.vzome.core.model.Group} container
         */
        public setContainer(container: Group) {
            this.mContainer = container;
        }

        constructor() {
            super();
            if (this.mContainer === undefined) { this.mContainer = null; }
        }
    }
    Group["__class"] = "com.vzome.core.model.Group";
    Group["__interfaces"] = ["java.util.RandomAccess","com.vzome.core.model.GroupElement","java.util.List","java.lang.Cloneable","java.util.Collection","java.lang.Iterable","java.io.Serializable"];


}

