import { java, javaemul } from "../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { PropertyChangeEvent } from "./PropertyChangeEvent.js";

/**
 * Constructs a new <code>IndexedPropertyChangeEvent</code> object.
 * 
 * @param {*} source  The bean that fired the event.
 * @param {string} propertyName  The programmatic name of the property that
 * was changed.
 * @param {*} oldValue      The old value of the property.
 * @param {*} newValue      The new value of the property.
 * @param {number} index index of the property element that was changed.
 * @class
 * @extends PropertyChangeEvent
 * @author Mark Davidson
 */
export class IndexedPropertyChangeEvent extends PropertyChangeEvent {
    static serialVersionUID: number = -320227448495806870;

    /*private*/ index: number;

    public constructor(source: any, propertyName: string, oldValue: any, newValue: any, index: number) {
        super(source, propertyName, oldValue, newValue);
        if (this.index === undefined) { this.index = 0; }
        this.index = index;
    }

    /**
     * Gets the index of the property that was changed.
     * 
     * @return {number} The index specifying the property element that was
     * changed.
     */
    public getIndex(): number {
        return this.index;
    }

    appendTo(sb: java.lang.StringBuilder) {
        sb.append("; index=").append(this.getIndex());
    }
}
IndexedPropertyChangeEvent["__class"] = "java.beans.IndexedPropertyChangeEvent";
IndexedPropertyChangeEvent["__interfaces"] = ["java.io.Serializable"];
