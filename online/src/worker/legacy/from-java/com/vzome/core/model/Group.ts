import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { GroupElement } from "./GroupElement.js";

export class Group extends java.util.ArrayList<GroupElement> implements GroupElement {
    /*private*/ mContainer: Group;

    public getContainer(): Group {
        return this.mContainer;
    }

    /**
     * 
     * @param {Group} container
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
