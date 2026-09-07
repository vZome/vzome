import { java, javaemul } from "../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { PropertyChangeEvent } from "./PropertyChangeEvent.js";
import { PropertyChangeListener } from "./PropertyChangeListener.js";

/**
 * Constructor which binds the {@code PropertyChangeListener}
 * to a specific property.
 * 
 * @param {string} propertyName  the name of the property to listen on
 * @param {*} listener      the listener object
 * @class
 * @extends java.util.EventListenerProxy
 */
export class PropertyChangeListenerProxy extends java.util.EventListenerProxy<PropertyChangeListener> implements PropertyChangeListener {
    /*private*/ propertyName: string;

    public constructor(propertyName: string, listener: PropertyChangeListener) {
        super(listener);
        if (this.propertyName === undefined) { this.propertyName = null; }
        this.propertyName = propertyName;
    }

    /**
     * Forwards the property change event to the listener delegate.
     * 
     * @param {PropertyChangeEvent} event  the property change event
     */
    public propertyChange(event: PropertyChangeEvent) {
        this.getListener().propertyChange(event);
    }

    /**
     * Returns the name of the named property associated with the listener.
     * 
     * @return {string} the name of the named property associated with the listener
     */
    public getPropertyName(): string {
        return this.propertyName;
    }
}
PropertyChangeListenerProxy["__class"] = "java.beans.PropertyChangeListenerProxy";
PropertyChangeListenerProxy["__interfaces"] = ["java.util.EventListener","java.beans.PropertyChangeListener"];
